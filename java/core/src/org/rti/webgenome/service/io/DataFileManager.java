/*L
 *  Copyright RTI International
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/webgenome/LICENSE.txt for details.
 */

/*
$Revision: 1.10 $
$Date: 2008-03-12 22:23:18 $


*/

package org.rti.webgenome.service.io;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.rti.webgenome.core.WebGenomeSystemException;
import org.rti.webgenome.domain.AnnotatedGenomeFeature;
import org.rti.webgenome.domain.Array;
import org.rti.webgenome.domain.ArrayDatum;
import org.rti.webgenome.domain.BioAssay;
import org.rti.webgenome.domain.BioAssayData;
import org.rti.webgenome.domain.ChromosomeArrayData;
import org.rti.webgenome.domain.ChromosomeReporters;
import org.rti.webgenome.domain.DataColumnMetaData;
import org.rti.webgenome.domain.DataFileMetaData;
import org.rti.webgenome.domain.DataSerializedBioAssay;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.Organism;
import org.rti.webgenome.domain.Plot;
import org.rti.webgenome.domain.Reporter;
import org.rti.webgenome.graphics.event.MouseOverStripes;
import org.rti.webgenome.graphics.io.ClickBoxes;
import org.rti.webgenome.service.session.WebGenomeDbService;
import org.rti.webgenome.util.StopWatch;
import org.rti.webgenome.util.SystemUtils;


/**
 * This class manages I/O of array data to the file system.
 * @author dhall
 *
 */
public final class DataFileManager {
    
    /** Logger. */
    private static final Logger LOGGER =
        Logger.getLogger(DataFileManager.class);
    
    
    // =====================================
    //        Attributes
    // =====================================
    
    /** Helper objects to serialize/de-serialize objects. */
    private final Serializer serializer;
    
    /**
     * This map caches <code>ChromosomeReporters</code>
     * objects in memory.  Keys are the names of files
     * containing the corresponding serialized
     * objects.  This cache is used not only for performance
     * but to ensure that instances of <code>ArrayDatum</code>
     * objects that logically point to the same reporters,
     * actually reference the same <code>Reporter</code>
     * objects.
     */
    private Map<String, ChromosomeReporters> chromosomeReportersCache =
        new HashMap<String, ChromosomeReporters>();
    
    /** Absolute path to directory where data are serialized. */
    private String dataDirectoryPath = null;
        
    
    // =====================================
    //       Constructors
    // =====================================
  
    
    /**
     * Constructor.
     * @param dataDirectoryPath Path to directory containing data files.
     * These are files that contain serialized data, not source
     * input data.
     */
    public DataFileManager(final String dataDirectoryPath) {
        this.serializer = new FileSerializer(dataDirectoryPath);
    }
    
    // ======================================
    //     Business methods
    // ======================================
    
    /**
     * Get rid of any data files not referenced by data in
     * shopping cart.
     * @param dbService Facade for transactional database operations
     */
    public void purgeOrphanDataFiles(final WebGenomeDbService dbService) {
    	File dir = new File(this.dataDirectoryPath);
    	if (!dir.exists() || !dir.isDirectory()) {
    		throw new WebGenomeSystemException(
    				"Invalid data directory: " + this.dataDirectoryPath);
    	}
    	File[] files = dir.listFiles();
    	Collection<String> referencedFiles =
    		dbService.getAllDataFileNames();
    	for (int i = 0; i < files.length; i++) {
    		File file = files[i];
    		String fName = file.getName();
    		if (!referencedFiles.contains(fName)) {
    			if (!file.delete()) {
    				LOGGER.warn("Could not delete data file: "
    						+ file.getAbsolutePath());
    			}
    		}
    	}
    }

    /**
     * Serialize reporters in given reader as a new array object.
     * @param smdFileReader Reader of SMD format files.
     * @return Array object containing persisted reporters
     */
    public Array serializeReporters(final SmdFileReader smdFileReader) {
    	
        // Create new array object and store reporters
        LOGGER.info("Serializing reporter data");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Reporter> reporters = smdFileReader.getReporters();
        ChromosomeReporters cr = null;
        Array array = new Array();
        for (Reporter r : reporters) {
            if (cr == null || cr.getChromosome() != r.getChromosome()) {
                if (cr != null) {
                    String fileName = this.serializer.serialize(cr);
                    array.setChromosomeReportersFileName(cr.getChromosome(),
                            fileName);
                    this.chromosomeReportersCache.put(fileName, cr);
                }
                cr = new ChromosomeReporters(r.getChromosome());
            }
            cr.add(r);
        }
        if (cr != null) {
            String fileName = this.serializer.serialize(cr);
            array.setChromosomeReportersFileName(cr.getChromosome(),
                    fileName);
            this.chromosomeReportersCache.put(fileName, cr);
        }
        LOGGER.info("Completed serialization of reporters");
        LOGGER.info("Elapsed time: " + stopWatch.getFormattedLapTime());
        return array;
    }
    
    /**
     * Serialize reporters whose properties are given in the three
     * given lists and return an Array object representing the
     * reporters.
     * @param arrayName Name of array to return
     * @param names Reporter names
     * @param chroms Reporter chromosome numbers
     * @param locs Reporter chromosome positions
     * @return Array containing reporters
     */
    public Array serializeReporters(final String arrayName,
    		final List<String> names,
    		final List<Short> chroms, final List<Long> locs) {
    	Array array = new Array();
    	array.setName(arrayName);
        ChromosomeReporters cr = null;
        Iterator<String> nameIt = names.iterator();
		Iterator<Short> chromIt = chroms.iterator();
		Iterator<Long> locIt = locs.iterator();
		while (nameIt.hasNext() && chromIt.hasNext() && locIt.hasNext()) {
			short tempChrom = chromIt.next();
            if (cr == null || cr.getChromosome() != tempChrom) {
                if (cr != null) {
                    String fileName = this.serializer.serialize(cr);
                    array.setChromosomeReportersFileName(cr.getChromosome(),
                            fileName);
                    this.chromosomeReportersCache.put(fileName, cr);
                }
                cr = new ChromosomeReporters(tempChrom);
            }
            Reporter r = new Reporter();
            r.setChromosome(tempChrom);
            r.setLocation(locIt.next());
            r.setName(nameIt.next());
            cr.add(r);
        }
        if (cr != null) {
            String fileName = this.serializer.serialize(cr);
            array.setChromosomeReportersFileName(cr.getChromosome(),
                    fileName);
            this.chromosomeReportersCache.put(fileName, cr);
        }
        return array;
    }
    
    /**
     * Extract specified data from an SMD-format file.
     * @param smdFileReader Reader of SMD data files
     * @param experiment Experiment into which parsed data will be
     * deposited.
     * @param dataFile File containing data to parse
     * @param dataFileMetaData Metadata on data file
     * @param organism Organism that experiment was performed against
     * @param array Array used in experiment
     * @throws SmdFormatException If the file is not valid
     * SMD format
     * @see {@link org.rti.webgenome.service.io.SmdFileReader}
     */
    public void convertSmdData(
    		final SmdFileReader smdFileReader,
    		final Experiment experiment,
    		final File dataFile,
            final DataFileMetaData dataFileMetaData,
            final Organism organism,
            final Array array)
        throws SmdFormatException {
        
        // Create new bioassay objects and store array data
        LOGGER.info("Serializing array data");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (DataColumnMetaData meta
        		: dataFileMetaData.getDataColumnMetaData()) {
            DataSerializedBioAssay ba =
                new DataSerializedBioAssay(meta.getBioAssayName(), organism);
            experiment.add(ba);
            ba.setArray(array);
            BioAssayData bad = smdFileReader.getBioAssayData(dataFile,
            		meta.getColumnName(),
            		dataFileMetaData.getReporterNameColumnName(),
            		dataFileMetaData.getFormat());
            LOGGER.info("Serializing bioassay " + ba.getName());
            for (ChromosomeArrayData cad
                    : bad.getChromosomeArrayData().values()) {
                ArrayDataAttributes ada = new ArrayDataAttributes(
                		cad.getChromosomeAlterations());
                for (Iterator<ArrayDatum> it = cad.getArrayData().iterator();
                    it.hasNext();) {
                    ArrayDatum ad = it.next();
                    ada.add(new ArrayDatumAttributes(ad.getValue(),
                            ad.getError()));
                    it.remove();
                }
                String fileName = this.serializer.serialize(ada);
                ba.registerChromosomeArrayData(cad, fileName);
            }
        }
        LOGGER.info("Completed serialization of array data");
        LOGGER.info("Elapsed time: " + stopWatch.getFormattedLapTime());
        LOGGER.info("Total elapsed time: "
                + stopWatch.getFormattedElapsedTime());
    }
        
    
    /**
     * Recover chromosome array data associated with
     * given bioassay from files.  Method is careful
     * to minimize memory usage.
     * @param bioAssay A bioassay
     * @param chromosome Chromosome number
     * @return Chromosome array data
     */
    public ChromosomeArrayData loadChromosomeArrayData(
            final DataSerializedBioAssay bioAssay, final short chromosome) {
    	ChromosomeArrayData cad = null;
    	
    	// Recover attributes not dependent on reporters
    	String fname = bioAssay.getFileName(chromosome);
    	if (fname != null) {
	        ArrayDataAttributes ada = (ArrayDataAttributes)
	            this.serializer.deSerialize(fname);
	        cad = new ChromosomeArrayData(chromosome);
	        cad.setChromosomeAlterations(ada.getChromosomeAlterations());
	        
	        // Recover reporter-dependent attributes
	    	if (bioAssay.getArray() != null) {
		        fname = bioAssay.getArray().getChromosomeReportersFileName(
		        		chromosome);
		        ChromosomeReporters cr = this.loadChromosomeReporters(fname);
		        Iterator<Reporter> rIt = cr.getReporters().iterator();
		        Iterator<ArrayDatumAttributes> aIt =
		            ada.getArrayDatumAttributes().iterator();
		        while (rIt.hasNext() && aIt.hasNext()) {
		            Reporter r = rIt.next();
		            ArrayDatumAttributes atts = aIt.next();
		            if (!Float.isNaN(atts.getValue())) {
		                ArrayDatum datum =
		                    new ArrayDatum(atts.getValue(), atts.getError(), r);
		                cad.add(datum);
		            }
		            aIt.remove();
		        }
	    	}
    	}
    	
        return cad;
    }
    
    
    /**
     * Recover reporters from given array.
     * @param array An array
     * @return Reporters from that array sorted by
     * chromosome and chromosome location.
     */
    public List<Reporter> recoverReporters(final Array array) {
    	List<Reporter> reporters = new ArrayList<Reporter>();
    	for (Short chrom : array.getChromosomeReportersFileNames().keySet()) {
    		String fname = array.getChromosomeReportersFileName(chrom);
    		ChromosomeReporters cr = this.loadChromosomeReporters(fname);
    		reporters.addAll(cr.getReporters());
    	}
    	Collections.sort(reporters);
    	return reporters;
    }
    
    /**
     * Save chromsome array data to disk and update
     * given bioassay with the file location of save data.
     * @param bioAssay Bioassay to which chromosome array data
     * is associated
     * @param chromosomeArrayData Chromosome array data
     */
    public void saveChromosomeArrayData(final DataSerializedBioAssay bioAssay,
            final ChromosomeArrayData chromosomeArrayData) {
    	short chromNum = chromosomeArrayData.getChromosome();
    	LOGGER.info("Serializing chromosome array data for "
    			+ bioAssay.getName() + " chromosome " + chromNum);
        Array array = bioAssay.getArray();
        if (array == null) {
        	if (bioAssay.numDatum() > 0) {
        		throw new IllegalArgumentException("Unknown reporter");
        	}
        } else {
        
	        // Save reporters if they have not been saved
	        if (array.getChromosomeReportersFileName(chromNum) == null) {
	        	LOGGER.info("Serializing reporters for " + bioAssay.getName()
	        			+ " chromosome " + chromNum);
	        	SortedSet<Reporter> reporters =
	        		chromosomeArrayData.getReporters();
	        	ChromosomeReporters cr = new ChromosomeReporters(chromNum);
	        	cr.setReporters(reporters);
	        	String fileName = this.serializer.serialize(cr);
	        	array.setChromosomeReportersFileName(chromNum, fileName);
	        	LOGGER.info("Completed serialization of reporters");
	        }
        }
        
        // Save array datum
        ArrayDataAttributes ada = new ArrayDataAttributes(
        		chromosomeArrayData.getChromosomeAlterations());
        for (ArrayDatum ad : chromosomeArrayData.getArrayData()) {
            ada.add(new ArrayDatumAttributes(ad.getValue(),
                    ad.getError()));
        }
        String fileName = this.serializer.serialize(ada);
        bioAssay.registerChromosomeArrayData(chromosomeArrayData,
                fileName);
        LOGGER.info("Completed serialization of array data");
    }
    
    
    /**
     * Delete files associated with given experiment.
     * @param exp An experiment
     * @param deleteReporters If set to <code>true</code>,
     * files containing reporter data are also deleted.
     */
    public void deleteDataFiles(final Experiment exp,
            final boolean deleteReporters) {
        
        // Delete array datum attribute files
        LOGGER.info("Deleting data files associated with experiment '"
                + exp.getName() + "'");
        for (BioAssay ba : exp.getBioAssays()) {
            if (ba instanceof DataSerializedBioAssay) {
               DataSerializedBioAssay dsba = (DataSerializedBioAssay) ba;
               for (Iterator<Short> it = dsba.getChromosomeArrayDataFileIndex()
                       .keySet().iterator(); it.hasNext();) {
                   short chromosome = it.next();
                   String fname = dsba.getFileName(chromosome);
                   try {
                	   this.serializer.decommissionObject(fname);
                   } catch (Exception e) {
                	   LOGGER.warn("Unable to delete serialized data file: "
                			   + fname);
                   }
                   it.remove();
               }
            }
        }
        
        // Delete reporters, if specified
        if (deleteReporters) {
            LOGGER.info("Deleting reporters associated with experiment '"
                    + exp.getName() + "'");
            
            // Collect list of reporter file names
            Set<String> fnames = new HashSet<String>();
            for (BioAssay ba : exp.getBioAssays()) {
                for (String fname : ba.getArray()
                        .getChromosomeReportersFileNames().values()) {
                    fnames.add(fname);
                }
            }
            
            // Delete files
            for (String fname : fnames) {
            	try {
            		this.serializer.decommissionObject(fname);
            	} catch (Exception e) {
            		LOGGER.warn("Error deleting reporter file: " + fname);
            	}
            }
        }
    }
    
    
    /**
     * Delete all data files associated with plot.
     * @param plot A plot
     */
    public void deleteDataFiles(final Plot plot) {
    	if (plot.getClickBoxesFileName() != null) {
    		this.deleteDataFile(plot.getClickBoxesFileName());
    	}
    	if (plot.getMouseOverStripesFileName() != null) {
    		this.deleteDataFile(plot.getMouseOverStripesFileName());
    	}
    }
    
    /**
     * Delete given data file.
     * @param fileName Name of file, not absolute path
     */
    public void deleteDataFile(final String fileName) {
    	this.serializer.decommissionObject(fileName);
    }
    

    /**
     * Persist given click boxes.
     * @param clickBoxes Click boxes to persist.
     * @return Name of file containing serialized click boxes
     */
    public String saveClickBoxes(final Set<ClickBoxes> clickBoxes) {
    	HashSet<ClickBoxes> hs = new HashSet<ClickBoxes>(clickBoxes);
    	return this.serializer.serialize(hs);
    }
    
    
    /**
     * Recover (deserialize) object in given file.
     * @param fileName Name of file
     * @return Oject serialized in file
     */
    public Serializable recoverObject(final String fileName) {
    	return this.serializer.deSerialize(fileName);
    }
    
    /**
     * Persist given mouse over stripes.
     * @param mouseOverStripes Mouseover stripes to persist
     * @return Name of file containing serialized mouseover stripes
     */
    public String saveMouseOverStripes(
    		final Set<MouseOverStripes> mouseOverStripes) {
    	HashSet<MouseOverStripes> hs =
    		new HashSet<MouseOverStripes>(mouseOverStripes);
    	return this.serializer.serialize(hs);
    }
    
    
    /**
     * Get chromosome reporters that are stored in given file.
     * This method uses in-memory caching to (1) provide
     * good performance and (2) to ensure that all references
     * to the same logical reporter actually point to the
     * same <code>Reporter</code> object.
     * @param fileName Name of file
     * @return Chromosome reporters
     */
    private ChromosomeReporters
        loadChromosomeReporters(final String fileName) {
        ChromosomeReporters cr = this.chromosomeReportersCache.get(fileName);
        if (cr == null) {
            LOGGER.info("De-serializing reporters");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            cr = (ChromosomeReporters)
                this.serializer.deSerialize(fileName);
            this.chromosomeReportersCache.put(fileName, cr);
            LOGGER.info("Completed de-serialization");
            LOGGER.info("Elapsed time: "
                    + stopWatch.getFormattedElapsedTime());
        }
        return cr;
    }
    
    
    /**
     * Aggregate of <code>ArrayDatumAttribute</code>
     * and {@code AnnotatedGenomeFeature}
     * objects from same chromosome.  This is used
     * for two purposes: (1) Multiple
     * <code>ArrayDatum</code> objects that point to the same
     * reporter can be serialized to different files.
     * (Calling method must know how to de-serialize reporters
     * from different files and properly associate <code>Reporter</code>
     * objects.)  (2) This format reduces memory requirements.
     * @author dhall
     *
     */
    static class ArrayDataAttributes implements Serializable {
        
        /** Serialized version ID. */
        private static final long serialVersionUID = 
    		SystemUtils.getLongApplicationProperty("serial.version.uid");
        
        // =============================
        //        Attributes
        // =============================
        
        /** Array datum attributes. */
        private List<ArrayDatumAttributes> arrayDatumAttributes =
            new ArrayList<ArrayDatumAttributes>();
        
        /** Chromosomal alterations. */
        private List<AnnotatedGenomeFeature> chromosomeAlterations =
        	new ArrayList<AnnotatedGenomeFeature>();
        	

        
        // ============================
        //     Getters
        // ============================
        
        /**
         * Get array datum attributes.
         * @return Array datum attributes
         */
        public List<ArrayDatumAttributes> getArrayDatumAttributes() {
            return arrayDatumAttributes;
        }
        
        /**
         * Get chromosome alterations.
         * @return Chromosome alterations
         */
        public List<AnnotatedGenomeFeature> getChromosomeAlterations() {
			return chromosomeAlterations;
		}
        
        
        // ============================
        //    Constructors
        // ============================


		/**
         * Constructor.
         * @param chromosomeAlterations Chromosome alterations
         */
        public ArrayDataAttributes(
        		final List<AnnotatedGenomeFeature> chromosomeAlterations) {
            this.chromosomeAlterations = chromosomeAlterations;
        }
        
        // =========================
        //       Business methods
        // =========================
        
        /**
         * Add an array datum attribute.
         * @param arrayDatumAttribute An array datum attribute
         */
        public void add(final ArrayDatumAttributes arrayDatumAttribute) {
            this.arrayDatumAttributes.add(arrayDatumAttribute);
        }
        
    }
    
    
    /**
     * Class for serializing attributes of <code>ArrayDatum</code>
     * objects.  This is used for two purposes: (1) Multiple
     * <code>ArrayDatum</code> objects that point to the same
     * reporter can be serialized to different files.
     * (Calling method must know how to de-serialize reporters
     * from different files and properly associate <code>Reporter</code>
     * objects.)  (2) This format reduces memory requirements.
     * @author dhall
     *
     */
    static class ArrayDatumAttributes implements Serializable {
        
        /** Serialized version ID. */
        private static final long serialVersionUID = (long) 1;
        
        // ===============================
        //      Attributes
        // ===============================
        
        /**
         * Corresponds to <code>value</code> attribute of
         * <code>ArrayDatum</code>.
         * */
        private float value = Float.NaN;
        
        /**
         * Corresponds to <code>error</code> attribute of
         * <code>ArrayDatum</code>.
         * */
        private float error = (float) -1.0;

        
        // =============================
        //    Getters/setters
        // =============================
        
        /**
         * Get error, which corresponds to <code>error</code> attribute of
         * <code>ArrayDatum</code>.
         * @return Error property
         */
        public float getError() {
            return error;
        }

        
        /**
         * Set error, which corresponds to <code>error</code> attribute of
         * <code>ArrayDatum</code>.
         * @param error Error
         */
        public void setError(final float error) {
            this.error = error;
        }

        
        /**
         * Get value, which corresponds to <code>value</code> attribute of
         * <code>ArrayDatum</code>.
         * @return Value
         */
        public float getValue() {
            return value;
        }

        /**
         * Set value, which corresponds to <code>value</code> attribute of
         * <code>ArrayDatum</code>.
         * @param value Value
         */
        public void setValue(final float value) {
            this.value = value;
        }


        // ===================================
        //      Constructors
        // ===================================
        
        /**
         * Constructor.
         */
        public ArrayDatumAttributes() {
            
        }
        
        /**
         * Constructor.
         * @param value Value, which corresponds to
         * <code>value</code> attribute of
         * <code>ArrayDatum</code>.
         * @param error Error, which corresponds to
         * <code>error</code> attribute of
         * <code>ArrayDatum</code>.
         */
        public ArrayDatumAttributes(final float value, final float error) {
            this.value = value;
            this.error = error;
        }
    }
}

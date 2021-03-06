/*L
 *  Copyright RTI International
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/webgenome/LICENSE.txt for details.
 */

/*
$Revision: 1.8 $
$Date: 2008-01-05 00:00:28 $


*/

package org.rti.webgenome.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.rti.webgenome.client.BioAssayDataConstraints;
import org.rti.webgenome.core.WebGenomeApplicationException;
import org.rti.webgenome.units.BpUnits;
import org.rti.webgenome.util.SystemUtils;


/**
 * Represents a genome interval.
 * @author dhall
 *
 */
public class GenomeInterval
implements Comparable<GenomeInterval>, Serializable {
	
	/** Serialized version ID. */
    private static final long serialVersionUID = 
		SystemUtils.getLongApplicationProperty("serial.version.uid");
	
	// ===========================
	//      Constants
	// ===========================
	
	/** Delimiter character between encoded genome intervals. */
	private static final String DELIMITER = ",";

	// =========================
	//      Attributes
	// =========================
	
	/** Primary key value for persistence. */
	private Long id = null;
	
	/** Chromosome number. */
	private short chromosome = (short) -1;
	
	/** Chromosomal start location of interval in base pairs. */
	private long startLocation = -1;
	
	/** Chromosomal end location of interval in base pairs. */
	private long endLocation = -1;
	
	
	// =========================
	//     Getters/setters
	// =========================

	/**
	 * Get primary key value for persistence.
	 * @return Primary key value
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * Set primary key value for persistence.
	 * @param id Primary key value
	 */
	public final void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Get chromosome number.
	 * @return Chromosome number
	 */
	public final short getChromosome() {
		return chromosome;
	}

	/**
	 * Set chromosome number.
	 * @param chromosome Chromosome number
	 */
	public final void setChromosome(final short chromosome) {
		this.chromosome = chromosome;
	}

	
	/**
	 * Get chromosomal end location of interval.
	 * @return Chromosomal end location of interval.
	 */
	public final long getEndLocation() {
		return endLocation;
	}

	
	/**
	 * Set chromosomal end location of interval.
	 * @param endLocation Chromosomal end location of interval.
	 */
	public final void setEndLocation(final long endLocation) {
		this.endLocation = endLocation;
	}

	
	/**
	 * Get chromosomal start location of interval.
	 * @return Chromosomal start location of interval.
	 */
	public final long getStartLocation() {
		return startLocation;
	}

	
	/**
	 * Set chromosomal start location of interval.
	 * @param startLocation Chromosomal start location of interval.
	 */
	public final void setStartLocation(final long startLocation) {
		this.startLocation = startLocation;
	}
	
	
	// ============================
	//     Constructors
	// ============================
	
	/**
	 * Constructor.
	 */
	public GenomeInterval() {
		
	}


	/**
	 * Constructor.
	 * @param chromosome Chromosome number
	 * @param startLocation Chromosomal start location of interval
	 * in base pairs
	 * @param endLocation Chromosomal end location of interval
	 * in base pairs
	 */
	public GenomeInterval(final short chromosome, final long startLocation,
			final long endLocation) {
		super();
		this.chromosome = chromosome;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
	}
	
	
	/**
	 * Constructor.
	 * @param interval A genome interval.
	 */
	public GenomeInterval(final GenomeInterval interval) {
		this.chromosome = interval.chromosome;
		this.endLocation = interval.endLocation;
		this.startLocation = interval.startLocation;
	}
	
	
	// ==============================
	//     Comparable interface
	// ==============================
	
	/**
	 * Comparison method.
	 * @param gi A genome interval
	 * @return -1, 0, 1 (see <code>Comparable.compareTo</code>)
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public final int compareTo(final GenomeInterval gi) {
		int value = 0;
		if (this.chromosome < gi.chromosome) {
			value = -1;
		} else if (this.chromosome == gi.chromosome) {
			if (this.startLocation < gi.startLocation) {
				value = -1;
			} else if (this.startLocation == gi.startLocation) {
				if (this.endLocation < gi.endLocation) {
					value = -1;
				} else if (this.endLocation == gi.endLocation) {
					value = 0;
				} else if (this.endLocation > gi.endLocation) {
					value = 1;
				}
			} else if (this.startLocation > gi.startLocation) {
				value = 1;
			}
		} else if (this.chromosome > gi.chromosome) {
			value = 1;
		}
		return value;
	}
	
	// ==================================
	//      Overrides
	// ==================================
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean equal = false;
		if (obj instanceof GenomeInterval) {
			equal = this.compareTo((GenomeInterval) obj) == 0;
		}
		return equal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return ("" + this.chromosome + this.endLocation
				+ this.startLocation).hashCode();
	}
	
	// =================================
	//     Business methods
	// =================================
	

	/**
	 * Does given interval overlap this?
	 * @param ival A genome interval
	 * @return T/F
	 */
	public final boolean overlaps(final GenomeInterval ival) {
		return this.chromosome == ival.chromosome
		&& this.endLocation >= ival.startLocation
		&& this.startLocation <= ival.endLocation;
	}
	
	
	/**
	 * Does this contain given genome interval?
	 * @param ival An interval
	 * @return T/F
	 */
	public final boolean contains(final GenomeInterval ival) {
		return this.chromosome == ival.chromosome
		&& ival.startLocation >= this.startLocation
		&& ival.endLocation <= this.endLocation;
	}
	
	
	/**
	 * Is the given chromosomal location in this interval?
	 * @param chromosome Chromosome number
	 * @param position Position on chromosome in base pairs
	 * @return T/F
	 */
	public final boolean contains(
			final short chromosome, final long position) {
		return this.chromosome == chromosome
		&& position >= this.startLocation
		&& position <= this.endLocation;
	}
	
	/**
	 * Is this interval completely to left of given?
	 * @param ival A genome interval
	 * @return T/F
	 */
	public final boolean leftOf(final GenomeInterval ival) {
		return this.endLocation < ival.startLocation;
	}
	
	
	/**
	 * Merge given interval into this one--i.e. expand
	 * this intervals endpoints if the given one extends
	 * past in either or both directions.
	 * @param ival A genome interval.
	 */
	public final void merge(final GenomeInterval ival) {
		if (this.overlaps(ival)) {
			if (ival.startLocation < this.startLocation) {
				this.startLocation = ival.startLocation;
			}
			if (ival.endLocation > this.endLocation) {
				this.endLocation = ival.endLocation;
			}
		}
	}
	
	
	/**
	 * Find intersection of two given genome intervals.
	 * @param ival1 A genome interval
	 * @param ival2 Another genome interval
	 * @return Intersetion or null if intervals do not overlap
	 */
	public static GenomeInterval intersection(final GenomeInterval ival1,
			final GenomeInterval ival2) {
		GenomeInterval newIval = null;
		if (ival1.overlaps(ival2)) {
			long start = ival1.startLocation;
			if (ival2.startLocation > start) {
				start = ival2.startLocation;
			}
			long end = ival1.endLocation;
			if (ival2.endLocation < end) {
				end = ival2.endLocation;
			}
			newIval = new GenomeInterval(ival1.chromosome, start, end);
		}
		return newIval;
	}
	
	/**
	 * Get bioassay data contraints.  This method will be used to
	 * convert a genome interval into an object that can be used
	 * to query client applications for data.
	 * @param units Base pair units
	 * @return Bioassay data constraints.
	 * @throws WebGenomeApplicationException If endpoints of this
	 * interval are not defined.
	 */
	public final BioAssayDataConstraints getBioAssayDataConstraints(
			final BpUnits units)
	throws WebGenomeApplicationException {
		if (this.startLocation < 0 || this.endLocation < 0) {
			throw new WebGenomeApplicationException(
					"Endpoints of genome interval must be specified");
		}
		BioAssayDataConstraints c = new BioAssayDataConstraints();
		c.setChromosome(String.valueOf(this.chromosome));
		long start = units.toBp(this.startLocation);
		long end = units.toBp(this.endLocation);
		c.setPositions(start, end);
		return c;
	}
	
	
	/**
	 * Get length of interval.
	 * @return Length of interval
	 */
	public final long length() {
		return this.endLocation - this.startLocation + 1;
	}
	
	/**
	 * Have the endpoints of the interval been specified?
	 * @return T/F
	 */
	public final boolean endpointsSpecified() {
		return this.startLocation >= 0 && this.endLocation >= 0;
	}
	
	
	/**
	 * Convert genome intervals into an array of bioassay data constraints.
	 * This method will be used to
	 * convert genome intervals into objects that can be used
	 * to query client applications for data.
	 * @param genomeIntervals Genome intervals
	 * @param units Base pair units
	 * @param expressionQType Quantitation type for expression data
	 * @param copyNumberQType Quantitation type for copy number data
	 * @return Bioassay data constraints
	 * @throws WebGenomeApplicationException If any of the bioassay data
	 * constraints does not have defined endpoints.
	 */
	public static final BioAssayDataConstraints[] getBioAssayDataConstraints(
			final Collection<GenomeInterval> genomeIntervals,
			final BpUnits units,
			final QuantitationType expressionQType,
			final QuantitationType copyNumberQType)
	throws WebGenomeApplicationException {
		List<BioAssayDataConstraints> c =
			new ArrayList<BioAssayDataConstraints>();
		for (GenomeInterval gi : genomeIntervals) {
			if (expressionQType != null) {
				BioAssayDataConstraints con =
					gi.getBioAssayDataConstraints(units);
				con.setQuantitationType(expressionQType.getId());
				c.add(con);
			}
			if (copyNumberQType != null) {
				BioAssayDataConstraints con =
					gi.getBioAssayDataConstraints(units);
				con.setQuantitationType(copyNumberQType.getId());
				c.add(con);
			}
		}
		BioAssayDataConstraints[] constraints = new BioAssayDataConstraints[0];
		constraints = c.toArray(constraints);
		return constraints;
	}
	
	/**
	 * Decode genome intervals encoded like '1:1000-2000;2:50-150'.
	 * This string encodes two genome intervals.  The first
	 * is chromosome 1 from position 1000 to 2000.  The positions
	 * units may be base pairs or a larger multiple of base pairs
	 * given by the {@code units} parameter.
	 * @param encoding Encoded genome interval
	 * @param units Units applicable to start and end locations
	 * @return Genome intervals
	 * @throws GenomeIntervalFormatException if intervals are
	 * not well-formed.
	 */
	public static final Collection<GenomeInterval> decode(
			final String encoding, final BpUnits units)
	throws GenomeIntervalFormatException {
		if (encoding == null) {
			throw new IllegalArgumentException("Genome intervals are null");
		}
		GenomeIntervalCoder coder = new GenomeIntervalCoder();
		Collection<GenomeInterval> intervals = new ArrayList<GenomeInterval>();
		StringTokenizer tok = new StringTokenizer(encoding, DELIMITER);
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			GenomeInterval interval = new GenomeInterval(
					coder.parseChromosome(token),
					units.toBp(coder.parseStart(token)), 
					units.toBp(coder.parseEnd(token)));
			intervals.add(interval);
		}
		return intervals;
	}
	
	
	/**
	 * Encode list of genome intervals as a string.
	 * @param intervals Genome intervals
	 * @param units Base pair units used for encoding
	 * start and end positions of intervals.
	 * @return Encoded genome intervals
	 * @see org.rti.webgenome.domain.GenomeInterval#decode(String, BpUnits)
	 * for a description of the encoded format.
	 */
	public static final String encode(
			final Collection<GenomeInterval> intervals,
			final BpUnits units) {
		if (intervals == null) {
			throw new IllegalArgumentException("Genome intervals are null");
		}
		StringBuffer buffer = new StringBuffer();
		int count = 0;
		for (GenomeInterval i : intervals) {
			if (count++ > 0) {
				buffer.append(DELIMITER);
			}
			buffer.append(encode(i, units));
		}
		return buffer.toString();
	}
	
	
	/**
	 * Encode given bioassay data constraints into
	 * a string of form '1:100-200;3:500-600'.
	 * The start and end points of the intervals will
	 * be in base pair units.
	 * @param constraints Bioassay data constraints
	 * @return Encoded constraints
	 */
	public static final String encode(
			final BioAssayDataConstraints[] constraints) {
		if (constraints == null) {
			throw new IllegalArgumentException(
					"Bioassay data onstraints are null");
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < constraints.length; i++) {
			if (i > 0) {
				buffer.append(DELIMITER);
			}
			buffer.append(encode(constraints[i]));
		}
		return buffer.toString();
	}
	
	
	/**
	 * Encode a genome interval as a string of form 1:100-200.
	 * @param interval Genome interval to encode
	 * @param units Units to use for encoding start and end
	 * locations on chromosome
	 * @return Encoded genome interval
	 */
	private static String encode(final GenomeInterval interval,
			final BpUnits units) {
		StringBuffer buff =
			new StringBuffer(String.valueOf(interval.chromosome));
		if (interval.getStartLocation() >= 0
				&& interval.getEndLocation() >= 0) {
			buff.append(":"
					+ units.fromBp(interval.getStartLocation())
					+ "-"
					+ units.fromBp(interval.getEndLocation()));
		}
		return buff.toString();
	}
	
	
	/**
	 * Encode given constraints into a string of format
	 * '1:100-200'.  Chromosome start and end positions
	 * will be in base pair units.
	 * @param constraints Bioassay data constraints
	 * @return Encoded bioassay data constraints
	 */
	private static String encode(final BioAssayDataConstraints constraints) {
		StringBuffer buff = new StringBuffer(constraints.getChromosome());
		if (constraints.getStartPosition() >= 0
				&& constraints.getEndPosition() >= 0) {
			buff.append(":" + constraints.getStartPosition() + "-"
					+ constraints.getEndPosition());
		}
		return buff.toString();
	}
	
	/**
	 * Get set of chromosomes from given collection.
	 * @param intervals Genome intervals
	 * @return Chromosomes
	 */
	public static final Set<Short> getChromosomes(
			final Collection<GenomeInterval> intervals) {
		if (intervals == null) {
			throw new IllegalArgumentException("Intervals null");
		}
		Set<Short> chromosomes = new HashSet<Short>();
		for (GenomeInterval gi : intervals) {
			chromosomes.add(gi.getChromosome());
		}
		return chromosomes;
	}
	
	
	/**
	 * Is given array datum from this genome interval?
	 * @param datum An array datum
	 * @return T/F
	 */
	public boolean contains(final ArrayDatum datum) {
		Reporter r = datum.getReporter();
		return this.contains(r.getChromosome(), r.getLocation());
	}
	
	
	/**
	 * Is the given array datum from at least one of the given
	 * genome intervals?
	 * @param intervals Genome intervals to check against
	 * @param datum An array datum
	 * @return T/F
	 */
	public static boolean contains(
			final Collection<GenomeInterval> intervals,
			final ArrayDatum datum) {
		boolean contains = false;
		for (GenomeInterval ival : intervals) {
			if (ival.contains(datum)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	
	// ==============================
	//      Helper classes
	// ==============================
	
	/**
	 * Parses fields out of individual genome interval
	 * encodings like '1:100-200'.
	 */
	static final class GenomeIntervalCoder {
		
		/**
		 * Parse chromosome number.
		 * @param encoding Genome interval encoding.
		 * @return Chromosome number of -1 if the
		 * chromosome number cannot be extracted.
		 * @throws GenomeIntervalFormatException if
		 * chromosome number is not numeric.
		 */
		short parseChromosome(final String encoding)
		throws GenomeIntervalFormatException {
			short chrom = (short) -1;
			int p = 0;
			int q = encoding.indexOf(":");
			if (q < 0) {
				q = encoding.length();
			}
			if (p < q) {
				String chromStr = encoding.substring(p, q).trim();
				try {
					chrom = Short.parseShort(chromStr);
				} catch (NumberFormatException e) {
					throw new GenomeIntervalFormatException(
							"Invalid chromosome :" + chromStr);
				}
			}
			return chrom;
		}
		
		/**
		 * Parse start location from given encoded genome interval.
		 * @param encoding Encoded genome interval.
		 * @return Start location or -1 if start location missing
		 * but format valid.
		 * @throws GenomeIntervalFormatException if format is
		 * invalid.
		 */
		long parseStart(final String encoding)
		throws GenomeIntervalFormatException {
			long start = -1;
			int p = encoding.indexOf(":");
			int q = encoding.indexOf("-");
			if (p >= 0 && q < 0) {
			    throw new GenomeIntervalFormatException(
			    		"Bad genome interval format");
			}
			if (p >= 0 && q >= 0 && p + 1 < q) {
				String startStr = encoding.substring(p + 1, q).trim();
				try {
					start = Long.parseLong(startStr);
				} catch (NumberFormatException e) {
					throw new GenomeIntervalFormatException(
							"Start position is not numeric: + startStr");
				}
			}
			return start;
		}
		
		
		/**
		 * Parse end position from given genome interval encoding.
		 * @param encoding Genome interval encoding.
		 * @return End position or -1 if end position missing
		 * but interval is well-formed.
		 * @throws GenomeIntervalFormatException if format is
		 * invalid.
		 */
		long parseEnd(final String encoding)
		throws GenomeIntervalFormatException {
			long end = -1;
			int p = encoding.indexOf("-");
			int q = encoding.length();
			if (p >= 0 && p + 1 < q) {
				String endStr = encoding.substring(p + 1, q).trim();
				try {
					end = Long.parseLong(endStr);
				} catch (NumberFormatException e) {
					throw new GenomeIntervalFormatException(
							"End position not numeric: " + endStr);
				}
			}	
			return end;
		}

	}
}

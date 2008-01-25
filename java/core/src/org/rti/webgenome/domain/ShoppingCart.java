/*
$Revision: 1.9 $
$Date: 2007-10-10 17:47:02 $

The Web CGH Software License, Version 1.0

Copyright 2003 RTI. This software was developed in conjunction with the
National Cancer Institute, and so to the extent government employees are
co-authors, any rights in such works shall be subject to Title 17 of the
United States Code, section 105.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions and the disclaimer of Article 3, below. Redistributions in 
binary form must reproduce the above copyright notice, this list of conditions 
and the following disclaimer in the documentation and/or other materials 
provided with the distribution.

2. The end-user documentation included with the redistribution, if any, must 
include the following acknowledgment:

"This product includes software developed by the RTI and the National Cancer 
Institute."

If no such end-user documentation is to be included, this acknowledgment shall 
appear in the software itself, wherever such third-party acknowledgments 
normally appear.

3. The names "The National Cancer Institute", "NCI", 
�Research Triangle Institute�, and "RTI" must not be used to endorse or promote 
products derived from this software.

4. This license does not authorize the incorporation of this software into any 
proprietary programs. This license does not authorize the recipient to use any 
trademarks owned by either NCI or RTI.

5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
(INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE
NATIONAL CANCER INSTITUTE, RTI, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.rti.webgenome.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rti.webgenome.core.WebGenomeSystemException;
import org.rti.webgenome.graphics.util.ColorChooser;
import org.rti.webgenome.service.io.ImageFileManager;
import org.rti.webgenome.util.SystemUtils;

/**
 * Represents an on-line data shopping cart.
 * @author dhall
 *
 */
public class ShoppingCart implements Serializable {
    
    /** Serialized version ID. */
    private static final long serialVersionUID = 
		SystemUtils.getLongApplicationProperty("serial.version.uid");
    
    
    // ====================================
    //       Attributes
    // ====================================
    
    /** Identifier used as primary key for persistent storage. */
    private Long id = null;
    
    /** Experiments in shopping cart. */
    private Set<Experiment> experiments = new HashSet<Experiment>();
    
    /** Plots in shopping cart. */
    private List<Plot> plots = new ArrayList<Plot>();
   
    /** User name associated with cart. */
    private String userName = null;
    
    /** Domain in which the user name is valid. */
    private String userDomain = null;
    
    /**
     * Image file manager.  If this propery is not null,
     * then when a shopping cart instance is finalized,
     * it will delete associated image files.
     */
    private ImageFileManager imageFileManager;
    
    /** Color chooser for assigning bioassay colors. */
    private ColorChooser bioassayColorChooser = new ColorChooser();
    
    // =============================
    //     Getters/setters
    // =============================

    /**
     * Get plots in cart.
     * @return Plots in cart.
     */
    public final List<Plot> getPlots() {
		return plots;
	}

    /**
     * Set plots in cart.
     * @param plots Plots in cart.
     */
	public final void setPlots(final List<Plot> plots) {
		this.plots = plots;
	}
	
	/**
	 * Get domain in which user name is valid.
	 * @return Domain
	 */
	public final String getUserDomain() {
		return userDomain;
	}

	
	/**
	 * Set domain in which user name is valid.
	 * @param userDomain Domain
	 */
	public final void setUserDomain(final String userDomain) {
		this.userDomain = userDomain;
	}

	/**
	 * Get color chooser for bioassays.
	 * @return Color chooser
	 */
	public final ColorChooser getBioassayColorChooser() {
		return bioassayColorChooser;
	}

	/**
	 * Set color chooser for bioassays.
	 * @param bioassayColorChooser Color chooser
	 */
	public final void setBioassayColorChooser(
			final ColorChooser bioassayColorChooser) {
		this.bioassayColorChooser = bioassayColorChooser;
	}

	/**
	 * Get last plot added to cart.
	 * @return Last plot added to cart.
	 */
	public Plot getLastPlotIn() {
		Plot last = null;
		if (this.plots.size() > 0) {
			last = this.plots.get(this.plots.size() - 1);
		}
		return last;
	}

	/**
     * Get identifier used as a primary key in persitence.
     * @return Identifier
     */
    public final Long getId() {
        return id;
    }

    /**
     * Set identifier used as primary key in persistence.
     * @param id Identifier
     */
    public final void setId(final Long id) {
        this.id = id;
    }

    /**
     * Get experiments in cart.
     * @return Experiments in cart
     */
    public final Set<Experiment> getExperiments() {
        return experiments;
    }

    /**
     * Set experiments in cart.
     * @param experiments Experiments in cart.
     */
    public final void setExperiments(final Set<Experiment> experiments) {
        this.experiments = experiments;
    }

    /**
     * Get user name associated with cart.
     * @return User name
     */
    public final String getUserName() {
        return userName;
    }

    /**
     * Set user name associated with cart.
     * @param userName User name
     */
    public final void setUserName(final String userName) {
        this.userName = userName;
    }
    
    /**
     * Get image file manager.
     * @return Image file manager.
     */
    public final ImageFileManager getImageFileManager() {
		return imageFileManager;
	}

    
    /**
     * Set image file manager.  If this property is set,
     * then associated image files will be deleted when
     * a shopping cart instance is finalized.
     * @param imageFileManager Image file manager.
     */
	public final void setImageFileManager(
			final ImageFileManager imageFileManager) {
		this.imageFileManager = imageFileManager;
	}
	
	
    
    // ================================
    //      Constructors
    // ================================

	/**
     * Constructor.
     */
    public ShoppingCart() {

    }
    
    /**
     * Constructor.
     * @param userName User name
     * @param userDomain Domain in which user name is valid
     */
    public ShoppingCart(final String userName, final String userDomain) {
        this.userName = userName;
        this.userDomain = userDomain;
    }
   

    // =================================
    //      Other public methods
    // =================================
    
    /**
     * Add an experiment to cart.
     * @param experiment An experiment
     */
    public final void add(final Experiment experiment) {
        this.experiments.add(experiment);
    }
    
    
    /**
     * Add experiments to cart.
     * @param experiments Experiments
     */
    public final void add(final Collection<Experiment> experiments) {
    	for (Experiment exp : experiments) {
    		this.add(exp);
    	}
    }
    
    
    /**
     * Remove an experiment from cart.
     * @param experiment An experiment.
     */
    public final void remove(final Experiment experiment) {
        this.experiments.remove(experiment);
    }
    
    
    /**
     * Add plot to cart.
     * @param plot A plot.
     */
    public final void add(final Plot plot) {
    	this.plots.add(plot);
    }
    
    
    /**
     * Remove plot from cart.
     * @param plot A plot.
     */
    public final void remove(final Plot plot) {
    	this.plots.remove(plot);
    	if (this.imageFileManager != null) {
    		for (String fName : plot.getAllImageFileNames()) {
    			this.imageFileManager.deleteImageFile(fName);
    		}
    	}
    }
    
    
    /**
     * Remove experiment with given id from cart.
     * @param id Experiment primary key identifier.
     */
    public final void removeExperiment(final Long id) {
    	Iterator<Experiment> it = this.experiments.iterator();
    	while (it.hasNext()) {
    		if (id.equals(it.next().getId())) {
    			it.remove();
    			break;
    		}
    	}
    }
    
    
    /**
     * Remove experiments with given IDs.
     * @param ids Experiment IDs.
     */
    public final void removeExperiments(final Collection<Long> ids) {
    	for (Long id : ids) {
    		this.removeExperiment(id);
    	}
    }
    
    
    /**
     * Remove plot with given id from cart.
     * @param id Plot primary key identifier.
     */
    public final void removePlot(final Long id) {
    	Iterator<Plot> it = this.plots.iterator();
    	while (it.hasNext()) {
    		if (id.equals(it.next().getId())) {
    			it.remove();
    			break;
    		}
    	}
    }
    
    
    /**
     * Get experiments with given IDs.
     * @param ids Experiment IDs
     * @return Experiments
     */
    public final Collection<Experiment> getExperiments(
    		final Collection<Long> ids) {
    	Collection<Experiment> experiments = new ArrayList<Experiment>();
    	for (Long id : ids) {
    		Experiment exp = null;
    		for (Experiment source : this.experiments) {
    			if (id.equals(source.getId())) {
    				exp = source;
    				break;
    			}
    		}
    		if (exp == null) {
    			throw new WebGenomeSystemException("Experiment '"
    					+ id + "' not in shopping cart");
    		}
    		experiments.add(exp);
    	}
    	return experiments;
    }
    
    
    /**
     * Get plot with given ID.
     * @param plotId Plot ID.
     * @return Plot
     */
    public final Plot getPlot(final Long plotId) {
    	Plot plot = null;
    	for (Plot p : this.plots) {
    		if (plotId.equals(p.getId())) {
    			plot = p;
    			break;
    		}
    	}
    	return plot;
    }
    
    
    /**
     * Get experiment with given ID.
     * @param id Experiment ID
     * @return An experiment
     */
    public final Experiment getExperiment(final Long id) {
    	if (id == null) {
    		throw new IllegalArgumentException("Experiment ID is null");
    	}
    	Experiment experiment = null;
    	for (Experiment exp : this.experiments) {
    		if (id.equals(exp.getId())) {
    			experiment = exp;
    			break;
    		}
    	}
    	return experiment;
    }
    
    /**
     * Get bioassay with given ID.
     * @param id Bioassay ID
     * @return A bioassay
     */
    public final BioAssay getBioAssay(final Long id) {
    	if (id == null) {
    		throw new IllegalArgumentException("Bioassay ID is null");
    	}
    	BioAssay bioAssay = null;
    	for (Iterator<Experiment> it = this.experiments.iterator();
    		it.hasNext() && bioAssay == null;) {
    		Experiment exp = it.next();
    		for (BioAssay ba : exp.getBioAssays()) {
    			if (id.equals(ba.getId())) {
    				bioAssay = ba;
    				break;
    			}
    		}
    	}
    	return bioAssay;
    }

    
    // ============================
    //      Overrides
    // ============================
    
    /**
     * Finalize object.
     * @throws Throwable if anything bad happens.
     */
	@Override
	protected final void finalize() throws Throwable {
		
		// Get rid of image files
		if (this.imageFileManager != null) {
			for (Plot p : this.plots) {
				for (String name : p.getAllImageFileNames()) {
					this.imageFileManager.deleteImageFile(name);
				}
			}
		}
		
		// Clear contents so app server will not try to
		// recover them in the event that the server
		// is restarted
		this.experiments.clear();
		this.plots.clear();
	}
}
/*
$Revision: 1.6 $
$Date: 2007-09-06 16:48:10 $

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
“Research Triangle Institute”, and "RTI" must not be used to endorse or promote 
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

package org.rti.webgenome.service.plot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.GenomeInterval;
import org.rti.webgenome.domain.QuantitationType;
import org.rti.webgenome.graphics.PlotBoundaries;
import org.rti.webgenome.graphics.event.EventHandlerGraphicBoundaries;
import org.rti.webgenome.graphics.event.MouseOverStripes;
import org.rti.webgenome.graphics.io.ClickBoxes;
import org.rti.webgenome.graphics.widget.Axis;
import org.rti.webgenome.graphics.widget.Background;
import org.rti.webgenome.graphics.widget.Caption;
import org.rti.webgenome.graphics.widget.Grid;
import org.rti.webgenome.graphics.widget.Legend;
import org.rti.webgenome.graphics.widget.PlotPanel;
import org.rti.webgenome.graphics.widget.ScatterPlot;
import org.rti.webgenome.service.util.ChromosomeArrayDataGetter;
import org.rti.webgenome.units.BpUnits;
import org.rti.webgenome.units.HorizontalAlignment;
import org.rti.webgenome.units.Location;
import org.rti.webgenome.units.Orientation;
import org.rti.webgenome.units.VerticalAlignment;

/**
 * Manages the painting scatter plots by getting
 * data and assembling plot widgets.
 * @author dhall
 *
 */
public class ScatterPlotPainter extends PlotPainter {
	
	// ===========================
	//       Constants
	// ===========================
	
	/** Grid color. */
	private static final Color GRID_COLOR = Color.WHITE;
	
	/** Background color. */
	private static final Color BG_COLOR = new Color(235, 235, 235);
	
	// ===============================
	//    Constructors
	// ===============================
	
	/**
	 * Constructor.
	 * @param chromosomeArrayDataGetter Chromosome array data getter
	 */
	public ScatterPlotPainter(
			final ChromosomeArrayDataGetter chromosomeArrayDataGetter) {
		super(chromosomeArrayDataGetter);
	}
	
	// =========================
	//    Business methods
	// =========================
    
    /**
     * Paints a plot on the given plot panel.
     * @param panel Plot panel to add the scatter plot to
     * @param experiments Experiments to plot
     * @param params Plotting parameters specified
     * by user
     * @return Boundaries of event handler regions.
     */
    public final EventHandlerGraphicBoundaries paintPlot(
    		final PlotPanel panel,
    		final Collection<Experiment> experiments,
    		final ScatterPlotParameters params) {
        
        // Check args
        if (experiments == null || panel == null) {
            throw new IllegalArgumentException(
                    "Experiments and panel cannot be null");
        }
        if (params.getGenomeIntervals() == null
        		|| params.getGenomeIntervals().size() < 1) {
        	throw new IllegalArgumentException(
        			"No genome intervals specified");
        }
        
        RowAdder rowAdder = new RowAdder(experiments, params,
        		this.getChromosomeArrayDataGetter());
        int rowCount = 1;
        while (rowAdder.hasMore()) {
        	PlotPanel row = panel.newChildPlotPanel();
        	rowAdder.addRow(row);
        	VerticalAlignment va = null;
        	if (rowCount++ == 1) {
				va = VerticalAlignment.TOP_JUSTIFIED;
			} else {
				va = VerticalAlignment.BELOW;
			}
			panel.add(row, HorizontalAlignment.LEFT_JUSTIFIED, va);
        }
		
		// Legend
        Legend legend = new Legend(experiments, params.getWidth());
        panel.add(legend, HorizontalAlignment.CENTERED,
                VerticalAlignment.BELOW);
        
        // Gather up click boxes and mouseover stripes
        Set<ClickBoxes> boxes = new HashSet<ClickBoxes>();
        Set<MouseOverStripes> stripes =
        	new HashSet<MouseOverStripes>();
        for (ScatterPlot plot : rowAdder.getPlotsCreated()) {
        	boxes.add(plot.getClickBoxes());
        	stripes.add(plot.getMouseOverStripes());
        }
        return new EventHandlerGraphicBoundaries(stripes, boxes);
    }
    
    
    /**
     * Helper class that adds a new row to the overall plot.
     * @author dhall
     */
    static final class RowAdder {
    	
    	//
    	//  A T T R I B U T E S
    	//
    	
    	/** Experiments containing data to plot. */
    	private Collection<Experiment> experiments =
    		new ArrayList<Experiment>();
    	
    	/** Index variable that points to next genome interval to plot. */
    	private int idx = 0;
    	
    	/** Plots created since object instantiated. */
    	private Collection<ScatterPlot> plots =
    		new ArrayList<ScatterPlot>();
    	
    	/** Plotting parameters. */
    	private ScatterPlotParameters params = null;;
    	
    	/** Genome intervals to plot. */
    	private List<GenomeInterval> genomeIntervals =
    		new ArrayList<GenomeInterval>();
    	
    	/**
    	 * Quantitation type of left Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.
    	 */
    	private QuantitationType leftYAxisQuantitationType = null;
    	
    	/**
    	 * Quantitation type of right Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.  This will remain null
    	 * unless the plot is co-visualization.
    	 */
    	private QuantitationType rightYAxisQuantitationType = null;
    	
    	/**
    	 * Minimum value on left Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.
    	 */
    	private double leftYAxisMinValue = Double.NaN;
    	
    	/**
    	 * Maximum value on left Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.
    	 */
    	private double leftYAxisMaxValue = Double.NaN;
    	
    	/**
    	 * Minimum value on right Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.  This will remain
    	 * NaN unless plot is co-visualization.
    	 */
    	private double rightYAxisMinValue;
    	
    	/**
    	 * Maximum value on right Y-axis.  Two Y-axes
    	 * are needed for co-visualization (i.e. copy number
    	 * and expression data) plots.  This will remain
    	 * NaN unless plot is co-visualization.
    	 */
    	private double rightYAxisMaxValue;
    	
    	/** Sizes individual genome interval scatter plots. */
    	private ScatterPlotSizer sizer;
    
    	/** Getter for data. */
    	private ChromosomeArrayDataGetter chromosomeArrayDataGetter;
    	
    	//
    	//  C O N S T R U C T O R S
    	//
    	
    	/**
    	 * Constructor.
    	 * @param experiments Experiments to plot.
    	 * @param params Plotting parameters
    	 * @param chromosomeArrayDataGetter Data getter
    	 */
    	private RowAdder(final Collection<Experiment> experiments,
    			final ScatterPlotParameters params,
    			final ChromosomeArrayDataGetter chromosomeArrayDataGetter) {
    		this.experiments.addAll(experiments);
    		this.params = params;
    		this.genomeIntervals.addAll(params.getGenomeIntervals());
    		Collections.sort(this.genomeIntervals);
    		Collection<Short> chromosomes =
    			Experiment.chromosomes(experiments);
    		QuantitationType copyNumberQT =
    			Experiment.getCopyNumberQuantitationType(experiments);
    		if (copyNumberQT != null) {
    			this.leftYAxisQuantitationType = copyNumberQT;
    			this.leftYAxisMaxValue = params.getCopyNumberMaxY();
    			this.leftYAxisMinValue = params.getCopyNumberMinY();
    		}
    		QuantitationType expressionQT = 
    			Experiment.getExpressionQuantitationType(experiments);
    		if (expressionQT != null) {
    			if (this.leftYAxisQuantitationType == null) {
    				this.leftYAxisQuantitationType = expressionQT;
    				this.leftYAxisMaxValue = params.getExpressionMaxY();
    				this.leftYAxisMinValue = params.getExpressionMinY();
    			} else {
    				this.rightYAxisQuantitationType = expressionQT;
    				this.rightYAxisMaxValue = params.getExpressionMaxY();
    				this.rightYAxisMinValue = params.getExpressionMinY();
    			}
    		}
    		this.sizer = new ScatterPlotSizer(params);
    		this.chromosomeArrayDataGetter = chromosomeArrayDataGetter;
    	}
    	
    	
    	//
    	//  B U S I N E S S  M E T H O D S
    	//
    	
    	/**
    	 * Add new "row" to given panel.  The row will consist
    	 * of a number of widgets layed out on the given panel
    	 * that show one or more scatter plots to different
    	 * genome intervals.
    	 * @param row Panel on which to draw row.
    	 */
    	private void addRow(final PlotPanel row) {
    		if (this.idx < this.genomeIntervals.size()) {
    			Axis yAxis = this.addLeftYAxis(row);
    			int endIdx = this.idx + this.params.getNumPlotsPerRow();
    			if (endIdx > this.genomeIntervals.size()) {
    				endIdx = this.genomeIntervals.size();
    			}
    			for (int i = this.idx; i < endIdx; i++) {
    				this.addGenomeIntervalPlot(row, this.genomeIntervals.get(i),
    						yAxis);
    			}
    			if (this.rightYAxisQuantitationType != null) {
    				this.addRightYAxis(row);
    			}
    			this.idx = endIdx;
    		}
    	}
    	
    	
    	/**
    	 * Add left Y-axis to row.
    	 * @param row Panel to draw axis on
    	 * @return Newly-created axis
    	 */
    	private Axis addLeftYAxis(final PlotPanel row) {
    		Axis yAxis = new Axis(this.leftYAxisMinValue,
	                this.leftYAxisMaxValue, this.sizer.height(),
	                Orientation.VERTICAL, Location.LEFT_OF,
	                row.getDrawingCanvas());
	        Caption yCaption = new Caption(
	        		leftYAxisQuantitationType.getName(),
	                Orientation.HORIZONTAL, true, row.getDrawingCanvas());
	        row.add(yAxis, HorizontalAlignment.LEFT_JUSTIFIED,
	                VerticalAlignment.BOTTOM_JUSTIFIED);
	        row.add(yCaption, HorizontalAlignment.LEFT_OF,
	                VerticalAlignment.CENTERED);
	        return yAxis;
    	}
    	
    	
    	/**
    	 * Co-visualization plots require two Y-axes.  This
    	 * method adds a second Y-axis to the right of the
    	 * given row of plots.
    	 * @param row Panel containing row of plots
    	 */
    	private void addRightYAxis(final PlotPanel row) {
    		Axis yAxis = new Axis(this.rightYAxisMinValue,
	                this.rightYAxisMaxValue, this.sizer.height(),
	                Orientation.VERTICAL, Location.RIGHT_OF,
	                row.getDrawingCanvas());
	        Caption yCaption = new Caption(
	        		rightYAxisQuantitationType.getName(),
	                Orientation.HORIZONTAL, true, row.getDrawingCanvas());
	        row.add(yAxis, HorizontalAlignment.RIGHT_JUSTIFIED,
	                VerticalAlignment.BOTTOM_JUSTIFIED);
	        row.add(yCaption, HorizontalAlignment.RIGHT_OF,
	                VerticalAlignment.CENTERED);
    	}
    	
    	
    	/**
    	 * Add plot of a single genome interval to the given row panel.
    	 * @param row Row panel
    	 * @param gi Genome interval to graph
    	 * @param referenceYAxis Reference Y-axis, which will always
    	 * be the left Y-axis
    	 */
    	private void addGenomeIntervalPlot(final PlotPanel row,
    			final GenomeInterval gi, final Axis referenceYAxis) {
            PlotPanel col = row.newChildPlotPanel();
            BpUnits units = this.params.getUnits();
            long start = units.fromBp(gi.getStartLocation());
            long end = units.fromBp(gi.getEndLocation());
            Axis xAxis = new Axis(start,
                    end, this.sizer.width(gi),
                    Orientation.HORIZONTAL, Location.BELOW,
                    col.getDrawingCanvas());
            
            // Background
            Background bg = new Background(this.sizer.width(gi),
            		this.sizer.height(), BG_COLOR);
            col.add(bg, true);
            
            // Grid lines
            if (this.params.isDrawVertGridLines()) {
            	Grid vertGrid = xAxis.newGrid(this.sizer.width(gi),
            			this.sizer.height(), GRID_COLOR, col);
            	col.add(vertGrid, HorizontalAlignment.LEFT_JUSTIFIED,
            			VerticalAlignment.TOP_JUSTIFIED);
            }
            if (this.params.isDrawHorizGridLines()) {
            	Grid horizGrid = referenceYAxis.newGrid(this.sizer.width(gi),
            			this.sizer.height(), GRID_COLOR, col);
            	col.add(horizGrid,  HorizontalAlignment.LEFT_JUSTIFIED,
            			VerticalAlignment.TOP_JUSTIFIED);
            }
            
            // Add scatter plot
            ScatterPlot scatterPlot = this.newScatterPlot(gi);
            col.add(scatterPlot, HorizontalAlignment.LEFT_JUSTIFIED,
            		VerticalAlignment.TOP_JUSTIFIED);
            
            // X-axis stuff
            String captionText = "Chromosome " + gi.getChromosome()
                + " (" + this.params.getUnits().getName() + ")";
            Caption xCaption = new Caption(captionText,
                    Orientation.HORIZONTAL, false, row.getDrawingCanvas());
            col.add(xAxis, HorizontalAlignment.LEFT_JUSTIFIED,
            		VerticalAlignment.BOTTOM_JUSTIFIED);
            col.add(xCaption, HorizontalAlignment.CENTERED,
                    VerticalAlignment.BELOW);
            
            
            row.add(col, HorizontalAlignment.RIGHT_OF,
            		VerticalAlignment.BOTTOM_JUSTIFIED);
            
            this.plots.add(scatterPlot);
    	}
    	
    	/**
    	 * Are there more rows to plot?
    	 * @return T/F
    	 */
    	private boolean hasMore() {
    		return this.idx < this.genomeIntervals.size();
    	}
    	
    	/**
    	 * Get all individual scatter plots created since this
    	 * object was instantiated.
    	 * @return Plot created
    	 */
    	private Collection<ScatterPlot> getPlotsCreated() {
    		return this.plots;
    	}
    	
    	
    	/**
    	 * Instantiate new scatter plot of given genome interval.
    	 * @param gi Interval to plot
    	 * @return New scatter plot
    	 */
    	private ScatterPlot newScatterPlot(final GenomeInterval gi) {
    		PlotBoundaries expressionPlotBoundaries = null;
            PlotBoundaries copyNumberPlotBoundaries = null;
            if (Experiment.getExpressionQuantitationType(this.experiments)
            		!= null) {
            	expressionPlotBoundaries = new PlotBoundaries(
            			gi.getStartLocation(), this.params.getExpressionMinY(),
            			gi.getEndLocation(), this.params.getExpressionMaxY());
            }
            if (Experiment.getCopyNumberQuantitationType(experiments) != null) {
            	copyNumberPlotBoundaries = new PlotBoundaries(
            			gi.getStartLocation(), this.params.getCopyNumberMinY(),
            			gi.getEndLocation(), this.params.getCopyNumberMaxY());
            }
            ScatterPlot scatterPlot = new ScatterPlot(this.experiments,
            		gi.getChromosome(), this.chromosomeArrayDataGetter,
            		this.sizer.width(gi), this.sizer.height,
            		expressionPlotBoundaries, copyNumberPlotBoundaries);
            scatterPlot.setDrawErrorBars(this.params.isDrawErrorBars());
            scatterPlot.setInterpolationType(
            		this.params.getInterpolationType());
            scatterPlot.setDrawPoints(this.params.isDrawPoints());
            scatterPlot.setDrawRawLohProbabilities(
            		this.params.isDrawRawLohProbabilities());
            scatterPlot.setInterpolateLohEndpoints(
            		this.params.isInterpolateLohEndpoints());
            scatterPlot.setLohThreshold(this.params.getLohThreshold());
            scatterPlot.setShowAnnotation(this.params.isShowAnnotation());
            scatterPlot.setShowGenes(this.params.isShowGenes());
            scatterPlot.setShowReporterNames(
            		this.params.isShowReporterNames());
            return scatterPlot;
        }
    }
    
    /**
     * Class that is responsible for calculating
     * the width and height of scatter plot widgets.
     * When there is only a single
     * genome interval to plot, the dimensions of the
     * single scatter plot widget are equal to the width
     * and height passed in through the scatter plot parameters
     * web form.
     * For multiple genome intervals, the sum of widths and
     * heights of the individual scatter plot widgets
     * for the widest row of plots is
     * less than or equal to these parameters, respectively.
     */
    private static final class ScatterPlotSizer {
    	
    	/** Scale of native units to pixels. */
    	private final double scale;
    	
    	/** Height of all plots in pixels. */
    	private final int height;
    	
    	/**
    	 * Constructor.
    	 * @param params Plot parameters
    	 */
    	private ScatterPlotSizer(final ScatterPlotParameters params) {
    		List<GenomeInterval> intervals =
    			new ArrayList<GenomeInterval>(params.getGenomeIntervals());
    		int numRows = (int) Math.ceil((double) intervals.size()
    				/ (double) params.getNumPlotsPerRow());
    		this.height = params.getHeight() / numRows;
    		long longestInterval = 0;
    		int p = 0;
    		while (p < intervals.size()) {
    			int q = p + params.getNumPlotsPerRow();
    			if (q > intervals.size()) {
    				q = intervals.size();
    			}
    			long candidateLongest = 0;
    			for (int i = p; i < q; i++) {
    				candidateLongest += intervals.get(i).length();
    			}
    			if (candidateLongest > longestInterval) {
    				longestInterval = candidateLongest;
    			}
    			p = q;
    		}
    		this.scale = (double) params.getWidth() / (double) longestInterval;
    	}
    	
    	
    	/**
    	 * Get width for plot.
    	 * @param interval Genome interval
    	 * @return Width in pixels
    	 */
    	private int width(final GenomeInterval interval) {
    		return (int) (this.scale * (interval.getEndLocation()
    				- interval.getStartLocation()));
    	}
    	
    	/**
    	 * Get height for plot.
    	 * @return Height in pixels.
    	 */
    	private int height() {
    		return this.height;
    	}
    }
}

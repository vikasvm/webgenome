/*
$Revision: 1.4 $
$Date: 2007-07-05 13:23:29 $

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
    		final BaseGenomicPlotParameters params) {
        
        // Check args
    	if (!(params instanceof ScatterPlotParameters)) {
    		throw new IllegalArgumentException(
    				"Plot parameters must be of type ScatterPlotParameters");
    	}
        if (experiments == null || panel == null) {
            throw new IllegalArgumentException(
                    "Experiments and panel cannot be null");
        }
        ScatterPlotParameters plotParameters = (ScatterPlotParameters) params;
        if (plotParameters.getGenomeIntervals() == null
        		|| plotParameters.getGenomeIntervals().size() < 1) {
        	throw new IllegalArgumentException(
        			"No genome intervals specified");
        }
        if (!Float.isNaN(plotParameters.getMinY())
                && !Float.isNaN(plotParameters.getMaxY())
                && plotParameters.getMinY() > plotParameters.getMaxY()) {
            throw new IllegalArgumentException("Invalid plot range: "
                    + plotParameters.getMinY() + " - "
                    + plotParameters.getMaxY());
        }
        
        // Paint plot
        QuantitationType quantitationType =
        	Experiment.getQuantitationType(experiments);
        
        Axis yAxis = null;
        Collection<ScatterPlot> plots = new ArrayList<ScatterPlot>();
        ScatterPlotSizer sizer =
        	new ScatterPlotSizer(plotParameters);
		int plotCount = 0;
		int rowCount = 1;
		PlotPanel row = panel.newChildPlotPanel();
		BpUnits units = plotParameters.getUnits();
		for (GenomeInterval gi : plotParameters.getGenomeIntervals()) {
			if (plotCount++ >=  plotParameters.getNumPlotsPerRow()) {
				VerticalAlignment va = null;
				if (rowCount++ == 1) {
					va = VerticalAlignment.TOP_JUSTIFIED;
				} else {
					va = VerticalAlignment.BELOW;
				}
				panel.add(row, HorizontalAlignment.LEFT_JUSTIFIED, va);
				row = panel.newChildPlotPanel();
				plotCount = 1;
			}
			row.setName("row");
			long start = units.toBp(gi.getStartLocation());
			long end = units.toBp(gi.getEndLocation());
	        PlotBoundaries pb = new PlotBoundaries(
	                (double) start,
	                (double) plotParameters.getMinY(),
	                (double) end,
	                (double) plotParameters.getMaxY());
	        ScatterPlot scatterPlot =
	            new ScatterPlot(experiments, gi.getChromosome(),
	            		this.getChromosomeArrayDataGetter(), sizer.width(gi),
	            		sizer.height(), pb);
	        scatterPlot.setDrawErrorBars(plotParameters.isDrawErrorBars());
	        scatterPlot.setInterpolationType(
	        		plotParameters.getInterpolationType());
	        scatterPlot.setDrawPoints(plotParameters.isDrawPoints());
	        scatterPlot.setDrawRawLohProbabilities(
	        		plotParameters.isDrawRawLohProbabilities());
	        scatterPlot.setInterpolateLohEndpoints(
	        		plotParameters.isInterpolateLohEndpoints());
	        scatterPlot.setLohThreshold(plotParameters.getLohThreshold());
	        scatterPlot.setShowAnnotation(plotParameters.isShowAnnotation());
	        scatterPlot.setShowGenes(plotParameters.isShowGenes());
	        scatterPlot.setShowReporterNames(
	        		plotParameters.isShowReporterNames());
	        plots.add(scatterPlot);
	        
	        PlotPanel col = row.newChildPlotPanel();
	        
	        // Axes
	        if (plotCount == 1) {
		        yAxis = new Axis(plotParameters.getMinY(),
		                plotParameters.getMaxY(), scatterPlot.height(),
		                Orientation.VERTICAL, Location.LEFT_OF,
		                panel.getDrawingCanvas());
		        Caption yCaption = new Caption(
		        		quantitationType.getName(),
		                Orientation.HORIZONTAL, true, panel.getDrawingCanvas());
		        row.add(yAxis, HorizontalAlignment.LEFT_JUSTIFIED,
		                VerticalAlignment.BOTTOM_JUSTIFIED);
		        row.add(yCaption, HorizontalAlignment.LEFT_OF,
		                VerticalAlignment.CENTERED);
	        }
	        Axis xAxis = new Axis(gi.getStartLocation(),
	                gi.getEndLocation(), scatterPlot.width(),
	                Orientation.HORIZONTAL, Location.BELOW,
	                col.getDrawingCanvas());
	        
	        // Background
	        Background bg = new Background(scatterPlot.width(),
	        		scatterPlot.height(), BG_COLOR);
	        col.add(bg, true);
	        
	        // Grid lines
	        if (plotParameters.isDrawVertGridLines()) {
	        	Grid vertGrid = xAxis.newGrid(scatterPlot.width(),
	        			scatterPlot.height(), GRID_COLOR, col);
	        	col.add(vertGrid, HorizontalAlignment.LEFT_JUSTIFIED,
	        			VerticalAlignment.TOP_JUSTIFIED);
	        }
	        if (plotParameters.isDrawHorizGridLines()) {
	        	Grid horizGrid = yAxis.newGrid(scatterPlot.width(),
	        			scatterPlot.height(), GRID_COLOR, col);
	        	col.add(horizGrid,  HorizontalAlignment.LEFT_JUSTIFIED,
	        			VerticalAlignment.TOP_JUSTIFIED);
	        }
	        
	        // Add scatter plot
	        col.add(scatterPlot, HorizontalAlignment.LEFT_JUSTIFIED,
	        		VerticalAlignment.TOP_JUSTIFIED);
	        
	        // X-axis stuff
	        String captionText = "Chromosome " + gi.getChromosome()
	            + " (" + plotParameters.getUnits().getName() + ")";
	        Caption xCaption = new Caption(captionText,
	                Orientation.HORIZONTAL, false, panel.getDrawingCanvas());
	        col.add(xAxis, HorizontalAlignment.LEFT_JUSTIFIED,
	        		VerticalAlignment.BOTTOM_JUSTIFIED);
	        col.add(xCaption, HorizontalAlignment.CENTERED,
	                VerticalAlignment.BELOW);
	        
	        
	        row.add(col, HorizontalAlignment.RIGHT_OF,
	        		VerticalAlignment.BOTTOM_JUSTIFIED);
		}
		
		// Add final row
		VerticalAlignment va = null;
		if (rowCount == 1) {
			va = VerticalAlignment.TOP_JUSTIFIED;
		} else {
			va = VerticalAlignment.BELOW;
		}
		panel.add(row, HorizontalAlignment.LEFT_JUSTIFIED, va);
		
		// Legend
        Legend legend = new Legend(experiments, plotParameters.getWidth());
        panel.add(legend, HorizontalAlignment.CENTERED,
                VerticalAlignment.BELOW);
        
        // Gather up click boxes and mouseover stripes
        Set<ClickBoxes> boxes = new HashSet<ClickBoxes>();
        Set<MouseOverStripes> stripes =
        	new HashSet<MouseOverStripes>();
        for (ScatterPlot plot : plots) {
        	boxes.add(plot.getClickBoxes());
        	stripes.add(plot.getMouseOverStripes());
        }
        return new EventHandlerGraphicBoundaries(stripes, boxes);
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
    static final class ScatterPlotSizer {
    	
    	/** Scale of native units to pixels. */
    	private final double scale;
    	
    	/** Height of all plots in pixels. */
    	private final int height;
    	
    	/**
    	 * Constructor.
    	 * @param params Plot parameters
    	 */
    	public ScatterPlotSizer(final ScatterPlotParameters params) {
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
    	public int width(final GenomeInterval interval) {
    		return (int) (this.scale * (interval.getEndLocation()
    				- interval.getStartLocation()));
    	}
    	
    	/**
    	 * Get height for plot.
    	 * @return Height in pixels.
    	 */
    	public int height() {
    		return this.height;
    	}
    }
}

/*
$Revision: 1.6 $
$Date: 2006-09-23 05:02:23 $

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

package org.rti.webcgh.graphics.widget;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rti.webcgh.domain.ArrayDatum;
import org.rti.webcgh.domain.BioAssay;
import org.rti.webcgh.domain.ChromosomeArrayData;
import org.rti.webcgh.domain.Experiment;
import org.rti.webcgh.graphics.DrawingCanvas;
import org.rti.webcgh.graphics.primitive.Line;
import org.rti.webcgh.graphics.primitive.Rectangle;
import org.rti.webcgh.graphics.primitive.Text;
import org.rti.webcgh.graphics.util.HeatMapColorFactory;
import org.rti.webcgh.service.plot.IdeogramPlotParameters;
import org.rti.webcgh.service.util.ChromosomeArrayDataGetter;
import org.rti.webcgh.units.ChromosomeIdeogramSize;
import org.rti.webcgh.units.HorizontalAlignment;


/**
 * One dimensional plot of data.  The first
 * dimension corresponds to chromosme location.
 * Values of some <code>QuantitationType</code>
 * are given as colors.  Low values
 * map to hues of green.  High values map to
 * hues of red.  Intermediate values map to
 * hues of blue.
 * @see org.rti.webcgh.graphics.util.HapMapColorFactory
 * @author dhall
 *
 */
public final class HeatMapPlot implements PlotElement {
	
	// =========================
	//     Constants
	// =========================
	
	/** Padding between plot features. */
	private static final int PADDING = 5;
	
	/** Font size. */
	private static final int FONT_SIZE = 10;
	
	/** Color of text. */
	private static final Color TEXT_COLOR = Color.BLACK;
	
	/** Rotation of data track labels in radians. */
	private static final double LABEL_ROTATION = 1.5 * Math.PI;
	
	/** Width of bracket lines. */
	private static final int LINE_WIDTH = 1;
	
	/** Color of bracket lines. */
	private static final Color LINE_COLOR = Color.BLACK;

	// ============================
	//        Attributes
	// ============================
	
	/** Experiments containing data to plot. */
	private final Collection<Experiment> experiments;
	
	/** Chromosome number to plot. */
	private final short chromosome;
	
	/** Factory for generating colors. */
    private final HeatMapColorFactory colorFactory;
    
    /** Plot parameters supplied by user. */
    private final IdeogramPlotParameters plotParameters;
    
    /**
     * Gets chromosome array data transparently regardless
     * of data location (i.e., in memory or on disk).
     */
    private final ChromosomeArrayDataGetter chromosomeArrayDataGetter;
    
//    /** Number formatter for generating mouseover text. */
//    private static final NumberFormat FORMAT =
//    	new DecimalFormat("###.###");
    
    /** Maximum X-coordinate in plot. */
    private int maxX = 0;
    
    /** Maximum Y-coordinate in plot. */
    private int maxY = 0;
    
    /** Minimum X-coordinate in plot. */
    private int minX = 0;
    
    /** Minimum Y-coordinate in plot. */
    private int minY = 0;
    
    /** Y-coordinate of tops of tracks. */
    private int trackMinY = 0;
    
    /** Y-coordinate of bottom of tracks. */
    private int trackMaxY = 0;
  
    
    // ===============================
    //       Constructors
    // ===============================
    
    /**
     * Constructor.
     * @param experiments Experiments containg data to plot
     * @param chromosome Chromosome number to plot
     * @param colorFactory Maps data values to colors
     * @param plotParameters Plot parameters supplied through
     * user input
     * @param chromosomeArrayDataGetter Gets chromosome array data
     * from bioassays transparently without regard to where
     * data reside
     * @param drawingCanvas A drawing canvas
     */
	public HeatMapPlot(final Collection<Experiment> experiments,
			final short chromosome,
			final HeatMapColorFactory colorFactory,
			final IdeogramPlotParameters plotParameters,
			final ChromosomeArrayDataGetter chromosomeArrayDataGetter,
			final DrawingCanvas drawingCanvas) {
		super();
		
		// Make sure args okay
		if (experiments == null) {
			throw new IllegalArgumentException("Experiments cannot be null");
		}
		
		// Set attributes
		this.experiments = experiments;
		this.chromosome = chromosome;
		this.colorFactory = colorFactory;
		this.plotParameters = plotParameters;
		this.chromosomeArrayDataGetter = chromosomeArrayDataGetter;
		
		// Adjust reference coordinates
		int longestString = 0;
		for (Experiment exp : experiments) {
			for (BioAssay ba : exp.getBioAssays()) {
				int candidateLongest = drawingCanvas.renderedWidth(
						ba.getName(), FONT_SIZE);
				if (candidateLongest > longestString) {
					longestString = candidateLongest;
				}
			}
		}
		this.trackMinY = PADDING + longestString;
		this.trackMaxY = this.trackMinY
			+ plotParameters.getIdeogramSize().pixels(
				Experiment.inferredChromosomeSize(experiments,
						this.chromosome));
		this.maxX = 0;
		for (Iterator<Experiment> it = experiments.iterator();
			it.hasNext();) {
			Experiment exp = it.next();
			maxX += this.renderedWidth(exp, drawingCanvas, plotParameters);
			if (it.hasNext()) {
				maxX += PADDING;
			}
		}
		this.maxY = this.trackMaxY + PADDING + FONT_SIZE;
	}
	
	
	/**
	 * Get rendered width of experiment.  This will be
	 * the maximum of the experiment label and
	 * data tracks.
	 * @param exp Experiment that will be rendered
	 * @param canvas Canvas experiment will be rendered on
	 * @param plotParameters Plot parameters
	 * @return Rendered width in pixels
	 */
	private int renderedWidth(final Experiment exp,
			final DrawingCanvas canvas,
			final IdeogramPlotParameters plotParameters) {
		int width = 0;
		int totalPaddingAroundBrackets = PADDING * 2;
		
		// Calculate width of experiment label
		int textWidth = canvas.renderedWidth(exp.getName(), FONT_SIZE);
		
		// Calculate width of data tracks
		int tracksWidth = this.tracksWidth(exp, plotParameters);
		
		// Take max
		if (textWidth > tracksWidth) {
			width = textWidth;
			totalPaddingAroundBrackets = PADDING * 4;
		} else {
			width = tracksWidth;
		}
		
		// Add padding to each side for brackets around experiment name.
		width += totalPaddingAroundBrackets;
		
		return width;
	}
	
	
	/**
	 * Get the width of rendered data tracks associated with
	 * given experiment.
	 * @param exp An experiment
	 * @param plotParameters Plot parameters
	 * @return Width of rendered data tracks in pixels
	 */
	private int tracksWidth(final Experiment exp,
			final IdeogramPlotParameters plotParameters) {
		int tracksWidth = 0;
		int numBioAssays = exp.getBioAssays().size();
		if (numBioAssays > 0) {
			tracksWidth = numBioAssays * plotParameters.getTrackWidth()
				+ (numBioAssays - 1) * PADDING;
		}
		return tracksWidth;
	}
	
	
	// ================================
	//       Plot element interface
	// ================================
	
    /**
     * Paint element.
     * @param canvas A canvas
     */
    public void paint(final DrawingCanvas canvas) {
    	int x = this.minX;
    	for (Experiment exp : this.experiments) {
    		int endX = this.paint(canvas, exp, x);
    		x = endX + PADDING;
    	}
    }
    
    
    /**
     * Paint experiment.
     * @param canvas A drawing canvas to paint on
     * @param experiment Experiment to paint
     * @param x Starting x-coordinate
     * @return Ending x-coordinate
     */
    private int paint(final DrawingCanvas canvas,
    		final Experiment experiment, final int x) {
    	int startX = x;
    	int endX = x + this.renderedWidth(experiment, canvas,
    			this.plotParameters);
    	int midX = (startX + endX) / 2;
    	int trackX = midX
    		- this.tracksWidth(experiment, this.plotParameters) / 2;
    	int textY = this.trackMinY - PADDING;
    	
    	// Draw data tracks and bioassay labels
    	for (BioAssay ba : experiment.getBioAssays()) {
    		ChromosomeArrayData cad = this.chromosomeArrayDataGetter.
				getChromosomeArrayData(ba, this.chromosome);
			if (cad != null) {
	    		this.paintTrack(canvas, trackX, cad);
	    		int textX = trackX + this.plotParameters.getTrackWidth() / 2;
	    		Text text = canvas.newText(ba.getName(), textX, textY,
	    				FONT_SIZE, HorizontalAlignment.LEFT_JUSTIFIED,
	    				TEXT_COLOR);
	    		text.setRotation(LABEL_ROTATION);
	    		canvas.add(text);
	    		trackX += this.plotParameters.getTrackWidth() + PADDING;
			}
    	}
    	
    	// Draw group label for entire experiment
    	int textWidth =
    		canvas.renderedWidth(experiment.getName(), FONT_SIZE);
    	int textStartX = midX - textWidth / 2;
    	int textEndX = textStartX + textWidth;
    	textY = this.trackMaxY + PADDING + FONT_SIZE;
    	Text text = canvas.newText(experiment.getName(), textStartX,
    			textY, FONT_SIZE, HorizontalAlignment.LEFT_JUSTIFIED,
    			TEXT_COLOR);
    	canvas.add(text);
    	
    	// Add bracket around group label
    	int lineY1 = textY - FONT_SIZE / 2;
    	int lineY2 = lineY1;
    	canvas.add(new Line(startX, lineY1,
    			textStartX - PADDING, lineY2, LINE_WIDTH, LINE_COLOR));
    	canvas.add(new Line(textEndX + PADDING, lineY1,
    			endX, lineY2, LINE_WIDTH, LINE_COLOR));
    	lineY1 -= PADDING;
    	canvas.add(new Line(startX, lineY1, startX, lineY2,
    			LINE_WIDTH, LINE_COLOR));
    	canvas.add(new Line(endX, lineY1, endX, lineY2,
    			LINE_WIDTH, LINE_COLOR));
    	return endX;
    }
    
    
    /**
     * Paint a single data track.
     * @param canvas Canvas
     * @param x X-coordinate of track
     * @param chromosomeArrayData Chromosome array data
     */
    private void paintTrack(final DrawingCanvas canvas, final int x,
    		final ChromosomeArrayData chromosomeArrayData) {
    	List<ArrayDatum> dataList =
    		chromosomeArrayData.getArrayData();
    	int n = dataList.size();
    	ChromosomeIdeogramSize idSize =
			this.plotParameters.getIdeogramSize();
    	int trackWidth = this.plotParameters.getTrackWidth();
    	if (n > 0) {
	    	for (int i = 0; i < n; i++) {
	    		ArrayDatum datum = dataList.get(i);
	    		float value = datum.getValue();
	    		if (value <= this.plotParameters.getMinMask()
	    				|| value >= this.plotParameters.getMaxMask()) {
		    		long start = 0;
		    		long middle = datum.getReporter().getLocation();
		    		if (i > 0) {
		    			start = (dataList.get(i - 1).getReporter().
		    				getLocation() + middle) / 2;
		    		} else {
		    			start = middle;
		    		}
		    		long end = 0;
		    		if (i < n - 1) {
		    			end = (middle
		    				+ dataList.get(i + 1).getReporter().getLocation())
		    				/ 2;
		    		} else {
		    			end = middle;
		    		}
		    		int y = this.trackMinY + idSize.pixels(start);
		    		int height = idSize.pixels(end - start);
		    		Color c = this.colorFactory.getColor(value);
		    		canvas.add(new Rectangle(x, y, trackWidth, height, c));
	    		}
	    	}
    	}
    }
    
    /**
     * Point at top left used to align with other plot elements.
     * @return A point
     */
    public Point topLeftAlignmentPoint() {
    	return new Point(this.minX, this.trackMinY);
    }
    
    
    /**
     * Point at bottom left used to align with other plot elements.
     * @return A point
     */
    public Point bottomLeftAlignmentPoint() {
    	return new Point(this.minX, this.trackMaxY);
    }
    
    
    /**
     * Point at top right used to align with other plot elements.
     * @return A point
     */
    public Point topRightAlignmentPoint() {
    	return new Point(this.maxX, this.trackMinY);
    }
    
    
    /**
     * Point at bottom right used to align with other plot elements.
     * @return A point
     */
    public Point bottomRightAlignmentPoint() {
    	return new Point(this.maxX, this.trackMaxY);
    }
    
    
    /**
     * Width in pixels.
     * @return Width in pixels
     */
    public int width() {
    	return this.maxX - this.minX;
    }
    
    
    /**
     * Height in pixels.
     * @return Height in pixels
     */
    public int height() {
    	return this.maxY - this.minY;
    }
    
    
    /**
     * Return point at top left of element.
     * @return A point
     */
    public Point topLeftPoint() {
    	return new Point(this.minX, this.minY);
    }
    
    
    /**
     * Move element.
     * @param deltaX Number of pixels horizontally
     * @param deltaY Number of pixels vertically
     */
    public void move(final int deltaX, final int deltaY) {
    	this.minX += deltaX;
    	this.maxX += deltaX;
    	this.minY += deltaY;
    	this.maxY += deltaY;
    	this.trackMinY += deltaY;
    	this.trackMaxY += deltaY;
    }
}
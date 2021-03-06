/*L
 *  Copyright RTI International
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/webgenome/LICENSE.txt for details.
 */

/*
$Revision: 1.7 $
$Date: 2008-01-08 21:07:41 $


*/

package org.rti.webgenome.graphics.widget;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

//import org.apache.log4j.Logger;
import org.rti.webgenome.domain.AnnotatedGenomeFeature;
import org.rti.webgenome.domain.AnnotationType;
import org.rti.webgenome.domain.ArrayDatum;
import org.rti.webgenome.domain.BioAssay;
import org.rti.webgenome.domain.ChromosomeArrayData;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.QuantitationType;
import org.rti.webgenome.domain.Reporter;
import org.rti.webgenome.graphics.DataPoint;
import org.rti.webgenome.graphics.DrawingCanvas;
import org.rti.webgenome.graphics.InterpolationType;
import org.rti.webgenome.graphics.PlotBoundaries;
import org.rti.webgenome.graphics.event.MouseOverStripe;
import org.rti.webgenome.graphics.event.MouseOverStripes;
import org.rti.webgenome.graphics.io.ClickBoxes;
import org.rti.webgenome.graphics.primitive.Circle;
import org.rti.webgenome.graphics.primitive.Diamond;
import org.rti.webgenome.graphics.primitive.Line;
import org.rti.webgenome.graphics.util.PointListCompressor;
import org.rti.webgenome.service.util.ChromosomeArrayDataGetter;
import org.rti.webgenome.units.Orientation;

import flanagan.interpolation.CubicSpline;

/**
 * A two dimensional plotting space that renders
 * array data as points connected by lines.  The plot may
 * graph one type (i.e. quantitation type) of copy number (or LOH) data and one
 * type of expression data.  If both are plotted at the same time,
 * this is called 'co-visualization.'
 * @author dhall
 *
 */
public final class ScatterPlot implements PlotElement {
	
	//private static final Logger LOGGER = Logger.getLogger(ScatterPlot.class);
    
    // =============================
    //     Constants
    // =============================
    
    /** Default radius of data point in pixels. */
    private static final int DEF_POINT_RADIUS = 3;
    
    /** Radius of selected data points in pixels. */
    private static final int SELECTED_POINT_RADIUS = 5;
    
    /** Default width of regression line in pixels. */
    private static final int DEF_LINE_WIDTH = 1;
    
    /** Width of selected lines in pixels. */
    private static final int SELECTED_LINE_WIDTH = 3;
    
    /** Chromosomal alteration line width. */
    private static final int ALTERATION_LINE_WIDTH = 5;
    
    /** Default length of error bar hatch lines in pixels. */
    private static final int DEF_ERROR_BAR_HATCH_LENGTH = 6;
    
    /** Minimum width of a region of LOH in pixels. */
    private static final int MIN_LOH_WIDTH = 10;
    
    /** Thickness of "stem" drawn from X-axis to expression data point. */
    private static final int STEM_THICKNESS = 2;
    
    /** Opacity (i.e. alpha) of expression data colors. */
    private static final int EXPRESSION_OPACITY = 125;
    
    
    // =============================
    //       Attributes
    // =============================
    
    /** Experiments to plot. */
    private final Collection<Experiment> experiments;
    
    /** Chromosome number. */
    private final short chromosome;
    
    /**
     * Gets chromosome array data making the location of those
     * data transparent.  Data may be in memory or on disk.
     */
    private final ChromosomeArrayDataGetter chromosomeArrayDataGetter;
    
    
    /** Width of plot in pixels. */
    private final int width;
    
    /** Height of plot in pixels. */
    private final int height;
    
    /** X-coordinate of plot origin (i.e, upper left-most point). */
    private int x = 0;
    
    /** Y-coordinate of plot origin (i.e., upper left-most point). */
    private int y = 0;
    
    /** Plot boundaries for expression data. */
    private final PlotBoundaries expressionPlotBoundaries;
    
    /** Plot boundaries for copy number data. */
    private final PlotBoundaries copyNumberPlotBoundaries;
    
    /**
     * Data point object that is reused during plot creation
     * in order to economize memory.
     */
    private final DataPoint reusableDataPoint1 = new DataPoint();
    
    /**
     * Data point object that is reused during plot creation
     * in order to economize memory.
     */
    private final DataPoint reusableDataPoint2 = new DataPoint();
    
    /**
     * Data point representing the base of the "stem"
     * of an expression data point diamond and stem.
     */
    private final DataPoint stemBaseReusableDataPoint = new DataPoint();
    
    /** Click boxes used for providing interactivity using Javascript. */
    private final ClickBoxes clickBoxes;
    
    /**
     * Mouseover stripes used for providing interactivity
     * using Javascript.
     */
    private MouseOverStripes mouseOverStripes;
    
    /** Draw data points. */
    private boolean drawPoints = true;
    
    /** Draw error bars. */
    private boolean drawErrorBars = false;
    
    /**
     * Threshold probability above which the corresponding
	 * value is considered to be indicative of LOH.
     */
    private float lohThreshold = (float) 0.5;
    
    /**
     * Interpolate the endpoints of LOH regions.  If false,
     * the endpoints will be set to the outermost
     * reporter positions in an LOH region.  If true,
     * the endpoints will be extended distally midway to the
     * next reporters.
     */
    private boolean interpolateLohEndpoints = false;
    
    /** Draw raw LOH probabilities along with scored data? */
    private boolean drawRawLohProbabilities = true;
    
    /** Quantitation type for copy number data. */
    private QuantitationType copyNumberQType = null;
    
    /** Quantitation type for expression data. */
    private QuantitationType expressionQType = null;
    
    /** Interpolation type. */
    private InterpolationType interpolationType = InterpolationType.NONE;
    
    /** Show annotation in mouseover text? */
    private boolean showAnnotation = true;
    
    /** Show genes in mouseover text? */
    private boolean showGenes = true;
    
    /** Show reporter names in mouseover text? */
    private boolean showReporterNames = true;
    
    /**
     * Show stem attached to diamond-shaped points for
     * expression data.
     */
    private boolean showStem = true;
    
    // =============================
    //     Getters/setters
    // =============================
    
	/**
	 * Draw raw LOH probabilities?
	 * @return T/F
	 */
	public boolean isDrawRawLohProbabilities() {
		return drawRawLohProbabilities;
	}


	/**
	 * Set whether to draw raw LOH probabilities.
	 * @param drawRawLohProbabilities Draw raw LOH probabilities?
	 */
	public void setDrawRawLohProbabilities(
			final boolean drawRawLohProbabilities) {
		this.drawRawLohProbabilities = drawRawLohProbabilities;
	}


	/**
	 * Show reporter names in mouseover text?
	 * @return T/F
	 */
	public boolean isShowReporterNames() {
		return showReporterNames;
	}


	/**
	 * Sets whether or not to show reporter names in mouseover
	 * text.
	 * @param showReporterNames Show reporter names in mouseover
	 * text?
	 */
	public void setShowReporterNames(final boolean showReporterNames) {
		this.showReporterNames = showReporterNames;
	}


	/**
	 * Will annotation be shown in mouseover text?
	 * @return T/F
	 */
	public boolean isShowAnnotation() {
		return showAnnotation;
	}
	
	

	/**
	 * Will stem be shown attached to diamond-shaped points for
	 * expression data?
	 * @return T/F
	 */
	public boolean isShowStem() {
		return showStem;
	}


	/**
	 * Set if stem will be shown attached to diamond-shaped points for
	 * expression data.
	 * @param showStem Whether stem will be shown
	 */
	public void setShowStem(final boolean showStem) {
		this.showStem = showStem;
	}


	/**
	 * Determines whether annotation will be shown in mouseover
	 * text.
	 * @param showAnnotation Show annotation in mouseover text?
	 */
	public void setShowAnnotation(final boolean showAnnotation) {
		this.showAnnotation = showAnnotation;
	}


	/**
	 * Show genes names in mouseover text?
	 * @return T/F
	 */
	public boolean isShowGenes() {
		return showGenes;
	}


	/**
	 * Set whether to show gene names in mouseover text.
	 * @param showGenes Show gene names in mouseover text?
	 */
	public void setShowGenes(final boolean showGenes) {
		this.showGenes = showGenes;
	}


	/**
	 * Get interpolation type.
	 * @return Interpolation type
	 */
	public InterpolationType getInterpolationType() {
		return interpolationType;
	}


	/**
	 * Set interpolation type.
	 * @param interpolationType Interpolation type
	 */
	public void setInterpolationType(
			final InterpolationType interpolationType) {
		this.interpolationType = interpolationType;
	}


	/**
	 * Interpolate the endpoints of LOH regions?  If false,
     * the endpoints will be set to the outermost
     * reporter positions in an LOH region.  If true,
     * the endpoints will be extended distally midway to the
     * next reporters.
	 * @return T/F
	 */
	public boolean isInterpolateLohEndpoints() {
		return interpolateLohEndpoints;
	}


	/**
	 * Set whether to interpolate the endpoints of LOH regions.  If false,
     * the endpoints will be set to the outermost
     * reporter positions in an LOH region.  If true,
     * the endpoints will be extended distally midway to the
     * next reporters.
	 * @param interpolateLohEndpoints Interpolate LOH endpoints?
	 */
	public void setInterpolateLohEndpoints(
			final boolean interpolateLohEndpoints) {
		this.interpolateLohEndpoints = interpolateLohEndpoints;
	}


	/**
	 * Get threshold probability above which the corresponding
	 * value is considered to be indicative of LOH.
	 * @return LOH threshold probability.
	 */
	public float getLohThreshold() {
		return lohThreshold;
	}


	/**
	 * Set threshold probability above which the corresponding
	 * value is considered to be indicative of LOH.
	 * @param lohThreshold LOH threshold probability.
	 */
	public void setLohThreshold(final float lohThreshold) {
		this.lohThreshold = lohThreshold;
	}
    
    /**
     * Get mouseover stripes.
     * @return Mouseover stripes.
     */
    public MouseOverStripes getMouseOverStripes() {
		return mouseOverStripes;
	}

	/**
     * Get click boxes.
     * @return Click boxes
     */
    public ClickBoxes getClickBoxes() {
    	return this.clickBoxes;
    }
    
    /**
     * Will error bars be drawn?
     * @return T/F
     */
    public boolean isDrawErrorBars() {
		return drawErrorBars;
	}

    /**
     * Set whether error bars will be drawn.
     * @param drawErrorBars Will error bars be drawn?
     */
	public void setDrawErrorBars(final boolean drawErrorBars) {
		this.drawErrorBars = drawErrorBars;
	}


	/**
	 * Will data points be drawn?
	 * @return T/F
	 */
	public boolean isDrawPoints() {
		return drawPoints;
	}

	/**
	 * Set whether data points will be drawn.
	 * @param drawPoints Will data points be drawn?
	 */
	public void setDrawPoints(final boolean drawPoints) {
		this.drawPoints = drawPoints;
	}
    
    
    // ==============================
    //       Constructors
    // ==============================
    

	/**
     * Constructor.
     * @param experiments Experiments to plot
     * @param chromosome Chromosome number
     * @param chromosomeArrayDataGetter Getter for
     * chromosome array data
     * @param width Width of plot in pixels
     * @param height Height of plot in pixels
     * @param expressionPlotBoundaries Plot boundaries in native data units
     * (i.e., base pairs vs. some quantitation type) for expression data
     * @param copyNumberPlotBoundaries Plot boundaries in native data units
     * (i.e., base pairs vs. some quantitation type) for copy number
     * of LOH data.
     */
    public ScatterPlot(final Collection<Experiment> experiments,
    		final short chromosome,
    		final ChromosomeArrayDataGetter chromosomeArrayDataGetter,
            final int width, final int height,
            final PlotBoundaries expressionPlotBoundaries,
            final PlotBoundaries copyNumberPlotBoundaries) {
    	
    	// Make sure args okay
    	if (experiments == null) {
    		throw new IllegalArgumentException("Experiments cannot be null");
    	}
    	if (chromosomeArrayDataGetter == null) {
    		throw new IllegalArgumentException(
    				"Chromosome array data getter cannot be null");
    	}
    	this.expressionQType = null;
    	this.copyNumberQType = null;
    	for (Experiment exp : experiments) {
    		QuantitationType qt = exp.getQuantitationType();
    		if (qt.isExpressionData()) {
    			if (this.expressionQType == null) {
    				this.expressionQType = qt;
    			} else if (this.expressionQType != qt) {
    				throw new IllegalArgumentException(
						"Cannot mix expression quantitation types in plot");
    			}
    		} else {
    			if (this.copyNumberQType == null) {
    				this.copyNumberQType = qt;
    			} else if (this.copyNumberQType != qt) {
    				throw new IllegalArgumentException(
						"Cannot mix copy number quantitation types in plot");
    			}
    		}
    	}
        
        // Initialize attributes
        this.experiments = experiments;
        this.chromosome = chromosome;
        this.chromosomeArrayDataGetter = chromosomeArrayDataGetter;
        this.width = width;
        this.height = height;
        this.expressionPlotBoundaries = expressionPlotBoundaries;
        this.copyNumberPlotBoundaries = copyNumberPlotBoundaries;
        this.clickBoxes = new ClickBoxes(width, height, DEF_POINT_RADIUS * 3,
        		DEF_POINT_RADIUS * 3);
        this.mouseOverStripes = new MouseOverStripes(
        		Orientation.HORIZONTAL, width, height);
    }


    // ===================================
    //      PlotElement interface
    // ===================================
    
    /**
     * Return point at top left of element.
     * @return A point
     */
    public Point topLeftPoint() {
        return new Point(this.x, this.y);
    }
    
    /**
     * Paint element.
     * @param canvas A canvas
     */
    public void paint(final DrawingCanvas canvas) {
    	SortedSet<Reporter> reporters = new TreeSet<Reporter>();
    	
        // Paint points and lines
    	BioAssay selected = null;
    	QuantitationType selectedQT = null;
        for (Experiment exp : this.experiments) {
            for (BioAssay bioAssay : exp.getBioAssays()) {
            	if (bioAssay.isSelected()) {
            		selected = bioAssay;
            		selectedQT = exp.getQuantitationType();
            	} else {
            		this.paint(canvas, bioAssay, reporters,
            				exp.getQuantitationType());
            	}
            }
        }
        
        // Selected bioassay painted last so that its line is on top
        if (selected != null) {
        	this.paint(canvas, selected, reporters, selectedQT);
        }
        
        // Initialize mouseover stripes
        this.initializeMouseOverStripes(reporters);
    }
    
    /**
     * Paint given bioassay.
     * @param canvas Canvas
     * @param bioAssay Bioassay to paint
     * @param reporters All reporters in all experiments that
     * are being painted
     * @param qType Quantitation type
     */
    private void paint(final DrawingCanvas canvas, final BioAssay bioAssay,
    		final SortedSet<Reporter> reporters,
    		final QuantitationType qType) {
    	
    	int pointRadius = DEF_POINT_RADIUS;
    	int lineWidth = DEF_LINE_WIDTH;
    	if (bioAssay.isSelected()) {
    		pointRadius = SELECTED_POINT_RADIUS;
    		lineWidth = SELECTED_LINE_WIDTH;
    	}
    	ChromosomeArrayData cad = this.chromosomeArrayDataGetter.
    		getChromosomeArrayData(bioAssay, this.chromosome);
    	if (cad != null) {
    		if (cad.getChromosomeAlterations() == null) {
    			if (qType.isExpressionData()) {
    				Color origColor = bioAssay.getColor();
    				Color newColor = new Color(origColor.getRed(),
    						origColor.getGreen(), origColor.getBlue(),
    						EXPRESSION_OPACITY);
    				this.paintExpressionData(canvas, cad, newColor,
    						lineWidth, pointRadius, bioAssay.getId(),
    						reporters);
    			} else {
    				this.painCopyNumberData(canvas, cad, bioAssay.getColor(),
    						lineWidth, pointRadius, bioAssay.getId(),
    						reporters);
    			}
    		} else {
    			if (cad.getChromosomeAlterations().size() > 0) {
    				List<AnnotatedGenomeFeature> feats =
    					cad.getChromosomeAlterations();
    				AnnotationType type = feats.get(0).getAnnotationType();
	    			this.paintAlterations(cad, bioAssay.getColor(), canvas,
	    					ALTERATION_LINE_WIDTH, type,
	    					this.copyNumberPlotBoundaries);
    			}
    		}
        }
    }
    
    /**
     * Paint gene expression data.
     * @param canvas Drawing canvas to render data
     * @param cad Data to paint
     * @param color Color of points
     * @param lineWidth Width of error bar lines
     * @param pointRadius Radius of data points
     * @param bioAssayId ID of bioassay data being plotted
     * @param reporters Reporters in bioassay being plotted
     */
    private void paintExpressionData(final DrawingCanvas canvas,
    		final ChromosomeArrayData cad, final Color color,
			final int lineWidth, final int pointRadius,
			final Long bioAssayId, final SortedSet<Reporter> reporters) {
    	List<ArrayDatum> data = this.getArrayData(cad, true);
          
    	// Points
        this.paintPoints(data, color, canvas, pointRadius,
        		bioAssayId, reporters, true);
        
        // Error bars
		if (this.drawErrorBars) {
			this.paintErrorBars(data, color, canvas,
					lineWidth, this.expressionPlotBoundaries, true);
		}
    }
    
    /**
     * Paint copy number data on given canvas.
     * @param canvas Drawing canvas to render data
     * @param cad Data to paint
     * @param color Color of points
     * @param lineWidth Width of error bar lines
     * @param pointRadius Radius of data points
     * @param bioAssayId ID of bioassay data being plotted
     * @param reporters Reporters in bioassay being plotted
     */
    private void painCopyNumberData(final DrawingCanvas canvas,
    		final ChromosomeArrayData cad, final Color color,
			final int lineWidth, final int pointRadius,
			final Long bioAssayId, final SortedSet<Reporter> reporters) {
    	if (this.copyNumberQType != QuantitationType.LOH
				|| (this.copyNumberQType == QuantitationType.LOH
						&& this.drawRawLohProbabilities)) {
    		List<ArrayDatum> data = this.getArrayData(cad, false);
            
            // Points
    		if (this.drawPoints) {
	            this.paintPoints(data, color, canvas, pointRadius,
	            		bioAssayId, reporters, false);
    		}
            
            // Error bars
    		if (this.drawErrorBars) {
    			this.paintErrorBars(data, color, canvas,
    					lineWidth, this.copyNumberPlotBoundaries, false);
    		}
        
            // Lines
    		if (this.interpolationType
    				== InterpolationType.STRAIGHT_LINE) {
	            this.paintStraightConnectingLines(
	            		data, color, canvas,
	            		lineWidth);
    		} else if (this.interpolationType
    				== InterpolationType.SPLINE) {
    			this.paintConnectingSpline(
    					data, color, canvas,
    					lineWidth);
    		} else if (this.interpolationType
    				== InterpolationType.STEP) {
    			this.paintConnectingSteps(data, color,
    					canvas, lineWidth);
    		}
		}
		
		// LOH scored lines
		if (this.copyNumberQType == QuantitationType.LOH) {
			this.paintAlterations(cad, color, canvas,
					ALTERATION_LINE_WIDTH, AnnotationType.LOH_SEGMENT,
					this.copyNumberPlotBoundaries);
		}
    }
    
    
    /**
     * Initialize mouseover stripes.
     * @param reporters Reporters
     */
    private void initializeMouseOverStripes(
    		final SortedSet<Reporter> reporters) {
    	PlotBoundaries boundaries = null;
    	if (this.copyNumberPlotBoundaries != null) {
    		boundaries = this.copyNumberPlotBoundaries;
    	} else if (this.expressionPlotBoundaries != null) {
    		boundaries = this.expressionPlotBoundaries;
    	}
    	if (!this.showAnnotation && !this.showGenes
    			&& !this.showReporterNames) {
    		this.mouseOverStripes = null;
    	} else {
	    	this.mouseOverStripes.getOrigin().x = this.x;
	    	this.mouseOverStripes.getOrigin().y = this.y;
	    	if (reporters.size() > 0) {
		    	MouseOverStripe lastStripe = null;
		    	Reporter lastReporter = null;
		    	for (Reporter currentReporter : reporters) {
		    		long currentStartBp = 0;
		    		if (lastReporter != null) {
		    			currentStartBp = (currentReporter.getLocation()
		    					+ lastReporter.getLocation()) / 2;
		    		}
		    		int currentStartPix = (int)
		    			(boundaries.
		    					fractionalDistanceFromLeft(
		    					currentStartBp) * (double) this.width);
		    		MouseOverStripe currentStripe = new MouseOverStripe();
		    		this.mouseOverStripes.add(currentStripe);
		    		currentStripe.setText(this.mouseOverText(currentReporter));
		    		currentStripe.setStart(currentStartPix);
		    		if (lastStripe != null) {
		    			lastStripe.setEnd(currentStartPix - 1);
		    		}
		    		lastStripe = currentStripe;
		    		lastReporter = currentReporter;
		    	}
		    	lastStripe.setEnd(this.width);
	    	}
    	}
    }
    
    
    /**
     * Generate mouseover text.
     * @param r A reporter
     * @return Mouseover text
     */
    private String mouseOverText(final Reporter r) {
    	StringBuffer buff = new StringBuffer();
    	
    	// Reporter name
    	if (this.showReporterNames) {
    		buff.append("Reporter: " + r.getName());
    	}
    	
    	// Annotations
    	if (this.showAnnotation) {
    		int count = 0;
    		StringBuffer annotation = new StringBuffer();
	    	for (String s : r.getAnnotations()) {
	    		if (s != null && s.length() > 0) {
		    		if (count++ > 0) {
		    			annotation.append(".  ");
		    		}
		    		annotation.append(s);
	    		}
	    	}
	    	if (annotation.length() > 0) {
	    		if (buff.length() > 0) {
	    			buff.append("; ");
	    		}
	    		buff.append("Annotations: " + annotation.toString());
	    	}
    	}
    	
    	// Genes
    	if (this.showGenes) {
	    	int count = 0;
	    	StringBuffer genes = new StringBuffer();
	    	for (String s : r.getAssociatedGenes()) {
	    		if (s != null && s.length() > 0) {
		    		if (count++ > 0) {
		    			genes.append(", ");
		    		}
		    		genes.append(s);
	    		}
	    	}
	    	if (genes.length() > 0) {
	    		if (buff.length() > 0) {
	    			buff.append("; ");
	    		}
	    		buff.append("Genes: " + genes.toString());
	    	}
    	}
    	
    	return buff.toString();
    }
    
    /**
     * Paint all points for given chromosome array data.
     * @param data Data to plot
     * @param color Color of points
     * @param drawingCanvas A drawing canvas
     * @param pointRadius Radius of data point in pixels
     * @param bioAssayId ID of bioassay datum comes from
     * @param reporters Sorted set of reporters
     * @param isExpressionData Do data come from an expression array?
     */
    private void paintPoints(final List<ArrayDatum> data, final Color color,
            final DrawingCanvas drawingCanvas, final int pointRadius,
            final Long bioAssayId,
            final SortedSet<Reporter> reporters,
            final boolean isExpressionData) {
    	if (data != null) {
	        for (ArrayDatum datum : data) {
	            this.paintPoint(datum, color, drawingCanvas, pointRadius,
	            		bioAssayId, isExpressionData);
	            reporters.add(datum.getReporter());
	        }
    	}
    }
    
    
    /**
     * Retrieves array datum objects to plot.  This method only returns the
     * objects that will be in the plot.
     * @param cad Chromosome array data
     * @param isExpressionData Are we plotting expression data?
     * @return Array datum objects in plot
     */
    private List<ArrayDatum> getArrayData(final ChromosomeArrayData cad,
    		final boolean isExpressionData) {
    	PlotBoundaries boundaries = null;
    	if (isExpressionData) {
    		boundaries = this.expressionPlotBoundaries;
    	} else {
    		boundaries = this.copyNumberPlotBoundaries;
    	}
    	return cad.getArrayData((long) boundaries.getMinValue1(),
    			(long) boundaries.getMaxValue1());
    }
    
    
    /**
     * Paint all error bars for given chromosome array data.
     * @param data Data to plot
     * @param color Color of error bars
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of lines
     * @param boundaries Boundaries of plot with regard to a quantitation
     * type
     * @param isExpressionData Are we plotting gene expression data?
     */
    private void paintErrorBars(final List<ArrayDatum> data,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth, final PlotBoundaries boundaries,
            final boolean isExpressionData) {
        for (ArrayDatum datum : data) {
            this.paintErrorBar(datum, color, drawingCanvas, lineWidth,
            		boundaries, isExpressionData);
        }
    }
    
    
    
    /**
     * Paint a single data point.
     * @param datum An array datum
     * @param color A color
     * @param drawingCanvas A drawing canvas
     * @param pointRadius Radius of data point
     * @param bioAssayId ID of bioassay datum comes from
     * @param isExpressionData Is the datum from gene expression
     * data?
     */
    private void paintPoint(final ArrayDatum datum,
            final Color color, final DrawingCanvas drawingCanvas,
            final int pointRadius, final Long bioAssayId,
            final boolean isExpressionData) {
        this.reusableDataPoint1.bulkSet(datum);
	    if (this.inPlotBoundaries(this.reusableDataPoint1, isExpressionData)) {
	    	PlotBoundaries boundaries = null;
	    	if (isExpressionData) {
	    		boundaries = this.expressionPlotBoundaries;
	    	} else {
	    		boundaries = this.copyNumberPlotBoundaries;
	    	}
	        int x = this.transposeX(this.reusableDataPoint1, boundaries);
	        int y = this.transposeY(this.reusableDataPoint1, boundaries);
	        
	        // Create point
	        if (!isExpressionData) {
		        this.drawPoint(x, y, color, datum.getReporter().getName(),
		                drawingCanvas, pointRadius,
		                datum.getReporter().isSelected());
	        } else {
	        	this.drawDiamond(x, y, color,
	        			datum.getReporter().getName(), drawingCanvas,
	        			pointRadius, datum.getReporter().isSelected());
	        	if (this.showStem) {
		        	this.drawStem(this.reusableDataPoint1, color,
		        			drawingCanvas, pointRadius);
	        	}
	        }
	        
	        // Add click box command
	        x -= this.x;
	        y -= this.y;
	        String command = this.clickBoxes.getClickBoxTextOfPixel(x, y);
	        if (command == null) {
	        	this.clickBoxes.addClickBoxText(bioAssayId.toString(), x, y);
	        }
        }
    }
    
    /**
     * Is given point within the bounaries of the plot?
     * @param point Data point
     * @param isExpressionData Is the data point expression data?
     * @return T/F
     */
    private boolean inPlotBoundaries(
    		final DataPoint point, final boolean isExpressionData) {
    	boolean in = false;
    	if (isExpressionData) {
    		in = this.expressionPlotBoundaries.inBoundary(
    				point.getValue1(), point.getValue2());
    	} else {
    		in = this.copyNumberPlotBoundaries.inBoundary(
    				point.getValue1(), point.getValue2());
    	}
    	return in;
    }
    
    
    /**
     * Paint error bars for a single data point.
     * @param datum An array datum
     * @param color A color
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of line
     * @param boundaries Boundaries of plot with regard to
     * a quantitation type
     * @param isExpressionData Are we plotting gene expression data?
     */
    private void paintErrorBar(final ArrayDatum datum,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth, final PlotBoundaries boundaries,
            final boolean isExpressionData) {
        this.reusableDataPoint1.bulkSet(datum);
        if (this.inPlotBoundaries(this.reusableDataPoint1, isExpressionData)) {
	        int x = this.transposeX(this.reusableDataPoint1, boundaries);
	        int y = this.transposeY(this.reusableDataPoint1, boundaries);
	        if (!Float.isNaN(datum.getError())) {
	            this.drawErrorBar(x, y, datum.getError(), color, drawingCanvas,
	            		lineWidth, boundaries);
	        }
        }
    }
    
    
    /**
     * Paint straight lines connecting data points.
     * @param data Data to plot
     * @param color A color
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of line in pixels
     */
    private void paintStraightConnectingLines(final List<ArrayDatum> data,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth) {
        for (int i = 1; i < data.size(); i++) {
            ArrayDatum d1 = data.get(i - 1);
            ArrayDatum d2 = data.get(i);
            this.reusableDataPoint1.bulkSet(d1);
            this.reusableDataPoint2.bulkSet(d2);
            this.paintLine(this.reusableDataPoint1,
            		this.reusableDataPoint2, drawingCanvas,
            		lineWidth, color);
        }
    }
    
    
    /**
     * Draw a line between two data points.  This method will only
     * be used if data are not expression.
     * @param p1 First data point
     * @param p2 Second data point
     * @param drawingCanvas Drawing canvas
     * @param lineWidth Width of line in pixels
     * @param color Color of line
     */
    private void paintLine(final DataPoint p1, final DataPoint p2,
    		final DrawingCanvas drawingCanvas, final int lineWidth,
    		final Color color) {
    	if (this.copyNumberPlotBoundaries.atLeastPartlyOnPlot(p1, p2)) {
            if (!this.copyNumberPlotBoundaries.withinBoundaries(p1)
                    || !this.copyNumberPlotBoundaries.withinBoundaries(p2)) {
                this.copyNumberPlotBoundaries.truncateToFitOnPlot(p1, p2);
            }
            int x1 = this.transposeX(p1, this.copyNumberPlotBoundaries);
            int y1 = this.transposeY(p1, this.copyNumberPlotBoundaries);
            int x2 = this.transposeX(p2, this.copyNumberPlotBoundaries);
            int y2 = this.transposeY(p2, this.copyNumberPlotBoundaries);
            Line line = new Line(x1, y1, x2, y2, lineWidth, color);
            drawingCanvas.add(line);
        }
    }
    
    
    /**
     * Generate cubic spline.
     * @param data Data that will be plotted
     * @return Cubic spline
     */
    private CubicSpline newCubicSpline(final List<ArrayDatum> data) {
    	int n = data.size();
    	List<Double> xxList = new ArrayList<Double>();
    	List<Double> yyList = new ArrayList<Double>();
    	for (int i = 0; i < n; i++) {
    		ArrayDatum datum = data.get(i);
    		xxList.add((double) this.transposeX(
    				datum.getReporter().getLocation(),
    				this.copyNumberPlotBoundaries));
    		yyList.add((double) this.transposeY(datum.getValue(),
    				this.copyNumberPlotBoundaries));
    	}
    	PointListCompressor.compress(xxList, yyList);
    	n = xxList.size();
    	double[] xx = new double[n];
    	double[] yy = new double[n];
    	for (int i = 0; i < n; i++) {
    		xx[i] = xxList.get(i);
    		yy[i] = yyList.get(i);
    	}
    	return new CubicSpline(xx, yy);
    }
    
    
    /**
     * Paint cubic spline connecting data points.
     * @param data Data to plot
     * @param color A color
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of line in pixels
     */
    private void paintConnectingSpline(final List<ArrayDatum> data,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth) {
    	if (data.size() > 0) {
    		int minY = this.y;
    		int maxY = minY + this.height;
	    	CubicSpline spline = this.newCubicSpline(data);
	    	ArrayDatum firstDatum = data.get(0);
	    	ArrayDatum lastDatum = data.get(data.size() - 1);
	    	int startX = this.x;
	    	int firstDatumX =
	    		this.transposeX(firstDatum.getReporter().getLocation(),
	    				this.copyNumberPlotBoundaries);
	    	if (firstDatumX > startX) {
	    		startX = firstDatumX;
	    	}
	    	int endX = this.x + this.width;
	    	int lastDatumX =
	    		this.transposeX(lastDatum.getReporter().getLocation(),
	    				this.copyNumberPlotBoundaries);
	    	if (lastDatumX < endX) {
	    		endX = lastDatumX;
	    	}
	    	int x1 = startX;
	    	int y1 = (int) spline.interpolate((double) x1);
	        for (int i = startX; i < endX - 1; i++) {
	        	int x2 = i + 1;
	        	int y2 = (int) spline.interpolate((double) x2);
	        	if (y1 < minY && y2 >= minY) {
	        		y1 = minY;
	        	} else if (y1 > maxY && y2 <= maxY) {
	        		y1 = maxY;
	        	}
	        	if (y2 < minY && y1 >= minY) {
	        		y2 = minY;
	        	}
	        	if (y2 > maxY && y1 <= maxY) {
	        		y2 = maxY;
	        	}
	        	if (y1 >= minY && y1 <= maxY
	        			&& y2 >= minY && y2 <= maxY) {
		        	Line line = new Line(x1, y1, x2, y2, lineWidth, color);
		        	drawingCanvas.add(line);
	        	}
	        	x1 = x2;
	        	y1 = y2;
	        }
    	}
    }
    
    
    /**
     * Paint "steps" connecting data points.
     * @param data Data to plot
     * @param color A color
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of line in pixels
     */
    private void paintConnectingSteps(final List<ArrayDatum> data,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth) {
        for (int i = 1; i < data.size(); i++) {
            ArrayDatum d1 = data.get(i - 1);
            ArrayDatum d2 = data.get(i);
            long start = d1.getReporter().getLocation();
            long end = d2.getReporter().getLocation();
            long mid = (start + end) / (long) 2;
            
            // First line - right horizontal from first datum
            this.reusableDataPoint1.bulkSet(d1);
            this.reusableDataPoint2.setValue1(mid);
            this.reusableDataPoint2.setValue2(d1.getValue());
            this.paintLine(this.reusableDataPoint1, this.reusableDataPoint2,
            		drawingCanvas, lineWidth, color);
            
            // Second line - vertical
            this.reusableDataPoint1.setValue1(mid);
            this.reusableDataPoint1.setValue2(d2.getValue());
            this.paintLine(this.reusableDataPoint1, this.reusableDataPoint2,
            		drawingCanvas, lineWidth, color);
            
            // Third line - left horizontal into second datum
            this.reusableDataPoint2.bulkSet(d2);
            this.paintLine(this.reusableDataPoint1, this.reusableDataPoint2,
            		drawingCanvas, lineWidth, color);
        }
    }
    
    
    /**
     * Paint chromosomal alterations.
     * @param cad Chromosome array data
     * @param color Color
     * @param drawingCanvas Canvas to paint on
     * @param lineWidth Width of line
     * @param alterationType Alteration type
     * @param boundaries Plot boundaries
     */
    private void paintAlterations(final ChromosomeArrayData cad,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth, final AnnotationType alterationType,
            final PlotBoundaries boundaries) {
    	
    	// Iterate over alterations
    	Iterator<AnnotatedGenomeFeature> it = cad.alteredSegmentIterator(
    			alterationType);
    	if (it == null) {
    		it = cad.alteredSegmentIterator(
        			this.lohThreshold, this.interpolateLohEndpoints,
        			alterationType);
    	}
    	while (it.hasNext()) {
    		AnnotatedGenomeFeature feat = it.next();
    		DataPoint left = DataPoint.leftDataPoint(feat);
    		DataPoint right = DataPoint.rightDataPoint(feat);
    		if (boundaries.atLeastPartlyOnPlot(left, right)) {
    			boundaries.truncateToFitOnPlot(left, right);
    				    				
				// Draw altered segment
				int startX = this.transposeX(left,
						boundaries);
				int endX = this.transposeX(right,
						boundaries);
				if (startX == endX) {
					startX -= MIN_LOH_WIDTH / 2;
					if (startX < this.x) {
						startX = this.x;
					}
					endX += MIN_LOH_WIDTH / 2;
					if (endX > this.x + this.width) {
						endX = this.x + this.width;
					}
				}
				int topY = this.transposeY(feat.getQuantitation(),
						this.copyNumberPlotBoundaries);
				drawingCanvas.add(new Line(startX, topY, endX, topY,
						lineWidth, color));
    		}
		}
    }
    
    
    /**
     * Draw single data point.
     * @param x X-coordinate of point center in pixels
     * @param y Y-coordinate of point in pixels
     * @param color Color of point
     * @param label Mouseover label for point
     * @param drawingCanvas A drawing canvas
     * @param pointRadius Radius of data point
     * @param selected Is individual data point selected (as opposed
     * to entire bioassay)?
     */
    private void drawPoint(final int x, final int y, final Color color,
            final String label, final DrawingCanvas drawingCanvas,
            final int pointRadius, final boolean selected) {
        Circle circle = new Circle(x, y, pointRadius, color);
        drawingCanvas.add(circle, false);
        if (selected) {
        	circle = new Circle(x, y, pointRadius + 3, color, false);
        	drawingCanvas.add(circle);
        }
    }
    
    /**
     * Draw diamond shape.
     * @param x X-coordinate of point center in pixels
     * @param y Y-coordinate of point in pixels
     * @param color Color of point
     * @param label Mouseover label for point
     * @param drawingCanvas A drawing canvas
     * @param pointRadius Radius of data point
     * @param selected Is individual data point selected (as opposed
     * to entire bioassay)?
     */
    private void drawDiamond(final int x, final int y, final Color color,
            final String label, final DrawingCanvas drawingCanvas,
            final int pointRadius, final boolean selected) {
    	Diamond d = new Diamond(x, y, pointRadius * 2, color);
    	drawingCanvas.add(d, false);
        if (selected) {
        	Circle circle = new Circle(x, y, pointRadius + 3, color, false);
        	drawingCanvas.add(circle);
        }
    }
    
    /**
     * Draw vertical stem from expression data diamond to Y-axis
     * zero value. 
     * @param point Data point being plotted
     * @param color Color of point
     * @param drawingCanvas A drawing canvas
     * @param pointRadius Radius of data point
     */
    private void drawStem(final DataPoint point, final Color color,
            final DrawingCanvas drawingCanvas,
            final int pointRadius) {
    	if (this.expressionPlotBoundaries.containsXAxis()) {
    		this.stemBaseReusableDataPoint.setValue1(point.getValue1());
    		this.stemBaseReusableDataPoint.setValue2(0.0);
    		int x1 = this.transposeX(point, this.expressionPlotBoundaries);
    		int y1 = this.transposeY(point, this.expressionPlotBoundaries);
    		int x2 = this.transposeX(this.stemBaseReusableDataPoint,
    				this.expressionPlotBoundaries);
    		int y2 = this.transposeY(this.stemBaseReusableDataPoint,
    				this.expressionPlotBoundaries);
    		if (point.getValue2() < 0) {
    			y2 += pointRadius / 2;
    		} else {
    			y2 -= pointRadius / 2;
    		}
    		Line line = new Line(x1, y1, x2, y2, STEM_THICKNESS, color);
    		drawingCanvas.add(line);
    	}
    }
    
    
    /**
     * Draw single error bar.
     * @param x X-axis position of bar in pixels
     * @param y Y-axis position of center of bar in pixels
     * @param error Error factor which determines the height of bar
     * @param color Color of bar
     * @param drawingCanvas A drawing canvas
     * @param lineWidth Width of line in pixels
     * @param boundaries Boundaries of the plot in relation to
     * the quantitation type
     */
    private void drawErrorBar(final int x, final int y, final double error,
            final Color color, final DrawingCanvas drawingCanvas,
            final int lineWidth, final PlotBoundaries boundaries) {
        
        // Compute reference points
        int deltaY = (int) ((double) height
                * boundaries.fractionalHeight(error));
        int y1 = y - deltaY / 2;
        int y2 = y1 + deltaY;
        int x1 = x - (DEF_ERROR_BAR_HATCH_LENGTH / 2);
        int x2 = x1 + DEF_ERROR_BAR_HATCH_LENGTH;
        
        // Vertical line
        Line line = new Line(x, y1, x, y2, lineWidth, color);
        drawingCanvas.add(line, false);
        
        // Top horizontal line
        line = new Line(x1, y1, x2, y1, lineWidth, color);
        drawingCanvas.add(line, false);
        
        // Bottom horizontal line
        line = new Line(x1, y2, x2, y2, lineWidth, color);
        drawingCanvas.add(line, false);
    }
    
    
    /**
     * Transpose the x-coordinate of given data point from
     * the native units of the plot (i.e., base pairs vs. some
     * quantitation type) to pixels.
     * @param dataPoint A data point
     * @param boundaries Plot boundaries with respect to a quantitation
     * type
     * @return Transposed x-coordinate in pixels
     */
    private int transposeX(final DataPoint dataPoint,
    		final PlotBoundaries boundaries) {
        return this.x + (int) ((double) width
                * boundaries.fractionalDistanceFromLeft(dataPoint));
    }
    
    
    /**
     * Transpose the given x-coordinate from
     * the native units of the plot (i.e., base pairs vs. some
     * quantitation type) to pixels.
     * @param x An x-coordinate in native units
     * @param boundaries Plot boundaries with respect to a quantitation
     * type
     * @return Transposed x-coordinate in pixels
     */
    private int transposeX(final long x,
    		final PlotBoundaries boundaries) {
        return this.x + (int) ((double) width
                * boundaries.fractionalDistanceFromLeft(x));
    }
    
    
    /**
     * Transpose the y-coordinate of given data point from
     * the native units of the plot (i.e., base pairs vs. some
     * quantitation type) to pixels.
     * @param dataPoint A data point
     * @param boundaries Plot boundaries with respect to a quantitation
     * type
     * @return Transposed y-coordinate in pixels
     */
    private int transposeY(final DataPoint dataPoint,
    		final PlotBoundaries boundaries) {
        return this.y + height - (int) ((double) height
                * boundaries.fractionalDistanceFromBottom(dataPoint));
    }
    
    
    /**
     * Transpose the given y-coordinate from
     * the native units of the plot (i.e., base pairs vs. some
     * quantitation type) to pixels.
     * @param y A y-coordinate in native units
     * @param boundaries Plot boundaries with respect to a quantitation
     * type
     * @return Transposed y-coordinate in pixels
     */
    private int transposeY(final float y,
    		final PlotBoundaries boundaries) {
        return this.y + height - (int) ((double) height
                * boundaries.fractionalDistanceFromBottom(y));
    }
    
    /**
     * Point at top left used to align with other plot elements.
     * @return A point
     */
    public Point topLeftAlignmentPoint() {
        return new Point(this.x, this.y);
    }
    
    
    /**
     * Point at bottom left used to align with other plot elements.
     * @return A point
     */
    public Point bottomLeftAlignmentPoint() {
        return new Point(this.x, this.y + this.height);
    }
    
    
    /**
     * Point at top right used to align with other plot elements.
     * @return A point
     */
    public Point topRightAlignmentPoint() {
        return new Point(this.x + this.width, this.y);
    }
    
    
    /**
     * Point at bottom right used to align with other plot elements.
     * @return A point
     */
    public Point bottomRightAlignmentPoint() {
        return new Point(this.x + this.width, this.y + this.height);
    }
    
    
    /**
     * Width in pixels.
     * @return Width in pixels
     */
    public int width() {
        return this.width;
    }
    
    
    /**
     * Height in pixels.
     * @return Height in pixels
     */
    public int height() {
        return this.height;
    }
    
    
    /**
     * Move element.
     * @param deltaX Number of pixels horizontally
     * @param deltaY Number of pixels vertically
     */
    public void move(final int deltaX, final int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
        this.clickBoxes.getOrigin().x += deltaX;
        this.clickBoxes.getOrigin().y += deltaY;
        this.mouseOverStripes.getOrigin().x += deltaX;
        this.mouseOverStripes.getOrigin().y += deltaY;
    }
}

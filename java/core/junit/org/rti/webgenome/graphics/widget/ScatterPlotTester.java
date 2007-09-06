/*
$Revision: 1.3 $
$Date: 2007-09-06 16:48:11 $

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

package org.rti.webgenome.graphics.widget;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.rti.webgenome.domain.ArrayDatum;
import org.rti.webgenome.domain.DataContainingBioAssay;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.QuantitationType;
import org.rti.webgenome.domain.Reporter;
import org.rti.webgenome.graphics.InterpolationType;
import org.rti.webgenome.graphics.PlotBoundaries;
import org.rti.webgenome.service.util.InMemoryChromosomeArrayDataGetter;
import org.rti.webgenome.units.HorizontalAlignment;
import org.rti.webgenome.units.VerticalAlignment;
import org.rti.webgenome.util.UnitTestUtils;

import junit.framework.TestCase;

/**
 * Tester for <code>ScatterPlot</code>.
 * @author dhall
 *
 */
public final class ScatterPlotTester extends TestCase {

	/** Name of directory where test output files are written. */
	private static final String TEST_DIR_NAME = "scatter-plot-test";
	
	/** Directory where test output files are written. */
	private static final File TEST_DIR =
		UnitTestUtils.createUnitTestDirectory(TEST_DIR_NAME);
	
	/** Width of plot in pixels. */
	private static final int WIDTH = 30;
	
	/** Height of plot in pixels. */
	private static final int HEIGHT = 30;
	
	/** Width of background object. */
	private static final int BG_WIDTH = 200;
	
	/** Height of background object. */
	private static final int BG_HEIGHT = 200;
	
	/** Color of background object. */
	private static final Color BG_COLOR = Color.PINK;
	
	/** All test experiments including copy number and expression. */
	private Collection<Experiment> experiments = new ArrayList<Experiment>();
	
	/** Experiments containing copy number data. */
	private Collection<Experiment> copyNumberExperiments =
		new ArrayList<Experiment>();
	
	/** Experiments containing expression data. */
	private Collection<Experiment> expressionExperiments =
		new ArrayList<Experiment>();
	
	/** Test plot boundaries for expression data. */
	private PlotBoundaries expressionPlotBoundaries =
		new PlotBoundaries(95.0, -3.0, 305.0, 3.0);
	
	/** Test plot boundaries for copy number data. */
	private PlotBoundaries copyNumberPlotBoundaries =
		new PlotBoundaries(95.0, -0.1, 305.0, 1.1);
	
	/** Array data getter. */
	private InMemoryChromosomeArrayDataGetter getter =
		new InMemoryChromosomeArrayDataGetter();
	
	/**
	 * Constructor.
	 */
	public ScatterPlotTester() {
		
		// Copy number test experiment
		Experiment exp = new Experiment();
		this.experiments.add(exp);
		this.copyNumberExperiments.add(exp);
		exp.setName("Copy number");
		DataContainingBioAssay ba = new DataContainingBioAssay();
		exp.add(ba);
		exp.setQuantitationType(QuantitationType.LOG_2_RATIO_COPY_NUMBER);
		ba.setName("Copy number");
		ba.setColor(new Color(0, 0, 255));
		ba.setId((long) 1);
		ba.add(new ArrayDatum((float) 0.0, new Reporter(null, (short) 1, 100)));
		ba.add(new ArrayDatum((float) 1.0, new Reporter(null, (short) 1, 200)));
		ba.add(new ArrayDatum((float) 0.75, new Reporter(null,
				(short) 1, 300)));
		
		// Expression test experiment
		exp = new Experiment();
		this.experiments.add(exp);
		this.expressionExperiments.add(exp);
		exp.setName("Expression");
		exp.setQuantitationType(QuantitationType.LOG_2_RATIO_FOLD_CHANGE);
		ba = new DataContainingBioAssay();
		exp.add(ba);
		ba.setName("Expression");
		ba.setColor(new Color(255, 0, 0));
		ba.setId((long) 2);
		ba.add(new ArrayDatum((float) 1.0, new Reporter(null, (short) 1, 100)));
		ba.add(new ArrayDatum((float) 2.9, new Reporter(null, (short) 1, 200)));
		ba.add(new ArrayDatum((float) -2.0, new Reporter(null,
				(short) 1, 220)));
	}
	
	/**
	 * Test scatter plot on copy number data with no other widgets.
	 */
	public void testAlone() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("alone.png");
	}
	

	/**
	 * Test scatter plot above another widget.
	 */
	public void testAbove() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Add background widget
		panel.add(new Background(BG_WIDTH, BG_HEIGHT, BG_COLOR));
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		panel.add(plot, HorizontalAlignment.CENTERED, VerticalAlignment.ABOVE);
		
		// Write graphics to file
		panel.toPngFile("above.png");
	}
	
	
	/**
	 * Test scatter plot above another widget.
	 */
	public void testBelow() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Add background widget
		panel.add(new Background(BG_WIDTH, BG_HEIGHT, BG_COLOR));
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		panel.add(plot, HorizontalAlignment.CENTERED, VerticalAlignment.BELOW);
		
		// Write graphics to file
		panel.toPngFile("below.png");
	}
	
	
	/**
	 * Test scatter plot to left of another widget.
	 */
	public void testLeft() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Add background widget
		panel.add(new Background(BG_WIDTH, BG_HEIGHT, BG_COLOR));
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		panel.add(plot, HorizontalAlignment.LEFT_OF,
				VerticalAlignment.CENTERED);
		
		// Write graphics to file
		panel.toPngFile("left.png");
	}
	
	
	/**
	 * Test scatter plot to right of another widget.
	 */
	public void testRight() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Add background widget
		panel.add(new Background(BG_WIDTH, BG_HEIGHT, BG_COLOR));
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		panel.add(plot, HorizontalAlignment.RIGHT_OF,
				VerticalAlignment.CENTERED);
		
		// Write graphics to file
		panel.toPngFile("right.png");
	}
	
	
	/**
	 * Test scatter plot with no interpolation.
	 */
	public void testNoInterpolation() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		plot.setInterpolationType(InterpolationType.NONE);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("no-interpolation.png");
	}
	
	
	/**
	 * Test scatter plot with straight line interpolation.
	 */
	public void testStraightLineInterpolation() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		plot.setInterpolationType(InterpolationType.STRAIGHT_LINE);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("straight-line-interpolation.png");
	}
	
	
	/**
	 * Test scatter plot with spline interpolation.
	 */
	public void testSplineInterpolation() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		plot.setInterpolationType(InterpolationType.SPLINE);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("spline-interpolation.png");
	}
	
	
	/**
	 * Test scatter plot with step interpolation.
	 */
	public void testStepInterpolation() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.copyNumberExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, null,
				this.copyNumberPlotBoundaries);
		plot.setInterpolationType(InterpolationType.STEP);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("step-interpolation.png");
	}
	
	
	/**
	 * Test on gene expression data.
	 */
	public void testExpression() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.expressionExperiments, (short) 1,
				this.getter, WIDTH, HEIGHT, this.expressionPlotBoundaries,
				null);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("expression.png");
	}
	
	
	/**
	 * Test on both copy number and expression data.
	 */
	public void testCoViz() {
		
		// Create plot panel
		RasterFileTestPlotPanel panel = new RasterFileTestPlotPanel(TEST_DIR);
		
		// Create plot
		ScatterPlot plot = new ScatterPlot(
				this.experiments, (short) 1,
				this.getter, WIDTH, HEIGHT, this.expressionPlotBoundaries,
				this.copyNumberPlotBoundaries);
		panel.add(plot);
		
		// Write graphics to file
		panel.toPngFile("co-viz.png");
	}
}

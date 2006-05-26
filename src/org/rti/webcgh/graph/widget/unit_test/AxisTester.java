/*

$Source$
$Revision$
$Date$

The Web CGH Software License, Version 1.0

Copyright 2003 RTI. This software was developed in conjunction with the National 
Cancer Institute, and so to the extent government employees are co-authors, any 
rights in such works shall be subject to Title 17 of the United States Code, 
section 105.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

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
FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL 
CANCER INSTITUTE, RTI, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.rti.webcgh.graph.widget.unit_test;

import java.awt.Color;

import junit.framework.TestCase;

import org.rti.webcgh.drawing.DrawingCanvas;
import org.rti.webcgh.drawing.HorizontalAlignment;
import org.rti.webcgh.drawing.Location;
import org.rti.webcgh.drawing.Orientation;
import org.rti.webcgh.drawing.VerticalAlignment;
import org.rti.webcgh.graph.unit_test.SvgTestPanel;
import org.rti.webcgh.graph.widget.Axis;
import org.rti.webcgh.graph.widget.Background;
import org.rti.webcgh.graph.widget.Caption;
import org.rti.webcgh.graph.widget.PlotPanel;

/**
 * 
 */
public class AxisTester extends TestCase {
    
    private SvgTestPanel panel = null;
    
    
    /**
     * 
     */
    public void setUp() {
    	this.panel = SvgTestPanel.newSvgTestPanel();
    }
    
    
//    /**
//     * 
//     *
//     */
//    public void testHorizAbove() {
//        Axis axis = new Axis(0, 10, 400, Orientation.HORIZONTAL, Location.ABOVE);
//        panel.add(axis, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.TOP_JUSTIFIED);
//        panel.toSvgFile("axis-horiz-above.svg");
//    }
//    
//    
//    /**
//     * 
//     *
//     */
//    public void testHorizBelow() {
//        Axis axis = new Axis(0, 10, 400, Orientation.HORIZONTAL, Location.BELOW);
//        panel.add(axis, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.TOP_JUSTIFIED);
//        panel.toSvgFile("axis-horiz-below.svg");
//    }
//    
//    
//    /**
//     * 
//     *
//     */
//    public void testVertLeft() {
//        Axis axis = new Axis(0, 10, 400, Orientation.VERTICAL, Location.LEFT_OF);
//        panel.add(axis, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.TOP_JUSTIFIED);
//        panel.toSvgFile("axis-vert-left.svg");
//    }
//    
//    
//    /**
//     * 
//     *
//     */
//    public void testVertRight() {
//        Axis axis = new Axis(0, 10, 400, Orientation.VERTICAL, Location.RIGHT_OF);
//        panel.add(axis, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.TOP_JUSTIFIED);
//        panel.toSvgFile("axis-vert-right.svg");
//    }
//    
//    
//    
//    /**
//     * 
//     *
//     */
//    public void testLeftBottomRight() {
//        Axis axisL = new Axis(0, 10, 400, Orientation.VERTICAL, Location.LEFT_OF);
//        Axis axisB = new Axis(0, 5, 400, Orientation.HORIZONTAL, Location.BELOW);
//        Axis axisR = new Axis(10, 50, 400, Orientation.VERTICAL, Location.RIGHT_OF);
//        Background background = new Background(400, 400, Color.yellow);
//        PlotPanel childPanel = this.panel.newChildPlotPanel();
//        childPanel.add(background, HorizontalAlignment.CENTERED, VerticalAlignment.CENTERED);
//        childPanel.add(axisL, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.BOTTOM_JUSTIFIED);
//        childPanel.add(axisR, HorizontalAlignment.RIGHT_JUSTIFIED, VerticalAlignment.BOTTOM_JUSTIFIED);
//        childPanel.add(axisB, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.BOTTOM_JUSTIFIED);
//        panel.add(childPanel, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.TOP_JUSTIFIED);
//        panel.toSvgFile("axis-left-bottom-right.svg");
//    }
//    
//    public void testZeroAlign() {
//    	Axis axisV = new Axis(-5, 10, 400, Orientation.VERTICAL, 
//    			Location.LEFT_OF);
//    	Axis axisB = new Axis(-20, 5, 400, Orientation.HORIZONTAL, Location.BELOW);
//    	this.panel.add(axisV, HorizontalAlignment.ON_ZERO, 
//    			VerticalAlignment.ON_ZERO);
//    	this.panel.add(axisB, HorizontalAlignment.LEFT_JUSTIFIED, 
//    			VerticalAlignment.ON_ZERO);
//    	panel.toSvgFile("axis-zero-align.svg");
//    }
    
    
    public void testScatterPlotLike() {
    	this.panel.setDrawBorder(true);
    	Axis axisV = new Axis(-5, 10, 400, Orientation.VERTICAL, 
    			Location.LEFT_OF);
    	Axis axisH = new Axis(-20, 5, 400, Orientation.HORIZONTAL, Location.BELOW);
    	Caption capt = new Caption("Caption", Orientation.HORIZONTAL, false);
    	PlotPanel hPanel = this.panel.newChildPlotPanel();
    	PlotPanel vPanel = this.panel.newChildPlotPanel();
    	hPanel.add(axisH, HorizontalAlignment.LEFT_JUSTIFIED, 
    			VerticalAlignment.BOTTOM_JUSTIFIED);
    	vPanel.add(axisV, HorizontalAlignment.LEFT_JUSTIFIED, 
    			VerticalAlignment.BOTTOM_JUSTIFIED, true);
    	vPanel.add(capt, HorizontalAlignment.LEFT_OF, VerticalAlignment.CENTERED);
    	this.panel.add(hPanel, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.BOTTOM_JUSTIFIED);
    	this.panel.add(vPanel, HorizontalAlignment.LEFT_JUSTIFIED, VerticalAlignment.BOTTOM_JUSTIFIED);
    	panel.toSvgFile("axis-scatterplot-like.svg");
    }

}

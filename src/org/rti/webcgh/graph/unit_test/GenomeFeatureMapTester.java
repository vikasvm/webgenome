/*

$Source: /share/content/gforge/webcgh/webgenome/src/org/rti/webcgh/graph/unit_test/GenomeFeatureMapTester.java,v $
$Revision: 1.1 $
$Date: 2005-12-14 19:43:02 $

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

package org.rti.webcgh.graph.unit_test;

import java.awt.Color;
import java.net.URL;

import org.rti.webcgh.drawing.DrawingCanvas;
import org.rti.webcgh.drawing.Location;
import org.rti.webcgh.drawing.Orientation;
import org.rti.webcgh.graph.CentromereWarper;
import org.rti.webcgh.graph.GenomeFeatureMap;
import org.rti.webcgh.graph.Warper;


/**
 * Tester for GenomeFeatureMap
 */
public class GenomeFeatureMapTester extends BasePlottingTester {
	
	private DrawingCanvas tile = null;
	
	/**
	 * 
	 */
	public void setUp() {
		super.setUp();
		this.tile = this.drawingCanvas.newTile();
		this.drawingCanvas.add(tile, 100, 100);
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testBasicHoriz() throws Exception {
		GenomeFeatureMap map = new GenomeFeatureMap(100, 200, 400, Orientation.HORIZONTAL);
		map.plotFeature(110, 120, "Feat 1", new URL("http://www.google.com"), true, 
			Color.green);
		map.plotFeatures(new long[] {150, 165, 180}, new long[] {151, 172, 190}, "Feat 2",
			null, true, Color.red);
		map.addFrame(Location.ABOVE, 2, Color.black);
		map.addFrame(Location.BELOW, 2, Color.black);
		map.paint(this.tile);
		PlotTesterUtils.writeDocument(this.document, "feat-map-basic-horiz.svg");
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testBasicVert() throws Exception {
		GenomeFeatureMap map = new GenomeFeatureMap(100, 200, 400, Orientation.VERTICAL);
		map.plotFeature(110, 120, "Feat 1", new URL("http://www.google.com"), true, 
			Color.green);
		map.plotFeatures(new long[] {150, 165, 180}, new long[] {151, 172, 190}, "Feat 2",
			null, true, Color.red);
		map.addFrame(Location.ABOVE, 2, Color.black);
		map.addFrame(Location.BELOW, 2, Color.black);
		map.paint(this.tile);
		PlotTesterUtils.writeDocument(this.document, "feat-map-basic-vert.svg");
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testChromosomeHoriz() throws Exception {
		GenomeFeatureMap map = new GenomeFeatureMap(100, 200, 400, Orientation.HORIZONTAL);
		Warper warper = new CentromereWarper(map.getFeatureHeight(), map.bpToPixel(110), 
			map.bpToPixel(150));
		map.setWarper(warper);
		
//		map.plotFeature(100, 140, "Feat 1", new URL("http://www.google.com"), false, 
//				Color.gray);
//		map.plotFeature(140, 200, "Feat 1", new URL("http://www.google.com"), false, 
//				Color.red);
		
		map.plotFeature(100, 120, "Feat 1", new URL("http://www.google.com"), false, 
			Color.gray);
		map.plotFeature(120, 125, "Feat 1", new URL("http://www.google.com"), false, 
				Color.red);
		map.plotFeature(125, 166, "Feat 1", new URL("http://www.google.com"), false, 
				Color.green);
		map.plotFeature(166, 172, "Feat 1", new URL("http://www.google.com"), false, 
				Color.gray);
		map.plotFeature(172, 191, "Feat 1", new URL("http://www.google.com"), false, 
				Color.darkGray);
		map.plotFeature(191, 200, "Feat 1", new URL("http://www.google.com"), false, 
				Color.blue);
		map.addFrame(Location.ABOVE, 2, Color.black);
		map.addFrame(Location.BELOW, 2, Color.black);
		map.paint(this.tile);
		PlotTesterUtils.writeDocument(this.document, "feat-map-chrom-horiz.svg");
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testChromosomeVert() throws Exception {
		GenomeFeatureMap map = new GenomeFeatureMap(100, 200, 400, Orientation.VERTICAL);
		Warper warper = new CentromereWarper(map.getFeatureHeight(), map.bpToPixel(110), 
			map.bpToPixel(150));
		map.setWarper(warper);
		
//		map.plotFeature(100, 140, "Feat 1", new URL("http://www.google.com"), false, 
//				Color.gray);
//		map.plotFeature(140, 200, "Feat 1", new URL("http://www.google.com"), false, 
//				Color.red);
		
		map.plotFeature(100, 120, "Feat 1", new URL("http://www.google.com"), false, 
			Color.gray);
		map.plotFeature(120, 125, "Feat 1", new URL("http://www.google.com"), false, 
				Color.red);
		map.plotFeature(125, 166, "Feat 1", new URL("http://www.google.com"), false, 
				Color.green);
		map.plotFeature(166, 172, "Feat 1", new URL("http://www.google.com"), false, 
				Color.gray);
		map.plotFeature(172, 191, "Feat 1", new URL("http://www.google.com"), false, 
				Color.darkGray);
		map.plotFeature(191, 200, "Feat 1", new URL("http://www.google.com"), false, 
				Color.blue);
		map.addFrame(Location.ABOVE, 2, Color.black);
		map.addFrame(Location.BELOW, 2, Color.black);
		map.paint(this.tile);
		PlotTesterUtils.writeDocument(this.document, "feat-map-chrom-vert.svg");
	}

}

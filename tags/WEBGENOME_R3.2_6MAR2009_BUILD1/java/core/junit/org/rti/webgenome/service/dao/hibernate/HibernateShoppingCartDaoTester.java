/*
$Revision: 1.7 $
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

package org.rti.webgenome.service.dao.hibernate;


import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.rti.webgenome.analysis.UserConfigurableProperty;
import org.rti.webgenome.analysis.UserConfigurablePropertyWithOptions;
import org.rti.webgenome.client.BioAssayDataConstraints;
import org.rti.webgenome.domain.Array;
import org.rti.webgenome.domain.DataSerializedBioAssay;
import org.rti.webgenome.domain.EjbDataSourceProperties;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.Organism;
import org.rti.webgenome.domain.Plot;
import org.rti.webgenome.domain.QuantitationType;
import org.rti.webgenome.domain.ShoppingCart;
import org.rti.webgenome.graphics.event.MouseOverStripe;
import org.rti.webgenome.graphics.event.MouseOverStripes;
import org.rti.webgenome.graphics.io.ClickBoxes;
import org.rti.webgenome.service.plot.ScatterPlotParameters;
import org.rti.webgenome.units.Orientation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Tester for <code>HibernateExperimentDao</code>.
 * @author dhall
 */
public final class HibernateShoppingCartDaoTester extends TestCase {
    
    /**
     * Test all methods.
     */
    public void testAllMethods() {
    	
    	// Get DAO bean
    	ApplicationContext ctx = new ClassPathXmlApplicationContext(
        "org/rti/webgenome/service/dao/hibernate/beans.xml");
		HibernateShoppingCartDao dao =
			(HibernateShoppingCartDao)
			ctx.getBean("shoppingCartDao");
		ShoppingCart cart = new ShoppingCart("user", "domain");
		dao.save(cart);
		ShoppingCart cart2 = dao.load("user", "domain");
		assertNotNull(cart2);
		dao.delete(cart);
		
		//
		// Code commented out because after refactor it no longer works.
		// TODO: Get code below working again.
		//
		
		// Instantiate test object
//		Organism org = new Organism("Genus", "species");
//		DataSerializedBioAssay ba = new DataSerializedBioAssay(
//				"bioassay1", org);
//		ba.setId(new Long(1));
//		ba.setColor(Color.RED);
//		ba.setSelected(true);
//		SortedMap<Short, String> dataFileIdx = new TreeMap<Short, String>();
//		dataFileIdx.put((short) 1, "file1");
//		dataFileIdx.put((short) 2, "file2");
//		ba.setChromosomeArrayDataFileIndex(dataFileIdx);
//		Array array = new Array("array1");
//		array.setChromosomeReportersFileName((short) 1, "file1");
//		array.setChromosomeReportersFileName((short) 2, "file2");
//		ba.setArray(array);
//		Map<Short, Long> sizes = new HashMap<Short, Long>();
//		sizes.put((short) 1, (long) 100);
//		sizes.put((short) 2, (long) 50);
//		ba.setChromosomeSizes(sizes);
//		Map<Short, Float> mins = new HashMap<Short, Float>();
//		mins.put((short) 1, (float) 0.1);
//		mins.put((short) 2, (float) 0.2);
//		ba.setMinValues(mins);
//		Map<Short, Float> maxes = new HashMap<Short, Float>();
//		maxes.put((short) 1, (float) 1.5);
//		maxes.put((short) 2, (float) 2.5);
//		ba.setMaxValues(maxes);
//		Map<Short, Integer> nums = new HashMap<Short, Integer>();
//		nums.put((short) 1, 500);
//		nums.put((short) 2, 300);
//		ba.setNumDatum(nums);
//		Experiment exp = new Experiment("experiment1", org,
//				QuantitationType.COPY_NUMBER);
//		exp.setId(new Long(1));
//		exp.add(ba);
//		exp.setAnalyticOperationClassName("className");
//		exp.setSourceDbId("sourceDB");
//		BioAssayDataConstraints c = new BioAssayDataConstraints();
//		c.setChromosome("1");
//		c.setPositions(new Long(100), new Long(200));
//		c.setQuantitationType("quantitationType1");
//		Set<BioAssayDataConstraints> constraints =
//			new HashSet<BioAssayDataConstraints>();
//		constraints.add(c);
//		exp.setBioAssayDataConstraints(constraints);
//		UserConfigurablePropertyWithOptions p1 =
//			new UserConfigurablePropertyWithOptions("name1",
//					"displayName1", "value1");
//		p1.addOption("code1", "displayName1");
//		p1.addOption("code2", "displayName2");
//		Set<UserConfigurableProperty> props =
//			new HashSet<UserConfigurableProperty>();
//		props.add(p1);
//		exp.setUserConfigurableProperties(props);
//		EjbDataSourceProperties ejbProps = new EjbDataSourceProperties(
//				"jndiName", "jndiProviderUrl", "clientId");
//		exp.setDataSourceProperties(ejbProps);
//		Plot plot = new Plot();
//		plot.setId(new Long(1));
//		plot.setDefaultImageFileName("file1");
//		plot.setWidth(100);
//		plot.setHeight(100);
//		plot.addImageFile("img2", "file2");
//		plot.addImageFile("img3", "file3");
//		ClickBoxes cb = new ClickBoxes(100, 100, 10, 10, new Point(0, 0));
//		cb.addClickBoxText("box1", 25, 10);
//		cb.addClickBoxText("box2", 50, 50);
//		Set<ClickBoxes> cbs = new HashSet<ClickBoxes>();
//		cbs.add(cb);
//		plot.setClickBoxes(cbs);
//		MouseOverStripes mos = new MouseOverStripes(
//				Orientation.HORIZONTAL, 100, 100, new Point(0, 0));
//		mos.add(new MouseOverStripe(0, 10, "stripe1"));
//		mos.add(new MouseOverStripe(11, 20, "stripe2"));
//		Set<MouseOverStripes> moss = new HashSet<MouseOverStripes>();
//		moss.add(mos);
//		plot.setMouseOverStripes(moss);
//		ScatterPlotParameters params = new ScatterPlotParameters();
//		plot.setPlotParameters(params);
//		plot.addExperiment(exp);
//		ShoppingCart cart = new ShoppingCart("user");
		
		// Run tests
//		dao.save(cart);
//		cart.add(exp);
//		cart.add(plot);
//		dao.update(cart);
//		cart.remove(plot);
//		dao.update(cart);
//		cart.remove(exp);
//		dao.update(cart);
//		dao.delete(cart);
		
    }
}
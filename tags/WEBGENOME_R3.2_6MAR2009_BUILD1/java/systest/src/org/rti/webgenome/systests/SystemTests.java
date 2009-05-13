/*
$Revision: 1.1 $
$Date: 2007-06-22 22:39:50 $

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

package org.rti.webgenome.systests;

import net.sourceforge.jwebunit.junit.WebTestCase;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

/**
 * Performs automated system tests against
 * web application.
 * @author dhall
 *
 */
public class SystemTests extends WebTestCase {
	
	//
	//  S T A T I C S
	//
	
	/** Key for system test property specifying base URL. */
	private static final String BASE_URL_PROP_KEY = "base.url";
	
	
	//
	//  O V E R R I D E S
	//
	
	/**
	 * {@inheritDoc}
	 */
	public void setUp() {
		this.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		String baseUrl =
			SystemTestUtils.getSystemTestProperty(BASE_URL_PROP_KEY);
		this.getTestContext().setBaseUrl(baseUrl);
	}
	
	
	//
	//  T E S T    C A S E S
	//

	/**
	 * Test that the context is up.
	 */
	public void testContextUp() {
		this.beginAt("/home.do");
		this.assertTitleEquals("webGenome: Overview");
	}
	
	
	/**
	 * Test the creation of a basic copy number scatter
	 * plot using simulated data.
	 *
	 */
	public void testCopyNumberScatterPlot() {
		this.beginAt("/home.do");
		this.assertLinkPresentWithExactText("copy number");
		this.clickLinkWithExactText("copy number");
		this.assertTitleEquals("webGenome: Plot");
	}
}
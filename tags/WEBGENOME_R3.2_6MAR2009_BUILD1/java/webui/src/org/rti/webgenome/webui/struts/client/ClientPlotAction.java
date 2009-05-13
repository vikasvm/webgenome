/*
$Revision: 1.13 $
$Date: 2008-09-09 18:36:52 $

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

package org.rti.webgenome.webui.struts.client;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webgenome.client.BioAssayDataConstraints;
import org.rti.webgenome.client.QuantitationTypes;
import org.rti.webgenome.core.InvalidClientQueryParametersException;
import org.rti.webgenome.core.PlotType;
import org.rti.webgenome.domain.BioAssay;
import org.rti.webgenome.domain.DataSourceProperties;
import org.rti.webgenome.domain.EjbDataSourceProperties;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.GenomeInterval;
import org.rti.webgenome.domain.Organism;
import org.rti.webgenome.domain.ShoppingCart;
import org.rti.webgenome.domain.SimulatedDataSourceProperties;
import org.rti.webgenome.graphics.util.ColorChooser;
import org.rti.webgenome.service.client.ClientDataService;
import org.rti.webgenome.service.client.ClientDataServiceManager;
import org.rti.webgenome.service.session.SessionMode;
import org.rti.webgenome.units.BpUnits;
import org.rti.webgenome.util.SystemUtils;
import org.rti.webgenome.webui.SessionTimeoutException;
import org.rti.webgenome.webui.struts.BaseAction;
import org.rti.webgenome.webui.struts.cart.PlotParametersForm;
import org.rti.webgenome.webui.struts.cart.SelectedExperimentsForm;
import org.rti.webgenome.webui.util.ClientQueryParser;
import org.rti.webgenome.webui.util.PageContext;

/**
 * Action that is invoked when a user is initially directed
 * to webGenome from a client application.
 */
public final class ClientPlotAction extends BaseAction {
	
	/**
	 * Quantitation type of initial data retrieval from
	 * application client.
	 */
	private static final String INIT_QUANTITATION_TYPE =
		QuantitationTypes.COPY_NUMBER;
    
	/** Logger. */
	private static final Logger LOGGER =
		Logger.getLogger(ClientPlotAction.class);
	

	/**
     * Execute action.
     * @param mapping Routing information for downstream actions
     * @param form Form data
     * @param request Servlet request object
     * @param response Servlet response object
     * @return Identification of downstream action as configured in the
     * struts-config.xml file
     * @throws Exception All exceptions thrown by classes in
     * the method are passed up to a registered exception
     * handler configured in the struts-config.xml file
     */
	public ActionForward execute(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request, 
    		final HttpServletResponse response) throws Exception {
		LOGGER.debug("Starting ClientPlotAction");
		
		System.out.println("qType = " + request.getParameter("qType"));
		System.out.println("contextPath = " + request.getContextPath());
		System.out.println("pathInfo = " + request.getPathInfo());
		System.out.println("queryString = " + request.getQueryString());
		System.out.println("requestURI = " + request.getRequestURI());
		System.out.println("servletPath = " + request.getServletPath());
		System.out.println("requestURL = " + request.getRequestURL());
		
		// Cache client ID in session
		String clientID = request.getParameter("clientID");
		if (clientID == null) {
			throw new InvalidClientQueryParametersException(
					"Missing 'clientID' parameter");
		}
		
		// Construct parameters for obtaining data through
		// the client data service
        String[] experimentIds = ClientQueryParser.getExperimentIds(request);
        BioAssayDataConstraints[] constraints =
        	ClientQueryParser.getBioAssayDataConstraints(request);
        
        // Instantiate data source properties and set quantitation type.
        DataSourceProperties props = null;
        
     
        // Case: client is true application client
        if (request.getParameter("test") == null) {
        	
        	// TODO: Get jndiName and jndiProviderURL from
            // request parameters
            String jndiName = SystemUtils.getApplicationProperty("jndi.name");
            String jndiProviderUrl =
            	SystemUtils.getApplicationProperty("jndi.provider.url");
        	props = new EjbDataSourceProperties(
            		jndiName, jndiProviderUrl, clientID);
        	
        	// Initialize quantitation type
        	for (int i = 0; i < constraints.length; i++) {
        		constraints[i].setQuantitationType(INIT_QUANTITATION_TYPE);
        	}
        	
        // Case: plotting simulated data
        } else {
        	props = new SimulatedDataSourceProperties();
        }
        
        // Get client data service
        ClientDataServiceManager mgr =
        	PageContext.getClientDataServiceManager(request);
        ClientDataService service = mgr.getClientDataService(props);
        
        // Retrieve data from client
        Collection<Experiment> experiments = 
        	service.getClientData(constraints,
        			experimentIds, clientID);
        for (Experiment exp : experiments) {
        	exp.setDataSourceProperties(props);
        }
        
        // stop here for now and quit
       // if (true)
         // return mapping.findForward("test");
        
        // TODO: In the future the organism should come from the
        // client query string
        
        // If session is new, instantiate new cart
        ShoppingCart cart = null;
        try {
        	cart = this.getShoppingCart(request);
        } catch (SessionTimeoutException e) {
        	cart = new ShoppingCart();
        	PageContext.setShoppingCart(request, cart);
        }
        
        // Give each experiment a unique ID and default
        // organism.  Give each bioassay a color and ID
        ColorChooser colorChooser = cart.getBioassayColorChooser();
        Organism org = this.getDbService().loadDefaultOrganism();
        for (Experiment exp : experiments) {
        	Long expId = this.getExperimentIdGenerator().nextId();
        	exp.setId(expId);
        	exp.setOrganism(org);
        	if (isExist(cart, exp.getName())){
        		exp.setName(exp.getName() + "_" + new Date().getTime());
        	}
        	for (BioAssay ba : exp.getBioAssays()) {
        		ba.setColor(colorChooser.nextColor());
        		ba.setId(this.getBioAssayIdGenerator().nextId());
        	}
        }
        
        // Put data in cart
        cart.add(experiments);
        
        // TODO: Make this cleaner.
        // Initialize image file manager.  This manager needs
        // to know the absolute path to the directory containing
        // plot files with URL <DOMAIN>:<PORT>/<CONTEXT>/plots
        if (!this.getImageFileManager().isInitialized()) {
	        String absPlotPath = this.getServlet().
	        	getServletContext().getRealPath("/plots");
	        File imageDir = new File(absPlotPath);
	        this.getImageFileManager().init(imageDir);
        }
        
        
        // Set image file manager property of shopping cart
        // so that image files will be deleted when the users
        // session ends
        cart.setImageFileManager(this.getImageFileManager());
        
        // Set session mode
        PageContext.setSessionMode(request, SessionMode.CLIENT);
        
        // Initialize plot parameters form
        PlotParametersForm pForm = (PlotParametersForm) form;
        pForm.init();
        pForm.setGenomeIntervals(GenomeInterval.encode(constraints));
        pForm.setUnits(BpUnits.BP.getName());
        pForm.setPlotType(PlotType.SCATTER.toString());
        
        // Set selected experiments form
        SelectedExperimentsForm sef =
        	PageContext.getSelectedExperimentsForm(request, true);
        sef.setSelectedExperimentIds(experiments);
        
		return mapping.findForward("success");
	}
	
	/**
	 * Checks if experiment with this name exist already.
	 * 
	 * @param cart
	 * @param experiment
	 * @return true - if exists otherwise false
	 * @throws Exception
	 */
	private boolean isExist(ShoppingCart cart, String experimentName) throws Exception{
		Set<Experiment> experiments = cart.getExperiments();
		
		for (Experiment e : experiments){
			if (e.getName().equals(experimentName))
				return true;
		}
		return false;
	}
}
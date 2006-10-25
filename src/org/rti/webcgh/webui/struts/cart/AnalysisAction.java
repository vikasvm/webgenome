/*
$Revision: 1.1 $
$Date: 2006-10-21 04:45:14 $

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

package org.rti.webcgh.webui.struts.cart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webcgh.analysis.AnalyticOperation;
import org.rti.webcgh.analysis.AnalyticOperationFactory;
import org.rti.webcgh.analysis.AnalyticPipeline;
import org.rti.webcgh.analysis.ScalarToScalarAnalyticOperation;
import org.rti.webcgh.domain.BioAssay;
import org.rti.webcgh.domain.Experiment;
import org.rti.webcgh.domain.ShoppingCart;
import org.rti.webcgh.graphics.util.ColorChooser;
import org.rti.webcgh.service.analysis.InMemoryDataTransformer;
import org.rti.webcgh.service.util.IdGenerator;
import org.rti.webcgh.webui.SessionTimeoutException;
import org.rti.webcgh.webui.struts.BaseAction;
import org.rti.webcgh.webui.util.PageContext;
import org.rti.webcgh.webui.util.SessionMode;

/**
 * Performs analytic operation on selected experiments.
 * If session is client-mode, this action drives the analytic
 * operation.  If stand-alone mode, the analytic operation is
 * driven by a batch process.
 * @author dhall
 *
 */
public final class AnalysisAction extends BaseAction {

	/** Analytic operation factory. */
	private final AnalyticOperationFactory analyticOperationFactory =
		new AnalyticOperationFactory();
	
	/** Data transformer. */
	private final InMemoryDataTransformer inMemoryDataTransformer =
		new InMemoryDataTransformer();
	
    /** Experiment ID generator. */
    private IdGenerator experimentIdGenerator = null;
    
    /** Bioassay ID generator. */
    private IdGenerator bioAssayIdGenerator = null;
    
    /**
     * Set bioassay ID generator.
     * @param bioAssayIdGenerator ID generator
     */
	public void setBioAssayIdGenerator(
			final IdGenerator bioAssayIdGenerator) {
		this.bioAssayIdGenerator = bioAssayIdGenerator;
	}


	/**
	 * Set experiment ID generator.
	 * @param experimentIdGenerator ID generator
	 */
	public void setExperimentIdGenerator(
			final IdGenerator experimentIdGenerator) {
		this.experimentIdGenerator = experimentIdGenerator;
	}
	
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
    public ActionForward execute(
        final ActionMapping mapping, final ActionForm form,
        final HttpServletRequest request,
        final HttpServletResponse response
    ) throws Exception {
    	
    	// Get shopping cart
    	ShoppingCart cart = PageContext.getShoppingCart(request);
    	
    	// Generate analytic operation
    	AnalyticOperationParametersForm aForm =
    		(AnalyticOperationParametersForm) form;
    	AnalyticOperation op = this.analyticOperationFactory.
    		newAnalyticOperation(aForm.getOperationKey());
    	
    	// Set user configurable analytic operation properties
    	Map paramMap = request.getParameterMap();
    	for (Object paramNameObj : paramMap.keySet()) {
    		String paramName = (String) paramNameObj;
    		if (paramName.indexOf("prop_") == 0) {
    			String propName = paramName.substring("prop_".length());
    			String propValue = request.getParameter(paramName);
    			op.setProperty(propName, propValue);
    		}
    	}
    	
    	// Construct map of input bioassay and experiment
    	// IDs to corresponding output bioassay and
    	// experiment names
    	Map<Long, String> outputBioAssayNames =
    		new HashMap<Long, String>();
    	Map<Long, String> outputExperimentNames =
    		new HashMap<Long, String>();
    	for (Object paramNameObj : paramMap.keySet()) {
    		String paramName = (String) paramNameObj;
    		String paramValue = request.getParameter(paramName);
    		if (paramName.indexOf("eo_") == 0) {
    			Long experimentId = Long.parseLong(
    					paramName.substring("eo_".length()));
    			outputExperimentNames.put(experimentId, paramValue);
    		} else if (paramName.indexOf("bo_") == 0) {
    			Long bioAssayId = Long.parseLong(
    					paramName.substring("bo_".length()));
    			outputBioAssayNames.put(bioAssayId, paramValue);
    		}
    	}
    	
    	// Retrieve selected experiments form bean.
    	// Note, this is not the form bean configured
    	// for this action in struts-config.xml.
    	SelectedExperimentsForm seForm =
    		PageContext.getSelectedExperimentsForm(request, false);
    	if (seForm == null) {
    		throw new SessionTimeoutException(
    				"Could not find selected experiments");
    	}
    	Collection<Long> ids = seForm.getSelectedExperimentIds();
    	
    	// Perform operation if in client mode and put in cart
    	SessionMode mode = PageContext.getSessionMode(request);
    	ColorChooser colorChooser = PageContext.getColorChooser(
    			request, true);
    	if (mode == SessionMode.CLIENT) {
    		Collection<Experiment> experiments = cart.getExperiments(ids);
    		for (Experiment input : experiments) {
    			Experiment output =
    				this.inMemoryDataTransformer.perform(input, op);
    			output.setId(this.experimentIdGenerator.nextId());
    			for (BioAssay ba : output.getBioAssays()) {
    				ba.setId(this.bioAssayIdGenerator.nextId());
    				ba.setColor(colorChooser.nextColor());
    			}
    			cart.add(output);
    			String expName = outputExperimentNames.get(input.getId());
    			if (expName != null) {
    				output.setName(expName);
    			}
    			if (op instanceof ScalarToScalarAnalyticOperation
    					|| (op instanceof AnalyticPipeline
    							&& ((AnalyticPipeline) op).
    							producesSingleBioAssayPerExperiment())) {
    				for (BioAssay ba : output.getBioAssays()) {
    					String bioAssayName =
    						outputBioAssayNames.get(
    								ba.getParentBioAssayId());
    					if (bioAssayName != null) {
    						ba.setName(bioAssayName);
    					}
    				}
    			} else {
    				Collection<BioAssay> bioAssays = output.getBioAssays();
    				if (bioAssays.size() > 0) {
    					bioAssays.iterator().next().setName(expName);
    				}
    			}
    		}
    	}
    	
    	return mapping.findForward("success");
    }
}
/*
$Revision: 1.1 $
$Date: 2006-12-03 22:23:43 $

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webcgh.domain.BioAssay;
import org.rti.webcgh.domain.Experiment;
import org.rti.webcgh.domain.Organism;
import org.rti.webcgh.domain.ShoppingCart;
import org.rti.webcgh.graphics.util.ColorChooser;
import org.rti.webcgh.service.client.ClientDataServiceManager;
import org.rti.webcgh.service.dao.OrganismDao;
import org.rti.webcgh.service.util.IdGenerator;
import org.rti.webcgh.webui.SessionTimeoutException;
import org.rti.webcgh.webui.util.PageContext;
import org.rti.webgenome.client.BioAssayDataConstraints;

/**
 * Imports data from a client application and
 * deposits in shopping cart.
 * @author dhall
 *
 */
public final class ImportAction extends Action {
	
    /** Experiment ID generator. */
    private IdGenerator experimentIdGenerator = null;
    
    /** Bioassay ID generator. */
    private IdGenerator bioAssayIdGenerator = null;
    
    /** Organism data access object. */
    private OrganismDao organismDao = null;
    
    /**
     * Set organism data access object.
     * @param organismDao Organism data access object
     */
	public void setOrganismDao(final OrganismDao organismDao) {
		this.organismDao = organismDao;
	}
	
	
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
    	
    	// Get selected experiments from cart
    	ShoppingCart cart = PageContext.getShoppingCart(request, true);
    	Collection<Experiment> selectedExperiments =
    		cart.getExperiments(ids);
    	
    	// Get bioassay data constraints from selected experiments
    	Collection<BioAssayDataConstraints> constraints =
    		Experiment.getBioAssayDataConstraints(selectedExperiments);
    	
    	// Set quantitation type
    	QuantitationTypeForm qForm = (QuantitationTypeForm) form;
    	String qType = qForm.getQuantitationTypeId();
    	for (BioAssayDataConstraints c : constraints) {
    		c.setQuantitationType(qType);
    	}
    	
    	// Get data from client
    	ClientDataServiceManager mgr =
    		PageContext.getClientDataServiceManager(request);
    	Collection<Experiment> experiments =
    		mgr.importData(selectedExperiments, constraints);
    	
    	// Give each experiment a unique ID and default
        // organism.  Give each bioassay a color and ID
        ColorChooser colorChooser = PageContext.getColorChooser(
        		request, true);
        Organism org = this.organismDao.loadDefault();
        for (Experiment exp : experiments) {
        	Long expId = this.experimentIdGenerator.nextId();
        	exp.setId(expId);
        	exp.setOrganism(org);
        	for (BioAssay ba : exp.getBioAssays()) {
        		ba.setColor(colorChooser.nextColor());
        		ba.setId(this.bioAssayIdGenerator.nextId());
        	}
        }
        
        // Put data in shopping cart
        cart.add(experiments);

    	return mapping.findForward("success");
    }
}
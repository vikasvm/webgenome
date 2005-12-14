/*

$Source: /share/content/gforge/webcgh/webgenome/src/org/rti/webcgh/webui/pipeline/EditPipelineAction.java,v $
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


package org.rti.webcgh.webui.pipeline;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionForm;

import org.rti.webcgh.analytic.AnalyticPipeline;
import org.rti.webcgh.analytic.DataFilterOperation;
import org.rti.webcgh.analytic.NormalizationOperation;
import org.rti.webcgh.analytic.SummaryStatisticOperation;
import org.rti.webcgh.array.persistent.PersistentDomainObjectMgr;
import org.rti.webcgh.array.persistent.PersistentPipeline;
import org.rti.webcgh.core.WebcghApplicationException;
import org.rti.webcgh.service.UserProfile;
import org.rti.webcgh.util.CollectionUtils;
import org.rti.webcgh.webui.util.AnalyticOperationDisplayProperties;
import org.rti.webcgh.webui.util.AnalyticOperationUIHelper;
import org.rti.webcgh.webui.util.Attribute;
import org.rti.webcgh.webui.util.AttributeManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.struts.ActionSupport;


/**
 * Gets list of virtual experiment metadata for presentation
 */
public class EditPipelineAction extends ActionSupport {
	


	/**
	 * Performs action of getting list of virtual experiment metadata for 
	 * presentation
	 *
	 * @param mapping Routing information for downstream actions
	 * @param form Data from calling form
	 * @param request Servlet request object
	 * @param response Servlet response object
	 * @return Identification of downstream action as configured in the
	 * struts-config.xml file
	 * @throws Exception
	 */
	public ActionForward execute
	(
		ActionMapping mapping, ActionForm form, HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {
	    HttpSession session = request.getSession();
	    UserProfile profile = AttributeManager.getUserProfile(request);
	    
	    // Attach pipeline to session
		String pipelineName = request.getParameter("pipelineName");
		ApplicationContext ctx = this.getWebApplicationContext();
		PersistentDomainObjectMgr objMgr = (PersistentDomainObjectMgr)
			ctx.getBean("persistentDomainObjectMgr");
		AnalyticPipeline pipeline = null;
		if (profile != null) {
			PersistentPipeline pipe = objMgr.getPersistentPipeline(pipelineName, profile.getName());
			if (pipe != null)
			    pipeline = pipe.toAnalyticPipeline();
		}
		if (pipeline == null)
		    pipeline = AnalyticPipeline.loadDefaultPipeline(pipelineName);
		if (pipeline == null)
		    throw new WebcghApplicationException("Pipeline '" + pipelineName + "' not found");
		session.setAttribute(Attribute.NEW_PIPELINE, pipeline);
		
		// Attach UI helper to session
		AnalyticOperationUIHelper uiHelper = (AnalyticOperationUIHelper)
			ctx.getBean("analyticOperationUIHelper");
		session.setAttribute("analyticOperationUIHelper", uiHelper);
		
		// Attach analytic operation options to session
		AnalyticOperationDisplayProperties[] dataFilterOperations = 
		    uiHelper.getAnalyticOperationDisplayProperties(DataFilterOperation.class);
		AnalyticOperationDisplayProperties[] normalizationOperations = 
		    uiHelper.getAnalyticOperationDisplayProperties(NormalizationOperation.class);
		AnalyticOperationDisplayProperties[] summaryStatisticOperations = 
		    uiHelper.getAnalyticOperationDisplayProperties(SummaryStatisticOperation.class);
		session.setAttribute("dataFilterOperations", CollectionUtils.arrayToArrayList(dataFilterOperations));
		session.setAttribute("normalizationOperations", CollectionUtils.arrayToArrayList(normalizationOperations));
		session.setAttribute("summaryStatisticOperations", CollectionUtils.arrayToArrayList(summaryStatisticOperations));
		
		return mapping.findForward("success");
	}
}

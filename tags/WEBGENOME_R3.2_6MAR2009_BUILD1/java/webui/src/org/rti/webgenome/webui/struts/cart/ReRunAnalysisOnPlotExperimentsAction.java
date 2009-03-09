/*
$Revision: 1.18 $
$Date: 2008-02-22 18:24:44 $

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

package org.rti.webgenome.webui.struts.cart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.rti.webgenome.core.WebGenomeApplicationException;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.Plot;
import org.rti.webgenome.domain.Principal;
import org.rti.webgenome.domain.ShoppingCart;
import org.rti.webgenome.service.analysis.DataTransformer;
import org.rti.webgenome.service.job.ReRunAnalysisOnPlotExperimentsJob;
import org.rti.webgenome.webui.util.PageContext;
import org.rti.webgenome.webui.util.ProcessingModeDecider;

/**
 * Rerun analytic operation on all derived
 * experiments from a plot.
 * @author dhall
 *
 */
public class ReRunAnalysisOnPlotExperimentsAction
extends BaseAnalysisAction {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionForward execute(
	        final ActionMapping mapping, final ActionForm form,
	        final HttpServletRequest request,
	        final HttpServletResponse response)
	throws Exception {
		
		// Recover plot
		ShoppingCart cart = this.getShoppingCart(request);
		Long plotId = Long.parseLong(request.getParameter("plotId"));
		Plot plot = cart.getPlot(plotId);
		
		// Recover derived experiments
		Collection<Experiment> derivedExperiments =
			new ArrayList<Experiment>();
		Collection<Experiment> experiments = plot.getExperiments();
		for (Experiment exp : experiments) {
			if (exp == null) {
				throw new WebGenomeApplicationException(
						"One or more experiments no longer workspace");
			}
			if (exp.isDerived()) {
				derivedExperiments.add(exp);
			}
		}
		
		// Set new analytic parameters
		ActionErrors errors = 
			this.setUserSpecifiedParameters(derivedExperiments,
					request);
		if (errors != null) {
			return mapping.findForward("errors");
		}
		
		ActionForward forward = null;
		
		// Case: Process imediately
		DataTransformer transformer = this.getDataTransformer(request);
		if (!(ProcessingModeDecider.analysisInBackground(derivedExperiments,
				request) || ProcessingModeDecider.plotInBackground(
						plot.getPlotParameters(), request))
						|| ProcessingModeDecider.plotInBackground(experiments,
								plot.getPlotParameters().getGenomeIntervals(),
								request)) {
			Set<String> replacedFiles =
				this.getAnalysisService().rePerformAnalyticOperation(
					derivedExperiments, transformer);
			if (PageContext.standAloneMode(request)) {
				this.getDbService().updateShoppingCart(cart);
			}
			this.getIoService().deleteDataFiles(replacedFiles);
			forward = mapping.findForward("non.batch");
			
		// Case: Process in background
		} else {
			Principal principal = PageContext.getPrincipal(request);
			ReRunAnalysisOnPlotExperimentsJob job =
				new ReRunAnalysisOnPlotExperimentsJob(
						new HashSet<Experiment>(experiments), plotId,
						principal.getId(), principal.getDomain());
			this.getJobManager().add(job);
			ActionMessages messages = new ActionMessages();
	    	messages.add("global", new ActionMessage("plot.job"));
	    	this.saveMessages(request, messages);
			forward = mapping.findForward("batch");
		}
	
		
		return forward;
	}
}

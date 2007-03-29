/*
$Revision: 1.2 $
$Date: 2007-03-29 18:02:01 $

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

package org.rti.webgenome.webui.struts.cart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webgenome.analysis.UserConfigurableProperty;
import org.rti.webgenome.core.WebGenomeApplicationException;
import org.rti.webgenome.domain.Experiment;
import org.rti.webgenome.domain.Plot;
import org.rti.webgenome.domain.QuantitationType;
import org.rti.webgenome.domain.ShoppingCart;
import org.rti.webgenome.webui.struts.BaseAction;
import org.rti.webgenome.webui.util.PageContext;

/**
 * Setup action for screen that enables the user to adjust
 * analysis parameters from within a plot screen.
 * @author dhall
 *
 */
public class AdjustPlotAnalysisParamsSetupAction extends BaseAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
    public ActionForward execute(
            final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request,
            final HttpServletResponse response
        ) throws Exception {
    	
		// Attach map of derived experiments to request
		ShoppingCart cart = PageContext.getShoppingCart(request);
		Long plotId = Long.parseLong(request.getParameter("id"));
		Plot plot = cart.getPlot(plotId);
		if (plot == null) {
			throw new WebGenomeApplicationException(
					"Unable to retrieve plot from shopping cart");
		}
		Collection<Long> expIds = plot.getExperimentIds();
		Collection<Experiment> experiments = cart.getExperiments(expIds);
		QuantitationType qType = Experiment.getQuantitationType(experiments);
		Map<Experiment, Collection<UserConfigurableProperty>>
			derivedExperiments = new HashMap<Experiment,
				Collection<UserConfigurableProperty>>();
		for (Long expId : expIds) {
			Experiment exp = cart.getExperiment(expId);
			if (exp == null) {
				throw new WebGenomeApplicationException(
						"Some experiments no longer in shopping cart");
			}
			if (exp.isDerived()) {
				derivedExperiments.put(exp,
						exp.getSourceAnalyticOperation().
						getUserConfigurableProperties(qType));
			}
		}
		request.setAttribute("derived.experiments", derivedExperiments);
		
		// Attach plot to request
		request.setAttribute("plot", plot);
		
    	return mapping.findForward("success");
    }
}
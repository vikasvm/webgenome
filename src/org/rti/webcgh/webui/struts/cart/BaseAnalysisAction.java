/*
$Revision: 1.1 $
$Date: 2007-03-06 02:06:28 $

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

package org.rti.webcgh.webui.struts.cart;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.rti.webcgh.analysis.AnalyticOperation;
import org.rti.webcgh.analysis.BadUserConfigurablePropertyException;
import org.rti.webcgh.webui.struts.BaseAction;

/**
 * Base class for actions that perform analytic operations.
 * This class mainly provides helper methods.
 * @author dhall
 *
 */
public class BaseAnalysisAction extends BaseAction {
	
	// Recover user configurable analytic operation properties.
	// Such properties will be passed in as query parameters
	// with a prefix of 'prop_.'
	
	/**
	 * Recover user specified parameters for given analytic
	 * operation from the given request object and set these
	 * properties in the operation.  Such parameters are passed
	 * to the action as HTTP query parameters wint a prefix
	 * of 'prop_'.
	 * @param op Analytic operation whose user specified
	 * parameters will be set
	 * @param request The request
	 * @param aForm Analytic operation parameters form
	 * @return Action errors if any of the user specified
	 * analytic operation parameters are not valid
	 */
	protected ActionErrors setUserSpecifiedParameters(
			final AnalyticOperation op,
			final HttpServletRequest request,
			final AnalyticOperationParametersForm aForm) {
		ActionErrors errors = null;
    	Map paramMap = request.getParameterMap();
    	boolean haveErrors = false;
    	for (Object paramNameObj : paramMap.keySet()) {
    		String paramName = (String) paramNameObj;
    		if (paramName.indexOf("prop_") == 0) {
    			String propName = paramName.substring("prop_".length());
    			String propValue = request.getParameter(paramName);
    			try {
    				op.setProperty(propName, propValue);
    			} catch (BadUserConfigurablePropertyException e) {
    				haveErrors = true;
    			}
    		}
    	}
    	
    	// If user input is invalid, return
    	if (haveErrors) {
    		errors = new ActionErrors();
    	}
    	
    	return errors;
	}
}

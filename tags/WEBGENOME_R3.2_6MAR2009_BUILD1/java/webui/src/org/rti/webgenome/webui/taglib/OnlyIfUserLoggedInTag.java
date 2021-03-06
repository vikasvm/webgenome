/*L
 *  Copyright RTI International
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/webgenome/LICENSE.txt for details.
 */

/*
$Revision: 1.1 $
$Date: 2007-03-29 17:03:31 $


*/

package org.rti.webgenome.webui.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.rti.webgenome.util.SystemUtils;
import org.rti.webgenome.webui.SessionTimeoutException;

/**
 * Contents of tag evaluated only if the user is logged in.
 * @author dhall
 */
public class OnlyIfUserLoggedInTag extends TagSupport {

	/** Serlialized version ID. */
	private static final long serialVersionUID = 
		SystemUtils.getLongApplicationProperty("serial.version.uid");
	
	
	/**
	 * Do after start tag parsed.
	 * @throws JspException if anything goes wrong.
	 * @return Return value
	 */
	@Override
	public final int doStartTag() throws JspException {
		int rVal = TagSupport.SKIP_BODY;
		try {
			if (org.rti.webgenome.webui.util.PageContext.getPrincipal(
					(HttpServletRequest) pageContext.getRequest())
					!= null) {
				rVal = TagSupport.EVAL_BODY_INCLUDE;
			}
		} catch (SessionTimeoutException e) {
			rVal = TagSupport.SKIP_BODY;
		}
		return rVal;
	}
}

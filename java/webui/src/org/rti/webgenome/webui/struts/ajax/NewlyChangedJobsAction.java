/*
$Revision: 1.3 $
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

package org.rti.webgenome.webui.struts.ajax;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webgenome.domain.Principal;
import org.rti.webgenome.service.job.Job;
import org.rti.webgenome.service.job.JobManager;
import org.rti.webgenome.webui.SessionTimeoutException;
import org.rti.webgenome.webui.struts.BaseAction;
import org.rti.webgenome.webui.util.PageContext;

/**
 * This class identifies any jobs whose start or completion
 * status has changed since the last call.
 * @author dhall
 *
 */
public class NewlyChangedJobsAction extends BaseAction {
	
	/** Logger. */
	private static final Logger LOGGER =
		Logger.getLogger(NewlyChangedJobsAction.class);
	
	/** Manager of compute-intensive jobs. */
	private JobManager jobManager = null;
	
	
	/**
	 * Set manager of compute-intensive jobs.
	 * @param jobManager Job manager
	 */
	public void setJobManager(final JobManager jobManager) {
		this.jobManager = jobManager;
	}



	/**
	 * {@inheritDoc}
	 */
	public ActionForward execute(
	        final ActionMapping mapping, final ActionForm form,
	        final HttpServletRequest request,
	        final HttpServletResponse response
	    ) throws Exception {
		LOGGER.debug("Looking for newly completed jobs");
		Collection<Job> completedJobs = new ArrayList<Job>();
		Collection<Job> startedJobs = new ArrayList<Job>();
		try {
			Principal principal = PageContext.getPrincipal(request);
			completedJobs = this.jobManager.getNewlyCompletedJobs(
					principal.getName(), principal.getDomain());
			startedJobs = this.jobManager.getNewlyStartedJobs(
					principal.getName(), principal.getDomain());
		} catch (SessionTimeoutException e) {
			LOGGER.info("Browser requesting completed jobs "
					+ "from an expired session");
		}
		if (completedJobs.size() > 0) {
			StringBuffer jobIds = new StringBuffer();
			int count = 0;
			for (Job job : completedJobs) {
				if (count++ > 0) {
					jobIds.append(", ");
				}
				jobIds.append(job.getId());
			}
			LOGGER.info("The following jobs have recently completed: "
					+ jobIds.toString());
		} else {
			LOGGER.debug("No newly completed jobs to report");
		}
		request.setAttribute("completedJobs", completedJobs);
		request.setAttribute("startedJobs", startedJobs);
		return mapping.findForward("success");
	}
}
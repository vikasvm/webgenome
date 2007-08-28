/*
$Revision: 1.4 $
$Date: 2007-08-28 17:24:13 $

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

package org.rti.webgenome.webui.struts.upload;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webgenome.domain.Organism;
import org.rti.webgenome.domain.UploadDataSourceProperties;
import org.rti.webgenome.service.dao.OrganismDao;
import org.rti.webgenome.service.io.IOService;
import org.rti.webgenome.units.BpUnits;
import org.rti.webgenome.webui.struts.BaseAction;
import org.rti.webgenome.webui.util.PageContext;

/**
 * Setup action for showing the main upload form.
 * @author dhall
 *
 */
public class ShowMainUploadFormSetupAction extends BaseAction {
	
	/** Service for file I/O. */
	private IOService ioService = null;
	
	/** Organism data access object. */
	private OrganismDao organismDao = null;

	/**
	 * Injector method for the organism data access object.
	 * @param organismDao Organism data access object
	 */
	public void setOrganismDao(final OrganismDao organismDao) {
		this.organismDao = organismDao;
	}
	
	
	/**
	 * Set service for file I/O.
	 * @param ioService File I/O service.
	 */
	public void setIoService(final IOService ioService) {
		this.ioService = ioService;
	}

	/**
	 * {@inheritDoc}
	 */
	public ActionForward execute(
	        final ActionMapping mapping, final ActionForm form,
	        final HttpServletRequest request,
	        final HttpServletResponse response
	    ) throws Exception {
		UploadDataSourceProperties upload = PageContext.getUpload(request);
		request.setAttribute("upload", upload);
		Set<String> allCols = new HashSet<String>();
		if (upload.getReporterLocalFileName() == null) {
			if (upload.getDataFileMetaData() != null) {
				allCols = this.ioService.getColumnHeadings(
						upload.getDataFileMetaData());
			}
		} else {
			allCols = this.ioService.getColumnHeadings(
					upload.getReporterLocalFileName(),
					upload.getReporterFileFormat());
		}
		request.setAttribute("allCols", allCols);
		List<Organism> organisms = this.organismDao.loadAll();
		request.setAttribute("organisms", organisms);
		Collection<BpUnits> units = BpUnits.getUnits();
		request.setAttribute("units", units);
		return mapping.findForward("success");
	}
}
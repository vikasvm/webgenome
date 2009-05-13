/*
$Revision: 1.3 $
$Date: 2007-12-17 23:29:23 $

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

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.rti.webgenome.domain.DataColumnMetaData;
import org.rti.webgenome.domain.DataFileMetaData;
import org.rti.webgenome.domain.UploadDataSourceProperties;
import org.rti.webgenome.domain.UploadedData;
import org.rti.webgenome.webui.struts.BaseAction;
import org.rti.webgenome.webui.util.PageContext;

/**
 * Action class that attached a data file to an upload.
 * @author dhall
 *
 */
public class AttachDataFileAction extends BaseAction {
	
	/**
	 * Suffix added to a data file column heading by the JSP
	 * 'specifyDataColumn.jsp' to indicate the parameter
	 * is a text box for specifying the name of the bioassay
	 * that is derived from data in the column.
	 */
	private static final String BIOASSAY_SUFFIX = "_bioassay";

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public ActionForward execute(
	        final ActionMapping mapping, final ActionForm form,
	        final HttpServletRequest request,
	        final HttpServletResponse response
	    ) throws Exception {
		ReporterColumnNameForm rForm = (ReporterColumnNameForm) form;
		UploadedData data = PageContext.getUploadedData(request);
		
		// Instantiate new data file meta data and add to upload
		DataFileMetaData meta = new DataFileMetaData();
		meta.setReporterNameColumnName(rForm.getReporterColumnName());
		meta.setFormat(data.getFileFormat());
		meta.setLocalFileName(data.getFile().getName());
		meta.setRemoteFileName(data.getRemoteFileName());
		UploadDataSourceProperties upload = PageContext.getUpload(request);
		upload.add(meta);
		
		// Add data column metadata to file metadata
		Enumeration<String> pNames = request.getParameterNames();
		while (pNames.hasMoreElements()) {
			String pName = pNames.nextElement();
			if (pName.endsWith(BIOASSAY_SUFFIX)) {
				String fieldName = pName.substring(0,
						pName.length() - BIOASSAY_SUFFIX.length());
				String bioAssayName = request.getParameter(pName);
				if (bioAssayName == null || bioAssayName.length() < 1) {
					ActionErrors errors = new ActionErrors();
					errors.add("global",
							new ActionError("missing.bioassay.name"));
					this.saveErrors(request, errors);
					return mapping.findForward("errors");
				}
				DataColumnMetaData colMeta =
					new DataColumnMetaData(fieldName, bioAssayName);
				meta.add(colMeta);
			}
		}
		
		return mapping.findForward("success");
	}
}
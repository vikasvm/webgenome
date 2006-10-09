/*
$Revision: 1.1 $
$Date: 2006-10-09 05:10:13 $

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

package org.rti.webcgh.webui.taglib;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.rti.webcgh.domain.BioAssay;
import org.rti.webcgh.util.ColorUtils;

/**
 * Outputs color of bioassay in hexadecimal RGB
 * form.  Tag expects an attribute 'name' that is
 * the name of a bean of type <code>BioAssay</code>.
 * @author dhall
 *
 */
public class BioAssayColorTag extends TagSupport {
	
	/** Serlialized version ID. */
	private static final long serialVersionUID = 1;
	
	/**
	 * Name of some bean of type <code>Experiment</code>.
	 */
	private String name = null;
	
	
	/**
	 * Set name of bean of type <code>Experiment</code>.
	 * @param name Name of bean.
	 */
	public final void setName(final String name) {
		this.name = name;
	}


	/**
	 * Do after start tag parsed.
	 * @throws JspException if anything goes wrong.
	 * @return Return value
	 */
	@Override
	public final int doStartTag() throws JspException {
		
		// Make sure bean is in good form
		if (this.name == null || this.name.length() < 1) {
			throw new JspException("Tag attribute '"
					+ this.name + "' missing or empty");
		}
		Object obj = pageContext.findAttribute(this.name);
		if (obj == null) {
			throw new JspException("Cannot find bean named '"
					+ this.name + "'");
		}
		if (!(obj instanceof BioAssay)) {
			throw new JspException("Bean '" + this.name + "' is "
					+ "not of type Experiment");
		}
		BioAssay ba = (BioAssay) obj;
		Color c = ba.getColor();
		if (c == null) {
			throw new JspException("Bioassay '" + ba.getName()
					+ "' does not have a color");
		}
		
		// Generate output
		String colorEncoding = ColorUtils.toRgbHexEncoding(c);
		Writer out = this.pageContext.getOut();
		try {
			out.write(colorEncoding);
		} catch (IOException e) {
			throw new JspException("Error writing to JSP page");
		}
		return TagSupport.SKIP_BODY;
	}
}

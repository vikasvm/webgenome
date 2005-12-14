/*

$Source: /share/content/gforge/webcgh/webgenome/src/org/rti/webcgh/drawing/GraphicPrimitive.java,v $
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


package org.rti.webcgh.drawing;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for primitive graphic objects
 */
public abstract class GraphicPrimitive {
	
	
	protected Color color = Color.black;
	protected Hyperlink hyperlink = null;
	protected String toolTipText = null;
	protected List eventResponses = new ArrayList();
	protected Cursor cursor = Cursor.NORMAL;
	

	/**
	 * @return Color of element
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color Color of element
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return Hyperlink
	 */
	public URL getUrl() {
		URL url = null;
		if (hyperlink != null)
			url = hyperlink.getUrl();
		return url;
	}

	/**
	 * @param url Hyperlink
	 */
	public void setUrl(URL url) {
		if (hyperlink == null)
			hyperlink = new Hyperlink();
		hyperlink.setUrl(url);
	}

	/**
	 * @return Tool tip text
	 */
	public String getToolTipText() {
		return toolTipText;
	}

	/**
	 * @param string Tool tip text
	 */
	public void setToolTipText(String string) {
		toolTipText = string;
	}
	
	
	/**
	 * Add a response to an event
	 * @param event An event
	 * @param response A response
	 */
	public void addGraphicEventResponse(GraphicEvent event, String response) {
		eventResponses.add(new GraphicEventResponse(event, response));
	}
	
	
	/**
	 * Get responses to graphic events
	 * @return Responses to graphic event
	 */
	public GraphicEventResponse[] getGraphicEventResponses() {
		GraphicEventResponse[] responses = new GraphicEventResponse[0];
		responses = (GraphicEventResponse[])eventResponses.toArray(responses);
		return responses;
	}

	/**
	 * @return Style of cursor (see public static variables) 
	 */
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * @param c Style of cursor (see public static variables)
	 */
	public void setCursor(Cursor c) {
		cursor = c;
	}

	/**
	 * @return Returns the hyperlink.
	 */
	public Hyperlink getHyperlink() {
		return hyperlink;
	}
	/**
	 * @param hyperlink The hyperlink to set.
	 */
	public void setHyperlink(Hyperlink hyperlink) {
		this.hyperlink = hyperlink;
	}
}

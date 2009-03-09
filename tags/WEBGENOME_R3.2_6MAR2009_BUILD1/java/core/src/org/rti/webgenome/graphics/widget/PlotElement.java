/*
$Revision: 1.1 $
$Date: 2007-03-29 17:03:31 $

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


package org.rti.webgenome.graphics.widget;

import java.awt.Point;

import org.rti.webgenome.graphics.DrawingCanvas;

/**
 * A graphical element in a plot.
 */
public interface PlotElement {
    
    
    /**
     * Paint element.
     * @param canvas A canvas
     */
    void paint(DrawingCanvas canvas);
    
    
    /**
     * Point at top left used to align with other plot elements.
     * @return A point
     */
    Point topLeftAlignmentPoint();
    
    
    /**
     * Point at bottom left used to align with other plot elements.
     * @return A point
     */
    Point bottomLeftAlignmentPoint();
    
    
    /**
     * Point at top right used to align with other plot elements.
     * @return A point
     */
    Point topRightAlignmentPoint();
    
    
    /**
     * Point at bottom right used to align with other plot elements.
     * @return A point
     */
    Point bottomRightAlignmentPoint();
    
    
    /**
     * Width in pixels.
     * @return Width in pixels
     */
    int width();
    
    
    /**
     * Height in pixels.
     * @return Height in pixels
     */
    int height();
    
    
    /**
     * Return point at top left of element.
     * @return A point
     */
    Point topLeftPoint();
    
    
    /**
     * Move element.
     * @param deltaX Number of pixels horizontally
     * @param deltaY Number of pixels vertically
     */
    void move(int deltaX, int deltaY);
}

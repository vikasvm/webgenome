/*
$Revision: 1.3 $
$Date: 2006-09-19 02:09:30 $

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


package org.rti.webcgh.graphics.primitive;

import java.awt.Color;


/**
 * Rectangle.
 */
public class Rectangle extends GraphicPrimitive {
	
	/** X-coordinate of top left point. */
	private int x = 0;
	
	/** Y-coordinate of top left point. */
	private int y = 0;
	
	/** Width in pixels. */
	private int width = 0;
	
	/** Height in pixels. */
	private int height = 0;
	
	/** Should rectangle be filled? */
	private boolean filled = true;
	
	
	/**
	 * Constructor.
	 *
	 */
	public Rectangle() {
		
	}
	
	
	/**
	 * Constructor.
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param width Width
	 * @param height Height
	 * @param color Color
	 */
	public Rectangle(final int x, final int y, final int width,
			final int height, final Color color) {
		super(color);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	

	/**
	 * Get height.
	 * @return Height of rectangle in pixels.
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Get width.
	 * @return Width of rectangle pin pixels
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * Get X-coordinate of top left point.
	 * @return X-coordinate of top left point
	 */
	public final int getX() {
		return x;
	}

	/**
	 * Get Y-coordinate of top left point.
	 * @return Y-coordinate of bottom left point
	 */
	public final int getY() {
		return y;
	}

	/**
	 * Set height of rectangle.
	 * @param i Height in pixels.
	 */
	public final void setHeight(final int i) {
		height = i;
	}

	/**
	 * Set width of rectangle.
	 * @param i Width in pixels
	 */
	public final void setWidth(final int i) {
		width = i;
	}

	/**
	 * Set X-coordinate of top left point.
	 * @param i X-coordinate of top left point
	 */
	public final void setX(final int i) {
		x = i;
	}

	/**
	 * Set Y-coordinate of top left point.
	 * @param i Y-coordinate of top left point
	 */
	public final void setY(final int i) {
		y = i;
	}

	/**
	 * Is rect filled in?
	 * @return T/F
	 */
	public final boolean isFilled() {
		return filled;
	}

	/**
	 * Is rect filled in?
	 * @param b Is rect filled in?
	 */
	public final void setFilled(final boolean b) {
		filled = b;
	}

	
	// ==================================
	//     Implemented abstract methods
	// ==================================
	
	/**
	 * Move graphic primitive.
	 * @param deltaX Change in X-coordinates in pixels
	 * @param deltaY Change in Y-coordinates in pixels
	 */
	public final void move(final int deltaX, final int deltaY) {
		
	}
}

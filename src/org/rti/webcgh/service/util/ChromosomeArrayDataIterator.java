/*
$Revision: 1.2 $
$Date: 2006-09-07 18:54:53 $

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

package org.rti.webcgh.service.util;

import java.util.Iterator;

import org.rti.webcgh.domain.BioAssay;
import org.rti.webcgh.domain.ChromosomeArrayData;


/**
 * Iterates over <code>ChromosomeArrayData</code>.
 * Shields client classes from the concern over
 * whether such data are in memory or serialized
 * to disk.
 * @author dhall
 *
 */
public abstract class ChromosomeArrayDataIterator {
    
    
    /** Iterator for chromosomes. */
    private final Iterator<Short> chromosomeIterator;
    
    
    /** Bioassay containing in-memory or serialized data. */
    private final BioAssay bioAssay;

    
    // =============================
    //       Constructors
    // =============================
        
    /**
     * Constructor.
     * @param bioAssay Bioassay containing in-memory or serialized data
     * to iterate over
     */
    protected ChromosomeArrayDataIterator(
            final BioAssay bioAssay) {
        this.bioAssay = bioAssay;
        this.chromosomeIterator = bioAssay.getChromosomes().iterator();
    }
    
    // ==================================
    //      Iterator interface
    // ==================================
    
    
    /**
     * Is there a next chromosome array data object?
     * @return T/F
     */
    public final boolean hasNext() {
        return this.chromosomeIterator.hasNext();
    }
    
    
    /**
     * Get next chromosome array data object.
     * @return Next chromosome array data object
     * or <code>null</code> if there is not one
     */
    public final ChromosomeArrayData next() {
        ChromosomeArrayData cad = null;
        if (this.hasNext()) {
            short chromosome = this.chromosomeIterator.next();
            cad = this.getChromosomeArrayData(this.bioAssay, chromosome);
        }
        return cad;
    }
    
    
    // ===============================
    //        Abstract methods
    // ===============================
    
    /**
     * Get chromosome array data.
     * @param bioAssay Bioassay containing data of interest
     * @param chromosome Chromosome number
     * @return Chromosome array data
     */
    protected abstract ChromosomeArrayData getChromosomeArrayData(
    		BioAssay bioAssay, short chromosome);
}
/*
$Revision: 1.3 $
$Date: 2007-12-04 23:06:40 $

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

package org.rti.webgenome.service.dao;

import java.util.Collection;
import java.util.Set;

import org.rti.webgenome.domain.ShoppingCart;

/**
 * Data access interface for <code>ShoppingCart</code>.
 * @author dhall
 *
 */
public interface ShoppingCartDao {
    
    /**
     * Save to persistent storage.
     * @param shoppingCart A shopping cart
     */
    void save(ShoppingCart shoppingCart);
    
    
    /**
     * Load cart with given identifier from persistent storage.
     * @param id Shopping cart identifier
     * @return A shopping cart or null if there is no cart
     * with given id.
     */
    ShoppingCart load(Long id);
    
    
    /**
     * Load cart associated with given user name from
     * persistent storage.
     * @param userName User name
     * @param domain The domain in which the user name applies
     * @return A shopping cart or null if there is not one
     * for given user.
     */
    ShoppingCart load(String userName, String domain);
    
    
    /**
     * Update persistently stored properties of given cart.
     * @param shoppingCart A shopping cart
     */
    void update(ShoppingCart shoppingCart);
    
    
    /**
     * Delete given cart from persistent storage.
     * @param shoppingCart A shopping cart
     */
    void delete(ShoppingCart shoppingCart);

    /**
     * Get names of all image files in shopping
     * cart.
     * @return Names of all image files in shopping
     * cart.
     */
    Collection<String> getAllImageFileNames();
    
    /**
     * Get name of all data files in all shopping carts.
     * @return Names of data files, not absolute paths.
     * These include files containing experimental results,
     * reporters, and plot interactivity object serializations.
     */
    Set<String> getAllDataFileNames();
}
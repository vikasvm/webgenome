/*L
 *  Copyright RTI International
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/webgenome/LICENSE.txt for details.
 */

/*
$Revision: 1.2 $
$Date: 2007-03-29 18:02:01 $


*/

package org.rti.webgenome.core;

import org.rti.webgenome.util.SystemUtils;


/**
 * Exception representing invalid query parameters from
 * an application client.
 * @author dhall
 *
 */
public class InvalidClientQueryParametersException
	extends WebGenomeApplicationException {
	
	/** Serialized version ID. */
	private static final long serialVersionUID = 
		SystemUtils.getLongApplicationProperty("serial.version.uid");

    /**
     * Constructor.
     */
    public InvalidClientQueryParametersException() {
        super();
    }
    
    /**
     * Constructor.
     * @param msg Message
     */
    public InvalidClientQueryParametersException(final String msg) {
        super(msg);
    }
    
    /**
     * Constructor.
     * @param origThrowable Original throwable
     */
    public InvalidClientQueryParametersException(
    		final Throwable origThrowable) {
        super(origThrowable);
    }
    
    /**
     * Constructor.
     * @param msg Message
     * @param origThrowable Original throwable
     */
    public InvalidClientQueryParametersException(final String msg,
    		final Throwable origThrowable) {
        super(msg, origThrowable);
    }
}

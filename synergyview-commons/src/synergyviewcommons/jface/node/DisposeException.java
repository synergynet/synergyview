/**
 * File: DisposeException.java Copyright (c) 2010 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcommons.jface.node;

/**
 * The Class DisposeException.
 * 
 * @author phyo
 */
public class DisposeException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new dispose exception.
     * 
     * @param message
     *            the message
     */
    public DisposeException(String message) {
	super(message);
    }

    /**
     * Instantiates a new dispose exception.
     * 
     * @param message
     *            the message
     * @param e
     *            the e
     */
    public DisposeException(String message, Throwable e) {
	super(message, e);
    }

}

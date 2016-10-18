/**
 * File: ModelPersistenceException.java Copyright (c) 2011 phyo This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.model;

/**
 * The Class ModelPersistenceException.
 * 
 * @author phyo
 */
public class ModelPersistenceException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new model persistence exception.
     * 
     * @param message
     *            the message
     * @param ex
     *            the ex
     */
    public ModelPersistenceException(String message, Exception ex) {
	super(message, ex);
    }
}

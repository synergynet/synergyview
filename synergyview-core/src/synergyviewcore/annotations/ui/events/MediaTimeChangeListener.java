/**
 * File: IMediaTimeChangeListener.java Copyright (c) 2010 phyo This program is
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

package synergyviewcore.annotations.ui.events;

/**
 * The listener interface for receiving mediaTimeChange events. The class that is interested in processing a mediaTimeChange event implements this interface, and the object created with that class is registered with a component using the component's <code>addMediaTimeChangeListener<code> method. When
 * the mediaTimeChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @author phyo
 */
public interface MediaTimeChangeListener {

    /**
     * Play back changed.
     * 
     * @param eventArg
     *            the event arg
     */
    public void playBackChanged(TimeChangeEvent eventArg);
}

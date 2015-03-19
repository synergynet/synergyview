/*
 *  File: TimeBarNowMarker.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.jaret.util.ui.timebars;

import de.jaret.util.date.JaretDate;

/**
 * An extension of the TimeBarMarkerImpl that will always mark the current time. <p/>ATTENTION: not a clean solution by
 * now - demonstration only. Users of this class have to take care of the thread.
 * 
 * @author Peter Kliem
 * @version $Id: TimeBarNowMarker.java 800 2008-12-27 22:27:33Z kliem $
 */
public class TimeBarNowMarker extends TimeBarMarkerImpl {

    Thread _mover;

    public TimeBarNowMarker() {
        super(false, new JaretDate());
        _mover = new Thread(new Mover(), "Mover");
        _mover.start();
    }

    public void stop() {
        _mover.interrupt();
    }

    class Mover implements Runnable {
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            while (true && !Thread.interrupted()) {
                setDate(new JaretDate());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

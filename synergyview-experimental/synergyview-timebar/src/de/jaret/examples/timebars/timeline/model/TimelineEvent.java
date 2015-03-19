/*
 *  File: TimelineEvent.java 
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
package de.jaret.examples.timebars.timeline.model;

import de.jaret.util.date.IntervalImpl;

/**
 * Interval for the timeline example.
 * 
 * @author kliem
 * @version $Id: $
 */
public class TimelineEvent extends IntervalImpl{
    private String _title;
    private String _content;
    private boolean _duration = false;
    private String _color;
    
    public String getTitle() {
        return _title;
    }
    public void setTitle(String title) {
        _title = title;
    }
    public String getContent() {
        return _content;
    }
    public void setContent(String content) {
        _content = content;
    }
    public boolean isDuration() {
        return _duration;
    }
    public void setDuration(boolean duration) {
        _duration = duration;
    }
    
    @Override
    public String toString() {
        return _title;
    }
    public String getColor() {
        return _color;
    }
    public void setColor(String color) {
        _color = color;
    }
    
}

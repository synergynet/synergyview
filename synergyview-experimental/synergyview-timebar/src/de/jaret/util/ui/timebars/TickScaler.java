/*
 *  File: TickScaler.java 
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

/**
 * Simple helper for scaling minor and major tick distances for pixelpersecond value.
 * 
 * @author Peter Kliem
 * @version $Id: TickScaler.java 800 2008-12-27 22:27:33Z kliem $
 */
public class TickScaler {
    public static enum Range {
        MONTH, WEEK, DAY, HOUR
    };

    private static final double _pixPerSecondLimits[] = {1.1574074074074073E-5, 2.3148148148148147E-5,
            8.101851851851852E-5, 2.199074074074074E-4, 0.0008, 0.0015, 0.0025, 0.005, 0.01, 0.012, 0.05, 0.1, 0.5};

    private static final int _majorTickMinutes[] = {24 * 60 * 7 * 4 * 2, 24 * 60 * 7 * 4, 24 * 60 * 7 * 2, 24 * 60 * 7,
            24 * 60, 12 * 60, 360, 240, 120, 60, 15, 5, 2};

    private static final int _minorTickMinutes[] = {24 * 60 * 7 * 4, 24 * 60 * 7 * 2, 24 * 60, 24 * 60, 240, 120, 60,
            60, 30, 15, 5, 1, 1};

    private static final Range _range[] = {Range.MONTH, Range.MONTH, Range.WEEK, Range.WEEK, Range.DAY, Range.HOUR,
            Range.HOUR, Range.HOUR, Range.HOUR, Range.HOUR, Range.HOUR, Range.HOUR, Range.HOUR, Range.HOUR};

    public static int getTickIdx(double pixPerSecond) {
        for (int i = 0; i < _pixPerSecondLimits.length; i++) {
            if (pixPerSecond <= _pixPerSecondLimits[i]) {
                // System.out.println(i+":"+pixPerSecond +" "+
                // _pixPerSecondLimits[i]);
                return i;
            }
            // if (pixPerSecond > _pixPerSecondLimits[i]) {
            // if (i==_pixPerSecondLimits.length-1 || pixPerSecond <
            // _pixPerSecondLimits[i+1]){
            // return i;
            // }
            // }
        }
        return _pixPerSecondLimits.length - 1;
    }

    public static int getMajorTickMinutes(int idx) {
        return _majorTickMinutes[idx];
    }

    public static int getMinorTickMinutes(int idx) {
        return _minorTickMinutes[idx];
    }

    public static Range getRange(int idx) {
        return _range[idx];
    }

}

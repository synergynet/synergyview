/*
 *  File: JaretTimeBarsActionFactory.java 
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
package de.jaret.util.ui.timebars.swt.util.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Factory producing some actions that are useful when using the timebar viewer.
 * 
 * @author Peter Kliem
 * @version $Id: JaretTimeBarsActionFactory.java 160 2007-01-02 22:02:40Z olk $
 */
public class JaretTimeBarsActionFactory {
    public static final String ACTION_SCALETOWEEK = ScaleWeekAction.class.getCanonicalName();
    public static final String ACTION_SCALETOMONTH = ScaleMonthAction.class.getCanonicalName();
    public static final String ACTION_SCALETOYEAR = ScaleYearAction.class.getCanonicalName();
    public static final String ACTION_CENTERSCALE = CenterScaleAction.class.getCanonicalName();
    public static final String ACTION_EXPANDALL = ExpandAllAction.class.getCanonicalName();
    public static final String ACTION_COLLAPSEALL = CollapseAllAction.class.getCanonicalName();
    public static final String ACTION_EXPANDNODE = ExpandNodeAction.class.getCanonicalName();
    public static final String ACTION_COLLAPSENODE = CollapseNodeAction.class.getCanonicalName();

    private final TimeBarViewer _viewer;
    private final TimeBarViewerDelegate _delegate;
    private Map<String, Action> _actionMap;

    public JaretTimeBarsActionFactory(TimeBarViewer viewer, TimeBarViewerDelegate delegate) {
        _viewer = viewer;
        _delegate = delegate;
    }

    /**
     * Create or return action denoted by constant.
     * 
     * @param name name of the action as one of the constants defined herin.
     * @return action or null if there is no action for the name
     */
    public Action createStdAction(String name) {
        if (_actionMap == null) {
            _actionMap = new HashMap<String, Action>();
        }
        Action result = _actionMap.get(name);
        if (result != null) {
            return result;
        }
        if (name.equals(ACTION_SCALETOWEEK)) {
            result = new ScaleWeekAction(_viewer, _delegate);
        } else if (name.equals(ACTION_SCALETOMONTH)) {
            result = new ScaleMonthAction(_viewer, _delegate);
        } else if (name.equals(ACTION_SCALETOYEAR)) {
            result = new ScaleYearAction(_viewer, _delegate);
        } else if (name.equals(ACTION_CENTERSCALE)) {
            result = new CenterScaleAction(_viewer, _delegate);
        } else if (name.equals(ACTION_COLLAPSEALL)) {
            result = new CollapseAllAction(_viewer, _delegate);
        } else if (name.equals(ACTION_EXPANDALL)) {
            result = new ExpandAllAction(_viewer, _delegate);
        } else if (name.equals(ACTION_COLLAPSENODE)) {
            result = new CollapseNodeAction(_viewer, _delegate);
        } else if (name.equals(ACTION_EXPANDNODE)) {
            result = new ExpandNodeAction(_viewer, _delegate);
        }
        if (result != null) {
            _actionMap.put(name, result);
        }
        return result;
    }

}

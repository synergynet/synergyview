/*
 *  File: CollapseAllAction.java 
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

import org.eclipse.jface.action.Action;

import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Action causing all nodes to collapse.
 * 
 * @author Peter Kliem
 * @version $Id: CollapseAllAction.java 160 2007-01-02 22:02:40Z olk $
 */
public class CollapseAllAction extends Action {
    /** the timebarviewer th action has been constructed for. */
    private final TimeBarViewer _viewer;
    /** the delegate of the timebarviewer. */
    private final TimeBarViewerDelegate _delegate;

    /**
     * Constructor.
     * 
     * @param viewer timebar viewer
     * @param delegate delegate of the viewer.
     */
    public CollapseAllAction(TimeBarViewer viewer, TimeBarViewerDelegate delegate) {
        _viewer = viewer;
        _delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (_delegate.getHierarchicalViewState() != null) {
            TimeBarNode root = _delegate.getHierarchicalModel().getRootNode();
            _delegate.getHierarchicalViewState().setExpandedRecursive(root, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return "Collapse all";
    }
}

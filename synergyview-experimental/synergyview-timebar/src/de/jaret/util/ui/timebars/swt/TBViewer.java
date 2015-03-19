/*
 *  File: TBViewer.java 
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
package de.jaret.util.ui.timebars.swt;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Jface StructuredViewer for the TimeBarViewer. Naming is odd due to historical reasons. This is not yet a complete
 * implementation.
 * 
 * @author Peter Kliem
 * @version $Id: TBViewer.java 800 2008-12-27 22:27:33Z kliem $
 */
public class TBViewer extends StructuredViewer {
    /** the underlying timebarviewer. */
    protected TimeBarViewer _tbv;

    /** this pointer. */
    protected TBViewer _this;

    /**
     * Constructor.
     * 
     * @param tbv timebarviewer that should be wrapped
     */
    public TBViewer(TimeBarViewer tbv) {
        _tbv = tbv;
        _this = this;
        super.hookControl(_tbv);
        _tbv.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
                DoubleClickEvent event = new DoubleClickEvent(_this, getSelection());
                fireDoubleClick(event);
            }

            public void mouseDown(MouseEvent e) {
            }

            public void mouseUp(MouseEvent e) {
            }

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        return _tbv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInput() {
        return _tbv.getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISelection getSelection() {
        return _tbv.getSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh() {
        _tbv.redraw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(ISelection selection, boolean reveal) {
        // TODO reveal
        _tbv.setSelection(selection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Widget doFindInputItem(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Widget doFindItem(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUpdateItem(Widget item, Object element, boolean fullMap) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List getSelectionFromWidget() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalRefresh(Object element) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reveal(Object element) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSelectionToWidget(List l, boolean reveal) {
        // TODO Auto-generated method stub

    }

}

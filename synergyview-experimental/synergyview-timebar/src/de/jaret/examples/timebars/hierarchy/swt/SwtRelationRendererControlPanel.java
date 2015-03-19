/*
 *  File: RelationRendererControlPanel.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.hierarchy.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.RelationRenderer;

/**
 * Settings for the relation renderer.
 * 
 * @author Peter Kliem
 * @version $Id: SwtRelationRendererControlPanel.java 758 2008-05-02 20:23:44Z kliem $
 */
public class SwtRelationRendererControlPanel extends Composite {

    private TimeBarViewer _tbv;

    public SwtRelationRendererControlPanel(Composite parent, int style, TimeBarViewer tbv) {
        super(parent, style);
        _tbv = tbv;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(Composite panel) {
        panel.setLayout(new RowLayout());

        // check whether this has been initialized and return otherwise
        if (_tbv == null) {
            return;
        }

        final RelationRenderer renderer = (RelationRenderer) _tbv.getRelationRenderer();

        Label l = new Label(this, SWT.NULL);
        l.setText("Linewidth");

        final Scale lineWidthScale = new Scale(this, SWT.HORIZONTAL);
        lineWidthScale.setMaximum(20);
        lineWidthScale.setMinimum(1);
        lineWidthScale.setSelection(renderer.getLineWidth());
        lineWidthScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = lineWidthScale.getSelection();
                renderer.setLineWidth(val);
                _tbv.redraw();
            }
        });

        l = new Label(this, SWT.NULL);
        l.setText("Arrowsize");

        final Scale arrowSizeScale = new Scale(this, SWT.HORIZONTAL);
        arrowSizeScale.setMaximum(30);
        arrowSizeScale.setMinimum(1);
        arrowSizeScale.setSelection(renderer.getArrowSize());
        arrowSizeScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = arrowSizeScale.getSelection();
                renderer.setArrowSize(val);
                _tbv.redraw();
            }
        });

    }

}

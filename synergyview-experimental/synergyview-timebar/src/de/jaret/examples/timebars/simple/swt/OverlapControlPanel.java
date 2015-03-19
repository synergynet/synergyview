/*
 *  File: SwtControlPanel.java 
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
package de.jaret.examples.timebars.simple.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.jaret.examples.timebars.simple.swt.renderer.CheckBoxHeaderRenderer;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.AbstractGridRenderer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultHeaderRenderer;

/**
 * Special setings for the swt overlap example.
 * 
 * @author Peter Kliem
 * @version $Id: OverlapControlPanel.java 810 2009-01-08 22:28:57Z kliem $
 */
public class OverlapControlPanel extends Composite {

    private TimeBarViewer _tbv;

    public OverlapControlPanel(Composite arg0, int arg1, TimeBarViewer tbv) {
        super(arg0, arg1);
        _tbv = tbv;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(OverlapControlPanel panel) {
        panel.setLayout(new RowLayout());

        // check whether this has been initialized nd return otherwise
        if (_tbv == null) {
            return;
        }

        final Button toggleSelectionMode = new Button(this, SWT.CHECK);
        toggleSelectionMode.setText("Toggle row selection mode");
        toggleSelectionMode.setSelection(_tbv.getSelectionModel().getRowSelectionToggleMode());
        toggleSelectionMode.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.getSelectionModel().setRowSelectionToggleMode(toggleSelectionMode.getSelection());
            }
        });

        Label l = new Label(this, SWT.NULL);
        l.setText("Header renderer");

        final CCombo headerRendererCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        headerRendererCombo.setItems(new String[] {"Default", "CheckBoxRenderer"});
        headerRendererCombo.select(0);
        headerRendererCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (headerRendererCombo.getSelectionIndex() == 0) {
                    _tbv.setHeaderRenderer(new DefaultHeaderRenderer());
                } else if (headerRendererCombo.getSelectionIndex() == 1) {
                    _tbv.setHeaderRenderer(new CheckBoxHeaderRenderer());
                }
            }
        });
        
        l = new Label(this, SWT.NULL);
        l.setText("RowSelectAlpha");

        final Scale selectionAlpha = new Scale(this, SWT.HORIZONTAL);
        selectionAlpha.setMaximum(255);
        selectionAlpha.setMinimum(0);
        final AbstractGridRenderer gridRenderer = (AbstractGridRenderer)_tbv.getGridRenderer();
        selectionAlpha.setSelection(gridRenderer.getRowSelectAlpha());
        selectionAlpha.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = selectionAlpha.getSelection();
                gridRenderer.setRowSelectAlpha(val);
                _tbv.redraw();
            }

        });
//        RowData rd = new RowData(800, 40);
//        selectionAlpha.setLayoutData(rd);

        final Button uniformHeight = new Button(this, SWT.CHECK);
        uniformHeight.setText("Uniform height");
        uniformHeight.setSelection(_tbv.getUseUniformHeight());
        uniformHeight.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setUseUniformHeight(uniformHeight.getSelection());
            }
        });
        
        
    }
}

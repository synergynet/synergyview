/*
 *  File: SwtHierarchyControlPanel.java 
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
package de.jaret.examples.timebars.hierarchy.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.jaret.util.ui.timebars.model.IIntervalRelation;
import de.jaret.util.ui.timebars.model.IRelationalInterval;
import de.jaret.util.ui.timebars.model.IntervalRelation;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;
import de.jaret.util.ui.timebars.swt.renderer.DefaultHierarchyRenderer;

/**
 * Hierarhy settings for the hierachy example.
 * 
 * @author Peter Kliem
 * @version $Id: SwtHierarchyControlPanel.java 801 2008-12-27 22:44:54Z kliem $
 */
public class SwtHierarchyControlPanel extends Composite {

    private TimeBarViewer _tbv;

    public SwtHierarchyControlPanel(Composite parent, int style, TimeBarViewer tbv) {
        super(parent, style);
        _tbv = tbv;
        createControls(this);
    }

    /**
     * @param panel
     */
    private void createControls(SwtHierarchyControlPanel panel) {
        panel.setLayout(new RowLayout());

        // check whether this has been initialized nd return otherwise
        if (_tbv == null) {
            return; 
        }

        final DefaultHierarchyRenderer renderer = (DefaultHierarchyRenderer) _tbv.getHierarchyRenderer();

        Label l = new Label(this, SWT.NULL);
        l.setText("Levelwidth:");

        final Scale levelWidthScale = new Scale(this, SWT.HORIZONTAL);
        levelWidthScale.setMaximum(300);
        levelWidthScale.setMinimum(5);
        levelWidthScale.setSelection(renderer.getLevelWidth());
        levelWidthScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = levelWidthScale.getSelection();
                renderer.setLevelWidth(val);
                _tbv.redraw();
            }
        });

        final Button fixedLevelCheck = new Button(this, SWT.CHECK);
        fixedLevelCheck.setText("fixed level width");
        fixedLevelCheck.setSelection(renderer.getFixedLevelWidth());
        fixedLevelCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                renderer.setFixedLevelWidth(fixedLevelCheck.getSelection());
                _tbv.redraw();
            }
        });

        final Button drawIconsCheck = new Button(this, SWT.CHECK);
        drawIconsCheck.setText("Draw icons");
        drawIconsCheck.setSelection(renderer.getDrawIcons());
        drawIconsCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                renderer.setDrawIcons(drawIconsCheck.getSelection());
                _tbv.redraw();
            }
        });

        final Button drawLabelsCheck = new Button(this, SWT.CHECK);
        drawLabelsCheck.setText("Draw labels");
        drawLabelsCheck.setSelection(renderer.getDrawLabels());
        drawLabelsCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                renderer.setDrawLabels(drawLabelsCheck.getSelection());
                _tbv.redraw();
            }
        });

        final Button symbolCheck = new Button(this, SWT.CHECK);
        symbolCheck.setText("Default symbols");
        symbolCheck.setSelection(false);
        symbolCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (symbolCheck.getSelection()) {
                    renderer.setRscNames(null, null, null);
                    renderer.setSize(12);
                } else {
                    renderer.setRscNames("/de/jaret/examples/timebars/hierarchy/swt/collapsed.png",
                            "/de/jaret/examples/timebars/hierarchy/swt/expanded.png",
                            "/de/jaret/examples/timebars/hierarchy/swt/leaf.png");
                    renderer.setSize(16);
                }
                _tbv.redraw();
            }
        });


        final Button drawTreeCheck = new Button(this, SWT.CHECK);
        drawTreeCheck.setText("Draw tree");
        drawTreeCheck.setSelection(renderer.getDrawTreeLines());
        drawTreeCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                renderer.setDrawTreeLines(drawTreeCheck.getSelection());
                _tbv.redraw();
            }
        });

        final Button hideRootCheck = new Button(this, SWT.CHECK);
        hideRootCheck.setText("hide root");
        hideRootCheck.setSelection(_tbv.getHideRoot());
        hideRootCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                _tbv.setHideRoot(hideRootCheck.getSelection());
            //    _tbv.redraw();
            }
        });
        
        l = new Label(this, SWT.NULL);
        l.setText("Hierarchy width:");

        final Scale hierarchyWidthScale = new Scale(this, SWT.HORIZONTAL);
        hierarchyWidthScale.setMaximum(400);
        hierarchyWidthScale.setMinimum(0);
        hierarchyWidthScale.setSelection(_tbv.getHierarchyWidth());
        hierarchyWidthScale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent ev) {
                int val = hierarchyWidthScale.getSelection();
                _tbv.setHierarchyWidth(val);
                _tbv.redraw();
            }
        });
        
        
        // button adding relation 
        // enabled whenever 2 intervals are selected
        final Button addRelation = new Button(this, SWT.PUSH);
        addRelation.setText("add Relation");
        addRelation.setEnabled(_tbv.getSelectionModel().getSelectedIntervals().size() == 2);
        addRelation.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                // exactly two intervals are selected
                // these are IRelationalIntervals
                IRelationalInterval i1 = (IRelationalInterval) _tbv.getSelectionModel().getSelectedIntervals().get(0);
                IRelationalInterval i2 = (IRelationalInterval) _tbv.getSelectionModel().getSelectedIntervals().get(1);
                IIntervalRelation relation = new IntervalRelation(i1,i2);
                i1.addRelation(relation);
                i2.addRelation(relation);
            }
        });

        _tbv.getSelectionModel().addTimeBarSelectionListener(new TimeBarSelectionListener() {

            public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
                addRelation.setEnabled(selectionModel.getSelectedIntervals().size() == 2 &&
                        selectionModel.getSelectedIntervals().get(0) instanceof IRelationalInterval &&
                        selectionModel.getSelectedIntervals().get(1) instanceof IRelationalInterval);
            }

            public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
                addRelation.setEnabled(selectionModel.getSelectedIntervals().size() == 2 &&
                        selectionModel.getSelectedIntervals().get(0) instanceof IRelationalInterval &&
                        selectionModel.getSelectedIntervals().get(1) instanceof IRelationalInterval);
            }

            public void selectionChanged(TimeBarSelectionModel selectionModel) {
                addRelation.setEnabled(selectionModel.getSelectedIntervals().size() == 2 &&
                        selectionModel.getSelectedIntervals().get(0) instanceof IRelationalInterval &&
                        selectionModel.getSelectedIntervals().get(1) instanceof IRelationalInterval);
            }
        });
        
        
        // button deleting relations 
        // enabled whenever relations are selected
        final Button delRelation = new Button(this, SWT.PUSH);
        delRelation.setText("del Relation");
        delRelation.setEnabled(_tbv.getSelectionModel().getSelectedIntervals().size() == 2);
        delRelation.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                for (IIntervalRelation relation : _tbv.getSelectionModel().getSelectedRelations()) {
                    relation.getStartInterval().removeRelation(relation);
                    relation.getEndInterval().removeRelation(relation);
                }
                _tbv.redraw();
            }
        });

     

        l = new Label(this, SWT.NULL);
        l.setText("Relation type");

        
        // combo changing the type of all selected relations
        final CCombo relationTypeCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        relationTypeCombo.setItems(new String[] {"BEGIN_END", "BEGIN_BEGIN", "END_BEGIN", "END_END"});
        relationTypeCombo.select(0);
        relationTypeCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                String selected = relationTypeCombo.getText();
                IIntervalRelation.Type type = IIntervalRelation.Type.valueOf(selected);
                for (IIntervalRelation relation : _tbv.getSelectionModel().getSelectedRelations()) {
                    relation.setType(type);
                }
                _tbv.redraw();
            }
        });
        
        l = new Label(this, SWT.NULL);
        l.setText("Relation direction");
        // combo changing the direction of all selected relations
        final CCombo relationDirectionCombo = new CCombo(this, SWT.READ_ONLY | SWT.BORDER);
        relationDirectionCombo.setItems(new String[] {"BI", "NONE", "FORWARD", "BACK"});
        relationDirectionCombo.select(2);
        relationDirectionCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                String selected = relationDirectionCombo.getText();
                IIntervalRelation.Direction dir = IIntervalRelation.Direction.valueOf(selected);
                for (IIntervalRelation relation : _tbv.getSelectionModel().getSelectedRelations()) {
                    relation.setDirection(dir);
                }
                _tbv.redraw();
            }
        });
        
        
        
        // selection listener for enabling/disabling relation buttons and combos
        _tbv.getSelectionModel().addTimeBarSelectionListener(new TimeBarSelectionListener() {

            public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
                delRelation.setEnabled(selectionModel.hasRelationSelection());
                relationTypeCombo.setEnabled(selectionModel.hasRelationSelection());
                relationDirectionCombo.setEnabled(selectionModel.hasRelationSelection());
            }

            public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
                delRelation.setEnabled(selectionModel.hasRelationSelection());
                relationTypeCombo.setEnabled(selectionModel.hasRelationSelection());
                relationDirectionCombo.setEnabled(selectionModel.hasRelationSelection());
            }

            public void selectionChanged(TimeBarSelectionModel selectionModel) {
                delRelation.setEnabled(selectionModel.hasRelationSelection());
                relationTypeCombo.setEnabled(selectionModel.hasRelationSelection());
                relationDirectionCombo.setEnabled(selectionModel.hasRelationSelection());
            }
        });
        
        // check setFirstRowDisplayed
//        final Button scrolltolast = new Button(this, SWT.PUSH);
//        scrolltolast.setText("scroll to last row ");
//        scrolltolast.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent arg0) {
//            // last model row = 
//            TimeBarRow lastrow = _tbv.getModel().getRow(_tbv.getModel().getRowCount()-1);
//            _tbv.setFirstRowDisplayed(lastrow);
//            }
//        });
    
        
    
        
        
//        final Scale horRatio = new Scale(this, SWT.HORIZONTAL);
//        horRatio.setMaximum(100);
//        horRatio.setMinimum(0);
//        final Scale verRatio = new Scale(this, SWT.HORIZONTAL);
//        verRatio.setMaximum(100);
//        verRatio.setMinimum(0);
//        final Button scrollRatio = new Button(this, SWT.PUSH);
//        scrollRatio.setText("scroll focussed intervall to ratio ");
//        scrollRatio.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent arg0) {
//                Interval interval = _tbv.getFocussedInterval();
//                double hor = (double)horRatio.getSelection()/100.0;
//                double ver = (double)verRatio.getSelection()/100.0;
//                System.out.println("hor/ver interval:"+hor+"/"+ver+" "+interval);
//                _tbv.scrollIntervalToVisible(interval, hor, ver);
//            }
//        });
        
        
    }

}

/*
 *  File: SwingHierarchy.java 
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
package de.jaret.examples.timebars.hierarchy.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.jaret.examples.timebars.hierarchy.model.ModelCreator;
import de.jaret.examples.timebars.pdi.swing.ControlPanel1;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.AddingTimeBarRowModel;
import de.jaret.util.ui.timebars.model.HierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultHierarchyRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultRelationRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTitleRenderer;

/**
 * Simple hierarchical view Swing version. Scaling, manipulating the intervals, tree structure, draggable marker.
 * 
 * @author Peter Kliem
 * @version $Id: SwingHierarchy.java 798 2008-12-27 21:51:27Z kliem $
 */
public class SwingHierarchy {

    public static void main(String[] args) {
        // set up the frame
        JFrame f = new JFrame(SwingHierarchy.class.getName());
        f.setSize(1000, 600);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // create the model
        HierarchicalTimeBarModel model = ModelCreator.createModel(2, 5);

        TimeBarViewer _tbv = new TimeBarViewer();
        _tbv.setName("Hierarchy");
        _tbv.setModel(model);
        _tbv.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
        _tbv.setHierarchyRenderer(new DefaultHierarchyRenderer());
        _tbv.setHierarchyWidth(100);
        _tbv.setYAxisWidth(100);
        // allow all interval modifications
        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        // register additional renderer for the erged intervals of the addind row model
        _tbv.registerTimeBarRenderer(AddingTimeBarRowModel.MergedInterval.class, new SumRenderer());

        // set the default title renderer
        _tbv.setTitleRenderer(new DefaultTitleRenderer());
        
        // setup relation rendering
        _tbv.setRelationRenderer(new DefaultRelationRenderer());
        
        
        
        // add a marker
        TimeBarMarkerImpl tm = new TimeBarMarkerImpl(true, new JaretDate().advanceDays(1));
        tm.setDescription("Timebarmarker");
        _tbv.addMarker(tm);

        f.getContentPane().add(_tbv, BorderLayout.CENTER);

        // expand all nodes
        TimeBarNode root = model.getRootNode();
        _tbv.getHierarchicalViewState().setExpandedRecursive(root, true);

        
        
        // add the control panel
        ControlPanel1 cp = new ControlPanel1(_tbv, null, true);
        f.getContentPane().add(cp, BorderLayout.NORTH);

        f.setVisible(true);
    }
}

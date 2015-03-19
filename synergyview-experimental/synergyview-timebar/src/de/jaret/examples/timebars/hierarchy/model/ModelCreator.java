/*
 *  File: ModelCreator.java 
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
package de.jaret.examples.timebars.hierarchy.model;

import java.util.Random;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.AddingTimeBarNode;
import de.jaret.util.ui.timebars.model.DefaultHierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.HierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.IRelationalInterval;
import de.jaret.util.ui.timebars.model.IntervalRelation;
import de.jaret.util.ui.timebars.model.RelationalInterval;
import de.jaret.util.ui.timebars.model.TimeBarNode;

/**
 * Simple model creator creating a hierachcial model and adds some dependencies.
 * 
 * @author kliem
 * @version $Id: ModelCreator.java 814 2009-01-11 20:57:24Z kliem $
 */
public class ModelCreator {

    static final boolean LONGMODEL = false;

    static Random _random = new Random(12345);

    public static HierarchicalTimeBarModel createModel(int maxDepth, int height) {

        DefaultRowHeader header = new DefaultRowHeader("root");
        TimeBarNode node;// = new DefaultTimeBarNode(header);
        node = new AddingTimeBarNode(header);

        for (int i = 0; i < height; i++) {
            node.addNode(createNode(i, node, maxDepth));
        }

        HierarchicalTimeBarModel model = new DefaultHierarchicalTimeBarModel(node);

        return model;

    }

    private static TimeBarNode createNode(int pos, TimeBarNode parent, int depthToGo) {

        DefaultRowHeader header = new DefaultRowHeader(parent.getRowHeader().getLabel() + "." + pos);

        TimeBarNode node = null;

        if (depthToGo == 0) {
            node = new DemoTimeBarNode(header);
            ((DemoTimeBarNode) node).setText("Text " + header.getLabel());
            ((DemoTimeBarNode) node).setType(_random.nextDouble() > 0.5);

            RelationalInterval interval = new RelationalInterval();
            JaretDate d = new JaretDate();
            d.advanceHours(_random.nextDouble() * 100);
            interval.setBegin(d);
            d = d.copy();
            d.advanceHours(_random.nextDouble() * 10);
            interval.setEnd(d);
            ((DefaultTimeBarNode) node).addInterval(interval);
            // add a second interval on the first row
            if (pos == 0) {
                RelationalInterval interval2 = new RelationalInterval();
                if (!LONGMODEL) {
                    d = interval.getBegin().copy().advanceHours(10);
                } else {
                    d = interval.getBegin().copy().advanceDays(200);
                }
                interval2.setBegin(d);
                if (!LONGMODEL) {
                    d = interval.getEnd().copy().advanceHours(10);
                } else {
                    d = interval.getEnd().copy().advanceDays(200);
                }
                interval2.setEnd(d);
                ((DefaultTimeBarNode) node).addInterval(interval2);

            }

        } else {
            node = new AddingTimeBarNode(header);
        }
        if (depthToGo > 0) {
            int height = (int) (_random.nextDouble() * 5) + 1;
            DemoTimeBarNode last = null;
            for (int i = 0; i < height; i++) {
                TimeBarNode n = createNode(i, node, depthToGo - 1);
                node.addNode(n);
                if (n instanceof DemoTimeBarNode) {
                    if (last != null) {
                        IRelationalInterval rInterval1 = (IRelationalInterval) last.getIntervals().get(0);
                        IRelationalInterval rInterval2 = (IRelationalInterval) n.getIntervals().get(0);
                        rInterval1.addRelation(new IntervalRelation(rInterval1, rInterval2));
                    }
                    last = (DemoTimeBarNode) n;
                }
            }
        }

        return node;
    }

}

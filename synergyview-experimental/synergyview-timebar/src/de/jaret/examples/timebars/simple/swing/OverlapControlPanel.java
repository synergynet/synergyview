/*
 *  File: OverlapControlPanel.java 
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
package de.jaret.examples.timebars.simple.swing;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultGapRenderer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultTimeScaleRenderer;

/**
 * Control panel for the overlap example.
 * 
 * @author Peter Kliem
 * @version $Id: OverlapControlPanel.java 795 2008-12-26 23:23:30Z kliem $
 */
@SuppressWarnings("serial")
public class OverlapControlPanel extends JPanel {
    TimeBarViewer _viewer;

    public OverlapControlPanel(TimeBarViewer viewer) {
        _viewer = viewer;
        setLayout(new GridLayout(5,7));
        createControls();
    }

    /**
     * 
     */
    private void createControls() {
        JLabel label = new JLabel("pps");
        add(label);
        final JSlider timeScaleSlider = new JSlider(500, 5500);
        timeScaleSlider.setValue((int) (_viewer.getPixelPerSecond() * 60.0 * 60.0 * 24));
        timeScaleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double pixPerSecond = (double) timeScaleSlider.getValue() / (24.0 * 60 * 60);
                _viewer.setPixelPerSecond(pixPerSecond);
            }
        });
        add(timeScaleSlider);

        label = new JLabel("rowHeight");
        add(label);

        final JSlider rowHeigthSlider = new JSlider(10, 300);
        rowHeigthSlider.setValue(_viewer.getRowHeight());
        rowHeigthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _viewer.setRowHeight(rowHeigthSlider.getValue());
            }
        });
        add(rowHeigthSlider);

        final JCheckBox gapCheck = new JCheckBox("GapRenderer");
        gapCheck.setSelected(_viewer.getGapRenderer() != null);
        gapCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gapCheck.isSelected()) {
                    _viewer.setGapRenderer(new DefaultGapRenderer());
                } else {
                    _viewer.setGapRenderer(null);
                }
            }
        });
        add(gapCheck);

        final JCheckBox optScrollingCheck = new JCheckBox("Optimize scrolling");
        optScrollingCheck.setSelected(_viewer.getOptimizeScrolling());
        optScrollingCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setOptimizeScrolling(optScrollingCheck.isSelected());
            }
        });
        add(optScrollingCheck);

        label = new JLabel("time scale position");
        add(label);
        final JComboBox timeScalePosCombo = new JComboBox();
        timeScalePosCombo.addItem("top");
        timeScalePosCombo.addItem("bottom");
        timeScalePosCombo.addItem("none");
        timeScalePosCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (timeScalePosCombo.getSelectedItem().equals("top")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
                } else if (timeScalePosCombo.getSelectedItem().equals("bottom")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM);
                } else if (timeScalePosCombo.getSelectedItem().equals("none")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_NONE);
                }
            }

        });
        add(timeScalePosCombo);

        label = new JLabel("orientation");
        add(label);
        final JComboBox orientationCombo = new JComboBox();
        orientationCombo.addItem("horizontal");
        orientationCombo.addItem("vertical");
        orientationCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (orientationCombo.getSelectedItem().equals("horizontal")) {
                    _viewer.setOrientation(TimeBarViewerInterface.Orientation.HORIZONTAL);
                } else if (orientationCombo.getSelectedItem().equals("vertical")) {
                    _viewer.setOrientation(TimeBarViewerInterface.Orientation.VERTICAL);
                }
            }
        });
        add(orientationCombo);

        final JCheckBox boxTSRCheck = new JCheckBox("BoxTimeScaleRenderer");
        boxTSRCheck.setSelected(_viewer.getTimeScaleRenderer() instanceof BoxTimeScaleRenderer);
        boxTSRCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setTimeScaleRenderer(boxTSRCheck.isSelected()? new BoxTimeScaleRenderer():new DefaultTimeScaleRenderer());
            }
        });
        add(boxTSRCheck);

    }

}
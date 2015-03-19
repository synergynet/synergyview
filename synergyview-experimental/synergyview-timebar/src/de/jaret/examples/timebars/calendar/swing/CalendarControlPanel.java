/*
 *  File: CalendarControlPanel.java 
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
package de.jaret.examples.timebars.calendar.swing;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.examples.timebars.calendar.model.CalendarModel;
import de.jaret.examples.timebars.calendar.model.Day;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.IRowHeightStrategy;
import de.jaret.util.ui.timebars.model.ITimeBarViewState;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.DefaultGapRenderer;

/**
 * Control panel for the calendar example.
 * 
 * @author Peter Kliem
 * @version $Id: CalendarControlPanel.java 533 2007-08-14 22:26:11Z olk $
 */
@SuppressWarnings("serial")
public class CalendarControlPanel extends JPanel {
    TimeBarViewer _viewer;

    public CalendarControlPanel(TimeBarViewer viewer) {
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
        timeScalePosCombo.addItem("left");
        timeScalePosCombo.addItem("right");
        timeScalePosCombo.addItem("none");
        timeScalePosCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (timeScalePosCombo.getSelectedItem().equals("left")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
                } else if (timeScalePosCombo.getSelectedItem().equals("right")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM);
                } else if (timeScalePosCombo.getSelectedItem().equals("none")) {
                    _viewer.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_NONE);
                }
            }

        });
        add(timeScalePosCombo);

        JPanel rScalePanel = new JPanel();
        add(rScalePanel);
        rScalePanel.setLayout(new FlowLayout());
        
        final JButton noRowScaling = new JButton();
        noRowScaling.setText("noRowScaling");
        noRowScaling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setAutoScaleRows(-1);
            }
        });
        rScalePanel.add(noRowScaling);
        
        final JButton oneRowScaling = new JButton();
        oneRowScaling.setText("1Day");
        oneRowScaling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setAutoScaleRows(1);
            }
        });
        rScalePanel.add(oneRowScaling);
        
        final JButton fiveRowScaling = new JButton();
        fiveRowScaling.setText("5days");
        fiveRowScaling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setAutoScaleRows(5);
            }
        });
        rScalePanel.add(fiveRowScaling);

        final JButton fourteenRowScaling = new JButton();
        fourteenRowScaling.setText("14Days");
        fourteenRowScaling.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.setAutoScaleRows(14);
            }
        });
        rScalePanel.add(fourteenRowScaling);

        final JButton scalingStrategy = new JButton();
        scalingStrategy.setText("Add strategy for scaling weekends");
        scalingStrategy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _viewer.getTimeBarViewState().setUseVariableRowHeights(true);
                _viewer.setAutoScaleRows(-1);
         _viewer.getTimeBarViewState().setRowHeightStrategy(new IRowHeightStrategy() {

                    public int calculateRowHeight(TimeBarViewerDelegate delegate, ITimeBarViewState timeBarViewState,
                            TimeBarRow row) {
                        Day day = (Day)row;
                        if (day.getDayDate().isWeekendDay()) {
                            return timeBarViewState.getDefaultRowHeight()/2;
                        } else {
                            return timeBarViewState.getDefaultRowHeight();
                        }
                    }

                    public boolean overrideDefault() {
                        return true;
                    }
                    
                });
            }
        });
        rScalePanel.add(scalingStrategy);

        
        
        final JCheckBox varScaleCheck = new JCheckBox("Use variable scale");
        varScaleCheck.setSelected(false);
        varScaleCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                if (varScaleCheck.isSelected()) {
                    _viewer.setVariableXScale(true);
                    if (_viewer.getPpsRow().getIntervals().size() == 0) {
                        DefaultTimeBarNode scaleRow = (DefaultTimeBarNode) _viewer.getPpsRow();
                        PPSInterval i = new PPSInterval(_viewer.getPixelPerSecond()/2);
                        i.setBegin(CalendarModel.BASEDATE.copy().setTime(0, 0, 0));
                        i.setEnd(CalendarModel.BASEDATE.copy().setTime(8, 0, 0));
                        scaleRow.addInterval(i);
                        i = new PPSInterval(_viewer.getPixelPerSecond()/2);
                        i.setBegin(CalendarModel.BASEDATE.copy().setTime(18, 0, 0));
                        i.setEnd(CalendarModel.BASEDATE.copy().setTime(23, 59, 59));
                        scaleRow.addInterval(i);
                    }
                } else {
                    _viewer.setVariableXScale(false);
                }
            }
        });
        add(varScaleCheck);

        
    }

}
/*
 *  File: SwingOverlapExample.java 
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

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.jaret.examples.timebars.simple.model.ModelCreator;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;

/**
 * Swing: the swing version of the overlap example (without drag and drop).
 * 
 * @author Peter Kliem
 * @version $Id: SwingTimeBarExample.java 202 2007-01-15 22:00:02Z olk $
 */
public class SwingOverlapExample {
	static TimeBarViewer _tbv;
	
    public static void main(String[] args) {
    	JFrame f = new JFrame(SwingOverlapExample.class.getName());
        f.setSize(800, 500);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        final TimeBarModel model = ModelCreator.createModel();
        _tbv = new TimeBarViewer(model);

        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        _tbv.setPixelPerSecond(0.05);
        _tbv.setDrawRowGrid(true);
        
        _tbv.setDrawOverlapping(false);
        _tbv.setSelectionDelta(6);
        _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
        
        // Box tsr with DST correction
//        BoxTimeScaleRenderer btsr = new BoxTimeScaleRenderer();
//        btsr.setCorrectDST(true);
//        _tbv.setTimeScaleRenderer(btsr);
        
        
        f.getContentPane().add(_tbv, BorderLayout.CENTER);

        f.getContentPane().add(new OverlapControlPanel(_tbv), BorderLayout.SOUTH);
        
        
        // Reproduce overflow bug
//        JPanel bPanel = new JPanel();
//        JButton button=new JButton("rem all");
//        bPanel.add(button);
//        f.getContentPane().add(bPanel, BorderLayout.WEST);
//        button.addActionListener(new ActionListener() {
//			
//			public void actionPerformed(ActionEvent e) {				
//				DefaultTimeBarModel m = (DefaultTimeBarModel)model;
//				for(int i=0;i<m.getRowCount();i++) {
//					DefaultTimeBarRowModel rm = (DefaultTimeBarRowModel) m.getRow(i);
//					List<Interval> intervals = new ArrayList<Interval>(rm.getIntervals());
//					for (Interval interval : intervals) {
//						rm.remInterval(interval);
//									}
//				}
//				
//			}
//		});
        
        
        // export the viewer to an image
//        
//        JPanel bPanel = new JPanel();
//        JButton button=new JButton("export image");
//        bPanel.add(button);
//        f.getContentPane().add(bPanel, BorderLayout.WEST);
//        button.addActionListener(new ActionListener() {
//			
//			public void actionPerformed(ActionEvent e) {				
//				DefaultTimeBarModel m = (DefaultTimeBarModel)model;
//
//				BufferedImage bi = new BufferedImage(2000, 2000, BufferedImage.TYPE_4BYTE_ABGR);
//				TimeBarViewer viewer = new TimeBarViewer(model);
//				viewer.setVisible(true);
//				viewer.setBounds(0, 0, 2000, 2000);
//				viewer.doLayout();
//				viewer.printAll(bi.getGraphics());
//				try {
//				    File outputfile = new File("saved.png");
//				    ImageIO.write(bi, "png", outputfile);
//				    
//				} catch (IOException ex) {
//				    ex.printStackTrace();
//				}
//			}
//		});
        
        f.setVisible(true);


    }
}

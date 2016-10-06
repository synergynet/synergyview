/*
 *  File: SwingExampleLauncher.java 
 *  Copyright (c) 2004-2009  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import de.jaret.examples.timebars.eventmonitoring.swing.EventMonitoringExample;
import de.jaret.examples.timebars.events.swing.SwingEventExample;
import de.jaret.examples.timebars.hierarchy.swing.SwingHierarchy;
import de.jaret.examples.timebars.simple.swing.SwingOverlapExample;

/**
 * Simple launcher for the swing examples to be used with OS X. The launcher should be started by ANT to avoid threading problems
 * that prevent mixed SWT and Swing code to work properly under OS X.
 * 
 * @author kliem
 * @version $Id$
 */
public class SwingExampleLauncher {
    public static void main(String[] args) {
    	JFrame f = new JFrame(SwingEventExample.class.getName());
        f.setSize(800, 500);
        f.getContentPane().setLayout(new GridLayout(10, 1));
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addLaunchButtons(f.getContentPane());
        
        
        f.setVisible(true);
    }

	private static void addLaunchButtons(Container contentPane) {
		JButton b = new JButton();
		b.setText("Hierarchy");
		b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				SwingHierarchy.main(new String[0]);
			}
		});
		contentPane.add(b);

		b = new JButton();
		b.setText("Events");
		b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				SwingEventExample.main(new String[0]);
			}
		});
		contentPane.add(b);

		b = new JButton();
		b.setText("Overlap");
		b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				SwingOverlapExample.main(new String[0]);
			}
		});
		contentPane.add(b);

		b = new JButton();
		b.setText("EventMonitoring");
		b.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				EventMonitoringExample.main(new String[0]);
			}
		});
		contentPane.add(b);

	}
}

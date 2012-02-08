/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.jgoodies.binding.value.ValueHolder;

public class ErrorDialog {
	private static final String BUG_REPORTING_TEXT = "<html>This is probably a bug. " +
		"Help us improve ADDIS by reporting this problem to us.<br/>" +
		"Attaching the stack trace and the .addis data file would be very helpful.<br/>" +
		"See <a href=\"http://drugis.org/addis-bug\">http://drugis.org/addis-bug</a> for instructions.<br/><br/>" +
		"Consider restarting ADDIS.</html>";
	private static final long serialVersionUID = 954780612211006478L;

	public static void showDialog(final Throwable e, String title) {
		showDialog(e, title, e.getMessage(), true);
	}

	public static void showDialog(final Throwable e, String title, String message, boolean indicatesBug) {
		JPanel topPanel = new JPanel(new BorderLayout(0, 10));
		topPanel.add(new JLabel(indicatesBug ? "An unexpected error occurred:" : "An error occurred:"), BorderLayout.NORTH);
		topPanel.add(AuxComponentFactory.createTextPane("<html><b>" + message + "</b></html>", true), BorderLayout.CENTER);
		if (indicatesBug) {
			JTextPane bugreport = AuxComponentFactory.createTextPaneWithHyperlinks(BUG_REPORTING_TEXT);
			topPanel.add(bugreport, BorderLayout.SOUTH);
		}

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(topPanel, BorderLayout.NORTH);
		final JScrollPane stackTrace = AuxComponentFactory.createTextArea(new ValueHolder(stackTrace(e)), false);
		stackTrace.setPreferredSize(new Dimension(750, 300));
		panel.add(stackTrace, BorderLayout.CENTER);
		stackTrace.setVisible(false);
        final JButton stackTraceButton = new JButton("Show Stack Trace");

        JOptionPane pane =
            new JOptionPane(panel, JOptionPane.ERROR_MESSAGE,
                            JOptionPane.YES_NO_OPTION, null,
                            new Object[] {stackTraceButton,"Continue"});

        // This is the dialog box containing the pane.
        final JDialog dialog = pane.createDialog(null, title);

        stackTraceButton.addActionListener(new ActionListener( ) {
                public void actionPerformed(ActionEvent event) {
                    String label = stackTraceButton.getText( );
                    if (label.startsWith("Show")) {
                        stackTrace.setVisible(true);
                        stackTraceButton.setText("Hide stack trace");
                        dialog.pack();
                    }
                    else {
                    	stackTrace.setVisible(false);
                        stackTraceButton.setText("Show stack trace");
                        dialog.pack();
                    }
                }
            });
        dialog.setVisible(true);
    }

	public static String stackTrace(Throwable throwable) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(os));
        return os.toString();
	}
	
	public static String getHTMLDetails(Throwable throwable) {
        StringBuffer b = new StringBuffer("<html>");
        int lengthOfLastTrace = 1;  // initial value

        while(throwable != null) {
            b.append("<b>" + throwable.getClass( ).getName( ) + "</b>: " +
                     throwable.getMessage( ) + "<ul>");
            StackTraceElement[] stack = throwable.getStackTrace( );
            for(int i = stack.length-lengthOfLastTrace; i >= 0; i--) {
                b.append("<li> in " +stack[i].getClassName( ) + ".<b>" +
                         stack[i].getMethodName( ) + "</b>( ) at <tt>"+
                         stack[i].getFileName( ) + ":" +
                         stack[i].getLineNumber( ) + "</tt>");
            }
            b.append("</ul>");
            throwable = throwable.getCause( );
            if (throwable != null) {
                b.append("<i>Caused by: </i>");
                lengthOfLastTrace = stack.length;  
            }
        }
        b.append("</html>"); 
        return b.toString( );
    }

}

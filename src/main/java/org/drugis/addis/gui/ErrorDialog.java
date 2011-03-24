package org.drugis.addis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ErrorDialog {

	private static final long serialVersionUID = 954780612211006478L;

	public static void showDialog(final Throwable e, String title) {
		final String smallMessage = e.getMessage();
		final JLabel basicMessage = new JLabel(smallMessage);
        final JButton stackTraceButton = new JButton("Show Stack Trace");

        JOptionPane pane =
            new JOptionPane(basicMessage, JOptionPane.ERROR_MESSAGE,
                            JOptionPane.YES_NO_OPTION, null,
                            new Object[] {stackTraceButton,"Continue"});

        // This is the dialog box containing the pane.
        final JDialog dialog = pane.createDialog(null, title);

        stackTraceButton.addActionListener(new ActionListener( ) {
                public void actionPerformed(ActionEvent event) {
                    String label = stackTraceButton.getText( );
                    if (label.startsWith("Show")) {
                        basicMessage.setText(getHTMLDetails(e));
                        stackTraceButton.setText("Hide stack trace");
                        dialog.pack( );
                    }
                    else {
                        basicMessage.setText(smallMessage);
                        stackTraceButton.setText("Show stack trace");
                        dialog.pack( );
                    }
                }
            });
        dialog.setVisible(true);
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

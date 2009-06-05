package nl.rug.escher.addis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	public AboutDialog(JFrame parent) {
		super(parent);
		setTitle("About " + Main.APPNAME);
		initComponents();
		pack();
	}

	private void initComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(Main.APPNAME + " v" + Main.APPVERSION),
				BorderLayout.NORTH);
		JPanel centerPanel = new JPanel();
		String usStr = new String(Main.APPNAME + " is open source and licensed under GPLv3.");
		String usStr2 = "(c) 2009 Gert van Valkenhoef and Tommi Tervonen.";
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(new JLabel(usStr));
		centerPanel.add(new JLabel(usStr2));
		panel.add(centerPanel, BorderLayout.CENTER);
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		panel.add(closeButton, BorderLayout.SOUTH);
		
		setContentPane(panel);
	}	
}

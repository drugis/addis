package org.drugis.addis.gui.builder;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.util.D80TableGenerator;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class D80ReportView extends JDialog {
	private static final long serialVersionUID = 8901532700897942147L;
	private JLabel d_htmlPane;

	public D80ReportView(Frame owner) {
		super(owner, "D80 Report Dialog", false);
		setMinimumSize(new Dimension(owner.getWidth()/4*3, owner.getHeight()/4*3));
		//setResizable(false);
		setLocationRelativeTo(owner);
		
		buildPanel();
	}

	private void buildPanel() {
		FormLayout layout = new FormLayout("fill:0:grow", "p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		// Editor pane
		final String D80Report = D80TableGenerator.getTemplate();
		d_htmlPane = new JLabel(D80Report);	
		builder.add(d_htmlPane, cc.xy(1, 1));		
		
		// Buttons Panel
		JPanel buttonsPanel = new JPanel();
		// Export button
		JButton exportButton = new JButton("Export table as html");
		buttonsPanel.add(exportButton);
		
		// Copy to clipboard button
		JButton clipboardButton = new JButton("Copy table to clip board");
		clipboardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection data = new StringSelection(D80Report);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(data, data);
			}
		});
		buttonsPanel.add(clipboardButton);
		builder.add(buttonsPanel, cc.xy(1, 3));
		// add everything to a scrollpane
		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(6);
		add(scrollPane);
	}
}

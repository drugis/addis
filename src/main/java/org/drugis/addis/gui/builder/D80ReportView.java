package org.drugis.addis.gui.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Study;
import org.drugis.addis.util.D80TableGenerator;
import org.drugis.common.gui.FileSaveDialog;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class D80ReportView extends JDialog {
	private static final long serialVersionUID = 8901532700897942147L;
	private String d_d80Report;
	private final Study d_study;

	public D80ReportView(Frame owner, Study study) {
		super(owner, "Summary of Efficacy Table", false);
		d_study = study;
		setMinimumSize(new Dimension(owner.getWidth()/4*3, owner.getHeight()/4*3));
		//setResizable(false);
		setLocationRelativeTo(owner);
		
		d_d80Report = D80TableGenerator.getHtml(d_study);
		
		buildPanel();
	}

	private void buildPanel() {
		FormLayout layout = new FormLayout("fill:0:grow", "p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		// Add Html to JLabel
		builder.add(new JLabel(d_d80Report), cc.xy(1, 1));		
		
		// Buttons Panel
		JPanel buttonsPanel = new JPanel();
		
		// Export button
		JButton exportButton = new JButton("Export table as html");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsHtmlDialog((Component) getParent());
			}
		});
		buttonsPanel.add(exportButton);
		
		// Copy to clipboard button
		JButton clipboardButton = new JButton("Copy table to clipboard");
		clipboardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection data = new StringSelection(d_d80Report);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(data, data);
			}
		});
		buttonsPanel.add(clipboardButton);
		builder.add(buttonsPanel, cc.xy(1, 3));
		
		// put everything into a scrollpane and add it to the dialog
		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(6);
		add(scrollPane);
	}
	
	private void saveAsHtmlDialog(final Component component) {
		new FileSaveDialog(component, "html", "HTML files") {
			@Override
			public void doAction(String path, String extension) {
				try {
					saveD80ToHtmlFile(path);
				} catch (IOException e) {
					throw new RuntimeException("Could not save html file", e);
				}
			}
		};
	}
	
	private void saveD80ToHtmlFile(String fileName) throws IOException {
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f));
		osw.write(d_d80Report);
		osw.close();
	}
}

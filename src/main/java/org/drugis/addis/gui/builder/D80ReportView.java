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

package org.drugis.addis.gui.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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

	private class D80Transferable implements Transferable, ClipboardOwner {
		private DataFlavor d_dataflavor = new DataFlavor("text/html; class=java.lang.String");
		private final String text;
		
		public D80Transferable(String d80Report)  throws ClassNotFoundException {
			text = d80Report;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if(flavor.equals(d_dataflavor)) {
				return text;
			} else {
				throw new UnsupportedFlavorException (flavor);
			}
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{d_dataflavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return "text/html".equals(flavor.getMimeType());
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
			// Do nothing
		}
		
	}
	
	public D80ReportView(Window parent, Study study) {
		super(parent, "Summary of Efficacy Table");
		setModal(false);
		d_study = study;
		setMinimumSize(new Dimension(parent.getWidth()/4*3, parent.getHeight()/4*3));
		//setResizable(false);
		setLocationRelativeTo(parent);
		
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
				D80Transferable data = null;
				try {
					data = new D80Transferable(d_d80Report);
				} catch (ClassNotFoundException e1) {}
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

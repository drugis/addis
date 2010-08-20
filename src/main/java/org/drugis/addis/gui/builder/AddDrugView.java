/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.util.AtcParser;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddDrugView implements ViewBuilder {
	private JTextField d_name;
	private JTextField d_atcCodeTextField;
	private String d_atcCode;
	private JButton d_loadButton;
	private PresentationModel<Drug> d_model;
	private NotEmptyValidator d_validator; 

	public AddDrugView(PresentationModel<Drug> presentationModel, JButton okButton) {
		d_validator = new NotEmptyValidator(okButton);
		d_model = presentationModel;
	}
	
	@SuppressWarnings("serial")
	public void initComponents() {
		d_atcCode = null;
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_NAME), false);
		d_name.setColumns(15);
		d_validator.add(d_name);
		d_loadButton = GUIFactory.createIconButton(FileNames.ICON_SEARCH, "Search ATC Code");
		d_atcCodeTextField = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_ATCCODE), false);
		d_validator.add(d_atcCodeTextField);
		
		d_loadButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
					AtcCodeRetriever codeRetriever = new AtcCodeRetriever();
					RunnableReadyModel readyModel = new RunnableReadyModel(codeRetriever);
					new Thread(readyModel).start();
			}
		});
	}

	private class AtcCodeRetriever implements Runnable {
		public void run() {
			try {
				d_loadButton.setIcon(ImageLoader.getIcon(FileNames.ICON_LOADING));
				d_loadButton.setEnabled(false);
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				d_atcCode = new AtcParser().getAtcCode(d_model.getModel(Drug.PROPERTY_NAME).getString()).getCode();
				d_loadButton.setIcon(ImageLoader.getIcon(FileNames.ICON_SEARCH));
				d_model.getModel(Drug.PROPERTY_ATCCODE).setValue(d_atcCode);
				d_loadButton.setEnabled(true);
				
				if(d_atcCode == null) {
					JOptionPane.showMessageDialog(new JPanel(), "The drug ("+d_model.getModel(Drug.PROPERTY_NAME).getString()+")\nhas no ATC code associated", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JPanel(), "Couldn't retrieve ATC code...", e.getMessage(), JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		}
	}
	
	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p"
				);	
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Drug", cc.xyw(1, 1, 5));
		builder.addLabel("Name:", cc.xy(1, 3));
		builder.add(d_name, cc.xy(3, 3));
		builder.add(d_loadButton, cc.xy(5, 3));
		builder.addLabel("ATC Code:", cc.xy(1, 5));
		builder.add(d_atcCodeTextField, cc.xyw(3, 5, 3));
		
		return builder.getPanel();	
	}
}
/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.util.AtcParser;
import org.drugis.addis.util.RunnableReadyModel;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
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
	private JPanel d_panel; 

	public AddDrugView(PresentationModel<Drug> presentationModel, JButton okButton) {
		d_validator = new NotEmptyValidator();
		Bindings.bind(okButton, "enabled", d_validator);
		d_model = presentationModel;
	}
	
	@SuppressWarnings("serial")
	public void initComponents() {
		d_validator.add(d_model.getModel(Drug.PROPERTY_NAME));
		d_name = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_NAME), false);
		d_name.setColumns(15);
		
		d_loadButton = GUIFactory.createIconButton(FileNames.ICON_SEARCH, "Search ATC Code");
		d_loadButton.setDisabledIcon(Main.IMAGELOADER.getIcon(FileNames.ICON_LOADING));
		
		d_validator.add(d_model.getModel(Drug.PROPERTY_ATCCODE));
		d_atcCodeTextField = BasicComponentFactory.createTextField(d_model.getModel(Drug.PROPERTY_ATCCODE), false);
		
		d_loadButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
					RunnableReadyModel readyModel = new RunnableReadyModel(new AtcCodeRetriever());
					new Thread(readyModel).start();
					//SwingUtilities.invokeLater(new Thread(readyModel));
			}
		});
	}

	private class AtcCodeRetriever implements Runnable {
		public void run() {
			try {
				d_atcCode = null;
				String drugName = d_model.getModel(Drug.PROPERTY_NAME).getString().trim();
				d_loadButton.setEnabled(false);
				
				d_atcCode = new AtcParser().getAtcCode(drugName.replace(" ", "%20")).getCode();
				
				d_loadButton.setEnabled(true);
				d_model.getModel(Drug.PROPERTY_NAME).setValue(drugName);
				if(d_atcCode == null) {
					d_model.getModel(Drug.PROPERTY_ATCCODE).setValue("");
					Thread.yield();
					JOptionPane.showMessageDialog(d_panel,
							((drugName.length() == 0) ? "Please enter a drug name" : "The drug \""+drugName+"\"\nhas no ATC code associated"), "Not found", 
							JOptionPane.WARNING_MESSAGE);
				} else {
					d_model.getModel(Drug.PROPERTY_ATCCODE).setValue(d_atcCode);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(d_panel, "Couldn't retrieve ATC code...\n"+e.getMessage(), "Connection problem" , JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
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
		
		d_panel = builder.getPanel();
		return d_panel;	
	}
}
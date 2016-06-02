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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.drugis.addis.gui.Addis2ExportDialog.ExportInfo;
import org.drugis.addis.gui.util.NonEmptyValueModel;
import org.drugis.common.gui.BuildViewWhenReadyComponent;
import org.drugis.common.gui.ViewBuilder;
import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.validation.BooleanAndModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Addis2ExportView implements ViewBuilder {
	private ValueModel d_rdfReady;
	private BooleanAndModel d_credentialsReady = new BooleanAndModel();
	private BooleanAndModel d_readyModel = new BooleanAndModel();
		
	private JTextField d_nameField;
	private JTextField d_titleField;
	private JTextField d_apiKeyField;
	private JTextField d_serverField;
	private JTextField d_datasetIdField;
	private JButton d_importButton;
	private JButton d_credentialsCheckButton;
	private JLabel d_statusLabel;

	private JPanel d_panel;
	private BuildViewWhenReadyComponent d_rdfReadyWaiter;
	private PresentationModel<ExportInfo> d_pm;
	private Runnable d_export;
	private Runnable d_checkCredentials;
	
	public Addis2ExportView(PresentationModel<ExportInfo> presentationModel, Runnable checkCredentials, Runnable export) {
		d_pm = presentationModel;
		d_checkCredentials = checkCredentials;
		d_export = export;
	}

	public void initComponents() {
		d_credentialsReady.add(new NonEmptyValueModel(d_pm.getModel("apiKey")));
		d_credentialsReady.add(new NonEmptyValueModel(d_pm.getModel("server")));
		
		d_readyModel.add(new NonEmptyValueModel(d_pm.getModel("userId")));
		d_readyModel.add(new NonEmptyValueModel(d_pm.getModel("name")));
		d_readyModel.add(new NonEmptyValueModel(d_pm.getModel("title")));
		d_readyModel.add(new NonEmptyValueModel(d_pm.getModel("datasetId")));

		d_serverField = BasicComponentFactory.createTextField(d_pm.getModel("server"), false);
		d_serverField.setColumns(30);
		d_apiKeyField = BasicComponentFactory.createTextField(d_pm.getModel("apiKey"), false);
		d_apiKeyField.setColumns(30);
		d_credentialsCheckButton = new JButton("Check credentials");
		Bindings.bind(d_credentialsCheckButton, "enabled", d_credentialsReady);
		d_credentialsCheckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTask(new SimpleSuspendableTask(d_checkCredentials, "Check credentials with ADDIS 2"));
			}
		});
		
		
		d_nameField = BasicComponentFactory.createTextField(d_pm.getModel("name"), false);
		d_nameField.setColumns(15);
		d_titleField = BasicComponentFactory.createTextField(d_pm.getModel("title"), false);
		d_titleField.setColumns(30);
		d_datasetIdField = BasicComponentFactory.createTextField(d_pm.getModel("datasetId"), false);
		d_datasetIdField.setColumns(30);
		d_statusLabel = BasicComponentFactory.createLabel(d_pm.getModel("status"));
		d_importButton = new JButton("Import");
		Bindings.bind(d_importButton, "enabled", d_readyModel);
		d_importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ThreadHandler.getInstance().scheduleTask(new SimpleSuspendableTask(d_export, "Export to ADDIS 2"));
			}
		});
	}

	private ViewBuilder rdfReadyPanelBuilder() {
		return new ViewBuilder() {
			@Override
			public JComponent buildPanel() {
				return new JLabel("Dataset ready!");
			}
		};
	}

	public JComponent buildPanel() {
		initComponents();
		
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, pref, 3dlu, pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Export to ADDIS 2", cc.xyw(1, 1, 5));

		builder.addLabel("Server URL:", cc.xy(1, 3));
		builder.add(d_serverField, cc.xyw(3, 3, 3));
		builder.addLabel("API key:", cc.xy(1, 5));
		builder.add(d_apiKeyField, cc.xyw(3, 5, 3));
		builder.add(d_credentialsCheckButton, cc.xy(5, 7));
		
		builder.addLabel("Name:", cc.xy(1, 9));
		builder.add(d_nameField, cc.xy(3, 9));
		builder.addLabel("Title:", cc.xy(1, 11));
		builder.add(d_titleField, cc.xyw(3, 11, 3));
		builder.addLabel("Dataset ID:", cc.xy(1, 13));
		builder.add(d_datasetIdField, cc.xyw(3, 13, 3));

		builder.add(d_statusLabel, cc.xyw(1, 15, 3));
		builder.add(d_importButton, cc.xy(5, 15));
		
//		builder.add(d_rdfReadyWaiter, cc.xyw(1, 9, 5));
		
		d_panel = builder.getPanel();
		return d_panel;	
	}
}
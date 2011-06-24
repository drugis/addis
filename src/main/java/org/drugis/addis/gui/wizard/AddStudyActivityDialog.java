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

package org.drugis.addis.gui.wizard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.drugis.addis.entities.CombinationTreatment;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.builder.DoseView;
import org.drugis.addis.presentation.wizard.StudyActivityPresentation;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyActivityDialog extends OkCancelDialog {
	private static final long serialVersionUID = 325928004747685827L;
	private final StudyActivityPresentation d_pm;
	private AddisWindow d_mainWindow;
	
	public AddStudyActivityDialog(JDialog parent, AddisWindow mainWindow, StudyActivityPresentation pm) {
		super(parent, "Activity", false);
		d_mainWindow = mainWindow;
		d_pm = pm;
		this.setMinimumSize(new Dimension(550, 280));
		this.setResizable(false);

		if(d_pm.isEditing()) {
			setTitle("Edit Activity");
		} else {
			setTitle("New Activity");
		}
		
		d_pm.getActivityModel().addValueChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				rebuild();
			}
		});
		
		rebuild();
	}

	@Override
	protected void cancel() {
		disposeOfDialog();
	}

	private void disposeOfDialog() {
		setVisible(false);
		dispose();
	}

	@Override
	protected void commit() {
		d_pm.commit();
		disposeOfDialog();
	}
	
	public void rebuild() {
		getUserPanel().setVisible(false);
		getUserPanel().removeAll(); // remove previous components (if any)
		getUserPanel().add(buildPanel());
		getUserPanel().setVisible(true);
		pack();
	 }

	private JScrollPane buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 7dlu, pref:grow:fill, 3dlu, left:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		final CellConstraints cc = new CellConstraints();
		
		
		// add type
		builder.addLabel("Type: ", cc.xy(1, 1));
		final JComboBox treatmentSelect = AuxComponentFactory.createBoundComboBox(d_pm.getActivityOptions().toArray(), d_pm.getActivityModel());
		final ListCellRenderer renderer = treatmentSelect.getRenderer();
		treatmentSelect.setRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return renderer.getListCellRendererComponent(list, 
						value instanceof TreatmentActivity ? "Treatment" : 
							value instanceof CombinationTreatment ? "Combination Treatment" : value,
						index, isSelected, cellHasFocus);
			}
		});
		builder.add(treatmentSelect, cc.xy(3, 1));
		
		// add name
		builder.addLabel("Name: ", cc.xy(1, 11));
		builder.add(BasicComponentFactory.createTextField(d_pm.getNameModel(), false), cc.xy(3, 11));
		// show or hide drug
		if (d_pm.getActivityModel().getValue() instanceof TreatmentActivity) {
			showDrug(builder, cc);
		}
		builder.addSeparator("", cc.xyw(1, 9, 5));
		// NOOOOTES
		builder.add(AddStudyWizard.buildNotesEditor(d_pm.getNotesModel()), cc.xyw(1, 13, 5));
		PropertyConnector.connectAndUpdate(d_pm.getValidModel(), d_okButton, "enabled");
		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		return scrollPane;
	}

	private void showDrug(PanelBuilder builder, CellConstraints cc) {
		// add drug
		builder.addSeparator("", cc.xyw(1, 3, 5));
		builder.addLabel("Drug: ", cc.xy(1, 5));
		
		JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_pm.getDrugOptions(), 
				d_pm.getTreatmentModel().getModel(TreatmentActivity.PROPERTY_DRUG));
		builder.add(drugSelect, cc.xy(3, 5));
		
		JButton btn = GUIFactory.createPlusButton("Create drug");
		builder.add(btn, cc.xy(5, 5));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), 
						d_pm.getTreatmentModel().getModel(TreatmentActivity.PROPERTY_DRUG));
			}
		});
		
		// add dose
		builder.addLabel("Dose: ", cc.xy(1, 7));
		DoseView doseView = new DoseView(d_pm.getTreatmentModel().getDoseModel());
		builder.add(doseView.buildPanel(), cc.xy(3, 7));
	}
}

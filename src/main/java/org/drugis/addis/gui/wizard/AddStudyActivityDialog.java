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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;

import org.drugis.addis.entities.Activity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.components.NotEmptyValidator;
import org.drugis.addis.presentation.DosePresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.TreatmentActivityPresentation;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AddStudyActivityDialog extends OkCancelDialog {
	@SuppressWarnings("serial")
	public final class Validator extends NotEmptyValidator {
		@Override
		public Boolean getValue() {
			return super.getValue() && isNameUnique();
		}
	}

	private static final long serialVersionUID = 325928004747685827L;
	private final AddStudyWizardPresentation d_pm;
	private AddisWindow d_mainWindow;
	private ModifiableHolder<Object> d_activityHolder;
	private TreatmentActivityPresentation d_treatmentModel;
	private NotEmptyValidator d_validator;
	private ValueHolder<String> d_nameModel;
	
	public AddStudyActivityDialog(JDialog parent, AddisWindow mainWindow, AddStudyWizardPresentation pm) {
		super(parent);
		d_mainWindow = mainWindow;
		d_pm = pm;
		d_treatmentModel = new TreatmentActivityPresentation(new TreatmentActivity(null, null), mainWindow.getPresentationModelFactory());
		d_validator = new Validator();
		
		d_nameModel = new ModifiableHolder<String>("");
		d_activityHolder = new ModifiableHolder<Object>("Treatment");
		d_activityHolder.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				rebuild();
			}
		});
		
		PropertyChangeListener nameUpdater = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateName();
			}
		};
		d_activityHolder.addValueChangeListener(nameUpdater);
		d_treatmentModel.getModel(TreatmentActivity.PROPERTY_DRUG).addValueChangeListener(nameUpdater);
		updateName();
		setResizable(false);
		setTitle("New Activity");
		rebuild();
	}
	
	private void updateName() {
		if (d_activityHolder.getValue().equals("Treatment")) {
			Object drug = d_treatmentModel.getModel(TreatmentActivity.PROPERTY_DRUG).getValue();
			d_nameModel.setValue(drug != null ? drug.toString() : "");
		} else {
			d_nameModel.setValue(d_activityHolder.getValue().toString());
		}
	}
	
	private boolean isNameUnique() {
		Object current = d_nameModel.getValue();
		ObservableList<StudyActivity> activities = d_pm.getNewStudyPM().getBean().getStudyActivities();
		
		for (StudyActivity act : activities) {
			if(act.getName().equals(current)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void cancel() {
		setVisible(false);
		dispose();
	}

	@Override
	protected void commit() {
		Study study = d_pm.getNewStudyPM().getBean();
		StudyActivity act = new StudyActivity(
				d_nameModel.getValue(),
				getActivity()
				);
		study.getStudyActivities().add(act);
		
		cancel();
	}

	private Activity getActivity() {
		return d_activityHolder.getValue().equals("Treatment") ? d_treatmentModel.getBean() : (Activity) d_activityHolder.getValue();
	}
	
	public void rebuild() {
		getUserPanel().setVisible(false);
		getUserPanel().removeAll(); // remove previous components (if any)
		d_validator.clear();
		initComps();
		getUserPanel().setVisible(true);
		pack();
	 }

	private void initComps() {
		FormLayout layout = new FormLayout(
				"fill:pref, 7dlu, fill:pref:grow, 3dlu, left:pref",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p"
				);
		final PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		final CellConstraints cc = new CellConstraints();
		
		// add type
		builder.addLabel("Type: ", cc.xy(1, 1));
		final JComboBox treatmentSelect = AuxComponentFactory.createBoundComboBox(getActivityTypes(), d_activityHolder); 
		builder.add(treatmentSelect, cc.xy(3, 1));
		
		// add name
		builder.addLabel("Name: ", cc.xy(1, 11));
		JTextField activityName = BasicComponentFactory.createTextField(d_nameModel, false);
		d_validator.add(activityName);
		builder.add(activityName, cc.xy(3, 11));
		
		// show or hide drug
		if (d_activityHolder.getValue().equals("Treatment")) {
			showDrug(builder, cc);
		}

		PropertyConnector.connectAndUpdate(d_validator, d_okButton, "enabled");
		
		getUserPanel().add(builder.getPanel());
	}

	private Object[] getActivityTypes() {
		List<Object> types = new ArrayList<Object>();
		types.add("Treatment");
		for (PredefinedActivity pa : PredefinedActivity.values()) {
			types.add(pa);
		}
		return types.toArray();
	}

	private void showDrug(PanelBuilder builder, CellConstraints cc) {
		// add drug
		builder.addSeparator("", cc.xyw(1, 3, 5));
		builder.addLabel("Drug: ", cc.xy(1, 5));
		
		JComboBox drugSelect = AuxComponentFactory.createBoundComboBox(d_pm.getDrugsModel(), d_treatmentModel.getModel(TreatmentActivity.PROPERTY_DRUG));
		d_validator.add(drugSelect);
		builder.add(drugSelect, cc.xy(3, 5));
		
		JButton btn = GUIFactory.createPlusButton("Create drug");
		builder.add(btn, cc.xy(5, 5));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d_mainWindow.showAddDialog(CategoryKnowledgeFactory.getCategoryKnowledge(Drug.class), d_treatmentModel.getModel(TreatmentActivity.PROPERTY_DRUG));
			}
		});
		
		// add dose
		builder.addLabel("Dose: ", cc.xy(1, 7));
		JPanel dosesPanel = new JPanel(new BorderLayout());
		
		// add min dose
		DosePresentation doseModel = d_treatmentModel.getDoseModel();
		JTextField minDoseField =  new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(doseModel.getMinModel(), minDoseField, "value");
		minDoseField.setColumns(4);
		d_validator.add(minDoseField);
		dosesPanel.add(minDoseField, BorderLayout.WEST);
		
		// add max dose
		JTextField maxDoseField = new JFormattedTextField(new DefaultFormatter());
		PropertyConnector.connectAndUpdate(doseModel.getMaxModel(), maxDoseField, "value");
		maxDoseField.setColumns(4);
		d_validator.add(maxDoseField);
		dosesPanel.add(maxDoseField, BorderLayout.EAST);
		builder.add(dosesPanel, cc.xy(3, 7));
		
		// add dose unit box
		JComboBox doseUnitBox = AuxComponentFactory.createBoundComboBox(SIUnit.values(), d_treatmentModel.getDoseModel().getUnitModel());
		d_validator.add(doseUnitBox);
		builder.add(doseUnitBox, cc.xy(5, 7));
		builder.addSeparator("", cc.xyw(1, 9, 5));
	}
}

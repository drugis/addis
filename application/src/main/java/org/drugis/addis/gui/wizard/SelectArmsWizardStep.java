/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyActivity;
import org.drugis.addis.entities.TreatmentActivity;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.components.ListPanel;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectArmsWizardStep extends PanelWizardStep {
	

	private final NetworkMetaAnalysisWizardPM d_pm;

	public SelectArmsWizardStep (NetworkMetaAnalysisWizardPM pm) {
		super ("Select Arms","Select the specific arms to be used for the meta-analysis");
		setLayout(new BorderLayout());
		d_pm = pm;
	}

	@Override
	public void prepare() {
		d_pm.rebuildArmSelection();
		
		removeAll();
		
		FormLayout layout = new FormLayout("3dlu, left:pref, 3dlu, pref:grow", "p");	
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		int row = 1;
		for (Study curStudy : d_pm.getSelectableStudyListPM().getSelectedStudiesModel()) {
			builder.addSeparator(curStudy.toString(), cc.xyw(1, row, 4));
			row = LayoutUtil.addRow(layout, row);
			
			for (TreatmentDefinition def: d_pm.getSelectedRefinedTreatmentDefinitions()) {
				if (!d_pm.getArmsPerStudyPerDefinition(curStudy, def).isEmpty()) {
					row = createArmSelect(builder, row, curStudy, def, cc);
				}
			}
		}
		
		JScrollPane sp = new JScrollPane(builder.getPanel());
		add(sp, BorderLayout.CENTER);
		sp.getVerticalScrollBar().setUnitIncrement(16);

		setComplete(true);
	}

	private int createArmSelect(PanelBuilder builder, int row, final Study curStudy, TreatmentDefinition def, CellConstraints cc) {
		builder.addLabel(def.getLabel(), cc.xy(2, row));
		
		ListModel arms = d_pm.getArmsPerStudyPerDefinition(curStudy, def);

		final JComboBox drugBox = AuxComponentFactory.createBoundComboBox(arms, d_pm.getSelectedArmModel(curStudy, def), true);
		if (arms.getSize() == 1)
			drugBox.setEnabled(false);
		final JPanel drugAndDosePanel = new JPanel(new BorderLayout());
		
		builder.add(drugBox, cc.xy(4, row));
		drugBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDrugAndDoseLabel(curStudy, drugBox, drugAndDosePanel);
			}
		});
		row = LayoutUtil.addRow(builder.getLayout(), row);
		updateDrugAndDoseLabel(curStudy, drugBox, drugAndDosePanel);
		builder.add(drugAndDosePanel, cc.xy(4, row));
		
		return LayoutUtil.addRow(builder.getLayout(), row);
	}

	private void updateDrugAndDoseLabel(Study curStudy, JComboBox drugBox, JPanel drugAndDosePanel) {
		drugAndDosePanel.setVisible(false);
		StudyActivity sa = curStudy.getStudyActivityAt((Arm) drugBox.getSelectedItem(), curStudy.findTreatmentEpoch());
		TreatmentActivity ta = (TreatmentActivity) sa.getActivity();
		drugAndDosePanel.removeAll();
		drugAndDosePanel.add(new ListPanel(ta.getTreatments()), BorderLayout.CENTER);
		drugAndDosePanel.setVisible(true);
	}
}
/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import javax.swing.JComboBox;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.AbstractMetaAnalysisWizardPM;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class SelectArmsWizardStep extends PanelWizardStep {
	
	private PanelBuilder d_builder;
	private FormLayout d_layout;
	private final AbstractMetaAnalysisWizardPM<? extends StudyGraphModel> d_pm;

	public SelectArmsWizardStep (AbstractMetaAnalysisWizardPM<? extends StudyGraphModel> pm) {
		super ("Select Arms","Select the specific arms to be used for the meta-analysis");
		setLayout(new BorderLayout());
		d_pm = pm;
		
		d_layout = new FormLayout("3dlu, left:pref, 3dlu, pref:grow", "p");	
		
		d_builder = new PanelBuilder(d_layout);
		d_builder.setDefaultDialogBorder();
	}

	@Override
	public void prepare() {
		removeAll();
		
		CellConstraints cc = new CellConstraints();
		
		d_builder = new PanelBuilder(d_layout);
		d_builder.setDefaultDialogBorder();
		
		int row = 1;
		for (Study curStudy : d_pm.getStudyListModel().getSelectedStudiesModel().getValue()) {
			d_builder.addSeparator(curStudy.toString(), cc.xyw(1, row, 4));
			LayoutUtil.addRow(d_layout);
			row += 2;
			
			for (Drug drug: d_pm.getSelectedDrugsModel().getValue()) {
				if (curStudy.getDrugs().contains(drug)) {
					row = createArmSelect(row, curStudy, drug, cc);
				}
			}
		}
		
		JScrollPane sp = new JScrollPane(d_builder.getPanel());
		add(sp, BorderLayout.CENTER);
		sp.getVerticalScrollBar().setUnitIncrement(16);

		setComplete(true);
	}

	private int createArmSelect(int row, Study curStudy, Drug drug, CellConstraints cc) {
		d_builder.addLabel(drug.toString(), cc.xy(2, row));
		
		ListHolder<Arm> arms = d_pm.getArmsPerStudyPerDrug(curStudy, drug);

		JComboBox drugBox  = AuxComponentFactory.createBoundComboBox(arms,
				d_pm.getSelectedArmModel(curStudy, drug));
		if (arms.getValue().size() == 1)
			drugBox.setEnabled(false);

		d_builder.add(drugBox, cc.xy(4, row));
		LayoutUtil.addRow(d_layout);
		
		return row + 2;
	}
}
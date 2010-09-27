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

package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.BenefitRiskView;
import org.drugis.addis.gui.wizard.BenefitRiskWizard;
import org.drugis.addis.presentation.BenefitRiskPresentation;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskAnalysesKnowledge extends CategoryKnowledgeBase {
	public BenefitRiskAnalysesKnowledge() {
		super("benefit-risk analysis", "Benefit-risk analyses", FileNames.ICON_BENEFITRISK, BenefitRiskAnalysis.class);
	}
	
	public JDialog getAddDialog(Main main, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(main, "Create Benefit-risk analysis", true);
		Wizard wizard = new BenefitRiskWizard(main,
				new BenefitRiskWizardPM(domain));
		dialog.getContentPane().add(wizard);
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		Main.bindPrintScreen(wizard);
		return dialog;
	}
	
	@Override
	public boolean isToolbarCategory() {
		return true;
	}
	
	@Override
	protected String[] getShownProperties() {
		return new String[] { "name", "indication", "outcomeMeasures",
				"metaAnalyses", "baseline", "drugs" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			Entity entity) {
		BenefitRiskPresentation model = (BenefitRiskPresentation) main.getPresentationModelFactory().getModel((BenefitRiskAnalysis) entity);
		return new BenefitRiskView(model, main);
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_BENEFITRISK_NEW;
	}
}

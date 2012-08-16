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

package org.drugis.addis.gui.knowledge;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.PairWiseMetaAnalysisView;
import org.drugis.addis.gui.wizard.PairwiseMetaAnalysisWizard;
import org.drugis.addis.presentation.PairWiseMetaAnalysisPresentation;
import org.drugis.addis.presentation.wizard.PairWiseMetaAnalysisWizardPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class PairWiseMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public PairWiseMetaAnalysesKnowledge() {
		super("Pair-wise meta-analysis", "Pair-wise meta-analyses", null, RandomEffectsMetaAnalysis.class);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_METASTUDY;
	}
	
	@Override
	public char getMnemonic() {
		return 'm';
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_METASTUDY_NEW;
	}
	
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(mainWindow, "Create DerSimonian-Laird random effects meta-analysis", true);
		Wizard wizard = new PairwiseMetaAnalysisWizard(mainWindow,
				new PairWiseMetaAnalysisWizardPresentation(domain, mainWindow.getPresentationModelFactory()));
		dialog.getContentPane().add(wizard);
		dialog.setMinimumSize(new Dimension(700, 550));
		dialog.setPreferredSize(AddisWindow.fitDimensionToScreen(790, 650));
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
		return new String[] { 
				PairWiseMetaAnalysis.PROPERTY_NAME,
				PairWiseMetaAnalysis.PROPERTY_TYPE,
				PairWiseMetaAnalysis.PROPERTY_INDICATION,
				PairWiseMetaAnalysis.PROPERTY_OUTCOME_MEASURE,
				PairWiseMetaAnalysis.PROPERTY_ALTERNATIVES,
				PairWiseMetaAnalysis.PROPERTY_INCLUDED_STUDIES, 
				PairWiseMetaAnalysis.PROPERTY_SAMPLE_SIZE};
	}

	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity) {
		return new PairWiseMetaAnalysisView(
				(PairWiseMetaAnalysisPresentation)main.getPresentationModelFactory().getModel(((RandomEffectsMetaAnalysis) entity)), 
				main);
	}
}

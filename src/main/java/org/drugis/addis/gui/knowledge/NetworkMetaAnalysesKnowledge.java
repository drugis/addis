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

package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.NetworkMetaAnalysisView;
import org.drugis.addis.gui.wizard.NetworkMetaAnalysisWizard;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.wizard.NetworkMetaAnalysisWizardPM;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public NetworkMetaAnalysesKnowledge() {
		super("network meta-analysis", "Network meta-analyses", null, NetworkMetaAnalysis.class);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_NETWMETASTUDY;
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_NETWMETASTUDY_NEW;
	}

	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain,
			ValueModel selectionModel) {
		JDialog dialog = new JDialog(mainWindow, "Create Network meta-analysis", true);
		Wizard wizard = new NetworkMetaAnalysisWizard(mainWindow,
				new NetworkMetaAnalysisWizardPM(domain, mainWindow.getPresentationModelFactory()));
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
		return new String[] { "name", "type", "indication", "outcomeMeasure",
				"includedDrugs", "studiesIncluded", "sampleSize" };
	}

	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain, Entity entity) {
		return new NetworkMetaAnalysisView(
				(NetworkMetaAnalysisPresentation) main.getPresentationModelFactory().getModel(((NetworkMetaAnalysis) entity)),
				main);
	}
}

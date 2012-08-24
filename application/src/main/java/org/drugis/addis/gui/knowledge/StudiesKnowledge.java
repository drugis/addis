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

package org.drugis.addis.gui.knowledge;

import java.awt.Dimension;

import javax.swing.JDialog;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.StudyView;
import org.drugis.addis.gui.builder.TitledPanelBuilder;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.gui.wizard.AddStudyWizard;
import org.drugis.addis.presentation.StudyListPresentation;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.addis.presentation.wizard.AddStudyWizardPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class StudiesKnowledge extends CategoryKnowledgeBase {
	public StudiesKnowledge() {
		super("study", "Studies", FileNames.ICON_STUDY, Study.class);
	}
	
	@Override
	public String getNewIconName() {
		return FileNames.ICON_STUDY_NEW;
	}
	
	public JDialog getAddDialog(AddisWindow mainWindow, Domain domain, ValueModel selectionModel) {
		AddStudyWizardPresentation pm = new AddStudyWizardPresentation(domain, mainWindow.getPresentationModelFactory(), mainWindow);
		return buildStudyWizardDialog(mainWindow, "Add Study", pm);
	}

	public static JDialog buildStudyWizardDialog(AddisWindow mainWindow, String title, AddStudyWizardPresentation pm) {
		JDialog dialog = new JDialog(mainWindow, title, true);
		AddStudyWizard wizard = new AddStudyWizard(pm, mainWindow, dialog);
		dialog.getContentPane().add(wizard);
		dialog.setMinimumSize(new Dimension(700, 550));
		dialog.setPreferredSize(AddisWindow.fitDimensionToScreen(790, 750));
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
	public ViewBuilder getCategoryViewBuilder(AddisWindow main, Domain domain) {
		StudyListPresentation studyListPM = new StudyListPresentation(domain.getStudies());
		TitledPanelBuilder view = new TitledPanelBuilder(new StudiesTablePanel(studyListPM, main), "Studies");
		return view;
	}

	public ViewBuilder getEntityViewBuilder(AddisWindow main, Domain domain,
			Entity entity) {
		return new StudyView((StudyPresentation) main.getPresentationModelFactory()
				.getModel(((Study) entity)), main.getDomain(), main);
	}
}

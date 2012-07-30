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
import org.drugis.addis.entities.treatment.DosedDrugTreatment;
import org.drugis.addis.gui.AddDosedDrugTreatmentWizard;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.DosedDrugTreatmentView;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.WizardFrameCloser;

import com.jgoodies.binding.value.ValueModel;

public class DosedDrugTreatmentKnowledge extends CategoryKnowledgeBase {

	public DosedDrugTreatmentKnowledge() {
		super("treatment", FileNames.ICON_HEART, DosedDrugTreatment.class);
	}

	@Override
	public JDialog getAddDialog(final AddisWindow mainWindow, final Domain domain, final ValueModel selectionModel) {
		final DosedDrugTreatment treatment = new DosedDrugTreatment();

		final DosedDrugTreatmentWizardPresentation pm = new DosedDrugTreatmentWizardPresentation(treatment, domain);
		return buildDosedDrugTreatmentWizardDialog(mainWindow, domain, "Add Treatment", pm);
	}

	public static JDialog buildDosedDrugTreatmentWizardDialog(final AddisWindow mainWindow, final Domain domain, final String title, final DosedDrugTreatmentWizardPresentation pm) {
		final JDialog dialog = new JDialog(mainWindow, title, true);
		final AddDosedDrugTreatmentWizard wizard = new AddDosedDrugTreatmentWizard(pm, dialog);
		dialog.getContentPane().add(wizard);
		dialog.setMinimumSize(new Dimension(550, 400));
		dialog.setPreferredSize(AddisWindow.fitDimensionToScreen(640, 600));
		dialog.pack();
		WizardFrameCloser.bind(wizard, dialog);
		Main.bindPrintScreen(wizard);
		return dialog;
	}

	@Override
	protected String[] getShownProperties() {
		return new String[] { "drug", "name", "categories" };
	}

	@Override
	public ViewBuilder getEntityViewBuilder(final AddisWindow main, final Domain domain, final Entity entity) {
		return new DosedDrugTreatmentView((DosedDrugTreatmentPresentation) main.getPresentationModelFactory().getModel(((DosedDrugTreatment) entity)), main);
	}

}

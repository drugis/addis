/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.gui;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.builder.StudyAddArmView;
import org.drugis.addis.presentation.StudyAddArmPresentation;
import org.drugis.common.gui.OkCancelDialog;

@SuppressWarnings("serial")
public class StudyAddArmDialog extends OkCancelDialog {

	private Domain d_domain;
	private Study d_study;
	private StudyAddArmView d_view;
	private Main d_main;
	private StudyAddArmPresentation d_pm;

	public StudyAddArmDialog(Main main, Domain domain, Study study) {
		super(main, "Add Patient Group to Study");
		d_main = main;
		this.setModal(true);
		d_domain = domain;
		d_study = study;
		d_pm = new StudyAddArmPresentation(d_study, main.getPresentationModelFactory());
		d_view = new StudyAddArmView(d_pm, d_domain, d_okButton);
		getUserPanel().removeAll();
		getUserPanel().add(d_view.buildPanel());
		pack();
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		d_pm.addToStudy();
		
		setVisible(false);
		d_main.leftTreeFocusOnStudy(d_study);
	}
}

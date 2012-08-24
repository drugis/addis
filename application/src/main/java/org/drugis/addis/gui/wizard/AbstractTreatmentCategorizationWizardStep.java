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

package org.drugis.addis.gui.wizard;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.drugis.common.validation.BooleanAndModel;
import org.pietschy.wizard.PanelWizardStep;

import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractTreatmentCategorizationWizardStep extends PanelWizardStep {
	private static final long serialVersionUID = 5608736267613312255L;
	protected static final int PANEL_WIDTH = 600;
	protected List<ValueModel> d_validators = new ArrayList<ValueModel>();
	private JPanel d_dialogCache = null;
	protected final Domain d_domain;
	protected final AddisWindow d_mainWindow;
	protected final TreatmentCategorizationWizardPresentation d_pm;
	protected JDialog d_dialog;
	protected ListDataListener d_rebuildListener = new ListDataListener() {
			public void intervalRemoved(final ListDataEvent e) {
				rebuildPanel();
			}

			public void intervalAdded(final ListDataEvent e) {
				rebuildPanel();
			}

			public void contentsChanged(final ListDataEvent e) {}
		};

	public AbstractTreatmentCategorizationWizardStep(TreatmentCategorizationWizardPresentation presentationModel, JDialog dialog) {
		this(presentationModel, null, null, null, dialog);
	}

	public AbstractTreatmentCategorizationWizardStep(TreatmentCategorizationWizardPresentation presentationModel,
			String name,
			String summary, JDialog dialog) {
		this(presentationModel, name, summary, null, dialog);
	}

	public AbstractTreatmentCategorizationWizardStep(TreatmentCategorizationWizardPresentation presentationModel,
			String name,
			String summary,
			Icon icon, JDialog dialog) {
		super(name, summary, icon);
		d_pm = presentationModel;
		d_dialog = dialog;
		d_mainWindow = Main.getMainWindow();
		d_domain = d_mainWindow.getDomain();
	}

	@Override
	public void prepare() {
		this.setVisible(false);
		initialize();
	 	buildWizardStep();
	 	BooleanAndModel valid = new BooleanAndModel(d_validators);
	 	PropertyConnector.connectAndUpdate(valid, this, "complete");
	 	this.setVisible(true);
	}

	protected void initialize() {}

	private void buildWizardStep() {
		if(d_dialogCache == null) {
			d_dialogCache = buildPanel();
		}
		removeAll();
		add(d_dialogCache);
	}

	protected void rebuildPanel() {
		setVisible(false);
		removeAll();
		d_dialogCache = buildPanel();
		add(d_dialogCache);
		setVisible(true);
	}

	protected abstract JPanel buildPanel();
}
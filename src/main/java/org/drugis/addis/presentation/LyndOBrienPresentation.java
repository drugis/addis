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

package org.drugis.addis.presentation;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.lyndobrien.BenefitRiskDistributionImpl;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.LyndOBrienModelImpl;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;

public class LyndOBrienPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {
	AnalysisType d_a;
	StudyBenefitRiskAnalysis sbr;
	private LyndOBrienModelImpl d_model;
	private TaskProgressModel d_tpm;
	private ValueHolder<Boolean> d_initializedModel = new ModifiableHolder<Boolean>(false);
	
	public LyndOBrienPresentation(AnalysisType at) {
		d_a = at;
		d_model = new LyndOBrienModelImpl(new BenefitRiskDistributionImpl<Alternative>(d_a));
		d_tpm = new TaskProgressModel(d_model.getTask());
	}
	
	public LyndOBrienModel getModel() {
		return d_model;
	}
	
	public ValueHolder<Boolean> getInitializedModel() {
		return d_initializedModel;
	}

	public void startLyndOBrien() {
		d_initializedModel.setValue(true);
		ThreadHandler.getInstance().scheduleTask(d_model.getTask());
	}

	public Task getTask() {
		return d_model.getTask();
	}

	public TaskProgressModel getProgressModel() {
		return d_tpm;
	}
}

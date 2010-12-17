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

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.simulator.BuildQueue;

@SuppressWarnings("serial")
public abstract class AbstractBenefitRiskPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> 
extends PresentationModel<AnalysisType> {

	protected PresentationModelFactory d_pmf;
	protected BuildQueue d_buildQueue;
	private SMAAPresentation<Alternative, AnalysisType> d_smaaPresentation;
	private LyndOBrienPresentation<Alternative, AnalysisType> d_lyndOBrienPresentation;

	public AbstractBenefitRiskPresentation(AnalysisType bean, PresentationModelFactory pmf) {
		super(bean);
		d_pmf = pmf;
		
		if (bean.getAnalysisType().equals(org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType.SMAA)) {
			d_smaaPresentation = new SMAAPresentation<Alternative, AnalysisType>(getBean());
			startSMAA();
		} else {
			d_lyndOBrienPresentation = new LyndOBrienPresentation<Alternative, AnalysisType>(getBean());
			startLyndOBrien();
		}
	}
	
	protected abstract void startSMAA();
	protected abstract void startLyndOBrien();
	
	public PresentationModelFactory getFactory() {
		return d_pmf;
	}

	public abstract void startAllSimulations();

	public abstract ValueHolder<Boolean> getMeasurementsReadyModel();

	public SMAAPresentation<Alternative, AnalysisType> getSMAAPresentation() {
		return d_smaaPresentation;
	}

	public LyndOBrienPresentation<Alternative, AnalysisType> getLyndOBrienPresentation() {
		return d_lyndOBrienPresentation;
	}
}

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

package org.drugis.addis.mocks;

import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.LogGaussian;

public class MockBenefitRiskAnalysis extends BenefitRiskAnalysis {

	public MockBenefitRiskAnalysis(String id, Indication indication,
			List<OutcomeMeasure> outcomeMeasureList,
			List<MetaAnalysis> metaAnalysisList, Drug baseline,
			List<Drug> drugList) {
		super(id,indication,outcomeMeasureList,metaAnalysisList,baseline,drugList);
	}

	/**
	 * Get the assumed distribution for the baseline odds.
	 */
	@Override
	public GaussianBase getBaselineDistribution(OutcomeMeasure om) {
		switch (om.getType()) {
			case RATE: return new LogGaussian(0.001, 0.0001);
			case CONTINUOUS: return new Gaussian(0.001, 0.0001);
		}
		return null;
	}

}

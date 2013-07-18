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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.Binomial;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.MCMCParameter;

import java.util.List;

import org.drugis.addis.entities.RateMeasurement;
import org.drugis.common.stat.DichotomousDescriptives;
import org.drugis.common.stat.EstimateWithPrecision;

public class BaselineOddsModel extends AbstractBaselineModel<RateMeasurement> {
	private DichotomousDescriptives d_dichotomousDescriptives = new DichotomousDescriptives(true);

	public BaselineOddsModel(List<RateMeasurement> measurements) {
		super(measurements);
	}

	@Override
	protected void createDataBond(MCMCParameter studyMu) {
		new BasicMCMCBond(new MCMCParameter[] {studyMu},
				new ArgumentMaker[] {
					new ConstantArgument(rateArray()),
					new ConstantArgument(sampleSizeArray()),  
					new InverseLogitArgumentMaker(0)
				}, new Binomial());
	}

	@Override
	protected EstimateWithPrecision estimateTreatmentEffect(int i) {
		final RateMeasurement m = d_measurements.get(i);
		double mean = d_dichotomousDescriptives.logOdds(m.getRate(), m.getSampleSize());
		double se = getError(i);
		return new EstimateWithPrecision(mean, se);
	}

	@Override
	protected double getError(int i) {
		return getError(d_measurements.get(i));
	}
	
	private double getError(final RateMeasurement m) {
		return d_dichotomousDescriptives.logOddsError(m.getRate(), m.getSampleSize());
	}
	
	private double[] sampleSizeArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getSampleSize();
		}
		return arr;
	}

	private double[] rateArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getRate();
		}
		return arr;
	}
}

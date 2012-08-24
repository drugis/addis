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

package org.drugis.addis.entities.analysis;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.common.Interval;


/**
 * Converts (odds-ratio) measurement scales to clinically interpretable numbers.
 * Should not be used for continuous variables.
 */
public class OddsRatioToClinicalConverter {
	private final MetaBenefitRiskAnalysis d_br;
	private final OutcomeMeasure d_om;

	public OddsRatioToClinicalConverter(MetaBenefitRiskAnalysis br, OutcomeMeasure om) {
		d_br = br;
		d_om = om;
		if (!(om.getVariableType() instanceof RateVariableType)) throw new IllegalArgumentException("Only rate-outcomes supported");
		if (!br.getCriteria().contains(om)) throw new IllegalArgumentException("OutcomeMeasure not present in Benefit-Risk analysis");
	}
	
	/**
	 * Converts the extremes of the measurement scale to odds-ratio.
	 * @param scale Measurement scale.
	 * @return OR associated with the extremes of the scale.
	 */
	public Interval<Double> getOddsRatio(Interval<Double> scale) {
		return scale;
	}

	/**
	 * Converts the extremes of the measurement scale to (absolute) risk.
	 * This number is based on the median of the baseline distribution given in the BenefitRiskAnalysis.
	 * @param scale Measurement scale.
	 * @return Risk associated with the extremes of the scale.
	 */
	public Interval<Double> getRisk(Interval<Double> scale) {
		double baselineOdds = d_br.getBaselineDistribution(d_om).getQuantile(0.5); // or: 1.0 for absolute measurements
		double oddsL = scale.getLowerBound() * baselineOdds;
		double oddsU = scale.getUpperBound() * baselineOdds;
		
		return new Interval<Double>(oddsToRisk(oddsL), oddsToRisk(oddsU));
	}

	/**
	 * Calculates the risk difference associated with scale.
	 * This number is based on the median of the baseline distribution given in the BenefitRiskAnalysis.
	 * @param scale Measurement scale.
	 * @return RD associated with the scale range.
	 */
	public double getRiskDifference(Interval<Double> scale) {
		Interval<Double> risk = getRisk(scale);
		return risk.getUpperBound() - risk.getLowerBound();
	}
	
	/**
	 * Calculates the number needed to treat associated with the difference between upper and lower bound of the scale.
	 * In case the OutcomeMeasure has direction LOWER_IS_BETTER, this should be interpreted as number needed to harm.
	 * This number is based on the median of the baseline distribution given in the BenefitRiskAnalysis.
	 * @param scale Measurement scale.
	 * @return NNT associated with the range.
	 */
	public double getNumberNeededToTreat(Interval<Double> scale) {
		return 1d / getRiskDifference(scale);
	}
	
	private static double oddsToRisk(double odds) {
		return odds / (1 + odds);
	}
	
}

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

package org.drugis.addis.presentation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.forestplot.IdentityScale;
import org.drugis.addis.forestplot.LinearScale;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.Scale;
import org.drugis.common.Interval;

public abstract class AbstractForestPlotPresentation implements ForestPlotPresentation {
	private OutcomeMeasure d_outcomeMeasure;
	private List<Study> d_studies;
	private List<BasicRelativeEffect<?>> d_effects;
	private RandomEffectMetaAnalysisRelativeEffect<Measurement> d_pooled;
	
	private BinnedScale d_scale;
	private double d_max = 0.0;
	private AxisType d_scaleType;
	
	protected AbstractForestPlotPresentation(
			OutcomeMeasure om, List<Study> studies,
			List<BasicRelativeEffect<?>> effects,
			RandomEffectMetaAnalysisRelativeEffect<Measurement> pooled) {
		d_outcomeMeasure = om;
		d_studies = studies;
		d_effects = effects;
		d_pooled = pooled;
		initScales();
	}

	protected static Interval<Double> getRange(List<ConfidenceInterval> cis, AxisType scaleType) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (ConfidenceInterval ci : cis) {
			min = Math.min((double) ci.getLowerBound(), min);
			max = Math.max((double) ci.getUpperBound(), max);
		}
		
		if (Double.isNaN(min) || Double.isNaN(max)) {
			if (scaleType.equals(AxisType.LOGARITHMIC)) {
				return new Interval<Double>(0.5, 2.0);
			} else {
				return new Interval<Double>(-1.0, 1.0);
			}
		}	
		
		Interval<Double> interval = new Interval<Double>(min, max);
		if (scaleType == AxisType.LINEAR)
			return niceIntervalLinear(interval);
		if (scaleType == AxisType.LOGARITHMIC)
			return niceIntervalLog(interval);
		
		return interval;
	}

	protected void initScales() {
		for (int i = 0; i < getNumRelativeEffects(); ++i) {
			if (!isPooledRelativeEffect(i)) {
				d_max = Math.max(((BasicRelativeEffect<?>)getRelativeEffectAt(i)).getSampleSize(), d_max);
			}
		}
		
		if (getRelativeEffectAt(0).getAxisType() == AxisType.LINEAR) {
			d_scaleType = AxisType.LINEAR;
			d_scale = new BinnedScale(new LinearScale(getRange()), 1, ForestPlot.BARWIDTH);
		}
		if (getRelativeEffectAt(0).getAxisType() == AxisType.LOGARITHMIC) {
			d_scaleType = AxisType.LOGARITHMIC;
			d_scale = new BinnedScale(new LogScale(getRange()), 1, ForestPlot.BARWIDTH);
		}
	}

	@Override
	public int getNumRelativeEffects() {
		return d_effects.size() + (d_pooled != null ? 1 : 0);
	}

	@Override
	public RelativeEffect<?> getRelativeEffectAt(int i) {
		if (i < d_studies.size()) {
			return d_effects.get(i);
		} else if (i == d_studies.size() && d_pooled != null) {
			return d_pooled;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public BinnedScale getScale() {
		return d_scale;
	}

	@Override
	public AxisType getScaleType() {
		return d_scaleType;
	}

	@Override
	public Interval<Double> getRange() {
		List<ConfidenceInterval> cis = new ArrayList<ConfidenceInterval>();
		for (int i = 0; i < getNumRelativeEffects(); ++i) {
			cis.add(getRelativeEffectAt(i).getConfidenceInterval());
		}
		return getRange(cis, d_scaleType);
	}

	public static Interval<Double> niceIntervalLog(Interval<Double> interval) {
		return niceIntervalLog(interval.getLowerBound(), interval.getUpperBound());
	}

	@Override
	public String getStudyLabelAt(int i) {
		return isPooledRelativeEffect(i) ? "Combined" : d_studies.get(i).toString();
	}

	public static Interval<Double> niceIntervalLog(double min, double max) {
		double lowersign = Math.floor(anylog(min, 2));
		double uppersign = Math.ceil(anylog(max, 2));
		
		double minM = Math.pow(2,lowersign);
		double maxM = Math.pow(2, uppersign);
		
		return new Interval<Double>(Math.min(0.5, minM), Math.max(2, maxM));	
	}

	public static Interval<Double> niceIntervalLinear(Interval<Double> interval) {
		return niceIntervalLinear(interval.getLowerBound(), interval.getUpperBound());
	}

	public static Interval<Double> niceIntervalLinear(double min, double max) {
		int sign = getSignificanceLevel(min, max);
	
		double minM = Math.floor(min / Math.pow(10, sign)) * Math.pow(10, sign);
		double maxM = Math.ceil(max / Math.pow(10, sign)) * Math.pow(10, sign);
	
		double smallest = Math.pow(10, sign);
	
		return new Interval<Double>(Math.min(-smallest, minM), Math.max(smallest, maxM));
	}

	private static int getSignificanceLevel(double min, double max) {
		int signMax = (int) Math.floor(Math.log10(Math.abs(max)));
		int signMin = (int) Math.floor(Math.log10(Math.abs(min)));
		
		int sign = Math.max(signMax, signMin);
		return sign;
	}

	private static double anylog(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

	public static List<Integer> getTicks(BinnedScale scale, Scale toRender) {
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(scale.getBin(toRender.getMin()).bin);
		tickList.add(scale.getBin(toRender instanceof LogScale ? 1 : 0).bin);
		tickList.add(scale.getBin(toRender.getMax()).bin);
		return tickList;
	}

	@Override
	public String getCIlabelAt(int i) {
		return new RelativeEffectPresentation(getRelativeEffectAt(i)).toString();
	}

	@Override
	public List<Integer> getTicks() {
		return getTicks(getScale(), getScale().getScale());
	}

	public static List<String> getTickVals(BinnedScale scale, Scale toRender) {
		ArrayList<String> tickVals = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("####.####");
		tickVals.add(df.format(toRender.getMin()));
		tickVals.add(toRender instanceof LogScale ? df.format(1D) : df.format(0D));
		tickVals.add(df.format(toRender.getMax()));
		return tickVals;
	}

	@Override
	public List<String> getTickVals() {
		return getTickVals(getScale(), getScale().getScale());
	}

	private double getWeightAt(int index) {
		return (double) (((BasicRelativeEffect<?>)getRelativeEffectAt(index)).getSampleSize()) / d_max;
	}

	@Override
	public int getDiamondSize(int index) {
		BinnedScale tempbin = new BinnedScale(new IdentityScale(), 1, 10);
		return isPooledRelativeEffect(index) ? 8 : tempbin.getBin(getWeightAt(index)).bin * 2 + 1;
	}

	@Override
	public OutcomeMeasure getOutcomeMeasure() {
		return d_outcomeMeasure;
	}

	@Override
	public boolean isPooledRelativeEffect(int i) {
		return i >= d_studies.size();
	}

	@Override
	public String getHeterogeneityI2() {
		DecimalFormat df = new DecimalFormat("##0.0");
		final double x = d_pooled.getHeterogeneityI2();
		return Double.isNaN(x) ? "N/A" : (df.format(x) + "%");
	}

	@Override
	public String getHeterogeneity() {
		DecimalFormat df = new DecimalFormat("##0.00");
		return df.format(d_pooled.getHeterogeneity());
	}

	@Override
	public boolean hasPooledRelativeEffect() {
		return d_pooled != null;
	}

	protected abstract String getSubjectLabel();

	protected abstract String getBaselineLabel();

	@Override
	public String getLowValueFavors() {
		return (getOutcomeMeasure().getDirection().equals(Direction.HIGHER_IS_BETTER) ? getBaselineLabel() : getSubjectLabel());
	}

	@Override
	public String getHighValueFavors() {
		return (getOutcomeMeasure().getDirection().equals(Direction.HIGHER_IS_BETTER) ? getSubjectLabel() : getBaselineLabel());
	}

}
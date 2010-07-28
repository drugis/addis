/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.treeplot.BinnedScale;
import org.drugis.addis.treeplot.ForestPlot;
import org.drugis.addis.treeplot.IdentityScale;
import org.drugis.addis.treeplot.LinearScale;
import org.drugis.addis.treeplot.LogScale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {
	private List<Study> d_studies;
	private List<BasicRelativeEffect<?>> d_relEffects;
	private OutcomeMeasure d_outMeas;
	private Drug d_baseline;
	private Drug d_subject;
	private Class<? extends RelativeEffect<?>> d_type;
	private BinnedScale d_scale;
	private double d_max = 0.0;
	private AxisType d_scaleType;
	private RandomEffectsMetaAnalysis d_analysis;
	private PresentationModelFactory d_pmf;
	
	public ForestPlotPresentation(List<Study> studies, OutcomeMeasure om, Drug baseline, Drug subject,
			Class<? extends RelativeEffect<?>> type, PresentationModelFactory pmf, RandomEffectsMetaAnalysis analysis) {
		d_studies = new ArrayList<Study>();
		d_outMeas = om;
		d_baseline = baseline;
		d_subject = subject;
		d_type = type;
		d_relEffects = new ArrayList<BasicRelativeEffect<?>>();
		d_analysis = analysis;
		
		fillRelativeEffectList(studies);
		initScales();
		d_pmf = pmf;
	}

	private void fillRelativeEffectList(List<Study> studies) {
		if (d_analysis != null) {
			for (StudyArmsEntry entry : d_analysis.getStudyArms()) {
				addRelativeEffect(entry);
			}
		} else {
			for (Study s : studies) {
				addRelativeEffect(s);
			}
		}
	}
	
	public ForestPlotPresentation(RandomEffectsMetaAnalysis analysis, Class<? extends RelativeEffect<?>> type, PresentationModelFactory pmf) {
		this(analysis.getIncludedStudies(), analysis.getOutcomeMeasure(), analysis.getFirstDrug(), analysis.getSecondDrug(), type, pmf, analysis);
	}
		
	public ForestPlotPresentation(Study s, OutcomeMeasure om, Drug baseline, Drug subject,
			Class<? extends RelativeEffect<?>> type, PresentationModelFactory pmf) {
		this(Collections.singletonList((Study)s), om, baseline, subject, type, pmf, null);
	}

	private void addRelativeEffect(Study s) {
		d_studies.add(s);
		d_relEffects.add((BasicRelativeEffect<?>) RelativeEffectFactory.buildRelativeEffect(s, d_outMeas, d_baseline, d_subject, d_type));
	}
	
	private void addRelativeEffect(StudyArmsEntry entry) {
		d_studies.add(entry.getStudy());
		d_relEffects.add((BasicRelativeEffect<?>) RelativeEffectFactory.buildRelativeEffect(entry, d_outMeas, d_type));
	}
	
	public RelativeEffect<?> getMetaAnalysisEffect() {
		if (d_analysis == null) {
			return null;
		}
		
		return d_analysis.getRelativeEffect(d_type); 
	}

	private void initScales() {
		for (int i = 0; i < getNumRelativeEffects(); ++i) {
			if (!isCombined(i)) {
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
	
	public int getNumRelativeEffects() {
		int size = d_relEffects.size();
		return size + (((d_analysis != null) && (size > 1)) ? 1 : 0);
	}
	
	public RelativeEffect<?> getRelativeEffectAt(int i) {
		if (i < d_studies.size()) {
			return d_relEffects.get(i);
		} else if (i == d_studies.size()) {
			return getMetaAnalysisEffect();
		}
		throw new IndexOutOfBoundsException();
	}
	
	public BinnedScale getScale() {
		return d_scale;
	}
	
	public AxisType getScaleType() {
		return d_scaleType;
	}

	public Interval<Double> getRange() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < getNumRelativeEffects(); ++i) {
			double lowerBound = getRelativeEffectAt(i).getConfidenceInterval().getLowerBound();
			min = (lowerBound < min) ? lowerBound : min;
			double upperBound = getRelativeEffectAt(i).getConfidenceInterval().getUpperBound();
			max = (upperBound > max) ? upperBound : max;
		}
		
		if (d_scaleType == AxisType.LINEAR)
			return niceIntervalLinear(min,max);
		if (d_scaleType == AxisType.LOGARITHMIC)
			return niceIntervalLog(min, max);
		
		return new Interval<Double>(min, max);
	}
	
	public Drug getLowValueFavorsDrug() {
		return d_outMeas.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_baseline : d_subject;
	}
	
	public Drug getHighValueFavorsDrug() {
		return d_outMeas.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_subject : d_baseline;
	}
	
	public String getStudyLabelAt(int i) {
		return isCombined(i) ? "Combined" : d_studies.get(i).toString();
	}
	
	Interval<Double> niceIntervalLog(double min, double max) {
		double lowersign = Math.floor(anylog(min, 2));
		double uppersign = Math.ceil(anylog(max, 2));
		
		double minM = Math.pow(2,lowersign);
		double maxM = Math.pow(2, uppersign);
		
		return new Interval<Double>(Math.min(0.5, minM), Math.max(2, maxM));	
	}
	
	private Interval<Double> niceIntervalLinear(double min, double max) {
		int sign = getSignificanceLevel(min, max);

		double minM = Math.floor(min / Math.pow(10, sign)) * Math.pow(10, sign);
		double maxM = Math.ceil(max / Math.pow(10, sign)) * Math.pow(10, sign);

		double smallest = Math.pow(10, sign);

		return new Interval<Double>(Math.min(-smallest, minM), Math.max(smallest, maxM));
	}

	private int getSignificanceLevel(double min, double max) {
		int signMax = (int) Math.floor(Math.log10(Math.abs(max)));
		int signMin = (int) Math.floor(Math.log10(Math.abs(min)));
		
		int sign = Math.max(signMax, signMin);
		return sign;
	}
	
	private double anylog(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

	public LabeledPresentation getCIlabelAt(int i) {
		return d_pmf.getLabeledModel(getRelativeEffectAt(i));
	}
	
	public List<Integer> getTicks() {
		Interval<Double> range = getRange();
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(d_scale.getBin(range.getLowerBound()).bin);
		tickList.add(d_scale.getBin(d_scaleType == AxisType.LOGARITHMIC ? 1 : 0).bin);
		tickList.add(d_scale.getBin(range.getUpperBound()).bin);
		return tickList;
	}

	public List<String> getTickVals() {
		Interval<Double> range = getRange();
		ArrayList<String> tickVals = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("####.####");
		tickVals.add(df.format(range.getLowerBound()));
		tickVals.add(d_scaleType == AxisType.LOGARITHMIC ? df.format(1D) : df.format(0D));
		tickVals.add(df.format(range.getUpperBound()));
		return tickVals;
	}
	
	private double getWeightAt(int index) {
		return (double) (((BasicRelativeEffect<?>)getRelativeEffectAt(index)).getSampleSize()) / d_max;
	}

	public int getDiamondSize(int index) {
		BinnedScale tempbin = new BinnedScale(new IdentityScale(), 1, 10);
		return isCombined(index) ? 0 : tempbin.getBin(getWeightAt(index)).bin * 2 + 1;
	}

	public OutcomeMeasure getOutcomeMeasure() {
		return d_outMeas;
	}
	
	public boolean isCombined(int i) {
		return i >= d_studies.size();
	}

	public String getHeterogeneityI2() {
		DecimalFormat df = new DecimalFormat("##0.0");
		return df.format(d_analysis.getRelativeEffect(d_type).getHeterogeneityI2()) + "%";
	}
	
	public String getHeterogeneity() {
		DecimalFormat df = new DecimalFormat("##0.00");
		return df.format(d_analysis.getRelativeEffect(d_type).getHeterogeneity());
	}

	public boolean isMetaAnalysis() {
		return (d_analysis != null && d_relEffects.size() > 1);
	}
}

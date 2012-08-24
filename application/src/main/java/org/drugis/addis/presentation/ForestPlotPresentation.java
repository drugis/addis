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

package org.drugis.addis.presentation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffectFactory;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.forestplot.IdentityScale;
import org.drugis.addis.forestplot.LinearScale;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.forestplot.Scale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {
	private List<Study> d_studies;
	private List<BasicRelativeEffect<?>> d_relEffects;
	private OutcomeMeasure d_outMeas;
	private TreatmentDefinition d_baseline;
	private TreatmentDefinition d_subject;
	private Class<? extends RelativeEffect<?>> d_type;
	private BinnedScale d_scale;
	private double d_max = 0.0;
	private AxisType d_scaleType;
	private RandomEffectsMetaAnalysis d_analysis;
	private PresentationModelFactory d_pmf;
	
	public ForestPlotPresentation(List<Study> studies, OutcomeMeasure om, TreatmentDefinition baseline, TreatmentDefinition subject,
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
		this(analysis.getIncludedStudies(), analysis.getOutcomeMeasure(), analysis.getFirstAlternative(), analysis.getSecondAlternative(), type, pmf, analysis);
	}
	
	public static ForestPlotPresentation createStudyForestPlot(Study s, OutcomeMeasure om, Arm arm1, Arm arm2,
			Class<? extends RelativeEffect<?>> type, PresentationModelFactory pmf) {
		List<Study> studyList = Collections.singletonList((Study)s);
		TreatmentDefinition catSet1 = s.getTreatmentDefinition(arm1);
		TreatmentDefinition catSet2 = s.getTreatmentDefinition(arm2);
		StudyArmsEntry entry = new StudyArmsEntry(s, arm1, arm2);
		RandomEffectsMetaAnalysis analysis = new RandomEffectsMetaAnalysis("", om, catSet1, catSet2, Collections.singletonList(entry), false);
		return new ForestPlotPresentation(studyList, om, catSet1, catSet2, type, pmf, analysis);
	}
		
	private void addRelativeEffect(Study s) {
		d_studies.add(s);
		d_relEffects.add((BasicRelativeEffect<?>) 
				RelativeEffectFactory.buildRelativeEffect(s, d_outMeas, d_baseline, d_subject, d_type, d_analysis.getIsCorrected()));
	}
	
	private void addRelativeEffect(StudyArmsEntry entry) {
		d_studies.add(entry.getStudy());
		BasicRelativeEffect<?> relEffect = (BasicRelativeEffect<?>) 
			RelativeEffectFactory.buildRelativeEffect(entry, d_outMeas, d_type, d_analysis.getIsCorrected());

		d_relEffects.add(relEffect);
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
		List<ConfidenceInterval> cis = new ArrayList<ConfidenceInterval>();
		for (int i = 0; i < getNumRelativeEffects(); ++i) {
			cis.add(getRelativeEffectAt(i).getConfidenceInterval());
		}
		return getRange(cis, d_scaleType);
	}

	static Interval<Double> getRange(List<ConfidenceInterval> cis, AxisType scaleType) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (ConfidenceInterval ci : cis) {
			min = Math.min((double) ci.getLowerBound(), min);
			max = Math.max((double) ci.getUpperBound(), max);
		}
		
		Interval<Double> interval = new Interval<Double>(min, max);
		
		if (scaleType == AxisType.LINEAR)
			return niceIntervalLinear(interval);
		if (scaleType == AxisType.LOGARITHMIC)
			return niceIntervalLog(interval);
		
		return interval;
	}
	
	public TreatmentDefinition getLowValueFavorsTreatment() {
		return d_outMeas.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_baseline : d_subject;
	}
	
	public TreatmentDefinition getHighValueFavorsTreatment() {
		return d_outMeas.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_subject : d_baseline;
	}
	
	public String getStudyLabelAt(int i) {
		return isCombined(i) ? "Combined" : d_studies.get(i).toString();
	}
	
	public static Interval<Double> niceIntervalLog(Interval<Double> interval) {
		return niceIntervalLog(interval.getLowerBound(), interval.getUpperBound());
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

	public LabeledPresentation getCIlabelAt(int i) {
		return d_pmf.getLabeledModel(getRelativeEffectAt(i));
	}
	
	public List<Integer> getTicks() {
		return getTicks(getScale(), getScale().getScale());
	}

	public static List<Integer> getTicks(BinnedScale scale, Scale toRender) {
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(scale.getBin(toRender.getMin()).bin);
		tickList.add(scale.getBin(toRender instanceof LogScale ? 1 : 0).bin);
		tickList.add(scale.getBin(toRender.getMax()).bin);
		return tickList;
	}
	
	public List<String> getTickVals() {
		return getTickVals(getScale(), getScale().getScale());
	}

	public static List<String> getTickVals(BinnedScale scale, Scale toRender) {
		ArrayList<String> tickVals = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("####.####");
		tickVals.add(df.format(toRender.getMin()));
		tickVals.add(toRender instanceof LogScale ? df.format(1D) : df.format(0D));
		tickVals.add(df.format(toRender.getMax()));
		return tickVals;
	}

	private double getWeightAt(int index) {
		return (double) (((BasicRelativeEffect<?>)getRelativeEffectAt(index)).getSampleSize()) / d_max;
	}

	public int getDiamondSize(int index) {
		BinnedScale tempbin = new BinnedScale(new IdentityScale(), 1, 10);
		return isCombined(index) ? 8 : tempbin.getBin(getWeightAt(index)).bin * 2 + 1;
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

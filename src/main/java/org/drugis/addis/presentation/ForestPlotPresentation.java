package org.drugis.addis.presentation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Endpoint.Direction;
import org.drugis.addis.entities.RelativeEffect.AxisType;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.entities.metaanalysis.RelativeEffectFactory;
import org.drugis.addis.plot.BinnedScale;
import org.drugis.addis.plot.ForestPlot;
import org.drugis.addis.plot.IdentityScale;
import org.drugis.addis.plot.LinearScale;
import org.drugis.addis.plot.LogScale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {
	private List<Study> d_studies;
	private List<RelativeEffect<?>> d_relEffects;
	private Endpoint d_endpoint;
	private Drug d_baseline;
	private Drug d_subject;
	private Class<? extends RelativeEffect<?>> d_type;
	private BinnedScale d_scale;
	private double d_max = 0.0;
	private AxisType d_scaleType;
	private RandomEffectsMetaAnalysis d_analysis;
	
	public ForestPlotPresentation(List<Study> studies, Endpoint e, Drug baseline, Drug subject,
			Class<? extends RelativeEffect<?>> type) {
		d_studies = new ArrayList<Study>();
		d_endpoint = e;
		d_baseline = baseline;
		d_subject = subject;
		d_type = type;
		d_relEffects = new ArrayList<RelativeEffect<?>>();
		for (Study s : studies) {
			addRelativeEffect(s, subject);
		}
		initScales();
	}
	
	public ForestPlotPresentation(RandomEffectsMetaAnalysis analysis, Class<? extends RelativeEffect<?>> type) {
		this(analysis.getStudies(), analysis.getEndpoint(), analysis.getFirstDrug(), analysis.getSecondDrug(), type);
		d_analysis = analysis;
	}
		
	public ForestPlotPresentation(BasicStudy s, Endpoint e, Drug baseline, Drug subject,
			Class<? extends RelativeEffect<?>> type) {
		this(Collections.singletonList((Study)s), e, baseline, subject, type);
	}

	private void addRelativeEffect(Study s, Drug subject) {
		d_studies.add(s);
		d_relEffects.add(RelativeEffectFactory.buildRelativeEffect(s, d_endpoint, d_baseline, subject, d_type));
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
				d_max = Math.max(getRelativeEffectAt(i).getSampleSize(), d_max);
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
		return d_endpoint.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_baseline : d_subject;
	}
	
	public Drug getHighValueFavorsDrug() {
		return d_endpoint.getDirection().equals(Direction.HIGHER_IS_BETTER) ? d_subject : d_baseline;
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

	public String getCIlabelAt(int i) {
		RelativeEffect<?> e = getRelativeEffectAt(i);
		return formatNumber2D(e.getRelativeEffect()) + " (" + formatNumber2D(e.getConfidenceInterval().getLowerBound()) 
									 + ", " + formatNumber2D(e.getConfidenceInterval().getUpperBound()) + ")";
	}
	
	public List<Integer> getTicks() {
		Interval<Double> range = getRange();
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(d_scale.getBin(range.getLowerBound()).bin);
		tickList.add(d_scale.getBin(d_scaleType == AxisType.LOGARITHMIC ? 1 : 0).bin);
		tickList.add(d_scale.getBin(range.getUpperBound()).bin);
		return tickList;
	}
	
	private String formatNumber2D(double x) {
		DecimalFormat df = new DecimalFormat("###0.00");
		return df.format(x);
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
		return (double) (getRelativeEffectAt(index).getSampleSize()) / d_max;
	}

	public int getDiamondSize(int index) {
		double weight = getWeightAt(index);
		BinnedScale tempbin = new BinnedScale(new IdentityScale(), 1, 10);
		return isCombined(index) ? 0 : tempbin.getBin(weight).bin * 2 + 1;
	}

	public Endpoint getEndpoint() {
		return d_endpoint;
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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.plot.BinnedScale;
import org.drugis.addis.plot.ForestPlot;
import org.drugis.addis.plot.LinearScale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {

	private List<RelativeEffect<?>> d_relEffects;
	private BinnedScale d_scale;

	@SuppressWarnings("unchecked")
	public ForestPlotPresentation(List<RelativeEffect<?>> relEffects) throws IllegalArgumentException {
		//Checks for consistent list of Relative Effects
		if (relEffects.isEmpty())
			throw new IllegalArgumentException("List of Relative Effects is Empty upon Constructing a ForestPlotPresentation.");
		
		Endpoint uniqueE = relEffects.get(0).getEndpoint(); 
		Drug base = relEffects.get(0).getBaseline().getPatientGroup().getDrug();
		Drug subject = relEffects.get(0).getSubject().getPatientGroup().getDrug();
		Class<RelativeEffect<?>> a = (Class<RelativeEffect<?>>) relEffects.get(0).getClass();
		for(RelativeEffect<?> r : relEffects) {
			if (!uniqueE.equals(r.getEndpoint()))
				throw new IllegalArgumentException("Relative Effects do not have same Endpoints.");
			if (!base.equals(r.getBaseline().getPatientGroup().getDrug()))
				throw new IllegalArgumentException("Relative Effects do not have same Drugs.");
			if (!subject.equals(r.getSubject().getPatientGroup().getDrug()))
				throw new IllegalArgumentException("Relative Effects do not have same Drugs.");
			if (!r.getClass().equals(a))
				throw new IllegalArgumentException("Relative Effects of different Type.");
		}		
		
		d_relEffects = relEffects;
		d_scale = new BinnedScale(new LinearScale(getRange()), 1, ForestPlot.BARWIDTH);
	}
	
	public int getNumRelativeEffects() {
		return d_relEffects.size();
	}
	
	public RelativeEffect<?> getRelativeEffectAt(int i) {
		return d_relEffects.get(i);
	}
	
	public BinnedScale getScale() {
		return d_scale;
	}

	public Interval<Double> getRange() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < d_relEffects.size(); ++i) {
			double lowerBound = d_relEffects.get(i).getConfidenceInterval().getLowerBound();
			min = (lowerBound < min) ? lowerBound : min;
			double upperBound = d_relEffects.get(i).getConfidenceInterval().getUpperBound();
			max = (upperBound > max) ? upperBound : max;
		}
		
		return niceInterval(min,max);
	}
	
	public String getBaselineDrugLabel() {
		return d_relEffects.size() != 0 ? d_relEffects.get(0).getBaseline().getPatientGroup().getDrug().toString() : "";
	}
	
	public String getSubjectDrugLabel() {
		return d_relEffects.size() != 0 ? d_relEffects.get(0).getSubject().getPatientGroup().getDrug().toString() : "";
	}
	
	public String getStudyLabelAt(int i) {
		return d_relEffects.size() > i ? d_relEffects.get(i).getBaseline().getPatientGroup().getStudy().toString() : "";
	}
	
	
	private Interval<Double> niceInterval(double min, double max) {
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

	public String getCIlabelAt(int i) {
		RelativeEffect<?> e = d_relEffects.get(i);
		return round2D(e.getRelativeEffect()) + " (" + round2D(e.getConfidenceInterval().getLowerBound()) 
									 + ", " + round2D(e.getConfidenceInterval().getUpperBound()) + ")";
	}
	
	public List<Integer> getTicks() {
		Interval<Double> range = getRange();
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(d_scale.getBin(range.getLowerBound()).bin);
		tickList.add(d_scale.getBin(0).bin); //FIXME: Also for logarithmic Scales
		tickList.add(d_scale.getBin(range.getUpperBound()).bin);
		return tickList;
	}
	
	private double round2D(double x) {
		return Math.round(x * 100.0) / 100.0;
	}

	public List<Double> getTickVals() {
		Interval<Double> range = getRange();
		ArrayList<Double> tickVals = new ArrayList<Double>();
		tickVals.add(range.getLowerBound());
		tickVals.add(0D); //FIXME: Also for logarithmic Scales
		tickVals.add(range.getUpperBound());
		return tickVals;
	}
	
	

}

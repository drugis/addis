package org.drugis.addis.presentation;

import java.util.List;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.forestplot.BinnedScale;
import org.drugis.common.Interval;

public interface ForestPlotPresentation {

	public abstract int getNumRelativeEffects();

	public abstract RelativeEffect<?> getRelativeEffectAt(int i);

	public abstract BinnedScale getScale();

	public abstract AxisType getScaleType();

	public abstract Interval<Double> getRange();

	public abstract String getLowValueFavors();

	public abstract String getHighValueFavors();

	public abstract String getStudyLabelAt(int i);

	public abstract String getCIlabelAt(int i);

	public abstract List<Integer> getTicks();

	public abstract List<String> getTickVals();

	public abstract int getDiamondSize(int index);

	public abstract OutcomeMeasure getOutcomeMeasure();

	public abstract boolean isPooledRelativeEffect(int i);

	public abstract String getHeterogeneityI2();

	public abstract String getHeterogeneity();

	public abstract boolean hasPooledRelativeEffect();

}
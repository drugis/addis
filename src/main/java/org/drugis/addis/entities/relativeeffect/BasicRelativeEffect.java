package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.Measurement;

public interface BasicRelativeEffect<T extends Measurement>  extends RelativeEffect<T>{

	public static final String PROPERTY_SAMPLESIZE = "sampleSize";

	public abstract Integer getSampleSize();

	public abstract T getSubject();

	public abstract T getBaseline();

	public abstract Double getError();

	public abstract AxisType getAxisType();

}
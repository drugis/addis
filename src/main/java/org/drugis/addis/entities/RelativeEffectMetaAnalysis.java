package org.drugis.addis.entities;

public interface RelativeEffectMetaAnalysis<T extends Measurement> extends RelativeEffect<T> {
	
	public double getHeterogeneity();
}

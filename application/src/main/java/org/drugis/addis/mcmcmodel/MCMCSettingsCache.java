package org.drugis.addis.mcmcmodel;

import org.drugis.addis.entities.data.MCMCSettings;

public final class MCMCSettingsCache {

	private final Integer d_inferenceIterations;
	private final Integer d_simulationIterations;
	private final Integer d_thinningInterval;
	private final Integer d_tuningIterations;
	private final Double d_varianceScalingFactor;

	public MCMCSettingsCache(MCMCSettings settings) {
		d_inferenceIterations = settings.getInferenceIterations();
		d_simulationIterations = settings.getSimulationIterations();
		d_thinningInterval = settings.getThinningInterval();
		d_tuningIterations = settings.getTuningIterations();
		d_varianceScalingFactor = settings.getVarianceScalingFactor();
	}

	public Double getVarianceScalingFactor() {
		return d_varianceScalingFactor;
	}

	public Integer getTuningIterations() {
		return d_tuningIterations;
	}

	public Integer getThinningInterval() {
		return d_thinningInterval;
	}

	public Integer getSimulationIterations() {
		return d_simulationIterations;
	}

	public Integer getInferenceIterations() {
		return d_inferenceIterations;
	}
	
}

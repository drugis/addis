package org.drugis.addis.entities.mtcwrapper;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

import com.jgoodies.binding.beans.Observable;

public interface MCMCModelWrapper extends Observable {

	public static final String PROPERTY_DESTROYED = "destroyed";

	/**
	 * Get a human-readable description of the analysis type.
	 */
	public abstract String getDescription();

	/**
	 * Whether or not the model has saved results (rather than newly-computed ones)
	 */
	public abstract boolean isSaved();

	/**
	 * Whether or not the user accepted the results/convergence. If so, the results can be saved.
	 */
	public abstract boolean isApproved();

	/** 
	 * Whether or not the model should be cleaned up on the next invocation from NetworkMetaAnalysis.
	 * This will cause NetworkMetaAnalysis to create a new instance of a AbstractSimulationModel.
	 */
	public abstract void selfDestruct();

	/**
	 * Returns true if selfDestruct called previously, false otherwise.
	 */
	public abstract boolean getDestroyed();

	/**
	 * @see org.drugis.mtc.MCMCResults#getParameters()
	 */
	public abstract Parameter[] getParameters();

	/**
	 * @see org.drugis.mtc.MixedTreatmentComparison#getSettings()
	 */
	public abstract MCMCSettingsCache getSettings();

	/**
	 * Get a convergence summary for the given parameter.
	 * The parameter must occur in the list returned by {@link #getParameters()}.
	 */
	public abstract ConvergenceSummary getConvergenceSummary(Parameter p);

	/**
	 * Get a quantile summary for the given parameter.
	 * The parameter must occur in the list returned by {@link #getParameters()}, 
	 * or be a relative effect from {@link #getRelativeEffect(DrugSet, DrugSet)}.
	 */
	public abstract QuantileSummary getQuantileSummary(Parameter ip);

	/**
	 * Get the underlying MCMC model.
	 * Can not be called when {@link #isSaved()} is true.
	 */
	public abstract MCMCModel getModel();

}
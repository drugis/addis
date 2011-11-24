package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.pietschy.wizard.InvalidStateException;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueModel;

public abstract class CriteriaAndAlternativesPresentation<Alternative extends Comparable<Alternative>> {
	final SelectableOptionsModel<OutcomeMeasure> d_selectedCriteria;
	protected final HashMap<Alternative, ModifiableHolder<Boolean>> d_alternativeEnabledMap;
	final SelectableOptionsModel<Alternative> d_selectedAlternatives;
	protected final ModifiableHolder<AnalysisType> d_analysisTypeHolder;
	protected final HashMap<OutcomeMeasure, ModifiableHolder<Boolean>> d_outcomeEnabledMap;
	protected final ModifiableHolder<Alternative> d_baselineModel;
	protected final ValueHolder<Indication> d_indication;

	public CriteriaAndAlternativesPresentation(final ValueHolder<Indication> indication, final ModifiableHolder<AnalysisType> analysisType) {
		d_indication = indication;
		d_analysisTypeHolder = analysisType;
		d_selectedCriteria = new SelectableOptionsModel<OutcomeMeasure>();
		d_alternativeEnabledMap = new HashMap<Alternative, ModifiableHolder<Boolean>>();
		d_selectedAlternatives = new SelectableOptionsModel<Alternative>();
		d_outcomeEnabledMap = new HashMap<OutcomeMeasure, ModifiableHolder<Boolean>>();
		d_baselineModel = new ModifiableHolder<Alternative>();
		
		PropertyChangeListener resetValuesListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				reset();
			}
		};
		reset();

		d_indication.addValueChangeListener(resetValuesListener);
		d_analysisTypeHolder.addValueChangeListener(resetValuesListener);
	}
	
	protected abstract void reset();

	public abstract ObservableList<OutcomeMeasure> getCriteriaListModel();
	
	public ValueHolder<Boolean> getCriterionSelectedModel(OutcomeMeasure om) {
		return d_selectedCriteria.getSelectedModel(om);
	}
	
	public ValueHolder<Boolean> getAlternativeSelectedModel(Alternative alternative) {
		return d_selectedAlternatives.getSelectedModel(alternative);
	}
	
	public abstract ObservableList<Alternative> getAlternativesListModel();

	public ValueHolder<Boolean> getAlternativeEnabledModel(Alternative e) {
		return d_alternativeEnabledMap.get(e);
	}
	
	public ValueHolder<Boolean> getCriterionEnabledModel(OutcomeMeasure out) {
		return d_outcomeEnabledMap.get(out);
	}

	public abstract ValueHolder<Boolean> getCompleteModel();
	
	public BenefitRiskAnalysis<Alternative> saveAnalysis(Domain domain, String id) throws InvalidStateException, EntityIdExistsException {
		BenefitRiskAnalysis<Alternative> brAnalysis = createAnalysis(id);

		if(domain.getBenefitRiskAnalyses().contains(brAnalysis))
			throw new EntityIdExistsException("Benefit Risk Analysis with this ID already exists in domain");

		domain.getBenefitRiskAnalyses().add(brAnalysis);
		return brAnalysis;
	}
	
	public abstract BenefitRiskAnalysis<Alternative> createAnalysis(String id) throws InvalidStateException;

	public ObservableList<Alternative> getSelectedAlternatives() {
		return d_selectedAlternatives.getSelectedOptions();
	}
	
	public ObservableList<OutcomeMeasure> getSelectedCriteria() {
		return d_selectedCriteria.getSelectedOptions();
	}

	public ValueModel getBaselineModel() {
		return d_baselineModel;
	}
	
	protected void initOutcomeMeasures() {
		d_selectedCriteria.clear();
		for (OutcomeMeasure om : getCriteriaListModel()) {
			d_selectedCriteria.addOption(om, false);
		}
		// create outcome enabled models
		for (OutcomeMeasure om : getCriteriaListModel()) {
			ModifiableHolder<Boolean> val = new ModifiableHolder<Boolean>(getCriterionShouldBeEnabled(om));
			d_outcomeEnabledMap.put(om, val);
		}
	}

	protected void initAlternatives(Collection<Alternative> alternatives) {
		// create alternative selected models
		for (Alternative d : alternatives) {
			ModifiableHolder<Boolean> val = d_selectedAlternatives.addOption(d, false);
//			val.addPropertyChangeListener(d_completeHolder);
		}
		// create alternative enabled models (they use the selected models -- don't merge the loops!)
		for (Alternative d : alternatives) {
			d_alternativeEnabledMap.put(d, new ModifiableHolder<Boolean>(true));
//			d_alternativeEnabledMap.put(d, createAlternativeEnabledModel(d));
		}
	}
	
	private boolean getCriterionShouldBeEnabled(OutcomeMeasure out) {
		if(getCriterionSelectedModel(out).getValue() == true) return true;
		else return (d_analysisTypeHolder.getValue() == AnalysisType.SMAA) || (d_selectedCriteria.getSelectedOptions().size() < 2);
	}
}

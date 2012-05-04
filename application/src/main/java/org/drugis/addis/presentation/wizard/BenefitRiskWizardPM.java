/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.DecisionContext;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.presentation.AbstractBenefitRiskPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.ValueModel;

public class BenefitRiskWizardPM extends AbstractAnalysisWizardPresentation<BenefitRiskAnalysis<?>> {

	@SuppressWarnings("serial")
	public static class CompleteHolder extends ModifiableHolder<Boolean> implements PropertyChangeListener {
		private final ValueHolder<BRAType> d_evidenceType;
		private final ValueModel d_studyComplete;
		private final ValueModel d_synthesisComplete;

		public CompleteHolder(ValueHolder<BRAType> evidenceType, ValueModel studyComplete, ValueModel synthesisComplete) {
			d_evidenceType = evidenceType;
			d_studyComplete = studyComplete;
			d_synthesisComplete = synthesisComplete;
			d_evidenceType.addValueChangeListener(this);
			d_studyComplete.addValueChangeListener(this);
			d_synthesisComplete.addValueChangeListener(this);
			super.setValue((Boolean)isComplete());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			super.setValue((Boolean)isComplete());
		}
		
		@Override
		public void setValue(Object value) {
			throw new RuntimeException();
		}

		private boolean isComplete() {
			return d_evidenceType.getValue().equals(BRAType.Synthesis) ? synthesisComplete() : singleStudyComplete();
		}

		private boolean synthesisComplete() {
			return (Boolean) d_synthesisComplete.getValue();
		}

		private boolean singleStudyComplete() {
			return (Boolean) d_studyComplete.getValue();
		}
	}

	public enum BRAType {
		SingleStudy,
		Synthesis
	}

	private CompleteHolder d_completeHolder;
	private ModifiableHolder<BRAType> d_evidenceTypeHolder;
	private ModifiableHolder<AnalysisType> d_analysisTypeHolder;

	private final StudyCriteriaAndAlternativesPresentation d_studyCritAlt;
	private final MetaCriteriaAndAlternativesPresentation d_metaCritAlt;
	private ValueHolder<Boolean> d_includeDescriptives = new ModifiableHolder<Boolean>(false);
	private List<AbstractBenefitRiskPresentation.DecisionContextField> d_decisionContextFields;
	private DecisionContext d_decisionContext = new DecisionContext();


	public BenefitRiskWizardPM(Domain d) {
		super(d, d.getBenefitRiskAnalyses());
		d_evidenceTypeHolder = new ModifiableHolder<BRAType>(BRAType.Synthesis);
		d_analysisTypeHolder = new ModifiableHolder<AnalysisType>(AnalysisType.SMAA);
		
		d_studyCritAlt = new StudyCriteriaAndAlternativesPresentation(d_indicationHolder, d_analysisTypeHolder, d_domain.getStudies());
		d_metaCritAlt = new MetaCriteriaAndAlternativesPresentation(d_indicationHolder, d_analysisTypeHolder, d_domain.getMetaAnalyses());
		
		d_completeHolder = new CompleteHolder(d_evidenceTypeHolder, d_studyCritAlt.getCompleteModel(), d_metaCritAlt.getCompleteModel());
		
		d_decisionContextFields = AbstractBenefitRiskPresentation.createDecisionContextFields(d_decisionContext);
	}

	public ValueHolder<Boolean> getCompleteModel() {
		return d_completeHolder;
	}

	public ValueModel getEvidenceTypeHolder() {
		return d_evidenceTypeHolder;
	}

	public ValueModel getAnalysisTypeHolder() {
		return d_analysisTypeHolder;
	}

	public StudyCriteriaAndAlternativesPresentation getStudyBRPresentation() {
		return d_studyCritAlt;
	}

	public  MetaCriteriaAndAlternativesPresentation getMetaBRPresentation() {
		return d_metaCritAlt;
	}
	
	public ValueHolder<Boolean> getIncludeDescriptivesModel() {
		return d_includeDescriptives ;
	}

	public List<AbstractBenefitRiskPresentation.DecisionContextField> getDecisionContextFields() {
		return d_decisionContextFields;
	}

	public DecisionContext getDecisionContext() {
		return d_includeDescriptives.getValue() ? d_decisionContext : null;
	}

	@Override
	public BenefitRiskAnalysis<?> createAnalysis(String name) {
		return d_evidenceTypeHolder.getValue().equals(BRAType.SingleStudy) ? 
				d_studyCritAlt.createAnalysis(name, d_decisionContext) :
				d_metaCritAlt.createAnalysis(name, d_decisionContext);
	}
}

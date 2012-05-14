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

package org.drugis.addis.presentation;

import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.DecisionContext;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

import fi.smaa.jsmaa.simulator.BuildQueue;

@SuppressWarnings("serial")
public abstract class AbstractBenefitRiskPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> 
extends PresentationModel<AnalysisType> {
	public static final String DC_THERAPEUTIC_CONTEXT_NAME = "Therapeutic context";
	public static final String DC_COMPARATOR_NAME = "Comparator";
	public static final String DC_TIME_HORIZON_NAME = "Time horizon";
	public static final String DC_STAKEHOLDER_PERSPECTIVE_NAME = "Stakeholder perspective";
	public static final String DC_THERAPEUTIC_CONTEXT_HELP = "Specify the therapeutic context: the product being assessed, its indication, target population, formulation, dosage and contra-indications.";
	public static final String DC_COMPARATOR_HELP = "Specify the comparator: standard of care, placebo, best in class, or...";
	public static final String DC_TIME_HORIZON_HELP = "Specify the time horizon: the duration of exposure to the product, the time period over which the outcomes should be measured.";
	public static final String DC_STAKEHOLDER_PERSPECTIVE_HELP = "Describe the stakeholder perspective: motivation for the selection of benefit-risk attributes, and the outcomes by which they are measured.";
	
	public static class DecisionContextField {
	
		private final String d_name;
		private final String d_helpText;
		private final ValueModel d_model;
	
		public DecisionContextField(String name, String helpText, ValueModel model) {
			d_name = name;
			d_helpText = helpText;
			d_model = model;
		}
	
		public String getName() {
			return d_name;
		}
	
		public String getHelpText() {
			return d_helpText;
		}
	
		public ValueModel getModel() {
			return d_model;
		}
	
	}

	protected PresentationModelFactory d_pmf;
	protected BuildQueue d_buildQueue;
	private SMAAPresentation<Alternative, AnalysisType> d_smaaPresentation;
	private LyndOBrienPresentation<Alternative, AnalysisType> d_lyndOBrienPresentation;

	public AbstractBenefitRiskPresentation(AnalysisType bean, PresentationModelFactory pmf) {
		super(bean);
		d_pmf = pmf;
		
		initSimulations();
		if (bean.getAnalysisType().equals(org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType.SMAA)) {
			d_smaaPresentation = new SMAAPresentation<Alternative, AnalysisType>(getBean());
			startSMAA();
		} else {
			d_lyndOBrienPresentation = new LyndOBrienPresentation<Alternative, AnalysisType>(getBean());
			startLyndOBrien();
		}
	}
	
	protected abstract void startSMAA();
	protected abstract void startLyndOBrien();
	
	public PresentationModelFactory getFactory() {
		return d_pmf;
	}
	
	public abstract ValueHolder<Boolean> getMeasurementsReadyModel();

	public SMAAPresentation<Alternative, AnalysisType> getSMAAPresentation() {
		return d_smaaPresentation;
	}

	public LyndOBrienPresentation<Alternative, AnalysisType> getLyndOBrienPresentation() {
		return d_lyndOBrienPresentation;
	}
	
	public BRATTableModel<Alternative, AnalysisType> createBRATTableModel(Alternative subject) {
		return new BRATTableModel<Alternative, AnalysisType>(getBean(), subject);
	}

	protected abstract void initSimulations();

	public static List<DecisionContextField> createDecisionContextFields(DecisionContext decisionContext) {
		PresentationModel<DecisionContext> dcModel = new PresentationModel<DecisionContext>(decisionContext);
		List<DecisionContextField> asList = Arrays.asList(
				new AbstractBenefitRiskPresentation.DecisionContextField(DC_THERAPEUTIC_CONTEXT_NAME, DC_THERAPEUTIC_CONTEXT_HELP, dcModel.getModel(DecisionContext.PROPERTY_THERAPEUTIC_CONTEXT)), 
				new AbstractBenefitRiskPresentation.DecisionContextField(DC_COMPARATOR_NAME, DC_COMPARATOR_HELP, dcModel.getModel(DecisionContext.PROPERTY_COMPARATOR)),
				new AbstractBenefitRiskPresentation.DecisionContextField(DC_TIME_HORIZON_NAME, DC_TIME_HORIZON_HELP, dcModel.getModel(DecisionContext.PROPERTY_TIME_HORIZON)),
				new AbstractBenefitRiskPresentation.DecisionContextField(DC_STAKEHOLDER_PERSPECTIVE_NAME, DC_STAKEHOLDER_PERSPECTIVE_HELP, dcModel.getModel(DecisionContext.PROPERTY_STAKEHOLDER_PERSPECTIVE))
				);
		return asList;
	}
}

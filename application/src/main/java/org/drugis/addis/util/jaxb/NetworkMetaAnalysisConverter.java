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

package org.drugis.addis.util.jaxb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.data.AnalysisArms;
import org.drugis.addis.entities.data.ArmReference;
import org.drugis.addis.entities.data.ConsistencyResults;
import org.drugis.addis.entities.data.EvidenceTypeEnum;
import org.drugis.addis.entities.data.InconsistencyParameter;
import org.drugis.addis.entities.data.InconsistencyResults;
import org.drugis.addis.entities.data.MCMCSettings;
import org.drugis.addis.entities.data.MetaAnalysisAlternative;
import org.drugis.addis.entities.data.NodeSplitResults;
import org.drugis.addis.entities.data.ParameterSummary;
import org.drugis.addis.entities.data.QuantileType;
import org.drugis.addis.entities.data.RelativeEffectParameter;
import org.drugis.addis.entities.data.RelativeEffectQuantileSummary;
import org.drugis.addis.entities.data.RelativeEffectsQuantileSummary;
import org.drugis.addis.entities.data.RelativeEffectsSummary;
import org.drugis.addis.entities.data.TreatmentDefinitionPair;
import org.drugis.addis.entities.data.VarianceParameter;
import org.drugis.addis.entities.data.VarianceParameterType;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.util.jaxb.JAXBConvertor.ConversionException;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.parameterization.ParameterComparator;
import org.drugis.mtc.parameterization.SplitParameter;
import org.drugis.mtc.presentation.ConsistencyWrapper;
import org.drugis.mtc.presentation.InconsistencyWrapper;
import org.drugis.mtc.presentation.MTCModelWrapper;
import org.drugis.mtc.presentation.NodeSplitWrapper;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;
import org.drugis.mtc.summary.SimpleMultivariateNormalSummary;

import edu.uci.ics.jung.graph.util.Pair;

public class NetworkMetaAnalysisConverter {
	
	// From JAXB to ADDIS (load)
	public static NetworkMetaAnalysis load(org.drugis.addis.entities.data.NetworkMetaAnalysis nma, Domain domain) throws ConversionException {
		String name = nma.getName();
		Indication indication = JAXBConvertor.findNamedItem(domain.getIndications(), nma.getIndication().getName());
		org.drugis.addis.entities.OutcomeMeasure om = JAXBConvertor.findOutcomeMeasure(domain, nma);
		List<Study> studies = new ArrayList<Study>();		
		List<TreatmentDefinition> defs = new ArrayList<TreatmentDefinition>();
		Map<Study, Map<TreatmentDefinition, Arm>> armMap = new HashMap<Study, Map<TreatmentDefinition, Arm>>();
		for (MetaAnalysisAlternative a : nma.getAlternative()) {
			TreatmentDefinition def = TreatmentDefinitionConverter.load(a.getTreatmentDefinition(), domain);
			defs.add(def);
			for (ArmReference armRef : a.getArms().getArm()) {
				Study study = JAXBConvertor.findNamedItem(domain.getStudies(), armRef.getStudy());
				if (!studies.contains(study)) {
					studies.add(study);
					armMap.put(study, new HashMap<TreatmentDefinition, Arm>());
				}
				Arm arm = JAXBConvertor.findArm(armRef.getName(), study.getArms());
				armMap.get(study).put(def, arm);
			}
		}

		NetworkMetaAnalysis networkMetaAnalysis = new NetworkMetaAnalysis(name, indication, om, armMap);

		if(nma.getInconsistencyResults() != null) { 
			loadInconsistencyModel(nma, networkMetaAnalysis, domain);
		}
		if(nma.getConsistencyResults() != null) { 
			loadConsistencyModel(nma, networkMetaAnalysis, domain);
		}

		for(NodeSplitResults nodeSplit : nma.getNodeSplitResults()) {
			loadNodeSplitModel(nodeSplit, nma, networkMetaAnalysis, domain);
		}
		
		return networkMetaAnalysis;
	}

	private static void loadNodeSplitModel(NodeSplitResults results,
			org.drugis.addis.entities.data.NetworkMetaAnalysis nma,
			NetworkMetaAnalysis networkMetaAnalysis, Domain domain) {
		final HashMap<Parameter, QuantileSummary> quantileSummaries = new HashMap<Parameter, QuantileSummary>();
		final HashMap<Parameter, ConvergenceSummary> convergenceSummaries = new HashMap<Parameter, ConvergenceSummary>();
		addParameterSummaries(networkMetaAnalysis, quantileSummaries, convergenceSummaries, results.getSummary(), domain);
		NodeSplitPValueSummary nodeSplitPValueSummary = new NodeSplitPValueSummary(results.getPValue());
		
		List<org.drugis.addis.entities.data.TreatmentDefinition> alternatives = results.getSplitNode().getTreatmentDefinition();
		Treatment base = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(alternatives.get(0), domain));
		Treatment subj = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(alternatives.get(1), domain));
		
		BasicParameter splitParameter = new BasicParameter(base, subj);
		
		networkMetaAnalysis.loadNodeSplitModel(splitParameter, buildMCMCSettingsCache(results.getMcmcSettings()), quantileSummaries, convergenceSummaries, nodeSplitPValueSummary);

	}

	private static MCMCSettingsCache buildMCMCSettingsCache(MCMCSettings settings) {
		return new MCMCSettingsCache(settings.getInferenceIterations(), settings.getSimulationIterations(), 
				settings.getThinningInterval(), settings.getTuningIterations(), settings.getVarianceScalingFactor(), settings.getNumberOfChains());
	}

	private static void loadInconsistencyModel(
			org.drugis.addis.entities.data.NetworkMetaAnalysis nma,
			NetworkMetaAnalysis networkMetaAnalysis, 
			Domain domain) {
		final InconsistencyResults results = nma.getInconsistencyResults();
		final HashMap<Parameter, QuantileSummary> quantileSummaries = new HashMap<Parameter, QuantileSummary>();
		final HashMap<Parameter, ConvergenceSummary> convergenceSummaries = new HashMap<Parameter, ConvergenceSummary>();
		
		addRelativeEffectQuantileSummaries(networkMetaAnalysis, quantileSummaries, results.getRelativeEffectsQuantileSummary().getRelativeEffectQuantileSummary(), domain);
		addParameterSummaries(networkMetaAnalysis, quantileSummaries, convergenceSummaries, results.getSummary(), domain);
		networkMetaAnalysis.loadInconsistencyModel(buildMCMCSettingsCache(results.getMcmcSettings()), quantileSummaries, convergenceSummaries);
	}
	
	private static void loadConsistencyModel(
			org.drugis.addis.entities.data.NetworkMetaAnalysis nma,
			NetworkMetaAnalysis networkMetaAnalysis, 
			Domain domain) {
		final ConsistencyResults results = nma.getConsistencyResults();
		final HashMap<Parameter, QuantileSummary> quantileSummaries = new HashMap<Parameter, QuantileSummary>();
		final HashMap<Parameter, ConvergenceSummary> convergenceSummaries = new HashMap<Parameter, ConvergenceSummary>();
		
		addRelativeEffectQuantileSummaries(networkMetaAnalysis, quantileSummaries, results.getRelativeEffectsQuantileSummary().getRelativeEffectQuantileSummary(), domain);
		addParameterSummaries(networkMetaAnalysis, quantileSummaries, convergenceSummaries, results.getSummary(), domain);
	
		RelativeEffectsSummary relativeEffects = results.getRelativeEffectsSummary();
		double[] mean = ArrayUtils.toPrimitive(relativeEffects.getMeans().toArray(new Double[results.getRelativeEffectsSummary().getMeans().size()]));
		int covarianceSize = calcCovMatrixDim(relativeEffects.getCovariance().size());

		double[][] covarianceMatrix = new double[covarianceSize][covarianceSize];
		int covIdx = 0;
		for(int row = 0; row < covarianceSize; row++) {
			for(int col = row; col < covarianceSize; col++) {
				double x = relativeEffects.getCovariance().get(covIdx);
				covarianceMatrix[row][col] = x;
				covarianceMatrix[col][row] = x;
				covIdx++;
			}
		}
		MultivariateNormalSummary relativeEffectsSummary = new SimpleMultivariateNormalSummary(mean, covarianceMatrix);
		
		// TreatmentCategorySets are always sorted in their natural order
		List<Treatment> treatments = new ArrayList<Treatment>();
		for(TreatmentDefinition d : networkMetaAnalysis.getAlternatives()) {
			treatments.add(networkMetaAnalysis.getTreatment(d));
		}		
		double[][] rankProbabilityMatrix = new double[treatments.size()][treatments.size()];
		int rankIdx = 0;
		for(int row = treatments.size() - 1; row >= 0; row--) { 
			for(int col = 0; col < treatments.size(); ++col) { 
				rankProbabilityMatrix[col][row] = results.getRankProbabilitySummary().get(rankIdx);
				rankIdx++; 
			}
		}	
		RankProbabilitySummary rankProbabilitySummary = new RankProbabilitySummary(rankProbabilityMatrix, treatments);
		networkMetaAnalysis.loadConsistencyModel(buildMCMCSettingsCache(results.getMcmcSettings()), quantileSummaries, convergenceSummaries, relativeEffectsSummary, rankProbabilitySummary);
	}

	private static int calcCovMatrixDim(int entries) {
		return (intSqrt(1 + 8 * entries) - 1) / 2;
	}

	private static int intSqrt(int n) {
		int r = (int)Math.round(Math.sqrt(n));
		if (r * r != n) {
			throw new RuntimeException(n + " does not have an integer square root.");
		}
		return r;
	}

	private static void addRelativeEffectQuantileSummaries(NetworkMetaAnalysis networkMetaAnalysis,
			final HashMap<Parameter, QuantileSummary> quantileSummaries,
			final List<RelativeEffectQuantileSummary> relativeEffects,
			Domain domain) {
		for(final RelativeEffectQuantileSummary reqs : relativeEffects) {
			BasicParameter p = convertRelativeEffect(domain, networkMetaAnalysis, reqs.getRelativeEffect());
			final QuantileSummary q = convertQuantileSummary(reqs.getQuantile());
			quantileSummaries.put(p, q);
		}
	}

	private static BasicParameter convertRelativeEffect(Domain domain,
			NetworkMetaAnalysis networkMetaAnalysis,
			final RelativeEffectParameter relativeEffectParameter) {
		Treatment baseTreatment = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(relativeEffectParameter.getTreatmentDefinition().get(0), domain));
		Treatment subjTreatment = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(relativeEffectParameter.getTreatmentDefinition().get(1), domain));
		return new org.drugis.mtc.parameterization.BasicParameter(baseTreatment, subjTreatment);
	}
	
	private static SplitParameter convertNodeSplit(Domain domain,
			NetworkMetaAnalysis networkMetaAnalysis,
			final RelativeEffectParameter relativeEffectParameter,
			boolean isDirect) {
		Treatment baseTreatment = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(relativeEffectParameter.getTreatmentDefinition().get(0), domain));
		Treatment subjTreatment = networkMetaAnalysis.getTreatment(TreatmentDefinitionConverter.load(relativeEffectParameter.getTreatmentDefinition().get(1), domain));
		return new org.drugis.mtc.parameterization.SplitParameter(baseTreatment, subjTreatment, isDirect);
	}

	private static void addParameterSummaries(NetworkMetaAnalysis networkMetaAnalysis,
			final HashMap<Parameter, QuantileSummary> quantileSummaries,
			final HashMap<Parameter, ConvergenceSummary> convergenceSummaries,
			List<ParameterSummary> summaries,
			Domain domain) {
		for(ParameterSummary ps : summaries) {
			Parameter p = createParameter(domain, networkMetaAnalysis, ps);
			final QuantileSummary q = convertQuantileSummary(ps.getQuantile());
			if(ps.getPsrf() != null) { 
				final ConvergenceSummary c = new ConvergenceSummary(ps.getPsrf());
				convergenceSummaries.put(p, c);
			}
			quantileSummaries.put(p, q);
		}
	}

	private static Parameter createParameter(Domain domain,
			NetworkMetaAnalysis networkMetaAnalysis, ParameterSummary ps) {
		Parameter p = null;
		if(ps.getInconsistency() != null) {
			ArrayList<TreatmentDefinition> alternatives = new ArrayList<TreatmentDefinition>();
			ArrayList<Treatment> alternativeTreatments = new ArrayList<Treatment>();
			for(org.drugis.addis.entities.data.TreatmentDefinition defs : ps.getInconsistency().getTreatmentDefinition()) {
				TreatmentDefinition def = TreatmentDefinitionConverter.load(defs, domain);
				alternatives.add(def);
				alternativeTreatments.add(networkMetaAnalysis.getTreatment(def));
			}
			p = new org.drugis.mtc.parameterization.InconsistencyParameter(alternativeTreatments);
		} else if(ps.getVariance() != null) {
			VarianceParameterType name = ps.getVariance().getName();
			if(name == VarianceParameterType.VAR_W) { 
				p = new org.drugis.mtc.parameterization.InconsistencyVariance();
			} 
			if(name == VarianceParameterType.VAR_D) { 
				p = new org.drugis.mtc.parameterization.RandomEffectsVariance();
			}
		} else if(ps.getRelativeEffect() != null) { 
			if(ps.getRelativeEffect().getWhichEvidence() == EvidenceTypeEnum.ALL) {
				p = convertRelativeEffect(domain, networkMetaAnalysis, ps.getRelativeEffect());
			}
			if(ps.getRelativeEffect().getWhichEvidence() == EvidenceTypeEnum.DIRECT) {
				p = convertNodeSplit(domain, networkMetaAnalysis, ps.getRelativeEffect(), true);
			}
			if(ps.getRelativeEffect().getWhichEvidence() == EvidenceTypeEnum.INDIRECT) {
				p = convertNodeSplit(domain, networkMetaAnalysis, ps.getRelativeEffect(), false);
			}
		}
		return p;
	}

	private static QuantileSummary convertQuantileSummary(List<QuantileType> quantile) {
		double[] probs = new double[quantile.size()]; 
		double[] values = new double[quantile.size()];  
		for(int i = 0; quantile.size() > i; ++i) { 
			probs[i] = quantile.get(i).getLevel();
			values[i] = quantile.get(i).getValue();
		}
		return new QuantileSummary(probs, values);
	}
	
	//  From ADDIS to JAXB (save)
	public static org.drugis.addis.entities.data.NetworkMetaAnalysis save(NetworkMetaAnalysis ma) throws ConversionException {
		org.drugis.addis.entities.data.NetworkMetaAnalysis nma = new org.drugis.addis.entities.data.NetworkMetaAnalysis();
		nma.setName(ma.getName());
		nma.setIndication(JAXBConvertor.nameReference(ma.getIndication().getName()));
		if(ma.getOutcomeMeasure() instanceof Endpoint) {
			nma.setEndpoint(JAXBConvertor.nameReference(ma.getOutcomeMeasure().getName()));
		} else if(ma.getOutcomeMeasure() instanceof AdverseEvent) {
			nma.setAdverseEvent(JAXBConvertor.nameReference(ma.getOutcomeMeasure().getName()));
		} else {
			throw new ConversionException("Outcome Measure type not supported: " + ma.getOutcomeMeasure());
		}
		for(TreatmentDefinition t : ma.getAlternatives()) {
			MetaAnalysisAlternative alt = new MetaAnalysisAlternative();
			alt.setTreatmentDefinition(TreatmentDefinitionConverter.save(t));
			AnalysisArms arms = new AnalysisArms();
			
			for(Study study : ma.getIncludedStudies()) {
				Arm arm = ma.getArm(study, t);
				if (arm != null) {
					arms.getArm().add(JAXBConvertor.armReference(study.getName(), arm.getName()));
				}
			}
			alt.setArms(arms);
			nma.getAlternative().add(alt);
		}
		
		nma.setInconsistencyResults(convertInconsistencyResults(ma));
		nma.setConsistencyResults(convertConsistencyResults(ma));
		
		for(NodeSplitWrapper<TreatmentDefinition> model : ma.getNodeSplitModels()) {
			if (model.isApproved()) {
				nma.getNodeSplitResults().add(convertNodeSplitResults(ma, model));
			}
		}
		return nma; 
	}

	private static NodeSplitResults convertNodeSplitResults(NetworkMetaAnalysis ma, NodeSplitWrapper<TreatmentDefinition> model) {
		NodeSplitResults results = new NodeSplitResults();
		results.setMcmcSettings(convertMCMCSettings(model));
		convertParameterSummaries(ma, model, results.getSummary());
		results.setPValue(model.getNodeSplitPValueSummary().getPvalue());
		TreatmentDefinitionPair alternativePair = new TreatmentDefinitionPair();
		BasicParameter splitNode = (BasicParameter) model.getSplitNode();
		alternativePair.getTreatmentDefinition().add(TreatmentDefinitionConverter.save(ma.getTreatmentDefinition(splitNode.getBaseline())));
		alternativePair.getTreatmentDefinition().add(TreatmentDefinitionConverter.save(ma.getTreatmentDefinition(splitNode.getSubject())));
		results.setSplitNode(alternativePair);
		return results;
	}

	private static InconsistencyResults convertInconsistencyResults(NetworkMetaAnalysis ma) {
		InconsistencyResults results = new InconsistencyResults();
		InconsistencyWrapper<TreatmentDefinition> model = ma.getInconsistencyModel();
		if (model.isApproved()) {
			results.setMcmcSettings(convertMCMCSettings(model));
			convertParameterSummaries(ma, model, results.getSummary());
			results.setRelativeEffectsQuantileSummary(convertRelativeEffectQuantileSummaries(ma, model));
			return results;
		}
		return null;
	}

	private static ConsistencyResults convertConsistencyResults(NetworkMetaAnalysis ma) {
		ConsistencyResults results = new ConsistencyResults();
		ConsistencyWrapper<TreatmentDefinition> model = ma.getConsistencyModel();
		if (model.isApproved()) { 
			results.setMcmcSettings(convertMCMCSettings(model));
			convertParameterSummaries(ma, model, results.getSummary());
			results.setRelativeEffectsQuantileSummary(convertRelativeEffectQuantileSummaries(ma, model));
			RelativeEffectsSummary relativeEffectSummary = new RelativeEffectsSummary();
			List<Double> list = relativeEffectSummary.getCovariance();
			double[][] matrix = model.getRelativeEffectsSummary().getCovarianceMatrix();
			for (int row = 0; row < matrix.length; ++row) {
				for (int col = row; col < matrix.length; ++col) {
					list.add(matrix[row][col]);
				}
			}
			List<Double> meanVector = Arrays.asList(ArrayUtils.toObject(model.getRelativeEffectsSummary().getMeanVector()));
			relativeEffectSummary.getMeans().addAll(meanVector);
			results.setRelativeEffectsSummary(relativeEffectSummary);
			
			RankProbabilitySummary rankProbabilities = model.getRankProbabilities();
			int rankProababilitySize = rankProbabilities.getTreatments().size();
			for(int row = 0; row < rankProababilitySize; ++row) { 
				for(int col = 0; col < rankProababilitySize; ++col) { 
					results.getRankProbabilitySummary().add(rankProbabilities.getValue(rankProbabilities.getTreatments().get(col), row + 1));
				}
			}
			return results;
		}
		return null;
	}

	private static RelativeEffectsQuantileSummary convertRelativeEffectQuantileSummaries(NetworkMetaAnalysis ma, MTCModelWrapper<TreatmentDefinition> model) {
		RelativeEffectsQuantileSummary relativeEffectsSummary = new RelativeEffectsQuantileSummary();
		relativeEffectsSummary.getRelativeEffectQuantileSummary().addAll(convertRelativeEffectParameters(ma, model));
		return relativeEffectsSummary;
	}
	
	private static void convertParameterSummaries(NetworkMetaAnalysis ma, MTCModelWrapper<TreatmentDefinition> model, List<ParameterSummary> summaries) {
		List<Parameter> parameters = new ArrayList<Parameter>(Arrays.asList(model.getParameters()));
		if(model instanceof NodeSplitWrapper) { 
			parameters.add(((NodeSplitWrapper<TreatmentDefinition>) model).getIndirectEffect());
		}
		Collections.sort(parameters, new ParameterComparator());
		for (Parameter p : parameters) {
			summaries.add(convertParameterSummary(p, model, ma));
		}
	}

	private static ParameterSummary convertParameterSummary(Parameter p, MTCModelWrapper<TreatmentDefinition> mtc, NetworkMetaAnalysis nma) { 
		ParameterSummary ps = new ParameterSummary();
		ps.setPsrf(mtc.getConvergenceSummary(p).getScaleReduction());
		ps.getQuantile().addAll(convertQuantileSummary(mtc.getQuantileSummary(p)));
		if (p instanceof org.drugis.mtc.parameterization.InconsistencyParameter) { 
			org.drugis.mtc.parameterization.InconsistencyParameter ip = (org.drugis.mtc.parameterization.InconsistencyParameter)p;
			ps.setInconsistency(new InconsistencyParameter());
			for (Treatment t : ip.getCycle()) { 
				ps.getInconsistency().getTreatmentDefinition().add(TreatmentDefinitionConverter.save(nma.getTreatmentDefinition(t)));
			}
		} else if (p instanceof org.drugis.mtc.parameterization.InconsistencyVariance) { 
			VarianceParameter value = new VarianceParameter();
			value.setName(VarianceParameterType.VAR_W);
			ps.setVariance(value);
		} else if (p instanceof org.drugis.mtc.parameterization.RandomEffectsVariance) { 
			VarianceParameter value = new VarianceParameter();
			value.setName(VarianceParameterType.VAR_D);
			ps.setVariance(value);
		} else if (p instanceof org.drugis.mtc.parameterization.BasicParameter) {
			TreatmentDefinition base = nma.getTreatmentDefinition(((org.drugis.mtc.parameterization.BasicParameter) p).getBaseline());
			TreatmentDefinition subj = nma.getTreatmentDefinition(((org.drugis.mtc.parameterization.BasicParameter) p).getSubject());
			Pair<TreatmentDefinition> relEffect = new Pair<TreatmentDefinition>(base, subj);
			ps.setRelativeEffect(convertRelativeEffectsParameter(relEffect));
		} else if (p instanceof org.drugis.mtc.parameterization.SplitParameter) {
			org.drugis.mtc.parameterization.SplitParameter np = (org.drugis.mtc.parameterization.SplitParameter) p;
			TreatmentDefinition base = nma.getTreatmentDefinition(np.getBaseline());
			TreatmentDefinition subj = nma.getTreatmentDefinition(np.getSubject());
			Pair<TreatmentDefinition> relEffect = new Pair<TreatmentDefinition>(base, subj);
			ps.setRelativeEffect(convertNodeSplitParameter(relEffect, np.isDirect()));
		}
		return ps;
	}

	private static List<QuantileType> convertQuantileSummary(QuantileSummary qs) {
		List<QuantileType> l = new ArrayList<QuantileType>();
		for(int i = 0; qs.getSize() > i; ++i) {
			QuantileType q = new QuantileType();
			q.setLevel(qs.getProbability(i));
			q.setValue(qs.getQuantile(i));
			l.add(q);
		}
		return l;
	}

	private static List<RelativeEffectQuantileSummary> convertRelativeEffectParameters(NetworkMetaAnalysis ma, MTCModelWrapper<TreatmentDefinition> mtc) {
		List<TreatmentDefinition> alternatives = ma.getAlternatives();
		List<RelativeEffectQuantileSummary> reqs = new ArrayList<RelativeEffectQuantileSummary>();
		for (int i = 0; i < alternatives.size(); ++i) {
			for (int j = 0; j < alternatives.size(); ++j) {
				if(j != i) { 
					Pair<TreatmentDefinition> relEffect = new Pair<TreatmentDefinition>(alternatives.get(i), alternatives.get(j));
					RelativeEffectQuantileSummary qs = new RelativeEffectQuantileSummary();
					qs.setRelativeEffect(convertRelativeEffectsParameter(relEffect));
					qs.getQuantile().addAll(convertQuantileSummary(mtc.getQuantileSummary(mtc.getRelativeEffect(alternatives.get(i), alternatives.get(j)))));
					reqs.add(qs);
				} 
			}
		}
		return reqs; 
	}
	
	private static RelativeEffectParameter convertNodeSplitParameter(Pair<TreatmentDefinition> pair, boolean direct) {
		return convertRelativeEffectParameter(pair, direct ? EvidenceTypeEnum.DIRECT : EvidenceTypeEnum.INDIRECT);
	}
	
	private static RelativeEffectParameter convertRelativeEffectsParameter(Pair<TreatmentDefinition> pair) {
		return convertRelativeEffectParameter(pair, EvidenceTypeEnum.ALL);
	}

	private static RelativeEffectParameter convertRelativeEffectParameter(
			Pair<TreatmentDefinition> pair, EvidenceTypeEnum evidenceType) {
		RelativeEffectParameter rel = new RelativeEffectParameter();
		rel.setWhichEvidence(evidenceType); 
		org.drugis.addis.entities.data.TreatmentDefinition first =  TreatmentDefinitionConverter.save(pair.getFirst());
		org.drugis.addis.entities.data.TreatmentDefinition second = TreatmentDefinitionConverter.save(pair.getSecond());
		rel.getTreatmentDefinition().addAll(Arrays.asList(first, second));
		return rel;
	}

	private static MCMCSettings convertMCMCSettings(MTCModelWrapper<TreatmentDefinition> wrapper) {
		MCMCSettings s = new MCMCSettings();
		s.setSimulationIterations(wrapper.getSettings().getSimulationIterations());
		s.setTuningIterations(wrapper.getSettings().getTuningIterations());
		s.setThinningInterval(wrapper.getSettings().getThinningInterval()); 
		s.setInferenceIterations(wrapper.getSettings().getInferenceSamples());
		s.setVarianceScalingFactor(wrapper.getSettings().getVarianceScalingFactor()); 
		s.setNumberOfChains(wrapper.getSettings().getNumberOfChains());
		return s;
	}
	
}

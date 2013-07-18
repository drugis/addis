/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import java.io.IOException;
import java.io.OutputStream;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.math3.linear.RealMatrix;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.util.JSMAAintegration.AbstractBenefitRiskSMAAFactory;

import fi.smaa.jsmaa.model.BetaMeasurement;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.FullJointMeasurements;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.ImpactMatrix;
import fi.smaa.jsmaa.model.Interval;
import fi.smaa.jsmaa.model.Measurement;
import fi.smaa.jsmaa.model.MultivariateGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.PerCriterionMeasurements;
import fi.smaa.jsmaa.model.RelativeGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;

public class SMAASerializer<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {

	private SMAAModel d_model;
	private BenefitRiskAnalysis<Alternative> d_analysis;
	private AbstractBenefitRiskSMAAFactory<Alternative> d_factory;
	private ObjectMapper d_mapper;

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String toSlug(String input) {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	public SMAASerializer(SMAAModel model, AnalysisType a, AbstractBenefitRiskSMAAFactory<Alternative> smaaFactory) {
		d_model = model;
		d_analysis = a;
		d_factory = smaaFactory;
		d_mapper = new ObjectMapper();
	}

	public JsonNode getRootNode() {

		ObjectNode rootNode = (ObjectNode) d_mapper.createObjectNode();
		rootNode.put("title", d_analysis.getName());

		insertCriteria(d_mapper, rootNode);

		insertAlternatives(d_mapper, rootNode);

		// Add PerfomanceTable
		FullJointMeasurements m = d_model.getMeasurements();

		ArrayNode performancesNode = (ArrayNode) d_mapper.createArrayNode();
		if (m instanceof ImpactMatrix) {
			insertMeasurements(d_mapper, m, performancesNode);
		}

		if (m instanceof PerCriterionMeasurements) {
			insertPerCriterionMeasurement(d_mapper, m, performancesNode);
		}
		rootNode.put("performanceTable", performancesNode);

		rootNode.put("preferences", d_mapper.createObjectNode());

		return rootNode;
	}

	public void serialize(OutputStream stream) {
		try {
			d_mapper.writerWithDefaultPrettyPrinter().writeValue(stream, getRootNode());
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insertPerCriterionMeasurement(ObjectMapper mapper, FullJointMeasurements m, ArrayNode performancesNode) {
		PerCriterionMeasurements measurements = (PerCriterionMeasurements) m;
		for (Criterion criterion : measurements.getCriteria()) {
			ObjectNode measurementNode = (ObjectNode) mapper.createObjectNode();
			measurementNode.put("criterion", toSlug(criterion.getName()));

			ObjectNode performanceNode = (ObjectNode) mapper.createObjectNode();
			CriterionMeasurement criterionMeasurement = measurements.getCriterionMeasurement(criterion);

			Class<? extends CriterionMeasurement> measurementType = criterionMeasurement.getClass();
			if (measurementType.equals(RelativeLogitGaussianCriterionMeasurement.class)
					|| measurementType.equals(RelativeGaussianCriterionMeasurement.class)) {
				ObjectNode parameterNode = (ObjectNode) mapper.createObjectNode();
				String type = measurementType.equals(RelativeLogitGaussianCriterionMeasurement.class) ? "relative-logit-normal" : "relative-normal";
				performanceNode.put("type", type);

				GaussianMeasurement baseline;
				MultivariateGaussianCriterionMeasurement relativeMeasurement;

				CriterionMeasurement measurement = measurementType.cast(criterionMeasurement);
				if (measurementType.equals(RelativeLogitGaussianCriterionMeasurement.class)) {
					RelativeLogitGaussianCriterionMeasurement tmp = (RelativeLogitGaussianCriterionMeasurement) measurement;
					baseline = tmp.getGaussianMeasurement().getBaselineMeasurement();
					relativeMeasurement = tmp.getGaussianMeasurement().getRelativeMeasurement();
				} else {
					RelativeGaussianCriterionMeasurement tmp = (RelativeGaussianCriterionMeasurement) measurement;
					baseline = tmp.getBaselineMeasurement();
					relativeMeasurement = tmp.getRelativeMeasurement();
				}

				// Add baseline
				ObjectNode baselineNode = (ObjectNode) mapper.createObjectNode();
				baselineNode.put("type", "dnorm");
				baselineNode.put("name", d_analysis.getBaseline().getLabel());
				baselineNode.put("mu", baseline.getMean());
				baselineNode.put("sigma", baseline.getStDev());
				parameterNode.put("baseline", baselineNode);

				// Add relative
				ObjectNode relativeNode = (ObjectNode) mapper.createObjectNode();
				relativeNode.put("type", "dmnorm");
				ObjectNode relativeMuNode = (ObjectNode) mapper.createObjectNode();

				ObjectNode relativeCovNode = (ObjectNode) mapper.createObjectNode();
				ArrayNode relativeCovRowNames = (ArrayNode) mapper.createArrayNode();
				ArrayNode relativeCovColNames = (ArrayNode) mapper.createArrayNode();

				for (int i = 0; i < relativeMeasurement.getAlternatives().size(); ++i) {
					String alternative = relativeMeasurement.getAlternatives().get(i).getName();
					relativeCovRowNames.add(alternative);
					relativeCovColNames.add(alternative);
					relativeMuNode.put(alternative, relativeMeasurement.getMeanVector().getEntry(i));
				}
				relativeCovNode.put("colnames", relativeCovColNames);
				relativeCovNode.put("rownames", relativeCovRowNames);

				ArrayNode relativeCovDataNode = (ArrayNode) mapper.createArrayNode();
				RealMatrix covarianceMatrix = relativeMeasurement.getCovarianceMatrix();
				for (int i = 0; i < covarianceMatrix.getRowDimension(); ++i) {
					ArrayNode row = (ArrayNode) mapper.createArrayNode();
					for (int j = 0; j < covarianceMatrix.getRowDimension(); ++j) {
						row.add(covarianceMatrix.getRow(i)[j]);
					}
					relativeCovDataNode.add(row);
				}
				relativeCovNode.put("data", relativeCovDataNode);

				relativeNode.put("mu", relativeMuNode);
				relativeNode.put("cov", relativeCovNode);

				parameterNode.put("relative", relativeNode);

				performanceNode.put("parameters", parameterNode);
			}
			measurementNode.put("performance", performanceNode);


			performancesNode.add(measurementNode);
		}
	}

	private void insertMeasurements(ObjectMapper mapper, FullJointMeasurements m, ArrayNode performancesNode) {
		ImpactMatrix impactMatrix = (ImpactMatrix) m;
		for (Criterion criterion : impactMatrix.getCriteria())  {
			for (fi.smaa.jsmaa.model.Alternative alternative : impactMatrix.getAlternatives()) {
				ObjectNode measurementNode = (ObjectNode) mapper.createObjectNode();
				measurementNode.put("alternative", toSlug(alternative.getName()));
				measurementNode.put("criterion", toSlug(criterion.getName()));

				ObjectNode performanceNode = (ObjectNode) mapper.createObjectNode();
				Measurement measurement = impactMatrix.getMeasurement(criterion, alternative);
				if (measurement instanceof BetaMeasurement) {
					BetaMeasurement betaMeasurement = (BetaMeasurement) measurement;
					performanceNode.put("type", "dbeta");
					ObjectNode parameters = (ObjectNode) mapper.createObjectNode();
					parameters.put("alpha", betaMeasurement.getAlpha());
					parameters.put("beta", betaMeasurement.getBeta());
					performanceNode.put("parameters", parameters);
				}

				if (measurement instanceof GaussianMeasurement) {
					GaussianMeasurement gaussianMeasurement = (GaussianMeasurement) measurement;
					performanceNode.put("type", "dnorm");
					ObjectNode parameters = (ObjectNode) mapper.createObjectNode();
					parameters.put("mu", gaussianMeasurement.getMean());
					parameters.put("sigma", gaussianMeasurement.getStDev());
					performanceNode.put("parameters", parameters);
				}

				measurementNode.put("performance", performanceNode);
				performancesNode.add(measurementNode);
			}
		}
	}

	private void insertAlternatives(ObjectMapper mapper, ObjectNode rootNode) {
		// Add Alternatives
		ObjectNode alternativesNode = (ObjectNode) mapper.createObjectNode();
		for (fi.smaa.jsmaa.model.Alternative alternative : d_model.getAlternatives()) {
			ObjectNode alternativeNode = (ObjectNode) mapper.createObjectNode();
			alternativeNode.put("title", alternative.getName());
			alternativesNode.put(toSlug(alternative.getName()), alternativeNode);
		}
		rootNode.put("alternatives", alternativesNode);
	}

	private void insertCriteria(ObjectMapper mapper, ObjectNode rootNode) {
		// Add Criteria
		ObjectNode criteriaNode = (ObjectNode) mapper.createObjectNode();
		for (OutcomeMeasure criterion : d_analysis.getCriteria()) {
			ObjectNode criterionNode = (ObjectNode) mapper.createObjectNode();
			criterionNode.put("title", criterion.getLabel());

			ObjectNode pvfNode = (ObjectNode) mapper.createObjectNode();
			pvfNode.put("direction", criterion.getDirection() == Direction.HIGHER_IS_BETTER ? "increasing" : "decreasing");
			pvfNode.put("type", "linear");
			ArrayNode scaleRangeNode = mapper.createArrayNode();
			Interval scale = d_factory.getCriterion(criterion).getScale();
			scaleRangeNode.add(scale.getStart());
			scaleRangeNode.add(scale.getEnd());
			pvfNode.put("range", scaleRangeNode);
			criterionNode.put("pvf", pvfNode);
			criteriaNode.put(toSlug(criterion.getName()), criterionNode);
		}
		rootNode.put("criteria", criteriaNode);
	}
}
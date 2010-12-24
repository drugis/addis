/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.Beta;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.TransformedStudentT;
import org.drugis.addis.util.EnumXMLFormat;
import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;

import scala.actors.threadpool.Arrays;

public class StudyBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Arm> {
	public static String PROPERTY_STUDY = "study";
	public static String PROPERTY_ARMS = "arms";
	private Study d_study;
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_criteria;
	private List<Arm> d_alternatives;
	private AnalysisType d_analysisType;
	
	private class AbsoluteMeasurementSource extends AbstractMeasurementSource<Arm> {
		public Distribution getMeasurement(Arm alternative, OutcomeMeasure criterion) {
			return StudyBenefitRiskAnalysis.this.getMeasurement(alternative, criterion);
		}
	}
	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, List<Arm> alternatives, AnalysisType analysisType) {
		d_name = name;
		d_indication = indication;
		d_study = study;
		d_criteria = Collections.unmodifiableList(criteria);
		d_alternatives = Collections.unmodifiableList(alternatives);
		d_analysisType = analysisType;
		if(d_analysisType == AnalysisType.LyndOBrien && (d_criteria.size() != 2 || d_alternatives.size() != 2) ) {
			throw new IllegalArgumentException("Attempt to create Lynd & O'Brien analysis with not exactly 2 criteria and 2 alternatives");
		}
				
	}

	private StudyBenefitRiskAnalysis() {
		
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		Set <Entity> deps = new HashSet<Entity>(d_study.getDependencies());
		deps.add(d_study);
		return deps;
	}
	
	public List<Arm> getArms() {
		return d_alternatives;
	}

	public List<Arm> getAlternatives() {
		return d_alternatives;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public Distribution getMeasurement(Arm alternative, OutcomeMeasure criterion) {
		Measurement measurement = d_study.getMeasurement(criterion, alternative);
		if (measurement instanceof RateMeasurement) {
			RateMeasurement rateMeasurement = (RateMeasurement) measurement;
			return new Beta(1 + rateMeasurement.getRate(), 1 + rateMeasurement.getSampleSize() - rateMeasurement.getRate());
		} else if (measurement instanceof ContinuousMeasurement) {
			ContinuousMeasurement contMeasurement = (ContinuousMeasurement) measurement;
			return new TransformedStudentT(contMeasurement.getMean(), contMeasurement.getStdDev(), 
					contMeasurement.getSampleSize() - 1);
		} else {
			throw new IllegalStateException("Unknown measurement type " + measurement.getClass().getSimpleName());
		}
	}

	public String getName() {
		return d_name;
	}

	public List<OutcomeMeasure> getCriteria() {
		return d_criteria;
	}

	public int compareTo(BenefitRiskAnalysis<?> o) {
		if (o == null)
			return 1;
		return d_name.compareTo(o.getName());
	}

	public Study getStudy() {
		return d_study;
	}

	
	@SuppressWarnings("unchecked")
	private List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition[]{
		new PropertyDefinition<Indication>(PROPERTY_INDICATION, Indication.class) {
			public Indication getValue() { return getIndication(); }
			public void setValue(Object val) { d_indication = (Indication) val; }
		},
		new PropertyDefinition<Study>(PROPERTY_STUDY, Study.class) {
			public Study getValue() { return getStudy(); }
			public void setValue(Object val) { d_study = (Study) val;}
		},
		new PropertyDefinition<ArrayList>(PROPERTY_ARMS, ArrayList.class) {
			public ArrayList<Arm> getValue() { return new ArrayList<Arm>(getArms()); }
			public void setValue(Object val) { d_alternatives = (ArrayList<Arm>) val;}
		},
		new PropertyDefinition<ArrayList>("outcomeMeasures", ArrayList.class) {
			public ArrayList<OutcomeMeasure> getValue() { return new ArrayList<OutcomeMeasure>(getCriteria()); }
			public void setValue(Object val) { d_criteria = (ArrayList<OutcomeMeasure>) val;}
		}
	});
	
	protected static final XMLFormat<StudyBenefitRiskAnalysis> STUDYBR_XML = 
		new XMLFormat<StudyBenefitRiskAnalysis>(StudyBenefitRiskAnalysis.class) {
			@Override
			public StudyBenefitRiskAnalysis newInstance(Class<StudyBenefitRiskAnalysis> cls, InputElement xml) {
				return new StudyBenefitRiskAnalysis();
			}
			
			@Override
			public void read(InputElement ie, StudyBenefitRiskAnalysis br) throws XMLStreamException {
				br.setName(ie.getAttribute(PROPERTY_NAME, null));
				br.d_analysisType = EnumXMLFormat.getEnumAttribute(ie, PROPERTY_ANALYSIS_TYPE, AnalysisType.SMAA); 
				XMLPropertiesFormat.readProperties(ie, br.d_propDefs);
			}
		
			@Override
			public void write(StudyBenefitRiskAnalysis br, OutputElement oe) throws XMLStreamException {
				oe.setAttribute(PROPERTY_NAME, br.getName());
				oe.setAttribute(PROPERTY_ANALYSIS_TYPE, br.getAnalysisType().toString());
				XMLPropertiesFormat.writeProperties(br.d_propDefs, oe);
			}
		};
		
	private void setName(String name) {
		d_name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public MeasurementSource<Arm> getAbsoluteMeasurementSource() {
		return new AbsoluteMeasurementSource();
	}

	public AnalysisType getAnalysisType() {
		return d_analysisType;
	}
}

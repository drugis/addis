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

public class StudyBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Arm> {
	public static String PROPERTY_STUDY = "study";
	public static String PROPERTY_ARMS = "arms";
	private Study d_study;
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_criteria;
	private List<Arm> d_alternatives;
	
	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, List<Arm> alternatives) {
		d_name = name;
		d_indication = indication;
		d_study = study;
		d_criteria = Collections.unmodifiableList(criteria);
		d_alternatives = Collections.unmodifiableList(alternatives);
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

	public List<OutcomeMeasure> getOutcomeMeasures() {
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

	protected static final XMLFormat<StudyBenefitRiskAnalysis> METABR_XML = 
		new XMLFormat<StudyBenefitRiskAnalysis>(StudyBenefitRiskAnalysis.class) {
			@Override
			public StudyBenefitRiskAnalysis newInstance(Class<StudyBenefitRiskAnalysis> cls, InputElement xml) {
				return new StudyBenefitRiskAnalysis();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void read(InputElement ie, StudyBenefitRiskAnalysis br) throws XMLStreamException {
				br.setName(ie.getAttribute(PROPERTY_NAME, null));
				br.d_indication = (Indication) ie.get(PROPERTY_INDICATION, Indication.class);
				br.d_study = (Study) ie.get(PROPERTY_STUDY, Study.class);
				br.d_alternatives = (List<Arm>) ie.get(PROPERTY_ARMS, ArrayList.class);
				br.d_criteria = (List<OutcomeMeasure>) ie.get(PROPERTY_OUTCOMEMEASURES, ArrayList.class);
			}
		
			@SuppressWarnings("unchecked")
			@Override
			public void write(StudyBenefitRiskAnalysis br, OutputElement oe) throws XMLStreamException {
				oe.setAttribute(PROPERTY_NAME, br.getName());
				oe.add(br.getIndication(), PROPERTY_INDICATION, Indication.class);
				oe.add(br.getStudy(), PROPERTY_STUDY, Study.class);
				oe.add(new ArrayList(br.getAlternatives()), PROPERTY_ARMS, ArrayList.class);
				oe.add(new ArrayList(br.getOutcomeMeasures()), PROPERTY_OUTCOMEMEASURES, ArrayList.class);
			}
		};

	private void setName(String name) {
		d_name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

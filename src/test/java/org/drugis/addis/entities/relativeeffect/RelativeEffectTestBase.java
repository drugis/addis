package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;

public class RelativeEffectTestBase {
	protected Drug d_fluox;
	protected Drug d_sertr;
	protected Indication d_ind;
	protected Endpoint d_rateEndpoint;
	
	protected Study d_bennie;
	protected Study d_boyer;
	protected Study d_fava;
	protected Study d_newhouse;
	protected Study d_sechter;
	protected Endpoint d_contEndpoint;
	
	protected RelativeEffectTestBase() {
		d_ind = new Indication(001L, "Impression");
		d_fluox = new Drug("Fluoxetine","01");
		d_sertr = new Drug("Sertraline","02");
		d_rateEndpoint = new Endpoint("rate", Endpoint.convertVarType(Variable.Type.RATE));
		d_contEndpoint = new Endpoint("continuous", Endpoint.convertVarType(Variable.Type.CONTINUOUS));
	}

	protected Study createRateStudy(String studyName, int fluoxResp, int fluoxSize, int sertraResp, int sertraSize) {
		Study s = new Study(studyName, d_ind);
	
		ExampleData.addDefaultEpochs(s);
	
		s.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(d_rateEndpoint));
		Arm g_fluox = s.createAndAddArm("Fluox", fluoxSize, d_fluox, new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY));
		Arm g_sertr = s.createAndAddArm("Sertr", sertraSize, d_sertr, new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY));		
		
		BasicRateMeasurement m_sertr = (BasicRateMeasurement) d_rateEndpoint.buildMeasurement(g_sertr);
		BasicRateMeasurement m_fluox = (BasicRateMeasurement) d_rateEndpoint.buildMeasurement(g_fluox);
		
		m_sertr.setRate(sertraResp);
		m_fluox.setRate(fluoxResp);
	
		ExampleData.addDefaultMeasurementMoments(s);
	
		s.setMeasurement(d_rateEndpoint, g_sertr, m_sertr);
		s.setMeasurement(d_rateEndpoint, g_fluox, m_fluox);		
		
		return s;
	}

	protected Study createContStudy(String studyName, int fluoxSize, double fluoxMean,
			double fluoxDev, int sertrSize, double sertrMean, double sertrDev, Indication ind) {
				Study s = new Study(studyName, ind);
				ExampleData.addDefaultEpochs(s);
				s.getEndpoints().add(new StudyOutcomeMeasure<Endpoint>(d_contEndpoint));
				
				Arm group = addArm(s, d_fluox, fluoxSize);
				BasicContinuousMeasurement measurement =
					(BasicContinuousMeasurement) d_contEndpoint.buildMeasurement(group);
				measurement.setMean(fluoxMean);
				measurement.setStdDev(fluoxDev);
				
				Arm group1 = addArm(s, d_sertr, sertrSize);
				BasicContinuousMeasurement measurement1 =
					(BasicContinuousMeasurement) d_contEndpoint.buildMeasurement(group1);
				measurement1.setMean(sertrMean);
				measurement1.setStdDev(sertrDev);

				ExampleData.addDefaultMeasurementMoments(s);

				s.setMeasurement(d_contEndpoint, group, measurement);
				s.setMeasurement(d_contEndpoint, group1, measurement1);
				
				return s ;
			}

	private Arm addArm(Study study, Drug drug, int nSubjects) {
		FixedDose dose = new FixedDose(10.0, ExampleData.MILLIGRAMS_A_DAY);
		Arm group = study.createAndAddArm(drug.getName(), nSubjects, drug, dose);
		return group;
	}
}

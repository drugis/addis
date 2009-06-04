package nl.rug.escher.addis.entities.test;

import java.util.Arrays;
import java.util.Collections;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;

public class TestData {
	private static Endpoint s_endpointHamd;
	private static Endpoint s_endpointCgi;
	private static Drug s_parox;
	private static Drug s_fluox;
	private static Drug s_viagra;

	public static void initDefaultData(Domain domain) {
		domain.addEndpoint(buildEndpointHamd());
		domain.addEndpoint(buildEndpointCgi());
		domain.addDrug(buildDrugFluoxetine());
		domain.addDrug(buildDrugParoxetine());
		domain.addStudy(buildDefaultStudy());
		domain.addStudy(buildDefaultStudy2());
	}

	public static BasicStudy buildDefaultStudy2() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		BasicStudy study = new BasicStudy("De Wilde et al, 1993");
		study.setEndpoints(Collections.singletonList(hamd));
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		pHamd.setRate(23);

		BasicPatientGroup parox = new BasicPatientGroup(study, buildDrugParoxetine(), dose, 37);
		parox.addMeasurement(pHamd);		


		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		fHamd.setRate(26);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 41);
		fluox.addMeasurement(fHamd);		
		
		dose = new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicRateMeasurement vHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		vHamd.setRate(100);
		BasicPatientGroup viagra = new BasicPatientGroup(study, buildDrugViagra(), dose, 100);
		viagra.addMeasurement(vHamd);
	
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		study.addPatientGroup(viagra);
		
		return study;
	}

	public static BasicStudy buildDefaultStudy() {
		Drug paroxetine = buildDrugParoxetine();
		Endpoint hamd = buildEndpointHamd();
		Endpoint cgi = buildEndpointCgi();
		Drug fluoxetine = buildDrugFluoxetine();
		BasicStudy study = new BasicStudy("Chouinard et al, 1999");
		study.setEndpoints(Arrays.asList(new Endpoint[]{hamd, cgi}));
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		pHamd.setRate(67);

		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)cgi.buildMeasurement();
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		
		BasicPatientGroup parox = new BasicPatientGroup(study, paroxetine, dose, 102);
		parox.addMeasurement(pHamd);		
		parox.addMeasurement(pCgi);

		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		fHamd.setRate(67);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)cgi.buildMeasurement();
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 101);
		fluox.addMeasurement(fHamd);
		fluox.addMeasurement(fCgi);		
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		
		return study;
	}

	public static Drug buildDrugParoxetine() {
		if (s_parox == null) {
			Drug paroxetine = new Drug();
			paroxetine.setName("Paroxetine");
			s_parox = paroxetine;
		}
		return s_parox;
	}

	public static Drug buildDrugFluoxetine() {
		if (s_fluox == null) {
			s_fluox = new Drug();
			s_fluox.setName("Fluoxetine");
		}
		return s_fluox;
	}
	
	public static Drug buildDrugViagra() {
		if (s_viagra == null) {
			s_viagra = new Drug();
			s_viagra.setName("Viagra");
		}
		return s_viagra;
	}

	public static Endpoint buildEndpointHamd() {
		if (s_endpointHamd == null) {
			Endpoint e = new Endpoint();
			e.setName("HAM-D");
			e.setDescription("");
			e.setType(Endpoint.Type.RATE);
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint();
			cgi.setName("CGI Severity");
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			cgi.setType(Endpoint.Type.CONTINUOUS);
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}
}
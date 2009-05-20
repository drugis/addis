package nl.rug.escher.addis.entities.test;

import java.util.Arrays;
import java.util.Collections;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.Study;

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

	public static Study buildDefaultStudy2() {
		Endpoint hamd = buildEndpointHamd();
		Drug paroxetine = buildDrugParoxetine();
		Drug fluoxetine = buildDrugFluoxetine();
		Study study = new Study();
		study.setId("De Wilde et al, 1993");
		study.setEndpoints(Collections.singletonList(hamd));
		
		PatientGroup parox = new PatientGroup();
		parox.setDrug(paroxetine);
		Dose dose = new Dose();
		dose.setQuantity(25.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		parox.setDose(dose);
		parox.setSize(37);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		pHamd.setPatientGroup(parox);
		pHamd.setRate(23);
		parox.addMeasurement(pHamd);
		
		PatientGroup fluox = new PatientGroup();
		fluox.setDrug(fluoxetine);
		dose = new Dose();
		dose.setQuantity(27.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		fluox.setDose(dose);
		fluox.setSize(41);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		fHamd.setPatientGroup(fluox);
		fHamd.setRate(26);
		fluox.addMeasurement(fHamd);
		
		PatientGroup viagra = new PatientGroup();
		viagra.setDrug(buildDrugViagra());
		dose = new Dose();
		dose.setQuantity(10.0);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		viagra.setDose(dose);
		viagra.setSize(100);
		BasicRateMeasurement vHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		vHamd.setPatientGroup(viagra);
		vHamd.setRate(100);
		viagra.addMeasurement(vHamd);
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		study.addPatientGroup(viagra);
		
		return study;
	}

	public static Study buildDefaultStudy() {
		Drug paroxetine = buildDrugParoxetine();
		Endpoint hamd = buildEndpointHamd();
		Endpoint cgi = buildEndpointCgi();
		Drug fluoxetine = buildDrugFluoxetine();
		Study study = new Study();
		study.setId("Chouinard et al, 1999");
		study.setEndpoints(Arrays.asList(new Endpoint[]{hamd, cgi}));
		
		PatientGroup parox = new PatientGroup();
		parox.setDrug(paroxetine);
		Dose dose = new Dose();
		dose.setQuantity(25.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		parox.setDose(dose);
		parox.setSize(102);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		pHamd.setPatientGroup(parox);
		pHamd.setRate(67);
		parox.addMeasurement(pHamd);
		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)cgi.buildMeasurement();
		pCgi.setPatientGroup(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		parox.addMeasurement(pCgi);
		
		PatientGroup fluox = new PatientGroup();
		fluox.setDrug(fluoxetine);
		dose = new Dose();
		dose.setQuantity(27.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		fluox.setDose(dose);
		fluox.setSize(101);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement();
		fHamd.setPatientGroup(fluox);
		fHamd.setRate(67);
		fluox.addMeasurement(fHamd);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)cgi.buildMeasurement();
		fCgi.setPatientGroup(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
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
			Endpoint e = new Endpoint("HAM-D");
			e.setDescription("");
			e.setType(Endpoint.Type.RATE);
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity");
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			cgi.setType(Endpoint.Type.CONTINUOUS);
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}
}
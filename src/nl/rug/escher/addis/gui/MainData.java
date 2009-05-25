package nl.rug.escher.addis.gui;

import java.util.ArrayList;
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

public class MainData {
	public static void initDefaultData(Domain domain) {
		buildDefaultStudy(domain);
		buildDefaultStudy2(domain);
	}

	private static Study buildDefaultStudy2(Domain domain) {
		Endpoint hamd = buildEndpointHamd(domain);
		Drug paroxetine = buildDefaultDrugParoxetine(domain);
		Drug fluoxetine = buildDefaultDrugFluoxetine(domain);
		Study study = new Study();
		study.setId("De Wilde et al, 1993");
		study.setEndpoints(new ArrayList<Endpoint>(Arrays.asList(new Endpoint[]{hamd})));
		
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
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		
		if (domain.getStudy("De Wilde et al, 1993") == null) {
			domain.addStudy(study);
		}
		
		return study;
	}

	private static Study buildDefaultStudy(Domain domain) {
		Drug paroxetine = buildDefaultDrugParoxetine(domain);
		Endpoint hamd = buildEndpointHamd(domain);
		Endpoint cgi = buildDefaultEndpointCgi(domain);
		System.out.println(domain.getEndpoints());
		Drug fluoxetine = buildDefaultDrugFluoxetine(domain);
		Study study = new Study();
		study.setId("Chouinard et al, 1999");
		study.setEndpoints(new ArrayList<Endpoint>(Arrays.asList(new Endpoint[]{hamd, cgi})));
		
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
		
		if (domain.getStudy("Chouinard et al, 1999") == null) {
			domain.addStudy(study);
		}
		
		System.out.println(domain.getEndpoints());
		
		return study;
	}

	private static Drug buildDefaultDrugParoxetine(Domain domain) {
		if (domain.getDrug("Paroxetine") == null) {
			Drug paroxetine = new Drug();
			paroxetine.setName("Paroxetine");
			domain.addDrug(paroxetine);
		}
		return domain.getDrug("Paroxetine");
	}

	private static Drug buildDefaultDrugFluoxetine(Domain domain) {
		if (domain.getDrug("Fluoxetine") == null) {
			Drug fluox = new Drug();
			fluox.setName("Fluoxetine");
			domain.addDrug(fluox);
		}
		return domain.getDrug("Fluoxetine");
	}

	public static Endpoint buildEndpointHamd(Domain domain) {
		if (domain.getEndpoint("HAM-D") == null) {
			Endpoint e = new Endpoint("HAM-D");
			e.setDescription("");
			e.setType(Endpoint.Type.RATE);
			domain.addEndpoint(e);
		}
		return domain.getEndpoint("HAM-D");
	}

	public static Endpoint buildDefaultEndpointCgi(Domain domain) {
		if (domain.getEndpoint("CGI Severity") == null) {
			Endpoint e = new Endpoint("CGI Severity");
			e.setDescription("Change from baseline CGI Severity of Illness score");
			e.setType(Endpoint.Type.CONTINUOUS);
			domain.addEndpoint(e);
		}
		return domain.getEndpoint("CGI Severity");
	}
}
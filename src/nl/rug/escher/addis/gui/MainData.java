package nl.rug.escher.addis.gui;

import java.util.ArrayList;
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
		Endpoint hamd = buildEndpointHamd();
		domain.addEndpoint(hamd);
		domain.addEndpoint(buildDefaultEndpointCgi());
		domain.addDrug(buildDefaultDrugFluoxetine());
		domain.addDrug(buildDefaultDrugParoxetine());
		domain.addStudy(buildDefaultStudy(domain));
		domain.addStudy(buildDefaultStudy2(domain));
	}

	private static Study buildDefaultStudy2(Domain domain) {
		Endpoint hamd = buildEndpointHamd();
		Drug paroxetine = buildDefaultDrugParoxetine();
		Drug fluoxetine = buildDefaultDrugFluoxetine();
		Study study = new Study();
		study.setId("Incomplete Study");
		study.setEndpoints(Collections.singletonList(hamd));
		
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
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		
		return study;
	}

	private static Study buildDefaultStudy(Domain domain) {
		Drug paroxetine = buildDefaultDrugParoxetine();
		Endpoint hamd = buildEndpointHamd();
		Endpoint cgi = buildDefaultEndpointCgi();
		Drug fluoxetine = buildDefaultDrugFluoxetine();
		Study study = new Study();
		study.setId("Chouinard et al, 1999");
		study.setEndpoints(new ArrayList<Endpoint>(domain.getEndpoints()));
		
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

	private static Drug buildDefaultDrugParoxetine() {
		Drug paroxetine = new Drug();
		paroxetine.setName("Paroxetine");
		return paroxetine;
	}

	private static Drug buildDefaultDrugFluoxetine() {
		Drug fluoxetine = new Drug();
		fluoxetine.setName("Fluoxetine");
		return fluoxetine;
	}

	public static Endpoint buildEndpointHamd() {
		Endpoint e = new Endpoint();
		e.setName("HAM-D");
		e.setDescription("");
		e.setType(Endpoint.Type.RATE);
		return e;
	}

	public static Endpoint buildDefaultEndpointCgi() {
		Endpoint cgi = new Endpoint();
		cgi.setName("CGI Severity");
		cgi.setDescription("Change from baseline CGI Severity of Illness score");
		cgi.setType(Endpoint.Type.CONTINUOUS);
		return cgi;
	}
}
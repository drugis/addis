package nl.rug.escher.gui;

import java.util.Collections;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Dose;
import nl.rug.escher.entities.Drug;
import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.SIUnit;
import nl.rug.escher.entities.Study;

public class MainData {
	public Drug d_paroxetine;
	public Drug d_fluoxetine;
	public Endpoint d_hamd;
	public Endpoint d_cgi;
	public Domain d_domain;

	public MainData() {
	}

	public void initDefaultData(Domain domain) {
		d_hamd = buildDefaultEndpoint();
		domain.addEndpoint(d_hamd);
		domain.addEndpoint(buildDefaultEndpoint2());
		domain.addDrug(buildDefaultDrug1());
		domain.addDrug(buildDefaultDrug2());
		domain.addStudy(buildDefaultStudy(domain));
		domain.addStudy(buildDefaultStudy2(domain));
	}

	private Study buildDefaultStudy2(Domain domain) {
		Study study = new Study();
		study.setId("Incomplete Study");
		study.setEndpoints(Collections.singletonList(d_hamd));
		
		PatientGroup parox = new PatientGroup();
		parox.setDrug(d_paroxetine);
		Dose dose = new Dose();
		dose.setQuantity(25.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		parox.setDose(dose);
		Measurement pHamd = new Measurement();
		pHamd.setEndpoint(d_hamd);
		pHamd.setPatientGroup(parox);
		pHamd.setMean(-13.92);
		pHamd.setStdDev(1.1);
		parox.addMeasurement(pHamd);
		
		PatientGroup fluox = new PatientGroup();
		fluox.setDrug(d_fluoxetine);
		dose = new Dose();
		dose.setQuantity(27.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		fluox.setDose(dose);
		Measurement fHamd = new Measurement();
		fHamd.setEndpoint(d_hamd);
		fHamd.setPatientGroup(fluox);
		fHamd.setMean(-14.78);
		fHamd.setStdDev(1.1);
		fluox.addMeasurement(fHamd);
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		
		return study;
	}

	Study buildDefaultStudy(Domain domain) {
		Study study = new Study();
		study.setId("Chouinard et al, 1999");
		study.setEndpoints(domain.getEndpoints());
		
		PatientGroup parox = new PatientGroup();
		parox.setDrug(d_paroxetine);
		Dose dose = new Dose();
		dose.setQuantity(25.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		parox.setDose(dose);
		Measurement pHamd = new Measurement();
		pHamd.setEndpoint(d_hamd);
		pHamd.setPatientGroup(parox);
		pHamd.setMean(-13.92);
		pHamd.setStdDev(1.1);
		parox.addMeasurement(pHamd);
		Measurement pCgi = new Measurement();
		pCgi.setEndpoint(d_cgi);
		pCgi.setPatientGroup(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		parox.addMeasurement(pCgi);
		
		PatientGroup fluox = new PatientGroup();
		fluox.setDrug(d_fluoxetine);
		dose = new Dose();
		dose.setQuantity(27.5);
		dose.setUnit(SIUnit.MILLIGRAMS_A_DAY);
		fluox.setDose(dose);
		Measurement fHamd = new Measurement();
		fHamd.setEndpoint(d_hamd);
		fHamd.setPatientGroup(fluox);
		fHamd.setMean(-14.78);
		fHamd.setStdDev(1.1);
		fluox.addMeasurement(fHamd);
		Measurement fCgi = new Measurement();
		fCgi.setEndpoint(d_cgi);
		fCgi.setPatientGroup(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		fluox.addMeasurement(fCgi);
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		
		return study;
	}

	Drug buildDefaultDrug2() {
		d_paroxetine = new Drug();
		d_paroxetine.setName("Paroxetine");
		return d_paroxetine;
	}

	Drug buildDefaultDrug1() {
		d_fluoxetine = new Drug();
		d_fluoxetine.setName("Fluoxetine");
		return d_fluoxetine;
	}

	public static Endpoint buildDefaultEndpoint() {
		Endpoint e = new Endpoint();
		e.setName("HAM-D");
		e.setDescription("Change from baseline in HAM-D total score (21 items)");
		return e;
	}

	public Endpoint buildDefaultEndpoint2() {
		d_cgi = new Endpoint();
		d_cgi.setName("CGI Severity");
		d_cgi.setDescription("Change from baseline CGI Severity of Illness score");
		return d_cgi;
	}
}
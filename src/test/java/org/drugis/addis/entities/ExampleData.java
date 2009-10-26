/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.drugis.addis.entities.AbstractStudy;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Endpoint.Type;


public class ExampleData {
	private static Indication s_indication;
	private static Endpoint s_endpointHamd;
	private static Endpoint s_endpointCgi;
	private static Drug s_parox;
	private static Drug s_fluox;
	private static Drug s_viagra;
	private static Endpoint s_endpointUnused;

	public static void initDefaultData(Domain domain) {
		domain.addIndication(buildIndicationDepression());
		domain.addEndpoint(buildEndpointHamd());
		domain.addEndpoint(buildEndpointCgi());
		domain.addDrug(buildDrugFluoxetine());
		domain.addDrug(buildDrugParoxetine());
		domain.addStudy(buildDefaultStudy());
		domain.addStudy(buildDefaultStudy2());
		domain.addEndpoint(buildEndpointUnused());
	}

	public static AbstractStudy buildDefaultStudy2() {
		Endpoint hamd = buildEndpointHamd();
		Drug fluoxetine = buildDrugFluoxetine();
		BasicStudy study = new BasicStudy("De Wilde et al, 1993", buildIndicationDepression());
		study.setEndpoints(Collections.singleton(hamd));
		

		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup parox = new BasicPatientGroup(study, buildDrugParoxetine(), dose, 37);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);

		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 41);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		
		dose = new Dose(10.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup viagra = new BasicPatientGroup(study, buildDrugViagra(), dose, 100);
		BasicRateMeasurement vHamd = (BasicRateMeasurement)hamd.buildMeasurement(viagra);
		vHamd.setRate(100);
	
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		study.addPatientGroup(viagra);
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(hamd, fluox, fHamd);
		study.setMeasurement(hamd, viagra, vHamd);
		return study;
	}

	public static AbstractStudy buildDefaultStudy() {
		Drug paroxetine = buildDrugParoxetine();
		Endpoint hamd = buildEndpointHamd();
		Endpoint cgi = buildEndpointCgi();
		Drug fluoxetine = buildDrugFluoxetine();
		BasicStudy study = new BasicStudy("Chouinard et al, 1999", buildIndicationDepression());
		study.setEndpoints(new HashSet<Endpoint>(Arrays.asList(new Endpoint[]{hamd, cgi})));
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup parox = new BasicPatientGroup(study, paroxetine, dose, 102);
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(67);

		BasicContinuousMeasurement pCgi = (BasicContinuousMeasurement)cgi.buildMeasurement(parox);
		pCgi.setMean(-1.69);
		pCgi.setStdDev(0.16);
		

		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 101);
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(67);
		BasicContinuousMeasurement fCgi = (BasicContinuousMeasurement)cgi.buildMeasurement(fluox);
		fCgi.setMean(-1.8);
		fCgi.setStdDev(0.16);
		
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(hamd, fluox, fHamd);		
		study.setMeasurement(cgi, parox, pCgi);
		study.setMeasurement(cgi, fluox, fCgi);
		
		return study;
	}

	public static Drug buildDrugParoxetine() {
		if (s_parox == null) {
			Drug paroxetine = new Drug("Paroxetine", "atc");
			s_parox = paroxetine;
		}
		return s_parox;
	}

	public static Drug buildDrugFluoxetine() {
		if (s_fluox == null) {
			s_fluox = new Drug("Fluoxetine", "atc");
		}
		return s_fluox;
	}
	
	public static Indication buildIndicationDepression() {
		if (s_indication == null) {
			s_indication = new Indication(310497006L, "Severe depression");
		}
		return s_indication;
	}
	
	public static Drug buildDrugViagra() {
		if (s_viagra == null) {
			s_viagra = new Drug("Viagra", "atc");
		}
		return s_viagra;
	}

	public static Endpoint buildEndpointHamd() {
		if (s_endpointHamd == null) {
			Endpoint e = new Endpoint("HAM-D", Type.RATE);
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity", Type.CONTINUOUS);
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}
	
	public static Endpoint buildEndpointUnused() {
		if (s_endpointUnused == null) { 
			Endpoint cgi = new Endpoint("Unused Endpoint", Type.RATE);
			cgi.setDescription("");
			s_endpointUnused = cgi;
		}
		return s_endpointUnused;
	}	

}

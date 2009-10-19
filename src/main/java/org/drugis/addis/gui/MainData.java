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

package org.drugis.addis.gui;

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
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.Endpoint.Type;


public class MainData {
	private static Endpoint s_endpointHamd;
	private static Endpoint s_endpointCgi;
	private static Drug s_parox;
	private static Drug s_fluox;
	private static Indication s_indication;

	public static void initDefaultData(Domain domain) {
		domain.addIndication(buildIndication());
		domain.addEndpoint(buildEndpointHamd());
		domain.addEndpoint(buildDefaultEndpointCgi());
		domain.addDrug(buildDefaultDrugFluoxetine());
		domain.addDrug(buildDefaultDrugParoxetine());
		domain.addStudy(buildDefaultStudy());
		domain.addStudy(buildDefaultStudy2());
	}

	private static AbstractStudy buildDefaultStudy2() {
		Endpoint hamd = buildEndpointHamd();
		Drug paroxetine = buildDefaultDrugParoxetine();
		Drug fluoxetine = buildDefaultDrugFluoxetine();
		BasicStudy study = new BasicStudy("De Wilde et al, 1993", buildIndication());
		study.setCharacteristic(StudyCharacteristic.ARMS, new Integer(2));
		study.setEndpoints(Collections.singleton(hamd));

		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup parox = new BasicPatientGroup(study, paroxetine, dose, 37);		
		BasicRateMeasurement pHamd = (BasicRateMeasurement)hamd.buildMeasurement(parox);
		pHamd.setRate(23);
		
		
		dose = new Dose(27.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup fluox = new BasicPatientGroup(study, fluoxetine, dose, 41);		
		BasicRateMeasurement fHamd = (BasicRateMeasurement)hamd.buildMeasurement(fluox);
		fHamd.setRate(26);
		
		
		study.addPatientGroup(parox);
		study.addPatientGroup(fluox);
		study.setMeasurement(hamd, parox, pHamd);
		study.setMeasurement(hamd, fluox, fHamd);
		
		return study;
	}

	private static AbstractStudy buildDefaultStudy() {
		Drug paroxetine = buildDefaultDrugParoxetine();
		Endpoint hamd = buildEndpointHamd();
		Endpoint cgi = buildDefaultEndpointCgi();
		Drug fluoxetine = buildDefaultDrugFluoxetine();
		BasicStudy study = new BasicStudy("Chouinard et al, 1999", buildIndication());
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
		study.setMeasurement(cgi, fluox, pCgi);
		
		return study;
	}

	private static Indication buildIndication() {
		if (s_indication == null) {
			s_indication = new Indication(310497006L, "Severe depression");
		}
		return s_indication;
	}

	private static Drug buildDefaultDrugParoxetine() {
		if (s_parox == null) {
			Drug paroxetine = new Drug();
			paroxetine.setName("Paroxetine");
			s_parox = paroxetine;
		}
		return s_parox;
	}

	private static Drug buildDefaultDrugFluoxetine() {
		if (s_fluox == null) {
			s_fluox = new Drug();
			s_fluox.setName("Fluoxetine");
		}
		return s_fluox;
	}

	public static Endpoint buildEndpointHamd() {
		if (s_endpointHamd == null) {
			Endpoint e = new Endpoint("HAM-D", Type.RATE);
			e.setDescription("");
			s_endpointHamd = e;
		}
		return s_endpointHamd;
	}

	public static Endpoint buildDefaultEndpointCgi() {
		if (s_endpointCgi == null) { 
			Endpoint cgi = new Endpoint("CGI Severity", Type.CONTINUOUS);
			cgi.setDescription("Change from baseline CGI Severity of Illness score");
			s_endpointCgi = cgi;
		}
		return s_endpointCgi;
	}
}
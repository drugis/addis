/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class NetworkData extends DefaultHandler {
	private final Domain d_domain;
	private XMLReader d_xr;
	private Indication d_indication;
	private OutcomeMeasure d_outcome;
	private Study d_study;

	public NetworkData(Domain domain, Indication indication, OutcomeMeasure outcome) throws SAXException {
		super();
		d_domain = domain;
		d_xr = XMLReaderFactory.createXMLReader();
		d_xr.setContentHandler(this);
		d_xr.setErrorHandler(this);
		
		d_outcome = outcome;
		d_indication = indication;
	}
	
	public void parse(InputStream xml) throws IOException, SAXException {
		d_xr.parse(new InputSource(xml));
	}
	
	public void startElement (String uri, String name,
		      String qName, Attributes atts) {
		if (name.equals("treatment")) {
			String id = atts.getValue("id");
			d_domain.addDrug(new Drug(id, id));
		} else if (name.equals("study")) {
			String id = studyId(atts.getValue("id"));
			d_study = addOrCreateStudy(id);
			d_study.addOutcomeMeasure(d_outcome);
		} else if (name.equals("measurement")) {
			Drug drug = findDrug(atts.getValue("treatment"));
			int resp = Integer.parseInt(atts.getValue("responders"));
			int samp = Integer.parseInt(atts.getValue("sample"));
			Arm arm = addOrCreateArm(d_study, drug, samp);
			BasicRateMeasurement meas = new BasicRateMeasurement(resp, samp);
			d_study.setMeasurement(d_outcome, arm, meas);
		}
	}

	private Arm addOrCreateArm(Study study, Drug drug, int samp) {
		Arm arm = findArm(study, drug);
		if (arm == null) {
			arm = new Arm(drug, new FixedDose(0.0, SIUnit.MILLIGRAMS_A_DAY), samp);
			study.addArm(arm);
		}
		return arm;
	}

	private Arm findArm(Study study, Drug drug) {
		for (Arm a : study.getArms()) {
			if (a.getDrug().equals(drug)) {
				return a;
			}
		}
		return null;
	}

	private Study addOrCreateStudy(String id) {
		Study study = findStudy(id);
		if (study == null) {
			study = new Study(id, d_indication);
			if (studyTitle.get(id) != null) {
				study.setCharacteristic(BasicStudyCharacteristic.TITLE, studyTitle.get(id));
			}
			d_domain.addStudy(study);
		}
		return study;
	}
	
	private Study findStudy(String studyId) {
		for (Study s : d_domain.getStudies()) {
			if (s.getStudyId().equals(studyId)) {
				return s;
			}
		}
		return null;
	}

	private Drug findDrug(String drugId) {
		for (Drug d : d_domain.getDrugs()) {
			if (d.getName().equals(drugId)) {
				return d;
			}
		}
		return null;
	}

	public static void addData(Domain domain, InputStream xml, Indication i, OutcomeMeasure o) {
		try {
			NetworkData reader = new NetworkData(domain, i, o);
			reader.parse(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Endpoint buildEndpointMADRS() {
		Endpoint e = new Endpoint("MADRS Responders", Type.RATE);
		e.setDescription("Responders with a 50% increase in MADRS score");
		return e;
	}
	
	public static Endpoint buildEndpointDropouts() {
		Endpoint e = new Endpoint("Dropouts", Type.RATE);
		e.setDescription("Number of patients dropping out of the study prematurely");
		e.setDirection(Direction.LOWER_IS_BETTER);
		return e;
	}
	
	public static String adeFile(String ade) {
		return ade.replaceAll(" ", "");
	}
	
	public static void main(String[] args) throws IOException {
		String basename = "/home/gert/escher/hansen/";
		String extension = ".xml";
		
		DomainImpl d = new DomainImpl();
		Indication depression = ExampleData.buildIndicationDepression();
		d.addIndication(depression);
		Map<String, OutcomeMeasure> toParse = new HashMap<String, OutcomeMeasure>();
		toParse.put("hamd", ExampleData.buildEndpointHamd());
		toParse.put("madrs", buildEndpointMADRS());
		toParse.put("dropouts", buildEndpointDropouts());
		
		BufferedReader adeReader = new BufferedReader(new FileReader(basename + "adeList.txt"));
		String ade = null;
		while ((ade = adeReader.readLine()) != null) {
			toParse.put(adeFile(ade), buildAdverseEvent(ade));
		}
		adeReader.close();
		
		for (Entry<String, OutcomeMeasure> entry : toParse.entrySet()) {
			try {
				OutcomeMeasure om = entry.getValue();
				String name = entry.getKey();
				
				d.addOutcomeMeasure(om);
				addData(d, new FileInputStream(basename + name + extension), depression, om);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			d.saveXMLDomainData(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static AdverseEvent buildAdverseEvent(String name) {
		return new AdverseEvent(name, Type.RATE);
	}
	
	private static Map<String, String> studyTitle = new HashMap<String, String>();
	static {
		studyTitle.put(studyId("Alves 1999"), "Efficacy and tolerability of venlafaxine and fluoxetine in outpatients with major depression");
		studyTitle.put(studyId("Ballus 2000"), "The efficacy and tolerability of venlafaxine and fluoxetine in outpatients with depressive disorder or dysthymia");
		studyTitle.put(studyId("Behnke 2003"), "Mirtazapine orally disintegrating tablet versus sertraline: a prospective onset of action study");
		studyTitle.put(studyId("Benkert 2000"), "Mirtazapine compared with paroxetine in major depression");
		studyTitle.put(studyId("Chouinard 1999"), "A Canadian multicenter, double-blind study of paroxetine and fluoxetine in major depressive disorder");
		studyTitle.put(studyId("Coleman 2001"), "A placebo-controlled comparison of the effects on sexual functioning of bupropion sustained release and fluoxetine");
		studyTitle.put(studyId("Dalery and Honig 2003"), "Fluvoxamine versus fluoxetine in major depressive episodes: a double-blind randomised comparison");
		studyTitle.put(studyId("Detke 2004"), "Duloxetine in the accute and long-term treatment of major depressive disorder: a placebo- and paroxetine-controlled trial");
		studyTitle.put(studyId("Fava 1998"), "A double-blind study of paroxetine, fluoxetine, and placebo in outpatients with major depression");
		studyTitle.put(studyId("Fava 2002"), "Accute efficacy of fluoxetine versus sertraline and paroxetine in major depressive disorder including effects of baseline insomnia");
		studyTitle.put(studyId("Feighner 1991"), "Double-blind comparison of bupropion and fluoxetine in depressed outpatients");
		studyTitle.put(studyId("Gagiano1993"), "A double blind comparison of paroxetine and fluoxetine in patients with major depression");
		studyTitle.put(studyId("Goldstein 2002"), "Duloxetine in the treatment of major depressive disorder: a double-blind clinical trial");
		studyTitle.put(studyId("Hong 2003"), "A double-blind, randomized, group-comparative study of the tolerability and efficacy of 6 weeks' treatment with mirtazapine or fluoxetine in depressed chinese patients");
		studyTitle.put(studyId("Schatzberg 2002"), "Double-blind randomized comparison of mirtazapine and paroxetine in elderly depressed patients");
		studyTitle.put(studyId("Schone&Ludwig 1993"), "A double-blind study of paroxetine compared with fluoxetine in geriatric patients with major depression");
		studyTitle.put(studyId("Weihs 2000"), "Bupropion sustained release versus paroxetine for the treatment of depression in the elderly");
		studyTitle.put(studyId("Aberg-Wistedt2000"), "Sertraline versus paroxetine in major depression: clinical outcome after six months of continuous therapy");
		studyTitle.put(studyId("Bennie1995"), "A double-blind multicenter trial comparing sertraline and fluoxetine in outpatients with major depression");
		studyTitle.put(studyId("Bielski2004"), "A double-blind comparison of escitalopram and venlafaxine extended release in the treatment of major depressive disorder");
		studyTitle.put(studyId("Boyer1998"), "Clinical and economic comparison of sertraline and fluoxetine in the treatment of depression. A 6-month double-blind study in a primary-care setting in France.");
		studyTitle.put(studyId("Burke2002"), "A fixed-dose trial of the single isomer SSRI escitalopram in depressed outpatients");
		studyTitle.put(studyId("Coleman1999"), "Sexual dysfunction associated with the treatment of depression: a placebo-controlled comparison of bupropion sustained release and sertraline treatment.");
		studyTitle.put(studyId("Croft1999"), "A placebo-controlled comparison of the antidepressant efficacy and effects on sexual functioning of sustained-release bupropion and sertraline.");
		studyTitle.put(studyId("De Nayer2002"), "Venlafaxine compared with fluoxetine in outpatients with depression and concomitant anxiety.");
		studyTitle.put(studyId("De Wilde1993"), "A double-blind, comparative, multicentre study comparing paroxetine with fluoxetine in depressed patients.");
		studyTitle.put(studyId("Dierick1996"), "A double-blind comparison of venlafaxine and fluoxetine for treatment of major depression in outpatients.");
		studyTitle.put(studyId("Ekselius1997"), "A double-blind multicenter trial comparing sertraline and citalopram in patients with major depression treated in general practice.");
		studyTitle.put(studyId("Franchini1997"), "A double-blind study of long-term treatment with sertraline or fluvoxamine for prevention of highly recurrent unipolar depression.");
		studyTitle.put(studyId("Kavoussi1997"), "Double-blind comparison of bupropion sustained release and sertraline in depressed outpatients.");
		studyTitle.put(studyId("Kiev&Feiger1997"), "A double-blind comparison of fluvoxamine and paroxetine in the treatment of depressed outpatients");
		studyTitle.put(studyId("Lepola2003"), "Escitalopram (10-20 mg/day) is effective and well tolerated in a placebo-controlled study in depression in primary care.");
		studyTitle.put(studyId("McPartlin1998"), "A comparison of once-daily venlafaxine XR and paroxetine in depressed outpatients treated in general practice.");
		studyTitle.put(studyId("Mehtonen2000"), "Randomized, double-blind comparison of venlafaxine and sertraline in outpatients with major depressive disorder. Venlafaxine 631 Study Group.");
		studyTitle.put(studyId("Montgomery2004"), "A randomised study comparing escitalopram with venlafaxine XR in primary care patients with major depressive disorder.");
		studyTitle.put(studyId("Nemeroff1995"), "Double-blind multicenter comparison of fluvoxamine versus sertraline in the treatment of depressed outpatients.");
		studyTitle.put(studyId("Newhouse2000"), "A double-blind comparison of sertraline and fluoxetine in depressed elderly outpatients");
		studyTitle.put(studyId("Patris1996"), "Citalopram versus fluoxetine: a double-blind, controlled, multicenter, phase II trial in patients with unipolar depression treated in general practice.");
		studyTitle.put(studyId("Rapaport1996"), "A comparison of fluvoxamine and fluoxetine in the treatment of major depression.");
		studyTitle.put(studyId("Rudolph&Feiger1999"), "A double-blind, randomized, placebo-controlled trial of once-daily venlafaxine extended release (XR) and fluoxetine for the treatment of depression.");
		studyTitle.put(studyId("Sechter1999"), "A double-blind comparison of sertraline and fluoxetine in the treatment of major depressive episode in outpatients.");
		studyTitle.put(studyId("Silverstone&Ravindran1999"), "Once-daily venlafaxine extended release (XR) compared with fluoxetine in outpatients with depression and anxiety.");
		studyTitle.put(studyId("Tylee1997"), "A double-blind, randomized, 12-week comparison study of the safety and efficacy of venlafaxine and fluoxetine in moderate to severe depression in general practice.");
		studyTitle.put(studyId("Weihs2000"), "Bupropion sustained release versus paroxetine for the treatment of depression in the elderly.");
	}
	
	private static String studyId(String str) {
		if (str.equals("Gagiano1993") || str.equals("Gagiano 1993")) return "Gagiano 1993";
		String and = str.replaceAll("&", " and ");
		try {
			Pattern p = Pattern.compile("^(\\D*[a-z])\\s*(\\d*)$");
			Matcher m = p.matcher(and);
			String sep = ", ";
			if (and.indexOf(" and ") == -1) {
				sep = " et al, ";
			}
			m.find();
			return m.group(1) + sep + m.group(2);
		} catch (IllegalStateException e) {
			System.err.println(and);
			throw e;
		}
	}
}

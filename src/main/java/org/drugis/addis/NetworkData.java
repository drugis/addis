package org.drugis.addis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
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
	private Endpoint d_endpoint;
	private Study d_study;

	public NetworkData(Domain domain) throws SAXException {
		super();
		d_domain = domain;
		d_xr = XMLReaderFactory.createXMLReader();
		d_xr.setContentHandler(this);
		d_xr.setErrorHandler(this);
		
		d_endpoint = ExampleData.buildEndpointHamd();
		d_domain.addEndpoint(d_endpoint);
		d_indication = ExampleData.buildIndicationDepression();
		d_domain.addIndication(d_indication);
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
			String id = atts.getValue("id");
			d_study = new Study(id, d_indication);
			d_study.addEndpoint(d_endpoint);
			d_domain.addStudy(d_study);
		} else if (name.equals("measurement")) {
			Drug drug = findDrug(atts.getValue("treatment"));
			int resp = Integer.parseInt(atts.getValue("responders"));
			int samp = Integer.parseInt(atts.getValue("sample"));
			Arm arm = new Arm(drug, new FixedDose(0.0, SIUnit.MILLIGRAMS_A_DAY), samp);
			BasicRateMeasurement meas = new BasicRateMeasurement(resp, samp);
			d_study.addArm(arm);
			d_study.setMeasurement(d_endpoint, arm, meas);
		}
	}

	private Drug findDrug(String drugId) {
		for (Drug d : d_domain.getDrugs()) {
			if (d.getName().equals(drugId)) {
				return d;
			}
		}
		return null;
	}

	public static void initDefaultData(Domain domain, InputStream xml) {
		try {
			NetworkData reader = new NetworkData(domain);
			reader.parse(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DomainImpl d = new DomainImpl();
		try {
			initDefaultData(d, new FileInputStream("/home/gert/workspace/mtc/hamd.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			d.saveXMLDomainData(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

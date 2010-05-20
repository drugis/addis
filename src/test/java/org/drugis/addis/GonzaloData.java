package org.drugis.addis;

import java.io.FileInputStream;
import java.io.InputStream;

import org.drugis.addis.NetworkData.IdGenerator;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.Variable.Type;

public class GonzaloData {
	
	public static Indication buildIndicationDiabetes() {
		return new Indication(73211009L, "Diabetes mellitus");
	}
	
	public static Endpoint buildEndpointMACE() {
		Endpoint endpoint = new Endpoint("MACE", Type.RATE);
		endpoint.setDirection(Direction.LOWER_IS_BETTER);
		endpoint.setDescription("Major Adverse Cardiovascular Events (Cardiac and Cerebrovascular)");
		return endpoint;
	}
	
	public static void main(String[] args) throws Exception {
		String basename = "/home/gert/escher/papers/gonzalo/";
		
		DomainImpl d = new DomainImpl();
		Indication diabetes = buildIndicationDiabetes();
		d.addIndication(diabetes);
		
		IdGenerator gen = new IdGenerator() {
			public String studyId(String id) {
				return id;
			}
		};
		
		Endpoint mace = buildEndpointMACE();
		d.addOutcomeMeasure(mace);
		InputStream xml = new FileInputStream(basename + "mace.xml");
		NetworkData.addData(d, xml, diabetes, mace, gen);
		xml.close();
		
		d.saveXMLDomainData(System.out);
	}
}

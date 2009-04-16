package nl.rug.escher.entities;

import java.util.ArrayList;
import java.util.List;

public class DomainImpl implements Domain {
	private List<Endpoint> d_endpoints;
	
	public DomainImpl() {
		d_endpoints = new ArrayList<Endpoint>();
	}

	public void addEndpoint(Endpoint e) {
		if (e == null) {
			throw new NullPointerException("Endpoint may not be null");
		}
		
		d_endpoints.add(e);
	}

	public List<Endpoint> getEndpoints() {
		return d_endpoints;
	}

}

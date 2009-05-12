/**
 * 
 */
package nl.rug.escher.addis.gui;

import java.util.ArrayList;
import java.util.List;

import nl.rug.escher.addis.entities.Endpoint;

import com.jgoodies.binding.beans.Model;

public class EndpointHolder extends Model { // TODO: implement as adapter or something?
	private Endpoint d_endpoint;
	public static final String PROPERTY_ENDPOINT = "endpoint";
	public void setEndpoint(Endpoint e) {
		Endpoint oldVal = d_endpoint;
		d_endpoint = e;
		firePropertyChange(PROPERTY_ENDPOINT, oldVal, d_endpoint);
	}
	public Endpoint getEndpoint() {
		return d_endpoint;
	}
	public List<Endpoint> asList() {
		List<Endpoint> list = new ArrayList<Endpoint>();
		if (d_endpoint != null) {
			list.add(d_endpoint);
		}
		return list;
	}
}
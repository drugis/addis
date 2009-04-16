package nl.rug.escher.entities;

import java.util.List;

public interface Domain {
	/**
	 * Adds an endpoint to the data model.
	 * 
	 * @param e the endpoint to add
	 * @throws NullPointerException if e is null
	 */
	public void addEndpoint(Endpoint e) throws NullPointerException;

	/**
	 * Get the endpoints stored in the data model.
	 * @return A list of endpoints. Never a null.
	 */
	public List<Endpoint> getEndpoints();
}

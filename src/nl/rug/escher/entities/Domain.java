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
	
	/**
	 * Adds an study to the data model.
	 * 
	 * @param e the study to add
	 * @throws NullPointerException if e is null
	 */
	public void addStudy(Study s) throws NullPointerException;

	/**
	 * Get the studies stored in the data model.
	 * @return A list of studies. Never a null.
	 */
	public List<Study> getStudies();
	
	
	/**
	 * Add a listener to the domain object.
	 */
	public void addListener(DomainListener listener);
	
	/**
	 * Remove a listener from the domain object.
	 */
	public void removeListener(DomainListener listener);
}

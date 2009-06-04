package nl.rug.escher.addis.entities;

import java.util.Set;

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
	public Set<Endpoint> getEndpoints();
	
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
	public Set<Study> getStudies();
	
	
	/**
	 * Get studies by Endpoint.
	 * @return A list of studies. Never null.
	 */
	public Set<Study> getStudies(Endpoint e);
	
	/**
	 * Adds an drug to the data model.
	 * 
	 * @param e the drug to add
	 * @throws NullPointerException if e is null
	 */
	public void addDrug(Drug d) throws NullPointerException;

	/**
	 * Get the drugs stored in the data model.
	 * @return A list of drugs. Never a null.
	 */
	public Set<Drug> getDrugs();
	
	
	/**
	 * Add a listener to the domain object.
	 */
	public void addListener(DomainListener listener);
	
	/**
	 * Remove a listener from the domain object.
	 */
	public void removeListener(DomainListener listener);
}

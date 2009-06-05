package nl.rug.escher.addis.entities;

import java.util.SortedSet;

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
	 * @return An unmodifiable sorted set of endpoints. Never a null.
	 */
	public SortedSet<Endpoint> getEndpoints();
		
	/**
	 * Adds a study to the data model.
	 * 
	 * @param e the study to add
	 * @throws NullPointerException if e is null
	 */
	public void addStudy(Study s) throws NullPointerException;

	/**
	 * Get the studies stored in the data model.
	 * @return An unmodifiable sorted set of studies. Never a null.
	 */
	public SortedSet<Study> getStudies();
	
	
	/**
	 * Get studies by Endpoint.
	 * @return An unmodifiable sorted set of studies. Never null.
	 */
	public SortedSet<Study> getStudies(Endpoint e);
	
	/**
	 * Adds an drug to the data model.
	 * 
	 * @param e the drug to add
	 * @throws NullPointerException if e is null
	 */
	public void addDrug(Drug d) throws NullPointerException;

	/**
	 * Get the drugs stored in the data model.
	 * @return An unmodifiable sorted set of drugs. Never a null.
	 */
	public SortedSet<Drug> getDrugs();
	
	/**
	 * Add a listener to the domain object.
	 */
	public void addListener(DomainListener listener);
	
	/**
	 * Remove a listener from the domain object.
	 */
	public void removeListener(DomainListener listener);
}

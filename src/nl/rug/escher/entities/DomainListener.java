package nl.rug.escher.entities;

public interface DomainListener {
	/**
	 * Called when the list of endpoints (or the endpoints within) has changed.
	 */
	public void endpointsChanged();
	
	/**
	 * Called when the list of studies (or the studies within) has changed.
	 */
	public void studiesChanged();

	/**
	 * Called when the list of drugs (or the drugs within) has changed.
	 */
	public void drugsChanged();
}

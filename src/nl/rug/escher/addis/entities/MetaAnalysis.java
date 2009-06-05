package nl.rug.escher.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a meta-analysis over all common drugs within the list of studies.
 * Current assumptions: each study has max one patient group for each drug;
 * every Measurement is a RateMeasurement.
 */
public class MetaAnalysis implements Serializable {
	private static final long serialVersionUID = 3621813375995564247L;
	private List<Study> d_studies;
	private Set<Drug> d_drugs;
	private Endpoint d_endpoint;
	
	public MetaAnalysis(Endpoint endpoint, List<Study> studies) throws IllegalArgumentException {
		validate(endpoint, studies);
		
		d_endpoint = endpoint;
		d_studies = studies;
		d_drugs = findCommonDrugs();
	}
		
	public Set<Drug> getDrugs() {
		return d_drugs;
	}
	
	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	/**
	 * 
	 * @param study A study contained in getStudies()
	 * @param drug A drug contained in getDrugs()
	 * @return The measurement from Study on Drug
	 */
	public Measurement getMeasurement(Study study, Drug drug) {
		for (PatientGroup g : study.getPatientGroups()) {
			if (g.getDrug().equals(drug)) {
				return g.getMeasurement(getEndpoint());
			}
		}
		return null;
	}
	
	public Measurement getPooledMeasurement(Drug drug) {
		List<RateMeasurement> measurements = new ArrayList<RateMeasurement>();
		for (Study s : d_studies) {
			measurements.add((RateMeasurement)getMeasurement(s, drug));
		}
		return new PooledRateMeasurement(measurements);
	}
	
	public List<Study> getStudies() {
		return d_studies;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + getEndpoint().hashCode();
		hash = 31 * hash + new HashSet<Study>(getStudies()).hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MetaAnalysis) {
			MetaAnalysis other = (MetaAnalysis)o;
			if (other.getStudies().size() != getStudies().size()) {
				return false;
			}
			return getStudies().containsAll(other.getStudies()) && 
					getEndpoint().equals(other.getEndpoint());
		}
		return false;
	}
	
	private Set<Drug> findCommonDrugs() {
		Set<Drug> drugs = d_studies.get(0).getDrugs();
		for (Study s : d_studies) {
			drugs.retainAll(s.getDrugs());
		}
		return drugs;
	}
	
	private void validate(Endpoint endpoint, List<Study> studies) {
		for (Study s : studies) {
			if (!s.getEndpoints().contains(endpoint)) {
				throw new IllegalArgumentException("Study " + s + " does not measure " + endpoint);
			}
		}
	}
}
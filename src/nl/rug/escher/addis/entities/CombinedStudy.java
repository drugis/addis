package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinedStudy extends AbstractStudy {

	private static final long serialVersionUID = -6356786572659381412L;
	private Set<Study> d_studies;

	public CombinedStudy(String id, Set<Study> studies) {
		super(id);
		assert(studies.size() > 0);
		d_studies = studies;
	}

	public Set<Drug> getDrugs() {
		Set<Drug> set = new HashSet<Drug>();
		for (Study s : d_studies) {
			set.addAll(s.getDrugs());
		}
		return set;
	}

	public List<? extends PatientGroup> getPatientGroups() {
		List<PatientGroup> list = new ArrayList<PatientGroup>();
		for (Study s : d_studies) {
			list.addAll(s.getPatientGroups());
		}
		return list;		
	}

	public Set<Entity> getDependencies() {
		Set<Entity> set = new HashSet<Entity>();
		for (Study s : d_studies) {
			set.addAll(s.getDependencies());
		}
		return set;
	}
	
	@Override
	public Set<Endpoint> getEndpoints() {
		Set<Endpoint> set = new HashSet<Endpoint>();
		set.addAll(super.getEndpoints());
		for (Study s : d_studies) {
			set.addAll(s.getEndpoints());
		}
		return set;
	}
	
	@Override
	public Measurement getMeasurement(Endpoint e, PatientGroup g) {
		for (Study s : d_studies) {
			if (s.getPatientGroups().contains(g) && s.getEndpoints().contains(e)) {
				return s.getMeasurement(e, g);
			}
		}
		return super.getMeasurement(e, g);
	}
	
	public Set<Study> getStudies() {
		return d_studies;
	}
	
	public Set<Drug> getCommonDrugs() {
		Set<Drug> drugs = null;
		for (Study s : getStudies()) {
			if (drugs == null) {
				drugs = new HashSet<Drug>();
				drugs.addAll(s.getDrugs());
			} else {
				drugs.retainAll(s.getDrugs());
			}
		}
		return drugs;
	}

}

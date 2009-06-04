package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MetaStudy extends AbstractStudy {
	
	private static final long serialVersionUID = 4551624355872585225L;
	private MetaAnalysis d_analysis;

	public MetaStudy(String id, MetaAnalysis analysis) {
		super(id);
		d_analysis = analysis;
	}
	
	public MetaAnalysis getAnalysis() {
		return d_analysis;
	}

	public Set<Drug> getDrugs() {
		return d_analysis.getDrugs();
	}

	public List<Endpoint> getEndpoints() {
		return Collections.singletonList(d_analysis.getEndpoint());
	}

	public List<PatientGroup> getPatientGroups() {
		List<PatientGroup> l = new ArrayList<PatientGroup>();

		for (Drug d : d_analysis.getDrugs()) {
			l.add(new PooledPatientGroup(this, d));
		}
		
		return l;
	}

}

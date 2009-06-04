package nl.rug.escher.addis.entities;

import java.util.Collections;
import java.util.List;

import com.jgoodies.binding.beans.Model;

public class PooledPatientGroup extends Model implements PatientGroup {
	
	private static final long serialVersionUID = 7548091994878904366L;
	
	private MetaStudy d_study;
	private Drug d_drug;

	public PooledPatientGroup(MetaStudy study, Drug drug) {
		d_study = study;
		d_drug = drug;
	}

	public Dose getDose() {
		return new UnknownDose();
	}

	public Drug getDrug() {
		return d_drug;
	}

	public String getLabel() {
		return "META " + d_drug.toString();
	}

	public Measurement getMeasurement(Endpoint endpoint) {
		if (!endpoint.equals(d_study.getAnalysis().getEndpoint())) {
			return null;
		}
		return d_study.getAnalysis().getPooledMeasurement(d_drug);
	}

	public List<Measurement> getMeasurements() {
		return Collections.singletonList(d_study.getAnalysis().getPooledMeasurement(d_drug));
	}

	public Integer getSize() {
		return d_study.getAnalysis().getPooledMeasurement(d_drug).getSampleSize();
	}

	public Study getStudy() {
		return d_study;
	}

}

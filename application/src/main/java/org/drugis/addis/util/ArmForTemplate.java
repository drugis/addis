package org.drugis.addis.util;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.DurationPresentation;

public class ArmForTemplate
{
	private Study d_study;
	
	/**
	 * Arm Class used by the template. 
	 * The getters are important, should not be renamed.
	 * $it.name$ in template corresponds to getName(), where $it is the iterator
	 */
	private Arm d_arm;

	public ArmForTemplate(Study study, Arm arm) {
		d_study = study;
		d_arm = arm;
	}

	public String getName() {
		return d_arm.getName();
	}
	public String getTreatment() {
		return d_study.getTreatment(d_arm).getLabel();
	}
	public String getDuration() {
		return getEpochDuration(d_study.findTreatmentEpoch());
	}
	public String getNrRandomized() {
		return d_arm.getSize().toString();
	}
	
	private static String getEpochDuration(Epoch epoch) {
		if (epoch != null && epoch.getDuration() != null) {
			DurationPresentation<Epoch> pm = new DurationPresentation<Epoch>(epoch);
			return pm.getLabel();
		}
		return "&lt;duration&gt;";
	}
}
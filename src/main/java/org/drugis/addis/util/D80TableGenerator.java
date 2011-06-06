package org.drugis.addis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.PredefinedActivity;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.EpochDurationPresentation;
import org.stringtemplate.v4.ST;

public class D80TableGenerator {
	private final Study d_study;

	public D80TableGenerator(Study study) {
		d_study = study;
	}

	public String render() {
		Epoch mainPhase = d_study.findTreatmentEpoch();
		Epoch runInPhase = d_study.findEpochWithActivity(PredefinedActivity.WASH_OUT);
		Epoch extensionPhase = d_study.findEpochWithActivity(PredefinedActivity.FOLLOW_UP);
		
		ST processor = new ST(getTemplate(), '$', '$');
		processor.add("_study_id_", d_study.getName());
		processor.add("_main_phase_",  getEpochDuration(mainPhase));
		processor.add("_run_in_phase_",  getEpochDuration(runInPhase));
		processor.add("_extension_phase_",  getEpochDuration(extensionPhase));
		processor.add("_arms_", getArms());
		processor.add("_endpoints_", getEndpoints());
		processor.add("_colspan_statistics_", d_study.getEndpoints().size() + 2);

		return processor.render();
	}

	private static String getEpochDuration(Epoch epoch) {
		if (epoch != null && epoch.getDuration() != null) {
			EpochDurationPresentation pm = new EpochDurationPresentation(epoch);
			return pm.getLabel();
		}
		return "&lt;time&gt;";
	}
	
	@SuppressWarnings("unused")
	private class ArmForTemplate {
		/**
		 * Arm Class used by the template. 
		 * The getters are important, should not be renamed.
		 * $it.name$ in template corresponds to getName(), where $it is the iterator
		 */
		private final Arm d_arm;
		public ArmForTemplate(Arm arm) {
			d_arm = arm;
		}

		public String getName() {
			return d_arm.getName();
		}
		public String getTreatment() {
			return d_study.getTreatment(d_arm).getDescription();
		}
		public String getDuration() {
			return getEpochDuration(d_study.findTreatmentEpoch());
		}
		public String getNrRandomized() {
			return d_arm.getSize().toString();
		}
	}
	
	private ArmForTemplate[] getArms() {
		ArmForTemplate[] ca = new ArmForTemplate[d_study.getArms().size()];
		for (int i = 0; i < ca.length; ++i) {
			ca[i] = new ArmForTemplate(d_study.getArms().get(i));
		}
		return ca;
	}

	@SuppressWarnings("unused")
	private class EndpointForTemplate {		
		private final Endpoint d_endpoint;

		public EndpointForTemplate(Endpoint endpoint) {
			d_endpoint = endpoint;
		}
		
		public String getType() { 
			return d_endpoint.getType().toString();
		}
		public String getName() {
			return d_endpoint.getName();
		}
		public String getDescription() {
			return d_endpoint.getDescription();
		}
		public String[] getMeasurements() {
			List<String> ms = new ArrayList<String>();
			for (Arm a : d_study.getArms()) {
				ms.add(d_study.getMeasurement(d_endpoint, a).toString());
			}
			return ms.toArray(new String[0]);
		}
	}
	
	public EndpointForTemplate[] getEndpoints() {
		EndpointForTemplate[] ep = new EndpointForTemplate[d_study.getEndpoints().size()];
		for (int i = 0; i < ep.length; ++i) {
			ep[i] = new EndpointForTemplate(d_study.getEndpoints().get(i).getValue());
		}
		return ep;
	}
	
	public static String getHtml(Study study) {
		return (new D80TableGenerator(study)).render();
	}
	
	public static String getTemplate() {
		String html = "";
		try {
			InputStreamReader fr = new InputStreamReader(D80TableGenerator.class.getResourceAsStream("TemplateD80Report.html"));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null ) {
				html += line;
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not find / load template file.", e);
		}
		return html;
	}
}
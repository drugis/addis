package org.drugis.addis.entities.metaanalysis;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.NetworkBuilder;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis, Runnable{
	private static final long serialVersionUID = -1646175155970420625L;
	
	transient private InconsistencyModel d_model;
	transient private NetworkBuilder d_builder;

	private Map<Study, Map<Drug, Arm>> d_armMap;

	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_armMap = armMap;
	}

	private InconsistencyModel createInconsistencyModel() {
		return (DefaultModelFactory.instance()).getInconsistencyModel(getBuilder().buildNetwork());
	}

	private NetworkBuilder createBuilder(List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) {
		NetworkBuilder builder = new NetworkBuilder();
		for(Study s : studies){
			for (Drug d : drugs) {
				for (Variable v : s.getVariables(Endpoint.class)) {
					if(! s.getDrugs().contains(d))
						break;
					Arm a = armMap.get(s).get(d);
//					TODO: this check must be changed after meta analysis can be done over other measurements as well 
					if(! (s.getMeasurement(v, a) instanceof BasicRateMeasurement)) 
						break;
					BasicRateMeasurement m = (BasicRateMeasurement)s.getMeasurement(v, a);	
					builder.add(s.getId(), a.getDrug().getName(), m.getRate(), m.getSampleSize());
				}
        	}
        }
		return builder;
	}

	public String getType() {
		return "Markov Chain Monte Carlo Network Meta-Analysis";
	}

	public InconsistencyModel getModel() {
		if (d_model == null) {
			d_model = createInconsistencyModel();
		}
		return d_model;
	}

	public NetworkBuilder getBuilder() {
		if (d_builder == null) {
			d_builder = createBuilder(d_studies, d_drugs, d_armMap);
		}
		return d_builder;
	}

	public void run() {
		// TODO Auto-generated method stub
		d_model = createInconsistencyModel();
	}
}

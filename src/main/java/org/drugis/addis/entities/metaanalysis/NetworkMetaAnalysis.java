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
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.NetworkBuilder;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis{
	private static final long serialVersionUID = -1646175155970420625L;
	
	transient private InconsistencyModel d_inconsistencyModel;
	transient private ConsistencyModel d_consistencyModel;
	transient private NetworkBuilder d_builder;
	transient private boolean d_hasRun = false;
	
	public NetworkMetaAnalysis(){
	}
	
	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_armMap = armMap;
	}
	
	public Double getRankProbability(Drug d, int rank){
		return d_consistencyModel.rankProbability(d_builder.getTreatment(d.getName()), rank);
	}

	private InconsistencyModel createInconsistencyModel() {
		return (DefaultModelFactory.instance()).getInconsistencyModel(getBuilder().buildNetwork());
	}
	
	private ConsistencyModel createConsistencyModel() {
		return (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
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
					builder.add(s.getStudyId(), a.getDrug().getName(), m.getRate(), m.getSampleSize());
				}
        	}
        }
		return builder;
	}

	public String getType() {
		return "Markov Chain Monte Carlo Network Meta-Analysis";
	}

	public InconsistencyModel getInconsistencyModel() {
		if (d_inconsistencyModel == null) {
			d_inconsistencyModel = createInconsistencyModel();
		}
		return d_inconsistencyModel;
	}
	
	public ConsistencyModel getConsistencyModel() {
		if (d_consistencyModel == null) {
			d_consistencyModel = createConsistencyModel();
		}
		return d_consistencyModel;
	}

	public NetworkBuilder getBuilder() {
		if (d_builder == null) {
			d_builder = createBuilder(d_studies, d_drugs, d_armMap);
		}
		return d_builder;
	}

	public void run() {
		if (!d_hasRun)
		{
			Thread inconsistency = new Thread(getInconsistencyModel());
			inconsistency.start();
		
			Thread consistency = new Thread(getConsistencyModel());
			consistency.start();
		}
		d_hasRun = true;	
	}
	
	public List<InconsistencyParameter> getInconsistencyFactors(){
		return getInconsistencyModel().getInconsistencyFactors();
	}
	
	public Estimate getInconsistency(InconsistencyParameter ip){
		return getInconsistencyModel().getInconsistency(ip);
	}
	
	@Override
	public String[] getXmlExclusions() {
		return new String[] {"armList", "builder", "consistencyModel", "inconsistencyModel", "inconsistencyFactors", "type", "studiesIncluded", "sampleSize"};
	}
}

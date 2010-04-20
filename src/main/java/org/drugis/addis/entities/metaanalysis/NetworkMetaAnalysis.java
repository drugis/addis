package org.drugis.addis.entities.metaanalysis;

import java.util.List;
import java.util.Map;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.DichotomousNetworkBuilder;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.Network;
import org.drugis.mtc.NetworkBuilder;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis{
	private static final long serialVersionUID = -1646175155970420625L;
	
	transient private InconsistencyModel d_inconsistencyModel;
	transient private ConsistencyModel d_consistencyModel;
	transient private NetworkBuilder<? extends org.drugis.mtc.Measurement> d_builder;
	transient private boolean d_hasRun = false;
	
	private NetworkMetaAnalysis() {
		super();
	}
	
	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
	}
	
	public Double getRankProbability(Drug d, int rank){
		return d_consistencyModel.rankProbability(d_builder.getTreatment(d.getName()), rank);
	}

	private InconsistencyModel createInconsistencyModel() {
		Network<? extends org.drugis.mtc.Measurement> network = getBuilder().buildNetwork();
		return (DefaultModelFactory.instance()).getInconsistencyModel(network);
	}
	
	private ConsistencyModel createConsistencyModel() {
		return (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
	}

	private NetworkBuilder<? extends org.drugis.mtc.Measurement> createBuilder(List<? extends Study> studies, List<Drug> drugs, Map<Study, Map<Drug, Arm>> armMap) {
		for(Study s : studies){
			for (Drug d : drugs) {
				if(! s.getDrugs().contains(d))
					continue;
				for (Variable v : s.getVariables(Endpoint.class)) {
					if (!v.equals(d_outcome))
						continue;
					Arm a = armMap.get(s).get(d);
					Measurement m = s.getMeasurement(v, a);
					if(m instanceof BasicRateMeasurement) {
						BasicRateMeasurement brm = (BasicRateMeasurement)m;	
						((DichotomousNetworkBuilder) getTypedBuilder(brm)).add(s.getStudyId(), a.getDrug().getName(),
																			   brm.getRate(), brm.getSampleSize());
					} else if (m instanceof BasicContinuousMeasurement) {
						BasicContinuousMeasurement cm = (BasicContinuousMeasurement) m;
						((ContinuousNetworkBuilder) getTypedBuilder(cm)).add(s.getStudyId(), a.getDrug().getName(),
																	           cm.getMean(), cm.getStdDev(), cm.getSampleSize());
					}
				}
        	}
        }
		return d_builder;
	}
	
	private NetworkBuilder <? extends org.drugis.mtc.Measurement> getTypedBuilder(Measurement m) {
		if(d_builder != null)
			return d_builder;
		else if (m instanceof BasicRateMeasurement)
			return d_builder = new DichotomousNetworkBuilder();
		else if (m instanceof BasicContinuousMeasurement)
			return d_builder = new ContinuousNetworkBuilder();
		else 
			throw new IllegalStateException("Unknown type of measurement: "+m);	
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

	public NetworkBuilder<? extends org.drugis.mtc.Measurement> getBuilder() {
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
	
	protected static final XMLFormat<NetworkMetaAnalysis> NETWORK_XML = 
			new XMLFormat<NetworkMetaAnalysis>(NetworkMetaAnalysis.class) {
		@Override
		public NetworkMetaAnalysis newInstance(Class<NetworkMetaAnalysis> cls, InputElement xml) {
			return new NetworkMetaAnalysis();
		}
		
		@Override
		public void read(InputElement arg0, NetworkMetaAnalysis arg1) throws XMLStreamException {
			XML.read(arg0, arg1);
		}

		@Override
		public void write(NetworkMetaAnalysis arg0, OutputElement arg1) throws XMLStreamException {
			XML.write(arg0, arg1);
		}
	};
}

/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.entities.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.DichotomousNetworkBuilder;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Network;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis{
	
	transient private InconsistencyModel d_inconsistencyModel;
	transient private ConsistencyModel d_consistencyModel;
	transient private NetworkBuilder<? extends org.drugis.mtc.Measurement> d_builder;
	protected Map<MCMCModel, Map<Parameter, NormalSummary>> d_summaries = 
		new HashMap<MCMCModel, Map<Parameter, NormalSummary>>();

	private boolean d_isContinuous = false;
	
	private NetworkMetaAnalysis() {
		super();
	}
	
	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
	}

	private InconsistencyModel createInconsistencyModel() {
		InconsistencyModel inconsistencyModel = (DefaultModelFactory.instance()).getInconsistencyModel((Network<? extends org.drugis.mtc.Measurement>) getBuilder().buildNetwork());
		d_summaries.put(inconsistencyModel, new HashMap<Parameter, NormalSummary>());
		return inconsistencyModel;
	}
	
	private ConsistencyModel createConsistencyModel() {
		ConsistencyModel consistencyModel = (DefaultModelFactory.instance()).getConsistencyModel(getBuilder().buildNetwork());
		d_summaries.put(consistencyModel, new HashMap<Parameter, NormalSummary>());
		return consistencyModel;
	}

	private NetworkBuilder<? extends org.drugis.mtc.Measurement> createBuilder(List<? extends Study> studies, List<Drug> drugs, Map<Study, Map<Drug, Arm>> armMap) {
		for(Study s : studies){
			for (Drug d : drugs) {
				if(!s.getDrugs().contains(d))
					continue;
				for (Variable v : s.getVariables(OutcomeMeasure.class)) {
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
		else if (m instanceof BasicContinuousMeasurement){ 
			d_isContinuous = true;
			return d_builder = new ContinuousNetworkBuilder();
		} else 
			throw new IllegalStateException("Unknown type of measurement: "+m);	
	}

	public String getType() {
		return "Markov Chain Monte Carlo Network Meta-Analysis";
	}

	public synchronized InconsistencyModel getInconsistencyModel() {
		if (d_inconsistencyModel == null) {
			d_inconsistencyModel = createInconsistencyModel();
		}
		return d_inconsistencyModel;
	}
	
	public synchronized ConsistencyModel getConsistencyModel() {
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
	
	public Network<?> getNetwork() {
		return d_builder.buildNetwork();
	}
	
	public void run() {
		List<Task> tasks = new ArrayList<Task>();
		if (!getConsistencyModel().isReady()) {
			tasks.add(getConsistencyModel().getActivityTask());
		}
		if (!getInconsistencyModel().isReady()) {
			tasks.add(getInconsistencyModel().getActivityTask());
		}
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}


	public List<Parameter> getInconsistencyFactors(){
		return getInconsistencyModel().getInconsistencyFactors();
	}
	
	public NormalSummary getNormalSummary(MixedTreatmentComparison networkModel, Parameter ip){
		NormalSummary summary = d_summaries.get(networkModel).get(ip);
		if (summary == null) {
			summary = new NormalSummary(networkModel.getResults(), ip);
			d_summaries.get(networkModel).put(ip, summary);
		}
		return summary;
	}
	
	public RankProbabilitySummary getRankProbabilities() {
		// Note that the Summary should be cached to avoid re-calculating it all the time.
		// new RankProbabilitySummary(d_consistencyModel, treatmentList);
		return null;
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
	
	public boolean isContinuous() {
		return d_isContinuous;
	}

	public NetworkRelativeEffect<? extends Measurement> getRelativeEffect(Drug d1, Drug d2, Class<? extends RelativeEffect<?>> type) {
		
		if(!getConsistencyModel().isReady())
			return new NetworkRelativeEffect<Measurement>(); // empty relative effect.
		
		ConsistencyModel consistencyModel = getConsistencyModel();
		Parameter param = consistencyModel.getRelativeEffect(new Treatment(d1.getName()), new Treatment(d2.getName()));
		NormalSummary estimate = getNormalSummary(consistencyModel, param);
		
		if (isContinuous()) {
			return NetworkRelativeEffect.buildMeanDifference(estimate.getMean(), estimate.getStandardDeviation());
		} else {
			return NetworkRelativeEffect.buildOddsRatio(estimate.getMean(), estimate.getStandardDeviation());
		}
	}
}

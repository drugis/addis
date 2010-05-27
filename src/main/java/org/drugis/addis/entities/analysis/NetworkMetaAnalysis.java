/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.DichotomousNetworkBuilder;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.Network;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Treatment;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis{
	
	transient private InconsistencyModel d_inconsistencyModel;
	transient private ConsistencyModel d_consistencyModel;
	transient private NetworkBuilder<? extends org.drugis.mtc.Measurement> d_builder;
	transient private boolean d_consistencyHasStarted = false;
	transient private boolean d_inconsistencyHasStarted = false;

	private boolean d_isContinuous = false;
	
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
		/*  
		 * Comments generate network xml code that can be read by the scala implementation.
		 */
//		System.out.println("<network>");
//		System.out.println("<treatments>");
//		for (Drug d : drugs) {
//			System.out.println("<treatment id=\"" + d.getName() + "\"/>");
//		}
//		System.out.println("</treatments>");
//		System.out.println("<studies>");
		for(Study s : studies){
//			System.out.println("<study id=\"" + s.getStudyId() + "\">");
			for (Drug d : drugs) {
				if(! s.getDrugs().contains(d))
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
//						System.out.println("<measurement treatment=\"" + a.getDrug().getName() + "\" responders=\"" + 
//								brm.getRate() + "\" sample=\"" + brm.getSampleSize() + "\"/>");
					} else if (m instanceof BasicContinuousMeasurement) {
						BasicContinuousMeasurement cm = (BasicContinuousMeasurement) m;
						((ContinuousNetworkBuilder) getTypedBuilder(cm)).add(s.getStudyId(), a.getDrug().getName(),
																	           cm.getMean(), cm.getStdDev(), cm.getSampleSize());
					}
				}
				
        	}
//			System.out.println("</study>");
        }
//		System.out.println("</studies>");
//		System.out.println("</network>");
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
		runInconsistency();
		runConsistency();
	}

	public void runInconsistency() {
		if (!d_inconsistencyHasStarted)
			new Thread(getInconsistencyModel()).start();
		d_inconsistencyHasStarted = true;
	}

	public void runConsistency() {
		if (!d_consistencyHasStarted)
			new Thread(getConsistencyModel()).start();
		d_consistencyHasStarted = true;
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
	
	public boolean isContinuous() {
		return d_isContinuous;
	}

	public NetworkRelativeEffect<? extends Measurement> getRelativeEffect(Drug d1, Drug d2, Class<? extends RelativeEffect<?>> type) {
		
		if(!getConsistencyModel().isReady())
			return null;
		
		ConsistencyModel consistencyModel = getConsistencyModel();
		Estimate estimate = consistencyModel.getRelativeEffect(new Treatment(d1.getName()), new Treatment(d2.getName()));
		
		if (isContinuous()) {
			return NetworkRelativeEffect.buildMeanDifference(estimate.getMean(), estimate.getStandardDeviation());
		} else {
			return NetworkRelativeEffect.buildOddsRatio(estimate.getMean(), estimate.getStandardDeviation());
		}
	}
}

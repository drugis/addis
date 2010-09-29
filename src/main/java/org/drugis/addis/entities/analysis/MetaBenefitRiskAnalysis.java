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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.NetworkRelativeEffect;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.mcmcmodel.AbstractBaselineModel;
import org.drugis.addis.mcmcmodel.BaselineMeanDifferenceModel;
import org.drugis.addis.mcmcmodel.BaselineOddsModel;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.common.AlphabeticalComparator;
import org.drugis.common.OutcomeComparator;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

public class MetaBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Drug> {
	
	private String d_name;
	private Indication d_indication;
	private List<OutcomeMeasure> d_outcomeMeasures;
	private List<MetaAnalysis> d_metaAnalyses;
	private List<Drug> d_drugs;
	private Drug d_baseline;
	private Map<OutcomeMeasure,AbstractBaselineModel<?>> d_baselineModelMap;
	private AnalysisType d_analysisType;
	
	public static String PROPERTY_DRUGS = "drugs";
	public static String PROPERTY_BASELINE = "baseline";
	public static String PROPERTY_METAANALYSES = "metaAnalyses";
	
	private final class SimulationFinishedNotifier implements ProgressListener {
		private final AbstractMeasurementSource<Drug> d_source;
		public SimulationFinishedNotifier(AbstractMeasurementSource<Drug> source) {
			d_source = source;
		}
		public void update(MCMCModel mtc, ProgressEvent event) {
			if (event.getType() == EventType.SIMULATION_FINISHED) {
				d_source.notifyListeners();
			}
		}
	}

	private class AbsoluteMeasurementSource extends AbstractMeasurementSource<Drug> {
		public AbsoluteMeasurementSource() {
			SimulationFinishedNotifier simulationFinishedNotifier = new SimulationFinishedNotifier(this);
			for (OutcomeMeasure om : getOutcomeMeasures()) {
				getBaselineModel(om).addProgressListener(simulationFinishedNotifier);
			}
			attachToAllNetworkModels(simulationFinishedNotifier);
			
		}
		
		public Distribution getMeasurement(Drug alternative, OutcomeMeasure criterion) {
			return getAbsoluteEffectDistribution(alternative, criterion);
		}
	}
	
	private void attachToAllNetworkModels(
			SimulationFinishedNotifier simulationFinishedNotifier) {
		for (MetaAnalysis ma : getMetaAnalyses()) {
			if (ma instanceof NetworkMetaAnalysis) {
				NetworkMetaAnalysis nma = (NetworkMetaAnalysis)ma;
				nma.getConsistencyModel().addProgressListener(simulationFinishedNotifier);
			}
		}
	}
	
	private class RelativeMeasurementSource extends AbstractMeasurementSource<Drug> {
		public RelativeMeasurementSource() {
			SimulationFinishedNotifier simulationFinishedNotifier = new SimulationFinishedNotifier(this);
			attachToAllNetworkModels(simulationFinishedNotifier);
		}

		public Distribution getMeasurement(Drug alternative, OutcomeMeasure criterion) {
			return getRelativeEffectDistribution(alternative, criterion);
		}
	}
	
	private MetaBenefitRiskAnalysis() {
		d_baselineModelMap = new HashMap<OutcomeMeasure,AbstractBaselineModel<?>>();
		d_metaAnalyses = new ArrayList<MetaAnalysis>();
	}
	
	public MetaBenefitRiskAnalysis(String id, Indication indication, List<MetaAnalysis> metaAnalysis,
			Drug baseline, List<Drug> drugs, AnalysisType analysisType) {
		super();
		d_indication = indication;
		d_metaAnalyses = metaAnalysis;
		d_outcomeMeasures = findOutcomeMeasures();
		d_drugs = drugs;
		d_baselineModelMap = new HashMap<OutcomeMeasure,AbstractBaselineModel<?>>();
		d_analysisType = analysisType;
		
		setBaseline(baseline);
		setName(id);
	}

	public Indication getIndication() {
		return d_indication;
	}

	private void setIndication(Indication indication) {
		d_indication = indication;
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		List<OutcomeMeasure> sortedList = findOutcomeMeasures();
		Collections.sort(sortedList, new OutcomeComparator());
		return sortedList;
	}

	private List<OutcomeMeasure> findOutcomeMeasures() {
		List<OutcomeMeasure> list = new ArrayList<OutcomeMeasure>();
		for (MetaAnalysis a : d_metaAnalyses) {
			list.add(a.getOutcomeMeasure());
		}
		return list;
	}

	public List<MetaAnalysis> getMetaAnalyses() {
		ArrayList<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>(d_metaAnalyses);
		Collections.sort(analyses, new AlphabeticalComparator());
		return Collections.unmodifiableList(analyses);
	}

	void setMetaAnalyses(List<MetaAnalysis> metaAnalysis) {
		d_metaAnalyses = metaAnalysis;
	}
	
	public List<Drug> getAlternatives() {
		return getDrugs();
	}

	public List<Drug> getDrugs() {
		List<Drug> sortedList = new ArrayList<Drug>(d_drugs);
		sortedList.add(getBaseline());
		Collections.sort(sortedList, new AlphabeticalComparator());
		return sortedList;
	}

	void setDrugs(List<Drug> drugs) {
		d_drugs = drugs;
		d_drugs.remove(getBaseline());
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		HashSet<Entity> dependencies = new HashSet<Entity>();
		dependencies.add(d_indication);
		dependencies.addAll(d_outcomeMeasures);
		dependencies.addAll(d_drugs);
		dependencies.addAll(d_metaAnalyses);
		return dependencies;
	}

	void setName(String name) {
		d_name = name;
	}

	public String getName() {
		return d_name;
	}

	@Override
	public boolean equals(Object other){
		if (other == null)
			return false;
		if (!(other instanceof MetaBenefitRiskAnalysis))
			return false;
		return this.getName().equals( ((BenefitRiskAnalysis<?>)other).getName() );
	}

	public int compareTo(BenefitRiskAnalysis<?> other) {
		if (other == null) {
			return 1;
		}
		return getName().compareTo(other.getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}

	private void setBaseline(Drug baseline) {
		d_baseline = baseline;
	}

	public Drug getBaseline() {
		return d_baseline;
	}
	
	private RelativeEffect<? extends Measurement> getRelativeEffect(Drug d, OutcomeMeasure om) {
		for(MetaAnalysis ma : getMetaAnalyses()){
			if(ma.getOutcomeMeasure().equals(om)){
				if (!d.equals(getBaseline())) {
					Class<? extends RelativeEffect<? extends Measurement>> type = (om.getType().equals(Variable.Type.RATE)) ? BasicOddsRatio.class : BasicMeanDifference.class;
					return ma.getRelativeEffect(d_baseline, d, type);
				}
				else {
					return (om.getType().equals(Variable.Type.RATE)) ?  NetworkRelativeEffect.buildOddsRatio(0.0, 0.0) : NetworkRelativeEffect.buildMeanDifference(0.0, 0.0); 
				}
			}
			
		}
		throw new IllegalArgumentException("No analyses comparing drug " + d + " and Outcome " + om + " in this Benefit-Risk analysis");
	}
	
	/**
	 * The effect of d on om relative to the baseline treatment. 
	 */
	public GaussianBase getRelativeEffectDistribution(Drug d, OutcomeMeasure om) {
		return (GaussianBase) getRelativeEffect(d, om).getDistribution();
	}
	
	public Distribution getMeasurement(Drug d, OutcomeMeasure om) {
		return getRelativeEffectDistribution(d, om);
	}
	
	public MeasurementSource<Drug> getAbsoluteMeasurementSource() {
		return new AbsoluteMeasurementSource();
	}
	
	public MeasurementSource<Drug> getRelativeMeasurementSource() {
		return new RelativeMeasurementSource();
	}
	
	/**
	 * Get the assumed distribution for the baseline odds.
	 */
	public GaussianBase getBaselineDistribution(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = getBaselineModel(om);
		if (!model.isReady()) {
			return null;
		}
		return (GaussianBase) model.getResult();
	}
	
	public AbstractBaselineModel<?> getBaselineModel(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = d_baselineModelMap.get(om);
		if (model == null) {
			model = createBaselineModel(om);
			d_baselineModelMap.put(om,model);
		}
		return model;
	}
	
	private AbstractBaselineModel<?> createBaselineModel(OutcomeMeasure om) {
		AbstractBaselineModel<?> model = null;
			switch (om.getType()) {
			case RATE:
				model = new BaselineOddsModel(getBaselineMeasurements(om, RateMeasurement.class));
			break;
			case CONTINUOUS:
				model = new BaselineMeanDifferenceModel(getBaselineMeasurements(om, ContinuousMeasurement.class));
			break;
			}
		return model;
	}
	
	@SuppressWarnings("unchecked")
	private <M extends Measurement> List<M> getBaselineMeasurements(OutcomeMeasure om, Class<M> cls) {
		List<M> result = new ArrayList<M>(); 
		for (MetaAnalysis ma : getMetaAnalyses())
			if (ma.getOutcomeMeasure().equals(om))
				for (Study s : ma.getIncludedStudies())
					for (Arm a : s.getArms())
						if (a.getDrug().equals(getBaseline()))
							result.add((M)s.getMeasurement(om,a));
		
		return result;
	}
	
	/**
	 * The absolute effect of d on om given the assumed odds of the baseline treatment. 
	 */
	public GaussianBase getAbsoluteEffectDistribution(Drug d, OutcomeMeasure om) {
		GaussianBase baseline = getBaselineDistribution(om);
		GaussianBase relative = getRelativeEffectDistribution(d, om);
		if (baseline == null || relative == null) return null;
		return baseline.plus(relative);
	}

	public void runAllConsistencyModels() {
		List<Runnable> tasks = new ArrayList<Runnable>();
		for (MetaAnalysis ma : getMetaAnalyses() ){
			if (ma instanceof NetworkMetaAnalysis) {
				ConsistencyModel model = ((NetworkMetaAnalysis) ma).getConsistencyModel();
				if (!model.isReady()) {
					tasks.add(model);
				}			
			}
		}
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}
	
	protected static final XMLFormat<MetaBenefitRiskAnalysis> METABR_XML = 
		new XMLFormat<MetaBenefitRiskAnalysis>(MetaBenefitRiskAnalysis.class) {
			@Override
			public MetaBenefitRiskAnalysis newInstance(Class<MetaBenefitRiskAnalysis> cls, InputElement xml) {
				return new MetaBenefitRiskAnalysis();
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void read(InputElement ie, MetaBenefitRiskAnalysis br) throws XMLStreamException {
				br.setName(ie.getAttribute(PROPERTY_NAME, null));
				try 
				{ // legacy: should not fail if no analysistype is set, for backwards compatibility with old xml files 
					br.d_analysisType = AnalysisType.valueOf(ie.<String>getAttribute(PROPERTY_ANALYSIS_TYPE, 
							AnalysisType.SMAA.toString())); 
				} 
				catch (IllegalArgumentException e ) { br.d_analysisType = AnalysisType.SMAA; }
				br.setBaseline(ie.get(PROPERTY_BASELINE, Drug.class));
				br.setDrugs((List<Drug>) ie.get(PROPERTY_DRUGS, ArrayList.class));
				br.setIndication((Indication) ie.get(PROPERTY_INDICATION, Indication.class));
				br.setMetaAnalyses((List<MetaAnalysis>) ie.get(PROPERTY_METAANALYSES, ArrayList.class));
				if (ie.hasNext()) { // support legacy XML (where both MetaAnalyses and OutcomeMeasures were saved)
					ie.get(PROPERTY_OUTCOMEMEASURES, ArrayList.class);
				}
			}
		
			@Override
			public void write(MetaBenefitRiskAnalysis br, OutputElement oe) throws XMLStreamException {
				oe.setAttribute(PROPERTY_NAME, br.getName());
				oe.setAttribute(PROPERTY_ANALYSIS_TYPE, br.d_analysisType.toString());
				oe.add(br.getBaseline(), PROPERTY_BASELINE, Drug.class);
				oe.add(new ArrayList<Drug>(br.getDrugs()), PROPERTY_DRUGS, ArrayList.class);
				oe.add(br.getIndication(), PROPERTY_INDICATION, Indication.class);
				oe.add(new ArrayList<MetaAnalysis>(br.getMetaAnalyses()), PROPERTY_METAANALYSES, ArrayList.class);
			}
		};

	public AnalysisType getAnalysisType() {
		return d_analysisType;
	}

}

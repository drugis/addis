package org.drugis.addis.mtc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.ModelFactory;
import org.drugis.mtc.Network;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.ProgressEvent.EventType;
import org.junit.Before;
import org.junit.Test;

public class NetworkMetaAnalysisITCase {
    private static final int NSIMULATION = 1000;
	private static final int NBURNIN = 40;
	private NetworkBuilder d_builder;
	private Network d_network;
	private InconsistencyModel d_model;

    @Before
    public void setUp() {
        d_builder = new NetworkBuilder();
        
        NetworkMetaAnalysis netwAnalysis = ExampleData.buildNetworkMetaAnalysis();
        
        for(Study s : netwAnalysis.getIncludedStudies()){
			for (Arm a : s.getArms()) {
				for (Variable v : s.getVariables(Endpoint.class)) {
					if(! (s.getMeasurement(v, a) instanceof BasicRateMeasurement))
						break;
					BasicRateMeasurement m = (BasicRateMeasurement)s.getMeasurement(v, a);		
					d_builder.add(s.getStudyId(), a.getDrug().getName(), m.getRate(), m.getSampleSize());
				}
        	}
        }
        d_network = d_builder.buildNetwork();
        
        ModelFactory factory = DefaultModelFactory.instance();
		d_model = factory.getInconsistencyModel(d_network);
    }
    
    @Test
    public void runModel() {
    	ProgressListener mock = createMock(ProgressListener.class);
    	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_STARTED));
    	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_FINISHED));
    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_STARTED));
    	for (int i = 1; i < NBURNIN; ++i) {
	    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_PROGRESS, i * 100));
    	}
    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_FINISHED));
    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_STARTED));
    	for (int i = 1; i < NSIMULATION; ++i) {
	    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_PROGRESS, i * 100));
    	}
    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_FINISHED));
    	replay(mock);
    	d_model.addProgressListener(mock);
    	d_model.run();
    	verify(mock);
    }

    @Test
    public void getResults() {
    	d_model.run();
    	Treatment fluox = d_builder.getTreatment("Fluoxetine");
    	Treatment sertr = d_builder.getTreatment("Sertraline");
    	Treatment parox = d_builder.getTreatment("Paroxetine");
    	
    	assertNotNull(d_model.getRelativeEffect(fluox, sertr));
    	assertNotNull(d_model.getRelativeEffect(sertr, fluox));	
    	assertNotNull(d_model.getRelativeEffect(fluox, parox));
    	assertNotNull(d_model.getRelativeEffect(parox, fluox));
    	assertNotNull(d_model.getRelativeEffect(sertr, parox));
    	assertNotNull(d_model.getRelativeEffect(parox, sertr));
    	
    	assertEquals(1, d_model.getInconsistencyFactors().size());
    }
}

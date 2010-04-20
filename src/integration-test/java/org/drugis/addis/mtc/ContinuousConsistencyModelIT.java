package org.drugis.addis.mtc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.ContinuousMeasurement;
import org.drugis.mtc.ContinuousNetworkBuilder;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.ModelFactory;
import org.drugis.mtc.Network;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.ProgressEvent.EventType;
import org.junit.Before;
import org.junit.Test;

public class ContinuousConsistencyModelIT {
	/* This test apparently cannot run in sequence with NetworkMetaAnalysisTest so this is commented out entirely */

	    private ContinuousNetworkBuilder d_builder;
		private Network<ContinuousMeasurement> d_network;
		private ConsistencyModel d_model;

	    @SuppressWarnings("unchecked")
		@Before
	    public void setUp() {
	        d_builder = new ContinuousNetworkBuilder();
	        d_builder.add("1", "A", 12.0, 3.0, 100);
	        d_builder.add("1", "B", 23.0, 2.0, 100);
	        d_builder.add("2", "B", 12.1, 9.0, 43);
	        d_builder.add("2", "C", 15.0, 8.0, 40);
	        d_builder.add("3", "A", 30.0, 5.0, 150);
	        d_builder.add("3", "C", 100.0, 40.0, 150);
	        d_network = d_builder.buildNetwork();
	        
	        ModelFactory factory = DefaultModelFactory.instance();
			d_model = factory.getConsistencyModel(d_network);
	    }
	    
	    @Test
	    public void runModel() {
	    	ProgressListener mock = createMock(ProgressListener.class);
	    	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_STARTED));
	    	mock.update(d_model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_FINISHED));
	    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_STARTED));
	    	for (int i = 100; i < d_model.getBurnInIterations(); i+=100) {
		    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_PROGRESS, i, d_model.getBurnInIterations()));
	    	}
	    	mock.update(d_model, new ProgressEvent(EventType.BURNIN_FINISHED));
	    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_STARTED));
	    	for (int i = 100; i < d_model.getSimulationIterations(); i+=100) {
		    	mock.update(d_model, new ProgressEvent(EventType.SIMULATION_PROGRESS, i, d_model.getSimulationIterations()));
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
	    	Treatment a = d_builder.getTreatment("A");
	    	Treatment b = d_builder.getTreatment("B");
	    	Treatment c = d_builder.getTreatment("C");
	    	assertNotNull(d_model.getRelativeEffect(a, b));
	    	assertNotNull(d_model.getRelativeEffect(b, a));
	    	assertNotNull(d_model.getRelativeEffect(a, c));
	    	assertNotNull(d_model.getRelativeEffect(c, a));
	    	assertNotNull(d_model.getRelativeEffect(c, b));
	    	assertNotNull(d_model.getRelativeEffect(b, c));
	    }
	

}

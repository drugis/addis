/* This test apparently cannot run in sequence with NetworkMetaAnalysisTest so this is commented out entirely */

package org.drugis.addis.mtc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.DefaultModelFactory;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.ModelFactory;
import org.drugis.mtc.Network;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.ProgressEvent.EventType;
import org.junit.Before;
import org.junit.Test;

public class MTCIT {
	private static final int NBURNIN = 40;
	private static final int NSIMULATION = 1000;
    private NetworkBuilder d_builder;
	private Network d_network;
	private InconsistencyModel d_model;

    @Before
    public void setUp() {
        d_builder = new NetworkBuilder();
        d_builder.add("1", "A", 5, 100);
        d_builder.add("1", "B", 23, 100);
        d_builder.add("2", "B", 12, 43);
        d_builder.add("2", "C", 15, 40);
        d_builder.add("3", "A", 12, 150);
        d_builder.add("3", "C", 100, 150);
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
    	Treatment a = d_builder.getTreatment("A");
    	Treatment b = d_builder.getTreatment("B");
    	Treatment c = d_builder.getTreatment("C");
    	assertNotNull(d_model.getRelativeEffect(a, b));
    	assertNotNull(d_model.getRelativeEffect(b, a));
    	assertNotNull(d_model.getRelativeEffect(a, c));
    	assertNotNull(d_model.getRelativeEffect(c, a));
    	assertNotNull(d_model.getRelativeEffect(c, b));
    	assertNotNull(d_model.getRelativeEffect(b, c));
    	
    	assertEquals(1, d_model.getInconsistencyFactors().size());
    	
    	for (InconsistencyParameter p : d_model.getInconsistencyFactors()) {
    		assertNotNull(d_model.getInconsistency(p));
    		System.out.println("\n"+d_model.getInconsistency(p));
    		System.out.println(p);
    		System.out.println(p.treatmentList());
    	}
    }
    
    @Test
    public void testGetRanks() {
    	ModelFactory factory = DefaultModelFactory.instance();
    	ConsistencyModel model = factory.getConsistencyModel(d_network);
    	
    	model.run();
    	
    	assertTrue(model.rankProbability(d_builder.getTreatment("A"), 1) < model.rankProbability( d_builder.getTreatment("B"), 1));
    	assertTrue(model.rankProbability(d_builder.getTreatment("B"), 1) < model.rankProbability( d_builder.getTreatment("C"), 1));
    	
    	assertTrue(model.rankProbability(d_builder.getTreatment("A"), 2) < model.rankProbability( d_builder.getTreatment("C"), 2));
    	assertTrue(model.rankProbability(d_builder.getTreatment("C"), 2) < model.rankProbability( d_builder.getTreatment("B"), 2));
    	
    	assertTrue(model.rankProbability(d_builder.getTreatment("C"), 3) < model.rankProbability( d_builder.getTreatment("B"), 3));
    	assertTrue(model.rankProbability(d_builder.getTreatment("B"), 3) < model.rankProbability( d_builder.getTreatment("A"), 3));
    	
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("A"), 1) + model.rankProbability(d_builder.getTreatment("B"), 1) + model.rankProbability(d_builder.getTreatment("C"), 1), 0.00001);
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("A"), 2) + model.rankProbability(d_builder.getTreatment("B"), 2) + model.rankProbability(d_builder.getTreatment("C"), 2), 0.00001);
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("A"), 3) + model.rankProbability(d_builder.getTreatment("B"), 3) + model.rankProbability(d_builder.getTreatment("C"), 3), 0.00001);
    	
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("A"), 1) + model.rankProbability(d_builder.getTreatment("A"), 2) + model.rankProbability(d_builder.getTreatment("A"), 3), 0.00001);
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("B"), 1) + model.rankProbability(d_builder.getTreatment("B"), 2) + model.rankProbability(d_builder.getTreatment("B"), 3), 0.00001);
    	assertEquals(1.0, model.rankProbability(d_builder.getTreatment("C"), 1) + model.rankProbability(d_builder.getTreatment("C"), 2) + model.rankProbability(d_builder.getTreatment("C"), 3), 0.00001);
    }
}

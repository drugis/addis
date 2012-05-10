package org.drugis.addis.entities.analysis.models;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.analysis.NetworkBuilderFactory;
import org.drugis.addis.mocks.MockConsistencyModel;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.junit.Before;
import org.junit.Test;

public class AbstractSimulationModelTest {


	private AbstractSimulationModel<ConsistencyModel> d_model;
	private List<DrugSet> d_treatments;

	@Before
	public void setUp() {
		d_treatments = Arrays.asList(new DrugSet(new Drug("A", "")), new DrugSet(new Drug("B", "")), new DrugSet(new Drug("C", "")));
		NetworkBuilder<DrugSet> builder = NetworkBuilderFactory.createBuilderStub(d_treatments);
		ArrayList<Treatment> treatmentList = new ArrayList<Treatment>();
		for(DrugSet s : d_treatments) { 
			treatmentList.add(builder.getTreatmentMap().get(s));
		}
		ConsistencyModel mtc = MockConsistencyModel.buildMockSimulationConsistencyModel(treatmentList);
		d_model = new AbstractSimulationModel<ConsistencyModel>(builder, mtc) {};
	}

	@Test
	public void testGetQuantileSummary() {
		Parameter dAB = d_model.getRelativeEffect(d_treatments.get(0), d_treatments.get(1));
		Parameter dAC = d_model.getRelativeEffect(d_treatments.get(0), d_treatments.get(2));
		assertNotNull(d_model.getQuantileSummary(dAB));
		assertSame(d_model.getQuantileSummary(dAB), d_model.getQuantileSummary(dAB));
		assertNotNull(d_model.getQuantileSummary(dAC));
		assertNotSame(d_model.getQuantileSummary(dAB), d_model.getQuantileSummary(dAC));
	}

}

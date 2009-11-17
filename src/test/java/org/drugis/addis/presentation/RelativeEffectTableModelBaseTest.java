package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.Study;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public abstract class RelativeEffectTableModelBaseTest {
	protected Study d_standardStudy;
	protected Study d_threeArmStudy;
	protected AbstractRelativeEffectTableModel d_stdModel;
	protected RelativeEffectTableModel d_threeArmModel;
	protected Endpoint d_endpoint;
	protected PresentationModelFactory d_pmf;
	protected Class<? extends RelativeEffect<?>> d_relativeEffectClass;

	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getColumnCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getRowCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtUpperRightPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<RelativeEffect<?>> val01 = (PresentationModel<RelativeEffect<?>>)d_threeArmModel.getValueAt(0, 1);
		assertTrue(d_relativeEffectClass.isInstance(val01.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val01.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val01.getBean().getSubject());
		
		PresentationModel<RelativeEffect<?>> val12 = (PresentationModel<RelativeEffect<?>>)d_threeArmModel.getValueAt(1, 2);
		assertTrue(d_relativeEffectClass.isInstance(val12.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val12.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val12.getBean().getSubject());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtLowerLeftPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<RelativeEffect<?>> val20 = (PresentationModel<RelativeEffect<?>>)d_threeArmModel.getValueAt(2, 0);
		assertTrue(d_relativeEffectClass.isInstance(val20.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val20.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val20.getBean().getSubject());
		
		PresentationModel<RelativeEffect<?>> val21 = (PresentationModel<RelativeEffect<?>>)d_threeArmModel.getValueAt(2, 1);
		assertTrue(d_relativeEffectClass.isInstance(val21.getBean()));
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val21.getBean().getBaseline());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val21.getBean().getSubject());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtDiagonal() {
		for (int i = 0; i < d_standardStudy.getPatientGroups().size(); ++i) {
			Object val = d_stdModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_standardStudy.getPatientGroups().get(i));
		}
		for (int i = 0; i < d_threeArmStudy.getPatientGroups().size(); ++i) {
			Object val = d_threeArmModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_threeArmStudy.getPatientGroups().get(i));
		}
	}

	@Test
	public void testGetDescriptionAtDiagonal() {
		assertNull(d_threeArmModel.getDescriptionAt(1, 1));
	}

	@Test
	public void testGetDescriptionAt() {
		LabeledPresentationModel pg0 = d_pmf.getLabeledModel(d_threeArmStudy.getPatientGroups().get(0));
		LabeledPresentationModel pg1 = d_pmf.getLabeledModel(d_threeArmStudy.getPatientGroups().get(1));
		String expected = "\"" + pg1.getLabelModel().getValue() + "\" relative to \"" +
				pg0.getLabelModel().getValue() + "\"";
		assertEquals(expected, d_threeArmModel.getDescriptionAt(0, 1));
	}

	@Test
	public void testGetDescription() {
		String description = d_threeArmModel.getTitle() + " for \"" + d_threeArmStudy.getId()
				+ "\" on Endpoint \"" + d_endpoint.getName() + "\"";
		assertEquals(description, d_threeArmModel.getDescription());
	}
	
	@Test
	public void testGetPlotPresentation() {
		ForestPlotPresentation pm = d_stdModel.getPlotPresentation(1, 0);
		assertEquals(d_relativeEffectClass, pm.getRelativeEffectAt(0).getClass());
		assertEquals(d_standardStudy.toString(), pm.getStudyLabelAt(0));
		assertEquals(1, pm.getNumRelativeEffects());
		assertEquals(d_standardStudy.getPatientGroups().get(1),
				pm.getRelativeEffectAt(0).getBaseline().getPatientGroup());
		assertEquals(d_standardStudy.getPatientGroups().get(0),
				pm.getRelativeEffectAt(0).getSubject().getPatientGroup());
		assertEquals(d_endpoint, pm.getEndpoint());
	}

	protected void baseSetUpRate() {
		d_standardStudy = ExampleData.buildDefaultStudy2();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointHamd();
		DomainImpl domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
	
	protected void baseSetUpContinuous() {
		d_standardStudy = ExampleData.buildDefaultStudy1();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointCgi();
		DomainImpl domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
}

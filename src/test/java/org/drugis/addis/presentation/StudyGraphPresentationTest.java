package org.drugis.addis.presentation;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.StudyGraphPresentation.Edge;
import org.drugis.addis.presentation.StudyGraphPresentation.Vertex;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;


public class StudyGraphPresentationTest {
	private StudyGraphPresentation d_pm;
	private List<Drug> d_drugs;
	private Domain d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<Drug>();
		d_drugs.add(ExampleData.buildDrugFluoxetine());
		d_drugs.add(ExampleData.buildDrugParoxetine());
		d_drugs.add(ExampleData.buildDrugSertraline());
		d_pm = new StudyGraphPresentation(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				new AbstractListHolder<Drug>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Drug> getValue() {
						return d_drugs;
					}}, d_domain);
	}
	
	@Test
	public void testGetDrugs() {
		assertAllAndOnly(d_drugs, d_pm.getDrugs());
	}
	
	@Test
	public void testGetStudies1() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(ExampleData.buildDrugFluoxetine()));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(studies, d_pm.getStudies(ExampleData.buildDrugParoxetine()));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(ExampleData.buildDrugSertraline()));
	}
	
	@Test
	public void testGetStudies2() {
		List<Study> studies = new ArrayList<Study>();
		
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getStudies(ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine()));
		
		studies.remove(ExampleData.buildStudyBennie());
		assertAllAndOnly(Collections.emptyList(), 
				d_pm.getStudies(ExampleData.buildDrugParoxetine(), ExampleData.buildDrugSertraline()));
		
		assertAllAndOnly(Collections.<Study>singletonList(ExampleData.buildStudyBennie()),
				d_pm.getStudies(ExampleData.buildDrugSertraline(), ExampleData.buildDrugFluoxetine()));
	}
	
	@Test
	public void testVertexSet() {
		Set<Vertex> vertexSet = d_pm.vertexSet();
		assertEquals(3, vertexSet.size());
		
		for (Vertex vertex : vertexSet) {
			assertTrue(d_drugs.contains(vertex.getDrug()));
			assertEquals(calcSampleSize(vertex.getDrug()), vertex.getSampleSize());
		}
	}
	
	@Test
	public void testEdgeSet() {
		Set<Edge> edgeSet = d_pm.edgeSet();
		assertEquals(2, edgeSet.size());
		
		Edge edge1 = d_pm.getEdge(
				getVertex(ExampleData.buildDrugFluoxetine()),
				getVertex(ExampleData.buildDrugParoxetine()));
		assertNotNull(edge1);
		assertEquals(3, edge1.getStudyCount());
		
		Edge edge2 = d_pm.getEdge(
				getVertex(ExampleData.buildDrugFluoxetine()),
				getVertex(ExampleData.buildDrugSertraline()));
		assertNotNull(edge2);
		assertEquals(1, edge2.getStudyCount());
		
		Edge edge3 = d_pm.getEdge(
				getVertex(ExampleData.buildDrugSertraline()),
				getVertex(ExampleData.buildDrugFluoxetine()));
		assertEquals(edge2, edge3);
	}
	
	@Test
	public void testFindVertex() {
		assertEquals(ExampleData.buildDrugFluoxetine(),
				d_pm.findVertex(ExampleData.buildDrugFluoxetine()).getDrug());
	}
	
	@Test
	public void testNullIndication() {
		d_pm = new StudyGraphPresentation(new UnmodifiableHolder<Indication>(null),
				new UnmodifiableHolder<OutcomeMeasure>(null),
				new AbstractListHolder<Drug>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Drug> getValue() {
						return new ArrayList<Drug>();
					}}, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
	}
	
	@Test
	public void testNullEndpoint() {
		d_pm = new StudyGraphPresentation(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(null),
				new AbstractListHolder<Drug>() {
					private static final long serialVersionUID = 1L;

					@Override
					public List<Drug> getValue() {
						return new ArrayList<Drug>();
					}}, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
	}
	
	@SuppressWarnings("serial")
	public static class MutableDrugListHolder extends AbstractListHolder<Drug> {
		private List<Drug> d_drugs;
		
		public MutableDrugListHolder(List<Drug> drugs) {
			d_drugs = new ArrayList<Drug>(drugs);
		}

		@Override
		public List<Drug> getValue() {
			return d_drugs;
		}
		
		@SuppressWarnings("unchecked")
		@Override 
		public void setValue(Object o) {
			setValue((List<Drug>)o);
		}
		
		public void setValue(List<Drug> drugs) {
			List<Drug> oldValue = d_drugs;
			d_drugs = new ArrayList<Drug>(drugs);
			fireValueChange(oldValue, d_drugs);
		}
		
	}
	
	@Test
	public void testChangeDrugList() {
		AbstractListHolder<Drug> drugListHolder = new MutableDrugListHolder(new ArrayList<Drug>());
		
		PropertyChangeListener l = JUnitUtil.mockListener(drugListHolder, "value",
				new ArrayList<Drug>(), new ArrayList<Drug>(d_drugs));
		drugListHolder.addValueChangeListener(l);
	
		d_pm = new StudyGraphPresentation(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				drugListHolder, d_domain);
		assertTrue(d_pm.vertexSet().isEmpty());
		
		drugListHolder.setValue(d_drugs);
		verify(l);
		assertEquals(3, d_pm.vertexSet().size());
		assertEquals(2, d_pm.edgeSet().size());
	}

	private Vertex getVertex(Drug drug) {
		return d_pm.findVertex(drug);
	}

	private int calcSampleSize(Drug drug) {
		int n = 0;
		for (Study s : d_pm.getStudies(drug)) {
			n += s.getSampleSize();
		}
		return n;
	}
}

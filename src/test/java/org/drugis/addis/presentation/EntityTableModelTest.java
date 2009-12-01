package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class EntityTableModelTest {
	Domain d_domain;
	EntityTableModel<Drug> d_tableModel;
	List<String> d_properties;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_properties = new ArrayList<String>();
		d_properties.add("name");
		d_properties.add("atcCode");
		List<PresentationModel<Drug>> pm = new ArrayList<PresentationModel<Drug>>();
		PresentationModelFactory pmf = new PresentationModelFactory(d_domain);
		for (Drug d : d_domain.getDrugs())
			pm.add(pmf.getModel(d));
		d_tableModel = new EntityTableModel<Drug>(pm, d_properties);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_properties.size(), d_tableModel.getColumnCount());
	}
	
	@Test 
	public void testGetRowCount() {
		assertEquals(d_domain.getDrugs().size(), d_tableModel.getRowCount());
	}
	
	@Test
	public void testGetValueAt() {
		assertEquals(d_domain.getDrugs().first().getName(), d_tableModel.getValueAt(0, 0));
		assertEquals(d_domain.getDrugs().first().getAtcCode(), d_tableModel.getValueAt(0, 1));
	}
	
	@Test
	public void testGetColumnNames() {
		assertEquals("Name", d_tableModel.getColumnName(0));
		assertEquals("Atc Code", d_tableModel.getColumnName(1));
	}

	@Ignore //We don't have a listholder for the list of drugs from domain
	@Test
	public void testDrugAddedUpdatesTable() {
		int prevSize = d_tableModel.getRowCount();
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		d_domain.addDrug(ExampleData.buildDrugViagra());
		verify(mock);
		assertEquals(prevSize + 1, d_tableModel.getRowCount());
	}
}

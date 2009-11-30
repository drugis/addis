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
import org.junit.Test;

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
		d_tableModel = new EntityTableModel<Drug>(d_domain.getDrugs(), d_domain, d_properties);
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
	public void testDrugAddedUpdatesTable() {
		int prevSize = d_tableModel.getRowCount();
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		d_domain.addDrug(ExampleData.buildDrugViagra());
		verify(mock);
		assertEquals(prevSize + 1, d_tableModel.getRowCount());
	}
}

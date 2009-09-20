package org.drugis.addis.presentation.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.test.TestData;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class StudyCharTableModelTest {
	private Domain d_domain;
	private MetaStudyPresentationModel d_pm;
	private StudyCharTableModel d_model;
	private MetaStudy d_study;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		MetaAnalysis ma = new MetaAnalysis(TestData.buildEndpointHamd(), new ArrayList<Study>(d_domain.getStudies()));
		d_study = new MetaStudy("Meta", ma);
		d_pm = new MetaStudyPresentationModel(d_study);
		d_model = new StudyCharTableModel(d_pm);
	}	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristic.values().length + 1, d_model.getColumnCount());
		d_pm.getCharacteristicVisibleModel(StudyCharacteristic.values()[0]).setValue(Boolean.FALSE);
		assertEquals(StudyCharacteristic.values().length, d_model.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_pm.getIncludedStudies().size(), d_model.getRowCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_pm.getIncludedStudies()) {
			assertEquals(s.getId(), d_model.getValueAt(row, 0));
			int column = 1;
			for (StudyCharacteristic c : StudyCharacteristic.values()) {
				assertEquals(s.getCharacteristics().get(c), d_model.getValueAt(row, column));
				++column;
			}
			++row;
		}
	}
	
	@Test
	public void testHideColumnFires() {
		ValueModel firstCharVisible = getFirstCharValueModel();
		TableModelListener mock = createMock(TableModelListener.class);
		d_model.addTableModelListener(mock);
		mock.tableChanged(new TableModelEvent(d_model));
		replay(mock);
		firstCharVisible.setValue(Boolean.FALSE);
		verify(mock);
	}
	private ValueModel getFirstCharValueModel() {
		ValueModel firstCharVisible = d_pm.getCharacteristicVisibleModel(StudyCharacteristic.values()[0]);
		return firstCharVisible;
	}
	
	@Test
	public void testCorrectColumnsAreShown() {
		getFirstCharValueModel().setValue(Boolean.FALSE);
		fail();
	}
	
	@Test
	public void testCorrectColumnsAreShownAfterConstructor() {
		fail();
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Study ID", d_model.getColumnName(0));
		int column = 1;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			assertEquals(c.getDescription(), d_model.getColumnName(column));
			++column;
		}
	}
}

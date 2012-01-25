package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class AddEpochsPresentationTest {
	private Study d_study;
	
	@Before
	public void setUp() {
		d_study = ExampleData.buildStudyBennie().clone();
	}
	
	@Test
	public void testRename() {
		AddEpochsPresentation presentation = new AddEpochsPresentation(d_study, "Epoch", 1);
		
		ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_study.getEpochs(), ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		EasyMock.replay(mockListener);
		
		d_study.getEpochs().addListDataListener(mockListener);
		
		final String newName = "Other epoch";
		presentation.rename(0, newName);
		
		EasyMock.verify(mockListener);
		assertEquals(newName, d_study.getEpochs().get(0).getName());
		assertEquals(newName, presentation.getList().get(0).getName());
	}
	
	@Test
	public void testRenameAfterSetStudy() {
		AddEpochsPresentation presentation = new AddEpochsPresentation(new Study(), "Epoch", 1);
		presentation.setStudy(d_study);
		
		ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_study.getEpochs(), ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		EasyMock.replay(mockListener);
		
		d_study.getEpochs().addListDataListener(mockListener);
		
		final String newName = "Other epoch";
		presentation.rename(0, newName);
		
		EasyMock.verify(mockListener);
		assertEquals(newName, d_study.getEpochs().get(0).getName());
		assertEquals(newName, presentation.getList().get(0).getName());
	}
}

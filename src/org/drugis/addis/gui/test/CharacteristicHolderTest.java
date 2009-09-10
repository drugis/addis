package org.drugis.addis.gui.test;

import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.gui.CharacteristicHolder;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.beans.Model;
import static org.easymock.EasyMock.*;

public class CharacteristicHolderTest {
	private BasicStudy d_study;
	private CharacteristicHolder d_holder;
	private StudyCharacteristic d_char;
	
	@Before
	public void setUp() {
		d_char = StudyCharacteristic.INDICATION;
		d_study = new BasicStudy("Test", null);
		d_holder = new CharacteristicHolder(d_study, d_char);
	}
	
	@Test
	public void testSetValue() {
		JUnitUtil.testSetter(d_holder, "value", null, new Indication(1L, "Indication"));
	}
	
	@Test
	public void testSetValueTriggersCharacteristicsChanged() {
		HashMap<StudyCharacteristic, Model> newVal = new HashMap<StudyCharacteristic, Model>();
		Indication indication = new Indication(1L, "Indication");
		newVal.put(d_char, indication);
		PropertyChangeListener l =
			JUnitUtil.mockListener(d_study, Study.PROPERTY_CHARACTERISTICS, d_study.getCharacteristics(), newVal);
		d_study.addPropertyChangeListener(l);
		d_holder.setValue(indication);
		verify(l);
	}
}

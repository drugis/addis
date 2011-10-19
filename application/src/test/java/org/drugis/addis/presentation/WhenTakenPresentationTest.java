package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.WhenTaken;
import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.presentation.DurationPresentation.DateUnits;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public class WhenTakenPresentationTest {

	private Epoch d_epoch1;
	private Epoch d_epoch2;
	private ObservableList<Epoch> d_epochs;
	private WhenTakenPresentation d_wtp;

	@Before
	public void setYup() {
		d_epoch1 = new Epoch("Je moeder", EntityUtil.createDuration("P31D"));
		d_epoch2 = new Epoch("Je vader", EntityUtil.createDuration("P33D"));
		d_epochs = new ArrayListModel<Epoch>();
		d_epochs.addAll(Arrays.asList(d_epoch1, d_epoch2));
		d_wtp = new WhenTakenPresentation(new WhenTaken(EntityUtil.createDuration("P29D"), RelativeTo.FROM_EPOCH_START, d_epoch1), d_epochs);
	}
	
	@Test
	public void testWhenTakenNullsWhenEpochDeleted() {
		AbstractValueModel model = d_wtp.getModel(WhenTaken.PROPERTY_EPOCH);
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(model, "value", d_epoch1, null);
		model.addPropertyChangeListener(listener);
		
		d_epochs.remove(d_epoch1);
		assertNull(d_wtp.getBean().getEpoch());
		EasyMock.verify(listener);
	}

	@Test
	public void testOffsetPresentation() {
		DurationPresentation<WhenTaken> op = d_wtp.getOffsetPresentation();
		op.setQuantity(1396);
		op.setUnits(DateUnits.Minutes);
		assertEquals(EntityUtil.createDuration("PT1396M"), d_wtp.getBean().getOffset());
	}
	
}

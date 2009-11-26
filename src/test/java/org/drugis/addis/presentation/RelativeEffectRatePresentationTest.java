package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class RelativeEffectRatePresentationTest {
	
	private static final String LABELCONTENTS = "1.36 (0.85, 2.17)";
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	OddsRatio d_ratio;
	RelativeEffectRatePresentation d_presentation;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.RATE);
		PatientGroup pnum = new BasicPatientGroup(null,null,s_sizeNum);
		PatientGroup pden = new BasicPatientGroup(null,null,s_sizeDen);
		d_numerator = new BasicRateMeasurement(e, s_effectNum, pnum);		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, pden);
		d_ratio = new OddsRatio(d_denominator, d_numerator);
		d_presentation = new RelativeEffectRatePresentation(d_ratio, new PresentationModelFactory(new DomainImpl()));
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(LABELCONTENTS, d_presentation.getLabelModel().getValue());
	}	
	
	@Test
	public void testPropertyChangeEvents() {
		d_denominator.setRate(1);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_denominator.setRate(s_effectDen);
		verify(l);
	}
	
	@Test
	public void testLabelNumeratorChanged() {
		d_numerator.setRate(1);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_numerator.setRate(s_effectNum);
		verify(l);
	}
	
}

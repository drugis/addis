package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class RelativeEffectContinuousPresentationTest {
	private static final String LABELCONTENTS = "0.25 (-0.59, 1.09)";
	private static final double s_mean1 = 0.50;
	private static final double s_mean2 = 0.25;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baslSize = 41;
	
	BasicContinuousMeasurement d_subj;
	BasicContinuousMeasurement d_basel;
	MeanDifference d_ratio;
	RelativeEffectContinuousPresentation d_presentation;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.CONTINUOUS);
		PatientGroup pnum = new BasicPatientGroup(null,null,s_baslSize);
		PatientGroup pden = new BasicPatientGroup(null,null,s_subjSize);
		d_subj = new BasicContinuousMeasurement(e, s_mean1, s_stdDev1, pnum);		
		d_basel = new BasicContinuousMeasurement(e, s_mean2, s_stdDev2, pden);
		d_ratio = new MeanDifference(d_basel, d_subj);
		d_presentation = new RelativeEffectContinuousPresentation(d_ratio, new PresentationModelFactory(new DomainImpl()));
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(LABELCONTENTS, d_presentation.getLabelModel().getValue());
	}	
	
	@Test
	public void testPropertyChangeMeanEvents() {
		d_basel.setMean(1D);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_basel.setMean(s_mean2);
		verify(l);
	}
	
	@Test
	public void testLabelNumeratorChangedMean() {
		d_subj.setMean(1D);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_subj.setMean(s_mean1);
		verify(l);
	}
	
	@Test
	public void testLabelNumeratorChangedStdDev() {
		d_subj.setStdDev(1D);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_subj.setStdDev(s_stdDev1);
		verify(l);
	}
	
	@Test
	public void testPropertyChangeStdDevEvents() {
		d_basel.setStdDev(1D);
		AbstractValueModel labelModel = d_presentation.getLabelModel();
		PropertyChangeListener l = 
			JUnitUtil.mockListener(labelModel, "value", null, LABELCONTENTS);
		labelModel.addPropertyChangeListener(l);
		d_basel.setStdDev(s_stdDev2);
		verify(l);
	}
		
}

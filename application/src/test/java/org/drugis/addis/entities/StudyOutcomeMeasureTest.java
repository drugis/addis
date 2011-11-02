package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class StudyOutcomeMeasureTest {

	@Test
	public void testWhenTakenChangePropagatesToStudyOutcomeMeasure() {
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()), wt );
		PropertyChangeListener l = JUnitUtil.mockStrictListener(som, StudyOutcomeMeasure.PROPERTY_WHEN_TAKEN_EDITED, false, true);
		som.addPropertyChangeListener(l);
		wt.setRelativeTo(RelativeTo.BEFORE_EPOCH_END);
		verify(l);
		som.removePropertyChangeListener(l);
	}
	
	@Test
	public void testWhenTakenChangePropagatesToStudyOutcomeMeasureOtherConstructor() {
		StudyOutcomeMeasure<Endpoint> som = new StudyOutcomeMeasure<Endpoint>(new Endpoint("Death", new RateVariableType()));
		PropertyChangeListener l = JUnitUtil.mockStrictListener(som, StudyOutcomeMeasure.PROPERTY_WHEN_TAKEN_EDITED, false, true);
		som.addPropertyChangeListener(l);
		WhenTaken wt = new WhenTaken(EntityUtil.createDuration("P08D"), RelativeTo.FROM_EPOCH_START, new Epoch("Epoch", null));
		som.getWhenTaken().add(wt);
		wt.setRelativeTo(RelativeTo.BEFORE_EPOCH_END);
		verify(l);
		som.removePropertyChangeListener(l);
	}
}

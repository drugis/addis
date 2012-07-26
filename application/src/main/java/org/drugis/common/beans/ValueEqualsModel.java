package org.drugis.common.beans;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public class ValueEqualsModel extends AbstractConverter {
	private static final long serialVersionUID = 7371154626359770150L;

	private final Object d_expectedValue;

	public ValueEqualsModel(final ValueModel model, final Object expectedValue) {
		super(model);
		d_expectedValue = expectedValue;
	}

	@Override
	public void setValue(final Object newValue) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only");
	}

	@Override
	public Object convertFromSubject(final Object subjectValue) {
		return EqualsUtil.equal(d_expectedValue, subjectValue);
	}
}

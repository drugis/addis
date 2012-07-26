package org.drugis.common.beans;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public class ValueInstanceOfModel extends AbstractConverter {
	private static final long serialVersionUID = 7371154626359770150L;

	private final Class<?> d_expectedClass;

	public ValueInstanceOfModel(final ValueModel model, final Class<?> expectedClass) {
		super(model);
		d_expectedClass = expectedClass;
	}

	@Override
	public void setValue(final Object newValue) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only");
	}

	@Override
	public Object convertFromSubject(final Object subjectValue) {
		return d_expectedClass.isAssignableFrom(subjectValue.getClass());
	}
}

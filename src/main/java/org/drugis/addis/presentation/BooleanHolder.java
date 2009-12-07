package org.drugis.addis.presentation;

@SuppressWarnings("serial")
class BooleanHolder extends AbstractHolder<Boolean> {
	public BooleanHolder() {
		setValue(true);
	}

	@Override
	protected void cascade() {
	}

	@Override
	protected void checkArgument(Object newValue) {
	}
}

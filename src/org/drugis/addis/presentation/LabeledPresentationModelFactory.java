package org.drugis.addis.presentation;

import org.drugis.addis.entities.Indication;

public class LabeledPresentationModelFactory {
	public static <B> LabeledPresentationModel<?> build(B b) {
		if (b instanceof Indication) {
			return new IndicationPresentation((Indication)b);
		}
		return null;
	}
}

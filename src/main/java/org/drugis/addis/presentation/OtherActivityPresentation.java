package org.drugis.addis.presentation;

import org.drugis.addis.entities.OtherActivity;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

public class OtherActivityPresentation extends PresentationModel<OtherActivity> {
	private static final long serialVersionUID = -6902996445875762517L;
	
	public OtherActivityPresentation(final OtherActivity oa) {
		super(oa);
	}
	
	public String getName() {
		return "Other";
	}
	
	public String getDescription() {
		return getBean().getLabel();
	}

	public ValueModel getDescriptionModel() {
		return new ModifiableHolder<String>(getBean().getDescription());
	}
}

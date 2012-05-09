package org.drugis.addis.entities.analysis.models;

import org.drugis.mtc.Parameter;

public interface NodeSplitWrapper extends MTCModelWrapper {

	public Parameter getDirectEffect();

	public Parameter getIndirectEffect();

}

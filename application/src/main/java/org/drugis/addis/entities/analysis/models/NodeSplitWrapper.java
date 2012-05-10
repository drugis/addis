package org.drugis.addis.entities.analysis.models;

import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NodeSplitPValueSummary;

public interface NodeSplitWrapper extends MTCModelWrapper {

	public Parameter getDirectEffect();

	public Parameter getIndirectEffect();
	
	public Parameter getSplitNode();

	public NodeSplitPValueSummary getNodesNodeSplitPValueSummary();
	
}

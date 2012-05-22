package org.drugis.addis.entities.analysis.models;

import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.parameterization.SplitParameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.NodeSplitPValueSummary;
import org.drugis.mtc.summary.QuantileSummary;

public class SavedNodeSplitModel extends AbstractSavedModel implements NodeSplitWrapper {

	private final NodeSplitPValueSummary d_nodeSplitPValueSummary;
	private final BasicParameter d_splitNode;

	public SavedNodeSplitModel(NetworkBuilder<DrugSet> builder,
			MCMCSettingsCache settings,
			Map<Parameter, QuantileSummary> quantileSummaries,
			Map<Parameter, ConvergenceSummary> convergenceSummaries,
			BasicParameter splitNode, 
			NodeSplitPValueSummary nodeSplitPValueSummary) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
		d_splitNode = splitNode;
		d_nodeSplitPValueSummary = nodeSplitPValueSummary;
	}

	@Override
	public Parameter getDirectEffect() {
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if(p instanceof SplitParameter && ((SplitParameter) p).isDirect()) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Parameter getIndirectEffect() {
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if(p instanceof SplitParameter && !((SplitParameter) p).isDirect()) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Parameter getSplitNode() {
		return d_splitNode;
	}

	@Override
	public NodeSplitPValueSummary getNodeSplitPValueSummary() {
		return d_nodeSplitPValueSummary;
	}

	@Override
	public String getName() {
		return "Node Split on " + getSplitNode().getName();
	}
}

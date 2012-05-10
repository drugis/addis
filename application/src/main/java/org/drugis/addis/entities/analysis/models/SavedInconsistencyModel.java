package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;


public class SavedInconsistencyModel extends AbstractSavedModel  implements InconsistencyWrapper {

	public SavedInconsistencyModel(NetworkBuilder<DrugSet> builder) {
		super(builder);
	}

	@Override
	public Parameter getInconsistencyVariance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parameter> getInconsistencyFactors() {
		// TODO Auto-generated method stub
		return null;
	}
}

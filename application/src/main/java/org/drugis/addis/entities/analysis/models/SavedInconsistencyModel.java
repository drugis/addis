package org.drugis.addis.entities.analysis.models;

import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.Parameter;


public class SavedInconsistencyModel extends AbstractSavedModel<InconsistencyModel> implements InconsistencyWrapper {

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

	@Override
	public Parameter getRelativeEffect(DrugSet a, DrugSet b) {
		// TODO Auto-generated method stub
		return null;
	}

}

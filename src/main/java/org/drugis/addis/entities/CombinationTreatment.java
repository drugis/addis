package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class CombinationTreatment extends AbstractEntity implements Activity {

	public static final String PROPERTY_TREATMENTS = "treatments";
	
	private ObservableList<TreatmentActivity> d_treatments = new ArrayListModel<TreatmentActivity>();

	@Override
	public Set<? extends Entity> getDependencies() {
		return new HashSet<Entity>(getDrugs());
	}

	@Override
	public String getDescription() {
		if(d_treatments.size() == 0) {
			return "No treatments.";
		}
		String out = "Combination treatment (";
		for(TreatmentActivity t : d_treatments) {
			out = out + t.getDescription().substring(t.getDescription().lastIndexOf('(') + 1, t.getDescription().lastIndexOf(')')) + "; " ;
		}
		return out.substring(0, out.length() - 2) + ")";	
	}

	public void addTreatment(Drug drug, AbstractDose dose) {
		TreatmentActivity ta = new TreatmentActivity(drug, dose);
		d_treatments.add(ta);
	}
	
	@Override
	protected CombinationTreatment clone() {
		CombinationTreatment clone = new CombinationTreatment();
		for(TreatmentActivity t : d_treatments) {
			clone.addTreatment(t.getDrug(),  t.getDose() == null ? null : t.getDose().clone());
		}
		return clone;
	}
	
	public List<Drug> getDrugs() {
		List<Drug> drugs = new ArrayList<Drug>();
		for(TreatmentActivity ta : d_treatments) {
			drugs.add(ta.getDrug());
		}
		return drugs;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CombinationTreatment) {
			CombinationTreatment other = (CombinationTreatment) obj;
			return EqualsUtil.equal(other.getTreatments(), getTreatments());
		}
		return false;	
	}

	public  List<AbstractDose> getDoses() {
		List<AbstractDose> doses = new ArrayList<AbstractDose>();
		for(TreatmentActivity ta : d_treatments) {
			doses.add(ta.getDose());
		}
		return doses;
	}
	
	public ObservableList<TreatmentActivity> getTreatments() {
		return d_treatments;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
}

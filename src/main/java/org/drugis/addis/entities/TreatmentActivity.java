package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class TreatmentActivity extends AbstractEntity implements Activity {

	private Drug d_drug;

	private AbstractDose d_dose;
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";


	public TreatmentActivity(Drug drug, AbstractDose dose) {
		d_drug = drug;
		d_dose = dose;
	}

	public Drug getDrug() {
		return d_drug;
	}

	public void setDrug(Drug drug) {
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
	}
	
	public AbstractDose getDose() {
		return d_dose;
	}
	
	public void setDose(AbstractDose dose) {
		AbstractDose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
	}
	
	@Override
	public String toString() {
		return  d_drug + ", " + d_dose ;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.<Entity>singleton(d_drug);
	}
	
	@Override
	public TreatmentActivity clone() {
		return new TreatmentActivity(d_drug, d_dose == null ? null : d_dose.clone());
	}

	public String getDescription() {
		if(d_drug != null) {
			return "Treatment (" + d_drug.getName() + ( d_dose == null ? "" : " " + d_dose.toString() ) + ")";
		}
		return "Treatment (undefined)";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TreatmentActivity) {
			TreatmentActivity other = (TreatmentActivity) obj;
			return EqualsUtil.equal(other.getDrug(), getDrug()) && EqualsUtil.equal(other.getDose(), getDose());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (d_drug != null ? d_drug.hashCode() : 0) * 31 + (d_dose != null ? d_dose.hashCode() : 0);
	}
	
	/**
	 * Deep equality and shallow equality are equivalent for this type.
	 */
	public boolean deepEquals(Entity other) {
		return equals(other);
	}
}

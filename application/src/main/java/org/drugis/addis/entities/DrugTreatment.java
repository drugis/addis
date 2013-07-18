/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class DrugTreatment extends AbstractEntity {

	private Drug d_drug;

	private AbstractDose d_dose;
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";


	public DrugTreatment(Drug drug, AbstractDose dose) {
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
		return getLabel();
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.<Entity>singleton(d_drug);
	}

	@Override
	public DrugTreatment clone() {
		return new DrugTreatment(d_drug, d_dose == null ? null : d_dose.clone());
	}

	public String getLabel() {
		if(d_drug != null) {
			return d_drug.getName() + (d_dose == null ? "" : " " + d_dose.getLabel());
		}
		return "Treatment (undefined)";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DrugTreatment) {
			DrugTreatment other = (DrugTreatment) obj;
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

	public boolean isComplete() {
		return getDose() != null && getDrug() != null;
	}
}

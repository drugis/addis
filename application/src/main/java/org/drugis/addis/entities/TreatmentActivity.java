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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class TreatmentActivity extends AbstractEntity implements Activity {

	public static final String PROPERTY_TREATMENTS = "treatments";
	
	private ObservableList<DrugTreatment> d_treatments = new ArrayListModel<DrugTreatment>();
	
	public TreatmentActivity() {
	}
	
	public TreatmentActivity(DrugTreatment ta) {
		d_treatments.add(ta);
	}

	public TreatmentActivity(List<DrugTreatment> treatments) {
		d_treatments.addAll(treatments);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		final HashSet<Entity> dep = new HashSet<Entity>();
		for (DrugTreatment d : d_treatments) {
			dep.add(d.getDrug());
			final AbstractDose dose = d.getDose();
			if (dose != null && dose.getDoseUnit() != null) {
				dep.add(dose.getDoseUnit().getUnit());
			}
		}
		return dep;
	}

	@Override
	public String getLabel() {
		if(d_treatments.size() == 0) {
			return "No treatments.";
		}
		return StringUtils.join(d_treatments, " + ");
	}

	public void addTreatment(Drug drug, AbstractDose dose) {
		DrugTreatment ta = new DrugTreatment(drug, dose);
		d_treatments.add(ta);
	}
	
	@Override
	protected TreatmentActivity clone() {
		TreatmentActivity clone = new TreatmentActivity();
		for(DrugTreatment t : d_treatments) {
			clone.addTreatment(t.getDrug(),  t.getDose() == null ? null : t.getDose().clone());
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TreatmentActivity) {
			TreatmentActivity other = (TreatmentActivity) obj;
			return EqualsUtil.equal(other.getTreatments(), getTreatments());
		}
		return false;	
	}
	
	public ObservableList<DrugTreatment> getTreatments() {
		return d_treatments;
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public boolean isComplete() {
		for (DrugTreatment dt : getTreatments()) {
			if (!dt.isComplete()) {
				return false;
			}
		}
		return true;
	}
}

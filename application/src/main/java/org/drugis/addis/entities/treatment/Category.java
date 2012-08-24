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

package org.drugis.addis.entities.treatment;

import java.util.HashSet;
import java.util.Set;

import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.TypeWithName;

public class Category extends AbstractEntity implements TypeWithName, Comparable<Category> {
	private TreatmentCategorization d_owner;
	private String d_name;

	public static Category createTrivial(Drug d) {
		return TreatmentCategorization.createTrivial(d).getCategories().get(0);
	}

	public Category(TreatmentCategorization owner) {
		this(owner, "");
	}

	public Category(TreatmentCategorization owner, final String name) {
		d_owner = owner;
		d_name = name;
	}

	@Override
	public String getName() {
		return d_name;
	}

	public void setName(String name) {
		String oldVal = d_name;
		d_name = name;
		firePropertyChange(PROPERTY_NAME, oldVal, d_name);
	}

	@Override
	public String getLabel() {
		return getCategorization().getDrug().getLabel() + (!getName().isEmpty() ? " " + getName() : "");
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		Set<Entity> dependencies = new HashSet<Entity>(d_owner.getDependencies());
		if (!d_owner.isTrivial()) {
			dependencies.add(d_owner);
		}
		return dependencies;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Category) {
			Category other = (Category) o;
			return d_name.equals(other.d_name) && d_owner.equals(other.d_owner);
		}
		return false;
	}

	/**
	 * The implementation of deepEquals(Entity) for TreatmentCategorization and
	 * Category is complicated by their circular dependency. However,
	 * {@link TreatmentCategorization#deepEquals(Entity)} only shallow equals
	 * the categories, so we can safely deepEquals the TreatmentCategorization.
	 */
	@Override
	public boolean deepEquals(Entity obj) {
		if(obj instanceof Category) {
			Category other = (Category) obj;
			return d_name.equals(other.d_name) && d_owner.deepEquals(other.d_owner);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + 31 * getCategorization().hashCode();
	}

	@Override
	public int compareTo(Category o) {
		int ownerSame = d_owner.compareTo(o.d_owner);
		if (ownerSame != 0) {
			return ownerSame;
		}
		return getName().compareTo(o.getName()) ;
	}

	public TreatmentCategorization getCategorization() {
		return d_owner;
	}

	public boolean isTrivial() {
		return d_owner.isTrivial();
	}

	public Drug getDrug() {
		return d_owner.getDrug();
	}

	/**
	 * @return true iff the given DrugTreatment falls within this Category.
	 */
	public boolean match(DrugTreatment t) {
		return getDrug().equals(t.getDrug()) && match(t.getDose());
	}

	/**
	 * @return true iff the given Dose falls within this Category.
	 */
	public boolean match(AbstractDose dose) {
		return this.equals(d_owner.getCategory(dose));
	}

	public String toString() {
		return getLabel();
	}

	public String getCriterionLabel() {
		return d_owner.getDecisionTree().getLabel(this);
	}

}

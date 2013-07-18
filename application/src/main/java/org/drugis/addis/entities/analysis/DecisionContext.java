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

package org.drugis.addis.entities.analysis;

import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Entity;
import org.drugis.common.EqualsUtil;

public class DecisionContext extends AbstractEntity {
	public static final String PROPERTY_THERAPEUTIC_CONTEXT = "therapeuticContext";
	
	private String d_therapeuticContext = "";

	public void setTherapeuticContext(String newValue) {
		String oldValue = d_therapeuticContext;
		d_therapeuticContext = newValue;
		firePropertyChange(PROPERTY_THERAPEUTIC_CONTEXT, oldValue, newValue);
	}

	public String getTherapeuticContext() {
		return d_therapeuticContext;
	}
	
	public static final String PROPERTY_COMPARATOR = "comparator";
	
	private String d_comparator = "";

	public void setComparator(String newValue) {
		String oldValue = d_comparator;
		d_comparator = newValue;
		firePropertyChange(PROPERTY_COMPARATOR, oldValue, newValue);
	}

	public String getComparator() {
		return d_comparator;
	}
	
	public static final String PROPERTY_TIME_HORIZON = "timeHorizon";
	
	private String d_timeHorizon = "";

	public void setTimeHorizon(String newValue) {
		String oldValue = d_timeHorizon;
		d_timeHorizon = newValue;
		firePropertyChange(PROPERTY_TIME_HORIZON, oldValue, newValue);
	}

	public String getTimeHorizon() {
		return d_timeHorizon;
	}
	
	public static final String PROPERTY_STAKEHOLDER_PERSPECTIVE = "stakeholderPerspective";
	
	private String d_stakeholderPerspective = "";

	public void setStakeholderPerspective(String newValue) {
		String oldValue = d_stakeholderPerspective;
		d_stakeholderPerspective = newValue;
		firePropertyChange(PROPERTY_STAKEHOLDER_PERSPECTIVE, oldValue, newValue);
	}

	public String getStakeholderPerspective() {
		return d_stakeholderPerspective;
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DecisionContext) {
			DecisionContext other = (DecisionContext) o;
			return EqualsUtil.equal(d_comparator, other.d_comparator) &&
				EqualsUtil.equal(d_stakeholderPerspective, other.d_stakeholderPerspective) &&
				EqualsUtil.equal(d_therapeuticContext, other.d_therapeuticContext) &&
				EqualsUtil.equal(d_timeHorizon, other.d_timeHorizon);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}

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

package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import org.drugis.common.EqualsUtil;

public class OtherActivity extends AbstractEntity implements Activity {

	public static final String PROPERTY_DESCRIPTION = "description";
	private String d_description;

	public OtherActivity(String description) {
		d_description = description;
	}
	
	public boolean deepEquals(Entity other) {
		return equals(other);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof OtherActivity) {
			return EqualsUtil.equal(toString(), other.toString());
		}
		return false;
	}

	public String getLabel() {
		return d_description;
	}
	
	public String getDescription() {
		return d_description;
	}
	
	public void setDescription(String d) {
		String oldValue = d_description;
		d_description = d;
		firePropertyChange(PROPERTY_DESCRIPTION, oldValue, d_description);
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	protected OtherActivity clone() {
		return new OtherActivity(d_description);
	}
}

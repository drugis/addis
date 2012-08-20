/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.common.EqualsUtil;

public class LeafNode implements DecisionTreeNode {
	public static final String NAME_EXCLUDE = "Exclude";
	private final Category d_category;

	public LeafNode(final Category category) {
		d_category = category;
	}

	public LeafNode() {
		this(null);
	}

	@Override
	public String getName() {
		return d_category == null ? NAME_EXCLUDE : d_category.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	public Category getCategory() {
		return d_category;
	}

	@Override
	public boolean equivalent(DecisionTreeNode o) {
		if (!(o instanceof LeafNode)) { 
			return false;
		} else { 
			return EqualsUtil.equal(getCategory(), ((LeafNode)o).getCategory());
		}
	}
}

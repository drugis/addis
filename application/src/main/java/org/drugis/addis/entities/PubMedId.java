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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PubMedId {
	private String d_id;
	
	public PubMedId(String id) {
		setId(id);
	}

	public String getId() {
		return d_id;
	}
	
	@Override
	public String toString() {
		return getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PubMedId) {
			return ((PubMedId)o).getId().equals(this.getId());
		}
		return false;
	}
	
	private static final Pattern s_onlyDigits = Pattern.compile("^[1-9][0-9]*$");
	
	private void setId(String id) {
		if (id == null || id.length() == 0) { // FIXME: more validation should be done
			throw new IllegalArgumentException("PubMedId may not be null or empty.");
		}
		Matcher matcher = s_onlyDigits.matcher(id);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Only digits are valid in a PubMedId, and it may not start with 0.");
		}
		d_id = id;
	}

}
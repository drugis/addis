/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

public interface DomainListener {
	/**
	 * Called when the list of endpoints (or the endpoints within) has changed.
	 */
	public void endpointsChanged();
	
	/**
	 * Called when the list of studies (or the studies within) has changed.
	 */
	public void studiesChanged();

	/**
	 * Called when the list of drugs (or the drugs within) has changed.
	 */
	public void drugsChanged();

	/**
	 * Called when the list of indications has changed.
	 */
	public void indicationsChanged();

	/**
	 * Called when the list of analyses has changed.
	 */
	public void analysesChanged();
}

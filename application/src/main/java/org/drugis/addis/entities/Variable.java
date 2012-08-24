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



public interface Variable extends Entity, Comparable<Variable>, TypeWithName {
	public enum Type {
		CONTINUOUS("Continuous"),
		RATE("Rate"),
		CATEGORICAL("Categorical");


		private String d_name;

		Type() {
		}

		Type(String name) {
			d_name = name;
		}

		@Override
		public String toString() {
			return d_name;
		}
	}

	public final static String PROPERTY_VARIABLE_TYPE = "variableType";
	public final static String PROPERTY_DESCRIPTION = "description";

	public static final String UOM_DEFAULT_RATE = "Proportion of Patients";
	public static final String UOM_DEFAULT_CONTINUOUS = "";

	public void setDescription(String description);

	public String getDescription();

	public VariableType getVariableType();

	public void setVariableType(VariableType type);

	/**
	 * Build a Measurement on this variable.
	 * @return An appropriate type of Measurement.
	 */
	public Measurement buildMeasurement();

	/**
	 * Build a Measurement on this variable.
	 * @param size Default group size
	 * @return An appropriate type of Measurement.
	 */
	public BasicMeasurement buildMeasurement(int size);

	public void setName(String name);
}
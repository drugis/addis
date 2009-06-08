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

package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Pre;
import org.contract4j5.errors.ContractError;
import org.junit.Test;

public class ContractTest {
	@Contract
	public class TheContract {
		/** Some count that must be within a certain range. */
		@Invar(value="d_count > 0 && d_count < 20",
				message="Count must be between 1 and 20")
		private int d_count = 1;
		
		/** A string that can't be set to null or the empty string. */
		private String d_string = "default";
		
		public int getCount() {
			return d_count;
		}
		
		public void setCount(int inCount) {
			d_count = inCount;
		}
		
		public String getString() {
			return d_string;
		}
		
		@Pre(value="inString != null && inString.size() > 0")
		public void setString(String inString) {
			d_string = inString;
		}
	}
	
	@Test(expected=ContractError.class)
	public void testCountLowerBound() {
		TheContract contract = new TheContract();
		contract.setCount(-1);
	}
	
	@Test
	public void testCountLowerBound2() {
		TheContract contract = new TheContract();
		contract.setCount(1);
	}
	
	@Test(expected=ContractError.class)
	public void testCountUpperBound() {
		TheContract contract = new TheContract();
		contract.setCount(20);
	}
	
	@Test
	public void testCountUpperBound2() {
		TheContract contract = new TheContract();
		contract.setCount(19);
	}
	
	@Test(expected=ContractError.class)
	public void testStringNotNull() {
		TheContract contract = new TheContract();
		contract.setString(null);
	}
	
	@Test(expected=ContractError.class)
	public void testStringNotEmpty() {
		TheContract contract = new TheContract();
		contract.setString("");
	}
	
	@Test
	public void testSetString() {
		String str = "foo";
		TheContract contract = new TheContract();
		contract.setString(str);
		assertEquals(str, contract.getString());
	}
}
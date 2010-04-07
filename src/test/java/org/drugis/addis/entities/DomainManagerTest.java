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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.junit.Before;
import org.junit.Test;

public class DomainManagerTest {
	private DomainManager d_manager;
	
	@Before
	public void setUp() {
		d_manager = new DomainManager();
	}
	
	@Test
	public void testDefaultDomain() {
		assertTrue(d_manager.getDomain().getDrugs().isEmpty());
		assertTrue(d_manager.getDomain().getEndpoints().isEmpty());
		assertTrue(d_manager.getDomain().getStudies().isEmpty());
	}
	
	@Test
	public void testSaveLoadDomain() throws IOException, ClassNotFoundException {
		ExampleData.initDefaultData(d_manager.getDomain());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_manager.saveDomain(bos);
		Domain domain = d_manager.getDomain();
		
		d_manager = new DomainManager();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_manager.loadDomain(bis);
		
		assertEquals(domain, d_manager.getDomain());
	}
	
	@Test
	public void testSaveLoadDomainXml() throws IOException, ClassNotFoundException {
		ExampleData.initDefaultData(d_manager.getDomain());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_manager.saveXMLDomain(bos);
		Domain domain = d_manager.getDomain();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_manager.loadXMLDomain(bis);
		
		assertEquals(domain, d_manager.getDomain());
	}
}

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

package org.drugis.addis.entities;

import static org.drugis.addis.entities.AssertEntityEquals.assertDomainEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.drugis.addis.ExampleData;
import org.junit.Before;
import org.junit.Ignore;
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
	public void testLoadLegacyXml() throws IOException, ClassNotFoundException, TransformerException {
		InputStream lis = DomainManagerTest.class.getResourceAsStream("../testDataA-0.xml");
		d_manager.loadLegacyXMLDomain(lis);
		
		InputStream nis = DomainManagerTest.class.getResourceAsStream("../testDataA-1.addis");
		DomainManager managerNew = new DomainManager();
		managerNew.loadXMLDomain(nis, 1);
		
		assertDomainEquals(managerNew.getDomain(), d_manager.getDomain());
	}
	
	@Test @Ignore // FIXME
	public void testSaveLoadXml() throws IOException, ClassNotFoundException {
		ExampleData.initDefaultData(d_manager.getDomain());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_manager.saveXMLDomain(bos);
		Domain domain = d_manager.getDomain();
		d_manager.resetDomain();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_manager.loadXMLDomain(bis, 2);
		
		assertDomainEquals(domain, d_manager.getDomain());
	}
}

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

package org.drugis.addis.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drugis.addis.entities.DomainManager;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Test;

public class MainTest {
	@Test
	public void testNewDomain() {
		Main main = new Main(new String[] {}, true);
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(main, Main.PROPERTY_DISPLAY_NAME, null, Main.DISPLAY_NEW);
		main.addPropertyChangeListener(listener);
		
		main.newDomain();
		EasyMock.verify(listener);
		assertEquals(Main.DISPLAY_NEW, main.getDisplayName());
		assertEquals(null, main.getCurFilename());
		assertNotNull(main.getDomain());
		assertFalse(main.getDomainChangedModel().getValue());
	}
	
	@Test
	public void testLoadDomain() throws IOException {
		// Create temporary domain XML file
		File tmpFile = File.createTempFile("data", ".addis");
		DomainManager mgr = new DomainManager();
		FileOutputStream fos = new FileOutputStream(tmpFile);
		mgr.saveXMLDomain(fos);
		fos.close();
		
		final String expectedDisplayName = tmpFile.getName().replace(".addis", "");
		Main main = new Main(new String[] {}, true);
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(main, Main.PROPERTY_DISPLAY_NAME, null, expectedDisplayName);
		main.addPropertyChangeListener(listener);

		main.loadDomainFromFile(tmpFile.getAbsolutePath());
		EasyMock.verify(listener);
		assertEquals(expectedDisplayName, main.getDisplayName());
		assertEquals(tmpFile.getAbsolutePath(), main.getCurFilename());
		assertNotNull(main.getDomain());
		assertFalse(main.getDomainChangedModel().getValue());
	}
	
	@Test
	public void testSaveDomainAs() throws IOException {
		File tmpFile = File.createTempFile("data", ".addis");
		final String expectedDisplayName = tmpFile.getName().replace(".addis", "");
		
		Main main = new Main(new String[] {}, true);
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(main, Main.PROPERTY_DISPLAY_NAME, null, expectedDisplayName);
		main.addPropertyChangeListener(listener);
		
		main.saveDomainToFile(tmpFile.getAbsolutePath());
		EasyMock.verify(listener);
		assertEquals(expectedDisplayName, main.getDisplayName());
		assertEquals(tmpFile.getAbsolutePath(), main.getCurFilename());
		assertNotNull(main.getDomain());
		assertFalse(main.getDomainChangedModel().getValue());
	}
}

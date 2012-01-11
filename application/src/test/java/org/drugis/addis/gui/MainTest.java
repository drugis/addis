package org.drugis.addis.gui;

import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.util.JAXBHandler;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Test;

import sun.font.CreatedFontTracker;

public class MainTest {
	@Test
	public void testNewDomain() {
		Main main = new Main(new String[] {});
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
		Main main = new Main(new String[] {});
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(main, Main.PROPERTY_DISPLAY_NAME, null, expectedDisplayName);
		main.addPropertyChangeListener(listener);

		main.loadDomainFromFile(tmpFile.getAbsolutePath());
		EasyMock.verify(listener);
		assertEquals(expectedDisplayName, main.getDisplayName());
		assertEquals(tmpFile.getAbsolutePath(), main.getCurFilename());
		assertNotNull(main.getDomain());
		assertFalse(main.getDomainChangedModel().getValue());
	}
	
	@Test public void testSaveDomainAs() throws IOException {
		File tmpFile = File.createTempFile("data", ".addis");
		final String expectedDisplayName = tmpFile.getName().replace(".addis", "");
		
		Main main = new Main(new String[] {});
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

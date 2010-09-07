package org.drugis.common.gui;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class FileChooserTest {

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void TestExtensionFix() {
		assertEquals("/home/test.xml/test.xml", FileDialog.fixExtension("/home/test.xml/test.xml", "xml"));
		assertEquals("/home/test.xml/test.xm.xml", FileDialog.fixExtension("/home/test.xml/test.xm", "xml"));
		assertEquals("/home/test.xml/test.jpg.xml", FileDialog.fixExtension("/home/test.xml/test.jpg", "xml"));
		assertEquals("/home/test.xml/test.xml", FileDialog.fixExtension("/home/test.xml/test", "xml"));
		assertEquals("./.xml", FileDialog.fixExtension("./", "xml"));
		assertEquals("a.xml", FileDialog.fixExtension("a", "xml"));
	}
}

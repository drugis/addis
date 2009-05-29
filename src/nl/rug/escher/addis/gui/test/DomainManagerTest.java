package nl.rug.escher.addis.gui.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.gui.DomainManager;
import nl.rug.escher.addis.gui.MainData;

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
		MainData.initDefaultData(d_manager.getDomain());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		d_manager.saveDomain(bos);
		Domain domain = d_manager.getDomain();
		
		d_manager = new DomainManager();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		d_manager.loadDomain(bis);
		
		assertEquals(domain, d_manager.getDomain());
	}
}

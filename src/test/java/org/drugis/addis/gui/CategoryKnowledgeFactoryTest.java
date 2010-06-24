package org.drugis.addis.gui;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.EntityCategory;
import org.junit.Before;
import org.junit.Test;

public class CategoryKnowledgeFactoryTest {
	private Domain d_domain;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
	}

	@Test
	public void testFactoryKnowsAllCategories() {
		for (EntityCategory cat : d_domain.getCategories()) {
			assertNotNull(CategoryKnowledgeFactory.getCategoryKnowledge(cat));
		}
	}
}

package org.drugis.addis.entities.treatment;

import static org.junit.Assert.*;

import org.junit.Test;

public class LeafNodeTest {
	@Test
	public void testWithCategory() {
		final Category category = new Category("Potato");
		LeafNode node = new LeafNode(category);
		assertEquals(category, node.getCategory());
		assertEquals(category.getName(), node.getName());
	}
	
	@Test
	public void testExclude() {
		LeafNode node = new LeafNode();
		assertEquals(null, node.getCategory());
		assertEquals(LeafNode.NAME_EXCLUDE, node.getName());
	}
}

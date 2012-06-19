package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TypeNodeTest {

	private ExcludeNode d_child;

	@Rule
    public final ExpectedException expected = ExpectedException.none();
	
	@Before
	public void setUp() { 
		d_child = new ExcludeNode();
	}
	
	@Test
	public void testInitialization() {
		TypeNode fixedNode = new TypeNode(FixedDose.class, d_child);
		FixedDose fixedDose = new FixedDose();
		FlexibleDose flexibleDose = new FlexibleDose();

		assertEquals(d_child, fixedNode.decide(fixedDose));
		
		expected.expect(IllegalArgumentException.class);
		fixedNode.decide(flexibleDose);
	}

	@Test
	public void testMultipleTypes() {
		CategoryNode someCatNode = new CategoryNode("dog");
		TypeNode typeNode = new TypeNode(FixedDose.class, d_child);
		typeNode.addType(FlexibleDose.class, someCatNode);
		FixedDose fixedDose = new FixedDose();
		FlexibleDose flexibleDose = new FlexibleDose();

		assertEquals(d_child, typeNode.decide(fixedDose));
		assertEquals(someCatNode, typeNode.decide(flexibleDose));
	}
}

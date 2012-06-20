package org.drugis.addis.entities.treatment;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
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
		CategoryNode unknownNode = new CategoryNode("unknown");
		TypeNode typeNode = new TypeNode(FixedDose.class, d_child);
		typeNode.addType(FlexibleDose.class, someCatNode);
		typeNode.addType(UnknownDose.class, unknownNode);
		
		assertEquals(d_child, typeNode.decide(new FixedDose()));
		assertEquals(someCatNode, typeNode.decide(new FlexibleDose()));
		assertEquals(unknownNode, typeNode.decide(new UnknownDose()));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArgument() {
		TypeNode typeNode = new TypeNode(FlexibleDose.class, d_child);
		typeNode.decide(new UnknownDose());
	}
}

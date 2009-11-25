package org.drugis.addis.gui.components;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlWordWrapperTest {

	@Test
	public void testWordWrap() {
		String inp = "word word word word word word word word word word word word word word word word word";
		String outp = HtmlWordWrapper.wordWrap(inp);
		assertEquals("<html>word word word word word word word word word word word word word word word word <br>word</html>", outp);
	}
}

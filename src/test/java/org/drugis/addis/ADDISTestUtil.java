package org.drugis.addis;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.relativeeffect.BasicRelativeEffect;

public class ADDISTestUtil {
	public static void assertRelativeEffectListEquals(List<BasicRelativeEffect<? extends Measurement>> expected, List<BasicRelativeEffect<? extends Measurement>> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); ++i) {
			assertEquals(expected.get(i).getClass(), actual.get(i).getClass());
			assertEquals(expected.get(i).getBaseline(), actual.get(i).getBaseline());
			assertEquals(expected.get(i).getSubject(), actual.get(i).getSubject());
		}
	}
}

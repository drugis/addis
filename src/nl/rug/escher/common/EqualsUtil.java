package nl.rug.escher.common;

public class EqualsUtil {

	public static boolean equal(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

}

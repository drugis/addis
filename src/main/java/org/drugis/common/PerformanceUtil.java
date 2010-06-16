package org.drugis.common;

import java.util.HashMap;
import java.util.Map;

public class PerformanceUtil {

	static Map<Object,Long> d_startTimeMap = new HashMap<Object,Long>();
	
	public static void start(Object o) {
		d_startTimeMap.put(o, System.currentTimeMillis());
	}
	
	public static void end(Object o) {
		Long start = d_startTimeMap.get(o);
		if (start != null) {
			System.err.println(o + " " + (System.currentTimeMillis() - start));
			d_startTimeMap.remove(o);
		}
	}
}

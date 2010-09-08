package org.drugis.common;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date getCurrentDateWithoutTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();
		return date;
	}

}

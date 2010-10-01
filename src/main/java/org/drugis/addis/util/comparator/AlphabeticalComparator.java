package org.drugis.addis.util.comparator;

import java.util.Comparator;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;

public class AlphabeticalComparator implements Comparator<Object>  {
	public int compare(Object o1, Object o2) {
		if (o1 instanceof OutcomeMeasure)
			return compareOm((OutcomeMeasure)o1, (OutcomeMeasure)o2);
		return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
	}

	public int compareOm(OutcomeMeasure o1, OutcomeMeasure o2) {
		if (o1.getClass().equals(o2.getClass()))
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		else 
			return o1 instanceof Endpoint ? -1 : 1;
	}
}
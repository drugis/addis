package org.drugis.addis.util.comparator;

import java.util.Comparator;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;

public class OutcomeComparator implements Comparator<OutcomeMeasure>  {
	public int compare(OutcomeMeasure o1, OutcomeMeasure o2) {
		if (o1.getClass().equals(o2.getClass()))
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		else 
			return o1 instanceof Endpoint ? -1 : 1;
	}

}
package org.drugis.addis.util.comparator;

import java.util.Comparator;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;

/**
 * Sorts criteria so that HIGHER_IS_BETTER are put before LOWER_IS_BETTER.
 */
public class CriteriaComparator implements Comparator<OutcomeMeasure> {

	public int compare(OutcomeMeasure x0, OutcomeMeasure x1) {
		if (x0.getDirection() == x1.getDirection()) {
			return x0.getName().compareTo(x1.getName());
		}
		if (x0.getDirection() == Direction.HIGHER_IS_BETTER) {
			return -1;
		} else {
			return 1;
		}
	}

}

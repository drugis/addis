package org.drugis.addis.mcmcmodel;

import org.drugis.mtc.summary.Summary;

public class SummaryHelper {

	public static void waitUntilDefined(Summary summary) throws InterruptedException {
		while (!summary.getDefined()) {
			Thread.sleep(30);
		}
	}

}

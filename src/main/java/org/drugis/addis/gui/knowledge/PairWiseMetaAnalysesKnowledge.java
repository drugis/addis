package org.drugis.addis.gui.knowledge;

import org.drugis.addis.FileNames;

public class PairWiseMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public PairWiseMetaAnalysesKnowledge() {
		super("Pair-wise meta-analysis", "Pair-wise meta-analyses", null);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_METASTUDY;
	}
}

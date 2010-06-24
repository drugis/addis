package org.drugis.addis.gui.knowledge;

import org.drugis.addis.FileNames;

public class NetworkMetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public NetworkMetaAnalysesKnowledge() {
		super("Network meta-analysis", "Network meta-analyses", null);
	}
	
	@Override
	public String getIconName() {
		return FileNames.ICON_NETWMETASTUDY;
	}
}

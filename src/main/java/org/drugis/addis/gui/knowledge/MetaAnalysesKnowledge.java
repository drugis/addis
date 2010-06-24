package org.drugis.addis.gui.knowledge;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;

public class MetaAnalysesKnowledge extends CategoryKnowledgeBase {
	public MetaAnalysesKnowledge() {
		super("Meta-analysis", "Meta-analyses", null);
	}
	
	@Override
	public String getIconName(Class<? extends Entity> cls) {
		if (NetworkMetaAnalysis.class.isAssignableFrom(cls)) {
			return FileNames.ICON_NETWMETASTUDY;
		} else {
			return FileNames.ICON_METASTUDY;
		}
	}
}

package org.drugis.addis.gui;

import java.util.HashMap;
import java.util.Map;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.EntityCategory;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.gui.knowledge.AdverseEventsKnowledge;
import org.drugis.addis.gui.knowledge.BenefitRiskAnalysesKnowledge;
import org.drugis.addis.gui.knowledge.DrugsKnowledge;
import org.drugis.addis.gui.knowledge.EndpointsKnowledge;
import org.drugis.addis.gui.knowledge.IndicationsKnowledge;
import org.drugis.addis.gui.knowledge.MetaAnalysesKnowledge;
import org.drugis.addis.gui.knowledge.PopulationCharacteristicsKnowledge;
import org.drugis.addis.gui.knowledge.StudiesKnowledge;

public class CategoryKnowledgeFactory {
	private static final Map<Class<? extends Entity>, CategoryKnowledge> s_knowledge =
		new HashMap<Class<? extends Entity>, CategoryKnowledge>();
	static {
		s_knowledge.put(Indication.class, new IndicationsKnowledge());
		s_knowledge.put(Drug.class, new DrugsKnowledge());
		s_knowledge.put(Endpoint.class, new EndpointsKnowledge());
		s_knowledge.put(AdverseEvent.class, new AdverseEventsKnowledge());
		s_knowledge.put(PopulationCharacteristic.class, new PopulationCharacteristicsKnowledge());
		s_knowledge.put(Study.class, new StudiesKnowledge());
		s_knowledge.put(MetaAnalysis.class, new MetaAnalysesKnowledge());
		s_knowledge.put(BenefitRiskAnalysis.class, new BenefitRiskAnalysesKnowledge());
	};
	
	public static CategoryKnowledge getCategoryKnowledge(EntityCategory category) {
		return s_knowledge.get(category.getEntityClass());
	}
	
	public static CategoryKnowledge getCategoryKnowledge(Class<? extends Entity> entityClass) {
		return s_knowledge.get(entityClass);
	}
}

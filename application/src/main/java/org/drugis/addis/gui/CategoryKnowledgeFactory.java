/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import org.drugis.addis.entities.Unit;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.gui.knowledge.AdverseEventsKnowledge;
import org.drugis.addis.gui.knowledge.BenefitRiskAnalysesKnowledge;
import org.drugis.addis.gui.knowledge.DrugsKnowledge;
import org.drugis.addis.gui.knowledge.EndpointsKnowledge;
import org.drugis.addis.gui.knowledge.IndicationsKnowledge;
import org.drugis.addis.gui.knowledge.NetworkMetaAnalysesKnowledge;
import org.drugis.addis.gui.knowledge.PairWiseMetaAnalysesKnowledge;
import org.drugis.addis.gui.knowledge.PopulationCharacteristicsKnowledge;
import org.drugis.addis.gui.knowledge.StudiesKnowledge;
import org.drugis.addis.gui.knowledge.UnitsKnowledge;

public class CategoryKnowledgeFactory {
	private static final Map<Class<? extends Entity>, CategoryKnowledge> s_knowledge =
		new HashMap<Class<? extends Entity>, CategoryKnowledge>();
	static {
		s_knowledge.put(Unit.class, new UnitsKnowledge());
		s_knowledge.put(Indication.class, new IndicationsKnowledge());
		s_knowledge.put(Drug.class, new DrugsKnowledge());
		s_knowledge.put(Endpoint.class, new EndpointsKnowledge());
		s_knowledge.put(AdverseEvent.class, new AdverseEventsKnowledge());
		s_knowledge.put(Study.class, new StudiesKnowledge());
		s_knowledge.put(PairWiseMetaAnalysis.class, new PairWiseMetaAnalysesKnowledge());
		s_knowledge.put(NetworkMetaAnalysis.class, new NetworkMetaAnalysesKnowledge());
		s_knowledge.put(BenefitRiskAnalysis.class, new BenefitRiskAnalysesKnowledge());
		
		PopulationCharacteristicsKnowledge popcharKnowledge = new PopulationCharacteristicsKnowledge();
		s_knowledge.put(PopulationCharacteristic.class, popcharKnowledge);
	};
	
	public static CategoryKnowledge getCategoryKnowledge(EntityCategory category) {
		return s_knowledge.get(category.getEntityClass());
	}
	
	/**
	 * Only use when you know the type at compile time, and only use for the identical type as
	 * defined for the category. If you need run-time type determination, use Domain.getEntityCategory(entity).
	 */
	public static CategoryKnowledge getCategoryKnowledge(Class<? extends Entity> entityClass) {
		return s_knowledge.get(entityClass);
	}
}

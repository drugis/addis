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

package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.junit.Test;

public class LyndOBrienPresentationTest {
	@Test
	public void testCreateBeforeMeasurementsReady() {
		MetaBenefitRiskAnalysis br = buildAnalysis();
		new LyndOBrienPresentation<DrugSet, MetaBenefitRiskAnalysis>(br);
	}
	
	public static MetaBenefitRiskAnalysis buildAnalysis() {
		Indication indication = ExampleData.buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		outcomeMeasureList.add(ExampleData.buildEndpointHamd());
		outcomeMeasureList.add(ExampleData.buildAdverseEventConvulsion());
		
		List<MetaAnalysis> metaAnalysisList = new ArrayList<MetaAnalysis>();
		metaAnalysisList.add(ExampleData.buildMetaAnalysisHamd());
		metaAnalysisList.add(ExampleData.buildMetaAnalysisConv());
		
		Drug parox = ExampleData.buildDrugParoxetine();
		List<DrugSet> fluoxList = Collections.singletonList(DrugSet.createTrivial(ExampleData.buildDrugFluoxetine()));
		
		return new MetaBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, metaAnalysisList, DrugSet.createTrivial(parox), fluoxList, AnalysisType.LyndOBrien);										
	}
}

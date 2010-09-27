/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import static org.drugis.common.JUnitUtil.assertAllAndOnly;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.junit.Before;
import org.junit.Test;

public class BenefitRiskPMTest {

	private PresentationModelFactory d_pmf;
	private MetaBenefitRiskPresentation d_mpm;
	private DomainImpl d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(d_domain);
		MetaBenefitRiskAnalysis metaBRanalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		StudyBenefitRiskAnalysis studyBRanalysis = ExampleData.buildStudyBenefitRiskAnalysis();
		d_mpm = new MetaBenefitRiskPresentation(metaBRanalysis, d_pmf);
		
		// test creation; no further tests required
		new StudyBenefitRiskPresentation(studyBRanalysis, d_pmf);
	}
	
	@Test
	public void testGetAnalysesModel() {
		assertAllAndOnly(d_mpm.getBean().getMetaAnalyses(), d_mpm.getAnalysesModel().getValue());
	}
}

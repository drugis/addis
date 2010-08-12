/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class BenefitRiskPMTest {

	private PresentationModelFactory d_pmf;
	private BenefitRiskPresentation d_pm;
	private boolean d_eventHasFired;
	private DomainImpl d_domain;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(d_domain);
		BenefitRiskAnalysis analysis = ExampleData.buildMockBenefitRiskAnalysis();
		d_pm = new BenefitRiskPresentation(analysis, d_pmf);
		d_eventHasFired = false;
	}
	
	@Test
	public void testGetAnalysesModel() {
		assertAllAndOnly(d_pm.getBean().getMetaAnalyses(), d_pm.getAnalysesModel().getValue());
	}
}

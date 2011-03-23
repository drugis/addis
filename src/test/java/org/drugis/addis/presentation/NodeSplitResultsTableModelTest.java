/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NodeSplitResultsTableModelTest {

	private Domain d_domain;
	private NodeSplitResultsTableModel d_model;
	private Indication d_ind;
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		d_ind = d_domain.getIndications().first();
		d_pmf = new PresentationModelFactory(d_domain);
		d_pm = new NetworkMetaAnalysisPresentation(ExampleData.buildNetworkMetaAnalysisHamD(), d_pmf);
		d_model = new NodeSplitResultsTableModel(d_pm);
		for (Characteristic c : StudyCharacteristics.values()) {
			d_pm.getCharacteristicVisibleModel(c).setValue(true);
		}
	}
	
	@Test @Ignore
	public void testGetColumnName() {
		fail();
	}
}

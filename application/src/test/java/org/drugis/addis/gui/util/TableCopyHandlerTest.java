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

package org.drugis.addis.gui.util;

import static org.drugis.addis.presentation.BRATTableModel.COLUMN_FOREST;
import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.gui.renderer.SummaryCellRenderer;
import org.drugis.addis.gui.renderer.BRATForestCellRenderer.ForestPlotTableCell;
import org.drugis.addis.presentation.BRATTableModel;
import org.drugis.addis.presentation.BRATTableModel.BRATForest;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

public class TableCopyHandlerTest {

	private BRATTableModel<Arm, StudyBenefitRiskAnalysis> d_btmStudy;
	private StudyBenefitRiskAnalysis d_sba;
	private Arm d_subject;

	@Before
	public void setUp() {
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		ObservableList<Arm> arms = ExampleData.buildStudyChouinard().getArms();

		d_sba = new StudyBenefitRiskAnalysis("Test SBA", ExampleData.buildIndicationDepression(), 
				ExampleData.buildStudyChouinard(), criteria, arms, AnalysisType.SMAA);
		d_subject = arms.get(1);
		d_btmStudy = new BRATTableModel<Arm, StudyBenefitRiskAnalysis>(d_sba, d_subject);
	}
	
	@Test
	public void testCopyForestAsPNG() throws IOException {
		SummaryCellRenderer summaryCellRenderer = new SummaryCellRenderer();

		BRATForest value = (BRATForest)d_btmStudy.getValueAt(1, COLUMN_FOREST);

		Component superRenderer = summaryCellRenderer.getTableCellRendererComponent(new JTable(), value, true, true, 3, 3);		
		final Color bg = superRenderer.getBackground();
		final Color fg = superRenderer.getForeground();
		
		final BRATForest forest = value;
		
		ForestPlotTableCell forestPlotTableCell = new ForestPlotTableCell(forest, bg, fg);
		String image =  TableCopyHandler.getMimeEncodedPng(forestPlotTableCell);
		assertEquals("iVBORw0KGgoAAAANSUhEUgAAAS0AAAAVCAIAAACc3y/NAAAAdklEQVR42u3ZQQ0AIQxFwXqqeVygAh8I4NoAgWmegh8me9hofejnMtMIxwsTcGgEDsWhOOSQQw7FoTgUhxyKQ3EoDjnULQ7XMwuH4pBDccghh++9abfnOJTvoTgUhxyKQ3Eo/w85FIfiUBxyKA7FoTjkUByqpAn9joI+RhAMuQAAAABJRU5ErkJggg==", image);
	}

}

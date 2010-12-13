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

package org.drugis.addis.gui.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.EntitiesTablePanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.threading.Task;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaBenefitRiskView extends AbstractBenefitRiskView<MetaBenefitRiskPresentation> {
	
	public MetaBenefitRiskView(MetaBenefitRiskPresentation pm, Main main) {
		super(pm, main);
	}

	@Override
	protected JPanel buildOverviewPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout, new JPanel());
		builder.setDefaultDialogBorder();
		builder.setOpaque(true);
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));

		builder.addSeparator("Included Analyses", cc.xy(1, 5));
		builder.add(buildAnalysesPart(), cc.xy(1, 7));
		
		final JComponent progressBars = buildProgressBars();
		builder.add(progressBars, cc.xy(1, 9));
		
		return builder.getPanel();
	}
	
	private JComponent buildProgressBars() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addSeparator("Running sub-analyses. Please wait.",cc.xy(1,1));
		int row = 1;
		for (Task t : d_pm.getMeasurementTasks()) {
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new TaskProgressBar(d_pm.getProgressModel(t));
			builder.add(bar,cc.xy(1, row));
		}
		
		return builder.getPanel();
	}
	
	protected JButton createSaveImageButton(final JComponent chart) {
		JButton button = new JButton("Save Image");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImageExporter.writeImage(d_main, chart, (int) chart.getSize().getWidth(), (int) chart.getSize().getHeight());
			}
		});
		return button;
	}
	protected JComponent buildAnalysesPart() {	
		String[] formatter = {"name","type","indication","outcomeMeasure","includedDrugs","includedStudies","sampleSize"};
		return new EntitiesTablePanel(Arrays.asList(formatter), d_pm.getAnalysesModel(), d_main, d_pm.getFactory());
	}

	@Override
	protected JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setOpaque(true);
		
		builder.add(AuxComponentFactory.createNoteField("Relative measurements: odds ratio or mean difference, with "
				+ d_pm.getBaseline() +" as the common comparator."),cc.xy(1, 1));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getRelativeMeasurementTableModel())), cc.xy(1, 3));
		
		builder.add(AuxComponentFactory.createNoteField("Absolute measurements: odds or mean calculated from the assumed odds or mean for " + 
				d_pm.getBaseline() + ". The method used to derive the assumed odds or mean are heuristic, "
				+ "and the absolute values should be interpreted with care."), cc.xy(1, 5));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getAbsoluteMeasurementTableModel())), cc.xy(1, 9));
	
		return builder.getPanel();
	}
}

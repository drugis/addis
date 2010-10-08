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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MetaBenefitRiskView extends AbstractBenefitRiskView<MetaBenefitRiskPresentation> {
	
	public MetaBenefitRiskView(MetaBenefitRiskPresentation pm, Main main) {
		super(pm, main);
	}

	protected JPanel buildMeasurementsPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p, 3dlu, p"// 4-7
				);
		CellConstraints cc = new CellConstraints();
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();

		builder.addSeparator("Included Analyses", cc.xy(1, 1));
		builder.add(buildAnalysesPart(), cc.xy(1, 3));
		
		builder.addSeparator("Measurements", cc.xy(1, 5));
		builder.add(buildMeasurementsPart(), cc.xy(1, 7));
		return builder.getPanel();
	}

	@Override
	protected JPanel buildOverviewPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout, new ScrollableJPanel());
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));
		
		final JComponent progressBars = buildProgressBars();
		builder.add(progressBars, cc.xy(1, 5));
		
		if (d_pm.getMeasurementsReadyModel().getValue()) {
			progressBars.setVisible(false);
		}
		
		d_pm.getMeasurementsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (progressBars != null) {
					progressBars.setVisible(false);
				}
			}
		});
		return builder.getPanel();
	}
	
	private JComponent buildProgressBars() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();

		builder.addLabel("Running sub-analyses. Please wait.",cc.xy(1,1));
		int row = 1;
		for (int i=0; i<d_pm.getNumNMAProgBars(); ++i){
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachNMAProgBar(bar,i);
			builder.add(bar,cc.xy(1, row));
		}

		for (int i=0; i<d_pm.getNumBaselineProgBars(); ++i){
			LayoutUtil.addRow(layout);
			row += 2;
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			d_pm.attachBaselineProgBar(bar,i);
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
		return new EntitiesTablePanel(Arrays.asList(formatter), d_pm.getAnalysesModel(), d_main, d_pm.getFactory(), null);
	}

	protected JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.add(AuxComponentFactory.createNoteField("Relative measurements: odds ratio or mean difference, with "
				+ d_pm.getBean().getBaseline() +" as the common comparator."),cc.xy(1, 1));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getRelativeMeasurementTableModel())), cc.xy(1, 3));
		
		builder.add(AuxComponentFactory.createNoteField("Absolute measurements: odds or mean calculated from the assumed odds or mean for " + 
				d_pm.getBean().getBaseline() + ". The method used to derive the assumed odds or mean are heuristic, "
				+ "and the absolute values should be interpreted with care."), cc.xy(1, 5));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getAbsoluteMeasurementTableModel())), cc.xy(1, 9));
	
		return builder.getPanel();
	}
}

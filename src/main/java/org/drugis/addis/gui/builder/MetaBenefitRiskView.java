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
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.BuildViewWhenReadyComponent;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.EntitiesTablePanel;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ChildComponenentHeightPropagater;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.PreferenceInformationView;

public class MetaBenefitRiskView extends AbstractBenefitRiskView<MetaBenefitRiskPresentation> {

	private PanelBuilder d_builder;
	protected JPanel d_panel;
	
	public MetaBenefitRiskView(MetaBenefitRiskPresentation pm, Main main) {
		super(pm, main);
		d_pm.startAllSimulations();
	}
	
	public JComponent buildPanel() {
		if (d_builder != null)
			d_builder.getPanel().removeAll();
		
		final FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, " + // 1-3 
				"3dlu, p, 3dlu, p, " + // 4-7
				"3dlu, p, 3dlu, p, " + // 8-11 
				"3dlu, p, 3dlu, p, " + // 12-15
				"3dlu, p, 3dlu, p, " + // 16-19
				"3dlu, p, 3dlu, p," + // 20-23
				"3dlu, p"
				);
		
		d_builder = new PanelBuilder(layout, new ScrollableJPanel());
		d_builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();
		
		d_builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(BenefitRiskAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		d_builder.add(buildOverviewPart(), cc.xy(1, 3));
		
		final JComponent progressBars = buildProgressBars();
		d_builder.add(progressBars, cc.xy(1, 5));
		
		d_pm.getAllModelsReadyModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (progressBars != null) {
					progressBars.setVisible(false);
					d_pm.startSMAA();
				}
			}
		});
		
		d_builder.addSeparator("Included Analyses", cc.xy(1, 7));
		d_builder.add(buildAnalysesPart(), cc.xy(1, 9));
		
		d_builder.addSeparator("Measurements", cc.xy(1, 11));
		d_builder.add(buildMeasurementsPart(), cc.xy(1, 13));
		
		d_builder.addSeparator("Preferences", cc.xy(1, 15));
		d_builder.add(buildPreferencesPart(), cc.xy(1, 17));
		
		d_builder.addSeparator("Rank Acceptabilities", cc.xy(1, 19));
		d_builder.add(buildRankAcceptabilitiesPart(), cc.xy(1, 21));
		
		d_builder.addSeparator("Central Weights", cc.xy(1, 23));
		d_builder.add(buildCentralWeightsPart(), cc.xy(1, 25));
		
		d_panel = d_builder.getPanel();
		ChildComponenentHeightPropagater.attachToContainer(d_panel);
		
		return d_panel;
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
	
	@Override
	protected JComponent buildCentralWeightsPart() {
		return createWaiter(new CentralWeightsBuilder());
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
	@Override
	protected JComponent buildRankAcceptabilitiesPart() {
		return createWaiter(new RankAcceptabilitiesBuilder());
	}

	protected JComponent buildAnalysesPart() {	
		String[] formatter = {"name","type","indication","outcomeMeasure","includedDrugs","includedStudies","sampleSize"};
		return new EntitiesTablePanel(Arrays.asList(formatter), d_pm.getAnalysesModel(), d_main, d_pm.getFactory(), null);
	}

	@Override
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

	protected BuildViewWhenReadyComponent createWaiter(ViewBuilder builder) {
		return new BuildViewWhenReadyComponent(builder, d_pm.getAllModelsReadyModel(), WAITING_MESSAGE);
	}

	@Override
	protected JComponent buildPreferencesPart() {
		return createWaiter(new PreferencesBuilder());
	}

	@Override
	protected JComponent buildPreferenceInformationView(PreferencePresentationModel preferencePresentationModel, 
			MetaBenefitRiskPresentation pm) {
		JComponent prefPanel = new PreferenceInformationView(d_pm.getPreferencePresentationModel(), new ClinicalScaleRenderer(d_pm)).buildPanel();
		return prefPanel;
	}
}

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

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicRiskDifference;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.treeplot.ForestPlot;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RandomEffectsMetaAnalysisView extends AbstractMetaAnalysisView<RandomEffectsMetaAnalysisPresentation>
implements ViewBuilder {
	
	public RandomEffectsMetaAnalysisView(RandomEffectsMetaAnalysisPresentation pm, Main parent) {
		super(pm, parent);
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setOpaque(true);
		
		CellConstraints cc =  new CellConstraints();		

		JTabbedPane tabbedPane = new JTabbedPane();
		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(PairWiseMetaAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildOverviewPart(), cc.xy(1, 3));

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
		builder.add(buildStudiesPart(), cc.xy(1, 7));
		
		tabbedPane.addTab("Overview", builder.getPanel());
		
		layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu");
		builder = new PanelBuilder(layout);

		builder.add(getPlotsPanel(false), cc.xy(1, 3));
		tabbedPane.addTab("Results", builder.getPanel());
		
		return tabbedPane;
	}

	public JComponent getPlotsPanel(boolean isOverview) {
		switch (d_pm.getAnalysisType()) {
		case RATE:
			return buildRatePlotsPart(isOverview);
		case CONTINUOUS:
			return buildContinuousPlotsPart(isOverview);
		default:
			throw new RuntimeException("Unexpected case: " +
					d_pm.getAnalysisType() + " is not a supported type of endpoint");
		}
	}

	private JComponent buildContinuousPlotsPart(boolean isOverview) {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill", "p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Mean difference", cc.xy(1, 1));
		
		builder.add(buildRelativeEffectPart(BasicMeanDifference.class, isOverview), cc.xy(1, 3));
		
		if (!isOverview) {
			builder.addSeparator("Standardised mean difference", cc.xy(1, 5));
			builder.add(buildRelativeEffectPart(BasicStandardisedMeanDifference.class, isOverview), cc.xy(1, 7));
		}
		return builder.getPanel();
	}

	private JComponent buildRatePlotsPart(boolean isOverview) {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Odds ratio", cc.xy(1, 1));
		builder.add(buildRelativeEffectPart(BasicOddsRatio.class, isOverview), cc.xy(1, 3));			

		JCheckBox checkBox = BasicComponentFactory.createCheckBox(d_pm.getCorrectedForZeroesHolder(), "Correct for zeroes");
		builder.add(checkBox, cc.xy(1, 5));
		
		if (!isOverview) {
			builder.addSeparator("Risk ratio", cc.xy(1, 7));
			builder.add(buildRelativeEffectPart(BasicRiskRatio.class, isOverview), cc.xy(1, 9));
		
			builder.addSeparator("Risk difference", cc.xy(1, 11));
			builder.add(buildRelativeEffectPart(BasicRiskDifference.class, isOverview), cc.xy(1, 13));
		}
		return builder.getPanel();
	}

	@SuppressWarnings("serial")
	private JComponent buildRelativeEffectPart(Class<? extends RelativeEffect<?>> type, boolean isOverview) {
		FormLayout layout1 = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p");
		FormLayout layout2 = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu");
		JPanel encapsulating = new JPanel(layout1);
		
		PanelBuilder builder = new PanelBuilder(layout2);
		builder.setDefaultDialogBorder();
		CellConstraints cc =  new CellConstraints();
		
		final RelativeEffectCanvas canvas = new RelativeEffectCanvas(d_pm.getForestPlotPresentation(type));
		builder.add(canvas, cc.xy(1, 1));
		
		builder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		builder.setBackground(Color.white);
		
		encapsulating.add(builder.getPanel(),cc.xy(1, 1));
		
		if (!isOverview) {
			JButton saveBtn = new JButton("Save Image");
			saveBtn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
				ForestPlot plot = canvas.getPlot();
				ImageExporter.writeImage(d_parent, plot, (int) plot.getSize().getWidth(),(int) plot.getSize().getHeight());
				}
			});
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(saveBtn);
			
			encapsulating.add(new JLabel(" "), cc.xy(1, 3));
			encapsulating.add(bbuilder.getPanel(), cc.xy(1, 5));
		}
		return encapsulating;	
	}

}

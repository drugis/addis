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

package org.drugis.addis.gui.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicRiskDifference;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.treeplot.ForestPlot;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RandomEffectsMetaAnalysisView extends AbstractMetaAnalysisView<RandomEffectsMetaAnalysisPresentation>
implements ViewBuilder {
	
	private boolean d_overView;

	public RandomEffectsMetaAnalysisView(RandomEffectsMetaAnalysisPresentation pm, Main parent, boolean overView) {
		super(pm, parent);
		d_overView = overView;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout/*, new ScrollableJPanel()*/);
		builder.setDefaultDialogBorder();
		builder.setOpaque(true);
		
		CellConstraints cc =  new CellConstraints();		

		if (!d_overView) {
			builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(PairWiseMetaAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
			builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));

			builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
			builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), cc.xy(1, 7));
		}

		switch (d_pm.getAnalysisType()) {
		case RATE:
			buildRatePlotsPart(builder, cc);
			break;
		case CONTINUOUS:
			buildContinuousPlotsPart(builder, cc);
			break;
		default:
			throw new RuntimeException("Unexpected case: " +
					d_pm.getAnalysisType() + " is not a supported type of endpoint");
		}
		
		return builder.getPanel();
	}

	private void buildContinuousPlotsPart(PanelBuilder builder,
			CellConstraints cc) {
		builder.addSeparator("Mean difference", cc.xy(1, 9));
		
		if (d_overView) {
			builder.add(buildRelativeEffectPart(BasicMeanDifference.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(BasicMeanDifference.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Standardised mean difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(BasicStandardisedMeanDifference.class)), cc.xy(1, 19));
		}
	}

	private void buildRatePlotsPart(PanelBuilder builder, CellConstraints cc) {
		builder.addSeparator("Odds ratio", cc.xy(1, 9));
		if (d_overView) {			
			builder.add(buildRelativeEffectPart(BasicOddsRatio.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(BasicOddsRatio.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Risk ratio", cc.xy(1, 13));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(BasicRiskRatio.class)), cc.xy(1, 15));
		
			builder.addSeparator("Risk difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(BasicRiskDifference.class)), cc.xy(1, 19));
		}
	}

	@SuppressWarnings("serial")
	private JComponent buildRelativeEffectPart(Class<? extends RelativeEffect<?>> type) {
		JPanel encapsulating = new JPanel(new BorderLayout());
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p");
		
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		final RelativeEffectCanvas canvas = new RelativeEffectCanvas(d_pm.getForestPlotPresentation(type));
		builder.add(canvas, cc.xy(1, 1));
		
		builder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		builder.setBackground(Color.white);
		
		encapsulating.add(builder.getPanel(),BorderLayout.NORTH);
		
		if (!d_overView) {
			JButton saveBtn = new JButton("Save Image");
			saveBtn.addActionListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
				ForestPlot plot = canvas.getPlot();
				ImageExporter.writeImage(d_parent, plot, (int) plot.getSize().getWidth(),(int) plot.getSize().getHeight());
				}
			});
			ButtonBarBuilder2 bbuilder = new ButtonBarBuilder2();
			bbuilder.addButton(saveBtn);
			
			encapsulating.add(new JLabel(" "), BorderLayout.CENTER);
			encapsulating.add(bbuilder.getPanel(), BorderLayout.SOUTH);
		}


		return encapsulating;	
	}

}

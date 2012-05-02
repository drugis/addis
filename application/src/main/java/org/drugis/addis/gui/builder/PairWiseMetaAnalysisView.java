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

package org.drugis.addis.gui.builder;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.drugis.addis.entities.ContinuousVariableType;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.PairWiseMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.BasicMeanDifference;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicRiskDifference;
import org.drugis.addis.entities.relativeeffect.BasicRiskRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.forestplot.ForestPlot;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.components.AddisTabbedPane;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.PairWiseMetaAnalysisPresentation;
import org.drugis.common.gui.ImageExporter;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PairWiseMetaAnalysisView extends AbstractMetaAnalysisView<PairWiseMetaAnalysisPresentation>
implements ViewBuilder {

	public PairWiseMetaAnalysisView(PairWiseMetaAnalysisPresentation pm, AddisWindow mainWindow) {
		super(pm, mainWindow);
	}

	public JComponent buildPanel() {
		JTabbedPane tabbedPane = new AddisTabbedPane();

		tabbedPane.addTab("Overview", buildOverviewPanel());
		tabbedPane.addTab("Results", getPlotsPanel(false));

		return tabbedPane;
	}

	private JPanel buildOverviewPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
	
		CellConstraints cc =  new CellConstraints();		

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(PairWiseMetaAnalysis.class).getSingularCapitalized(), cc.xy(1, 1));
		builder.add(buildPropertiesPart(), cc.xy(1, 3));

		builder.addSeparator(CategoryKnowledgeFactory.getCategoryKnowledge(Study.class).getPlural(), cc.xy(1, 5));
		builder.add(buildStudiesPart(), cc.xy(1, 7));
		
		return builder.getPanel();
	}

	public JComponent getPlotsPanel(boolean isWizard) {
		if (d_pm.getAnalysisType() instanceof RateVariableType) {
			return buildRatePlotsPart(isWizard);
		}
		if (d_pm.getAnalysisType() instanceof ContinuousVariableType) {
			return buildContinuousPlotsPart(isWizard);
		}
		throw new RuntimeException("Unexpected case: " +
				d_pm.getAnalysisType() + " is not a supported type of endpoint");
	}

	private JComponent buildContinuousPlotsPart(boolean isWizard) {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		builder.addSeparator("Mean difference", cc.xy(1, 1));
		
		builder.add(buildRelativeEffectPart(BasicMeanDifference.class, isWizard), cc.xy(1, 3));
		
		if (!isWizard) {
			builder.addSeparator("Standardised mean difference", cc.xy(1, 5));
			builder.add(buildRelativeEffectPart(BasicStandardisedMeanDifference.class, isWizard), cc.xy(1, 7));
		}
		return builder.getPanel();
	}

	private JComponent buildRatePlotsPart(boolean isWizard) {
		
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Odds ratio", cc.xy(1, 1));
		builder.add(buildRelativeEffectPart(BasicOddsRatio.class, isWizard), cc.xy(1,5));
		
		if (!isWizard) {
			builder.addSeparator("Risk ratio", cc.xy(1, 7));
			builder.add(buildRelativeEffectPart(BasicRiskRatio.class, isWizard), cc.xy(1, 9));
		
			builder.addSeparator("Risk difference", cc.xy(1, 11));
			builder.add(buildRelativeEffectPart(BasicRiskDifference.class, isWizard), cc.xy(1, 13));
		}
		return builder.getPanel();
	}

	@SuppressWarnings("serial")
	private JComponent buildRelativeEffectPart(Class<? extends RelativeEffect<?>> type, boolean isWizard) {
		FormLayout layout1 = new FormLayout(
				"left:0:grow",
				"p, 3dlu, p, 3dlu, p");
		FormLayout layout2 = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu");
		JPanel encapsulating = new JPanel(layout1);
		
		PanelBuilder builder = new PanelBuilder(layout2);
		CellConstraints cc =  new CellConstraints();
		
		final RelativeEffectCanvas canvas = new RelativeEffectCanvas(d_pm.getForestPlotPresentation(type));
		builder.add(canvas, cc.xy(1, 1));
		
		builder.setBorder(BorderFactory.createLineBorder(Color.black));
		builder.setBackground(Color.white);
		
		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		
		encapsulating.add(scrollPane,cc.xy(1, 1));
		
		if (!isWizard) {
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

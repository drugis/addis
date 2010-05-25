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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.drugis.addis.entities.relativeeffect.MeanDifference;
import org.drugis.addis.entities.relativeeffect.OddsRatio;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.entities.relativeeffect.RiskDifference;
import org.drugis.addis.entities.relativeeffect.RiskRatio;
import org.drugis.addis.entities.relativeeffect.StandardisedMeanDifference;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.common.gui.ViewBuilder;

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
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();		

		if (!d_overView) {
			builder.addSeparator("Meta-analysis", cc.xy(1, 1));
			builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));

			builder.addSeparator("Included studies", cc.xy(1, 5));
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
			builder.add(buildRelativeEffectPart(MeanDifference.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(MeanDifference.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Standardised mean difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(StandardisedMeanDifference.class)), cc.xy(1, 19));
		}
	}

	private void buildRatePlotsPart(PanelBuilder builder, CellConstraints cc) {
		builder.addSeparator("Odds ratio", cc.xy(1, 9));
		if (d_overView) {
			builder.add(buildRelativeEffectPart(OddsRatio.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(OddsRatio.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Risk ratio", cc.xy(1, 13));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(RiskRatio.class)), cc.xy(1, 15));
		
			builder.addSeparator("Risk difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(RiskDifference.class)), cc.xy(1, 19));
		}
	}

	private JComponent buildRelativeEffectPart(Class<? extends RelativeEffect<?>> type) {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		RelativeEffectCanvas canvas = new RelativeEffectCanvas(d_pm.getForestPlotPresentation(type));
		builder.add(canvas, cc.xy(1, 1));
		builder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		builder.setBackground(Color.white);
		
		return builder.getPanel();	
	}

}

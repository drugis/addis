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

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.addis.presentation.DrugPresentationModel;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.addis.presentation.NetworkMetaAnalysisPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.addis.presentation.VariablePresentationModel;
import org.drugis.common.gui.ViewBuilder;

public class ViewFactory {

	public static ViewBuilder createView(Entity node, PresentationModelFactory pmf, Main main) {
		if (node instanceof RandomEffectsMetaAnalysis) {
			return new RandomEffectsMetaAnalysisView(
					(RandomEffectsMetaAnalysisPresentation) pmf.getModel(((RandomEffectsMetaAnalysis) node)), 
					main, false);
		} else if (node instanceof NetworkMetaAnalysis) {
			return new NetworkMetaAnalysisView(
					(NetworkMetaAnalysisPresentation) pmf.getModel(((NetworkMetaAnalysis) node)),
					main);
		} else if (node instanceof Study) {
			return new StudyView((StudyPresentationModel) pmf
					.getModel(((Study) node)), main.getDomain(), main);
		} else if (node instanceof Variable) {
			return new VariableView(
					(VariablePresentationModel) pmf.getModel(((Variable) node)), main);
		} else if (node instanceof Drug) {
			return new DrugView((DrugPresentationModel) pmf.getModel(((Drug) node)), main);
		} else if (node instanceof Indication) {
			return new IndicationView(
					(IndicationPresentation) pmf.getModel(((Indication) node)), main);
		}  else if (node instanceof BenefitRiskAnalysis) {
			return new BenefitRiskView((BenefitRiskPM) pmf.getModel((BenefitRiskAnalysis) node), main);
		}
		return new ViewBuilder() {
			public JComponent buildPanel() {
				return new JLabel("not implemented in ViewFactory");
			}
		};
	}

}

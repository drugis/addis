package org.drugis.addis.gui.builder;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.Main;
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
		}
		return null;
	}

}

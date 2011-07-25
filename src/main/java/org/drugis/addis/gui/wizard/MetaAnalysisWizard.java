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

package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.RateVariableType;
import org.drugis.addis.entities.analysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.AddisWindow;
import org.drugis.addis.gui.AuxComponentFactory;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.builder.RandomEffectsMetaAnalysisView;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.MetaAnalysisWizardPresentation;
import org.drugis.addis.util.ListHolderWrapperPlsDel;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class MetaAnalysisWizard extends Wizard {
	
	public MetaAnalysisWizard(AddisWindow mainWindow, MetaAnalysisWizardPresentation pm) {
		super(buildModel(pm, mainWindow));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}
	
	private static WizardModel buildModel(MetaAnalysisWizardPresentation pm, AddisWindow mainWindow) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SelectDrugsWizardStep(pm, mainWindow));
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm, mainWindow);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getMetaAnalysisCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, mainWindow));
		return wizardModel;
	}

	public static class OverviewWizardStep extends AbstractOverviewWizardStep<StudyGraphModel> {
		public OverviewWizardStep(MetaAnalysisWizardPresentation pm, AddisWindow mainWindow) {
			super(pm, mainWindow);
		}
		
		@Override
		public void prepare() {
			removeAll();

			setLayout(new BorderLayout()); // needed for placement
			
			RandomEffectsMetaAnalysisPresentation pm = ((MetaAnalysisWizardPresentation)d_pm).getMetaAnalysisModel();
			final RandomEffectsMetaAnalysisView mav = new RandomEffectsMetaAnalysisView(
					(RandomEffectsMetaAnalysisPresentation)pm, d_mainWindow);
			final JComponent panel = mav.getPlotsPanel(true);

			if(d_pm.getOutcomeMeasureModel().getValue().getVariableType() instanceof RateVariableType){
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_CORRECTED), "Correct for zeroes");
				add(checkBox, BorderLayout.NORTH);
			}
			
			final JScrollPane sp = new JScrollPane(panel);
			sp.getVerticalScrollBar().setUnitIncrement(16);
			add(sp, BorderLayout.CENTER);
			
			pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_CORRECTED).addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					sp.setVisible(false);
					sp.setViewportView(mav.getPlotsPanel(true));
					sp.setVisible(true);
				}
			});
			
			setComplete(true);
		}
	}

	public static class SelectDrugsWizardStep extends PanelWizardStep {
		MetaAnalysisWizardPresentation d_pm;
		JFrame d_frame;
		private StudyGraph d_studyGraph;

		public SelectDrugsWizardStep(MetaAnalysisWizardPresentation pm, JFrame frame) {
			super("Select Drugs","Select the drugs to be used for meta analysis.");
			
			d_pm = pm;
			d_frame = frame;
			setLayout(new BorderLayout());
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildSelectDrugsPanel(), cc.xy(1, 1));			
			builder.add(BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel()),
					cc.xy(1, 3));
			builder.setBorder(BorderFactory.createEmptyBorder());
			builder.add(buildStudiesGraph(), cc.xy(1, 5));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);			
			
			Bindings.bind(this, "complete", d_pm.getMetaAnalysisCompleteModel());
		}
		
		private Component buildStudiesGraph() {
			StudyGraphModel pm = d_pm.getStudyGraphModel();
			d_studyGraph = new StudyGraph(pm);
			return d_studyGraph;
		}
		
		@Override public void prepare() {
			d_studyGraph.layoutGraph();
		}

		private JPanel buildSelectDrugsPanel() {
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			CellConstraints cc = new CellConstraints();
			builder.addLabel("First Drug",cc.xy(1, 1));
			builder.addLabel("Second Drug",cc.xy(5, 1));
						
			JComboBox firstDrugBox = AuxComponentFactory.createBoundComboBox(new ListHolderWrapperPlsDel<DrugSet>(d_pm.getDrugListModel()), d_pm.getFirstDrugModel(), true);
			JComboBox secondDrugBox = AuxComponentFactory.createBoundComboBox(new ListHolderWrapperPlsDel<DrugSet>(d_pm.getDrugListModel()), d_pm.getSecondDrugModel(), true);
			
			builder.add(firstDrugBox,cc.xy(1, 3));
			builder.add(secondDrugBox,cc.xy(5, 3));
			builder.addLabel("VS",cc.xy(3, 3));
			JPanel panel = builder.getPanel();			
			
			return panel;
		}
	}	
}

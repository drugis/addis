package org.drugis.addis.gui.builder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.gui.CategoryKnowledgeFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.EnhancedTable;
import org.drugis.addis.gui.components.ScrollableJPanel;
import org.drugis.addis.gui.components.TablePanel;
import org.drugis.addis.presentation.StudyBenefitRiskPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ChildComponenentHeightPropagater;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.views.PreferenceInformationView;

public class StudyBenefitRiskView extends AbstractBenefitRiskView<StudyBenefitRiskPresentation> {

	private PanelBuilder d_builder;
	protected JPanel d_panel;
	public StudyBenefitRiskView(StudyBenefitRiskPresentation model, Main main) {
		super(model, main);
		d_pm.startSMAA();
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

	@Override
	protected JComponent buildMeasurementsPart() {
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout("pref:grow:fill",
				"p, 3dlu, p");
		PanelBuilder builder = new PanelBuilder(layout);
		
		builder.add(AuxComponentFactory.createNoteField("Measurements: incidence approximated with Beta-distribution, or continuous variables approximated with a Normal distribution."
				),cc.xy(1, 1));
		builder.add(new TablePanel(new EnhancedTable(d_pm.getMeasurementTableModel())), cc.xy(1, 3));
	
		return builder.getPanel();
	}

	@Override
	protected JComponent buildPreferencesPart() {
		return new PreferencesBuilder().buildPanel();
	}

	@Override
	protected JComponent buildPreferenceInformationView(PreferencePresentationModel preferencePresentationModel, StudyBenefitRiskPresentation pm) {
		JComponent prefPanel = new PreferenceInformationView(d_pm.getPreferencePresentationModel()).buildPanel();
		return prefPanel;
	}

	protected JComponent buildRankAcceptabilitiesPart() {
		return new RankAcceptabilitiesBuilder().buildPanel();
	}

	protected JComponent buildCentralWeightsPart() {
		return new CentralWeightsBuilder().buildPanel();
	}
}

package org.drugis.addis.gui.builder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.RelativeEffectTableDialog;
import org.drugis.addis.presentation.MeanDifferenceTableModel;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.addis.presentation.RiskDifferenceTableModel;
import org.drugis.addis.presentation.RiskRatioTableModel;
import org.drugis.addis.presentation.StandardisedMeanDifferenceTableModel;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyEndpointsView implements ViewBuilder {
	
	private StudyPresentationModel d_model;
	private PresentationModelFactory d_pmf;
	private JFrame d_mainWindow;
	private boolean d_isEndpoints;

	public StudyEndpointsView(StudyPresentationModel model, Main main, boolean endpoints) {
		d_model = model;
		d_pmf = main.getPresentationModelFactory();
		d_mainWindow = main;
		d_isEndpoints = endpoints;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, left:pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		if (d_model.getBean().getOutcomeMeasures().isEmpty()) {
			builder.addLabel("No endpoints", cc.xy(1, 1));
		} else {
			int row = 1;
			boolean addRow = false;
			Set<OutcomeMeasure> outcomeMeasures = d_isEndpoints ? d_model.getEndpoints() : d_model.getAdes();
			for (OutcomeMeasure om : outcomeMeasures) {
				if (addRow) {
					LayoutUtil.addRow(layout);
				}
				builder.add(
						GUIFactory.createOutcomeMeasureLabelWithIcon(om),
						cc.xy(1, row));
				
				if (!(om instanceof Endpoint))
					continue;
				
				Endpoint e = (Endpoint) om;							
				
				JPanel panel = new JPanel(new FlowLayout());
				if (e.getType().equals(Type.RATE)) {
					panel.add(createOddsRatioButton(e));
					panel.add(createRiskRatioButton(e));
					panel.add(createRiskDifferenceButton(e));
				} else if (e.getType().equals(Type.CONTINUOUS)) {
					panel.add(createWMDButton(e));
					panel.add(createSMDButton(e));
				}
				builder.add(panel, cc.xy(3, row));
				row += 2;
				addRow = true;
			}
		}
		return builder.getPanel();
	}

	private JButton createOddsRatioButton(final Endpoint e) {
		final RelativeEffectTableModel tableModel = new OddsRatioTableModel(d_model.getBean(), e, d_pmf);
		return createRatioButton(tableModel);
	}

	private JButton createRiskRatioButton(Endpoint e) {
		final RelativeEffectTableModel tableModel = new RiskRatioTableModel(d_model.getBean(), e, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createRiskDifferenceButton(Endpoint e) {
		final RelativeEffectTableModel tableModel = new RiskDifferenceTableModel(d_model.getBean(), e, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createWMDButton(Endpoint e) {
		final RelativeEffectTableModel tableModel = new MeanDifferenceTableModel(d_model.getBean(), e, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createSMDButton(Endpoint e) {
		final RelativeEffectTableModel tableModel = new StandardisedMeanDifferenceTableModel(d_model.getBean(), e, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createRatioButton(final RelativeEffectTableModel tableModel) {
		JButton button = new JButton(tableModel.getTitle());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RelativeEffectTableDialog dlg = new RelativeEffectTableDialog(d_mainWindow, tableModel);
				GUIHelper.centerWindow(dlg, d_mainWindow);
				dlg.setVisible(true);
			}
		});
		return button;
	}
}

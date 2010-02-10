package org.drugis.addis.gui.builder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.RelativeEffectTableDialog;
import org.drugis.addis.gui.components.MeasurementTable;
import org.drugis.addis.presentation.MeanDifferenceTableModel;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.addis.presentation.RiskDifferenceTableModel;
import org.drugis.addis.presentation.RiskRatioTableModel;
import org.drugis.addis.presentation.StandardisedMeanDifferenceTableModel;
import org.drugis.addis.presentation.StudyPresentationModel;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyOutcomeMeasuresView implements ViewBuilder {
	
	private StudyPresentationModel d_model;
	private PresentationModelFactory d_pmf;
	private JFrame d_mainWindow;
	private boolean d_isEndpoints;

	public StudyOutcomeMeasuresView(StudyPresentationModel model, Main main, boolean endpoints) {
		d_model = model;
		d_pmf = main.getPresentationModelFactory();
		d_mainWindow = main;
		d_isEndpoints = endpoints;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, left:pref, 3dlu, pref:grow", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		List<OutcomeMeasure> outcomeMeasures = d_isEndpoints ? d_model.getEndpoints() : d_model.getAdverseEvents();
		if (outcomeMeasures.isEmpty()) {
			builder.addLabel("No " + (d_isEndpoints ? "Endpoints" : "Adverse Events"), cc.xy(1, 1));
		} else {
			int row = 1;
			for (OutcomeMeasure om : outcomeMeasures) {
				JComponent outcomeMeasureLabelWithIcon = GUIFactory.createOutcomeMeasureLabelWithIcon(om);
				
				outcomeMeasureLabelWithIcon.setToolTipText(GUIHelper.createToolTip(
						d_model.getBean().getNote(om)));
				builder.add(
						outcomeMeasureLabelWithIcon,
						cc.xy(1, row));
				
				JPanel panel = new JPanel(new FlowLayout());
				if (om.getType().equals(Type.RATE)) {
					panel.add(createOddsRatioButton(om));
					panel.add(createRiskRatioButton(om));
					panel.add(createRiskDifferenceButton(om));
				} else if (om.getType().equals(Type.CONTINUOUS)) {
					panel.add(createWMDButton(om));
					panel.add(createSMDButton(om));
				}
				builder.add(panel, cc.xy(3, row));
				row += 2;

				LayoutUtil.addRow(layout);
			}
		
			MeasurementTable measurementTable = null;
			if (d_isEndpoints) {
				measurementTable = new MeasurementTable(d_model.getEndpointTableModel());
			} else {
				measurementTable = new MeasurementTable(d_model.getAdverseEventTableModel());
			}
			builder.add(AuxComponentFactory.createUnscrollableTablePanel(measurementTable),
					cc.xyw(1, row, 5));
		}
		
		return builder.getPanel();
	}

	private JButton createOddsRatioButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new OddsRatioTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}

	private JButton createRiskRatioButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new RiskRatioTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createRiskDifferenceButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new RiskDifferenceTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createWMDButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new MeanDifferenceTableModel(d_model.getBean(), om, d_pmf);
		return createRatioButton(tableModel);
	}
	
	private JButton createSMDButton(OutcomeMeasure om) {
		final RelativeEffectTableModel tableModel = new StandardisedMeanDifferenceTableModel(d_model.getBean(), om, d_pmf);
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

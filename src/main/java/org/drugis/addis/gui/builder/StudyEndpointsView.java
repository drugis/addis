package org.drugis.addis.gui.builder;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.RatioTableDialog;
import org.drugis.addis.presentation.MeanDifferenceTableModel;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RelativeEffectTableModel;
import org.drugis.addis.presentation.RiskDifferenceTableModel;
import org.drugis.addis.presentation.RiskRatioTableModel;
import org.drugis.addis.presentation.StandardisedMeanDifferenceTableModel;
import org.drugis.common.ImageLoader;
import org.drugis.common.gui.GUIHelper;
import org.drugis.common.gui.LayoutUtil;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyEndpointsView implements ViewBuilder {
	
	private PresentationModel<? extends Study> d_model;
	private ImageLoader d_loader;
	private PresentationModelFactory d_pmf;
	private JFrame d_mainWindow;

	public StudyEndpointsView(PresentationModel<? extends Study> model, Main main) {
		d_model = model;
		d_loader = main.getImageLoader();
		d_pmf = main.getPresentationModelManager();
		d_mainWindow = main;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 3dlu, left:pref",
				"p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
	
		if (d_model.getBean().getEndpoints().isEmpty()) {
			builder.addLabel("No endpoints", cc.xy(1, 1));
		} else {
			int row = 1;
			boolean addRow = false;
			for (Endpoint e : d_model.getBean().getEndpoints()) {
				if (addRow) {
					LayoutUtil.addRow(layout);
				}
				builder.add(
						GUIFactory.createEndpointLabelWithIcon(d_loader, d_model.getBean(), e),
						cc.xy(1, row));
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
				RatioTableDialog dlg = new RatioTableDialog(d_mainWindow, tableModel);
				GUIHelper.centerWindow(dlg, d_mainWindow);
				dlg.setVisible(true);
			}
		});
		return button;
	}
}

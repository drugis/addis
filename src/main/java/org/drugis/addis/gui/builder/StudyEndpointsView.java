package org.drugis.addis.gui.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.RatioTableDialog;
import org.drugis.addis.presentation.OddsRatioTableModel;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RatioTableModel;
import org.drugis.common.ImageLoader;
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
				"left:pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
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
				builder.add(createOddsRatioButton(e), cc.xy(3, row));
				builder.add(createRiskRatioButton(e), cc.xy(5, row));
				builder.add(createRiskDifferenceButton(e), cc.xy(7, row));
				row += 2;
				addRow = true;
			}
		}
		return builder.getPanel();
	}

	private JButton createOddsRatioButton(final Endpoint e) {
		JButton button = new JButton("Odds-Ratio Table");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OddsRatioTableModel tableModel = new OddsRatioTableModel(d_model.getBean(), e, d_pmf);
				createRatioDialog(tableModel).setVisible(true);
			}

		});
		return button;
	}
	
	private JDialog createRatioDialog(RatioTableModel tableModel) {
		return new RatioTableDialog(d_mainWindow, tableModel);
	}
	
	private JButton createRiskRatioButton(Endpoint e) {
		return new JButton("Risk-Ratio Table");
	}
	
	private JButton createRiskDifferenceButton(Endpoint e) {
		return new JButton("Risk-Difference Table");
	}
}

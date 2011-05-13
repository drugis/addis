package org.drugis.addis.gui.builder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.gui.NoteViewButton;
import org.drugis.addis.presentation.EpochDurationPresentation;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.StudyPresentation;
import org.drugis.common.gui.LayoutUtil;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StudyEpochsView {

	private final StudyPresentation d_pm;
	private final PresentationModelFactory d_pmf;
	private final JFrame d_parent;

	public StudyEpochsView(JFrame parent, StudyPresentation spm, PresentationModelFactory pmf) {
		d_parent = parent;
		d_pm = spm;
		d_pmf = pmf;
	}

	public JPanel buildPanel() {
		FormLayout layout = new FormLayout(
				"left:pref, 5dlu, left:pref, 5dlu, left:pref", 
				"p");
		PanelBuilder builder = new PanelBuilder(layout );
		
		CellConstraints cc = new CellConstraints();
		
		int row = 1;

		builder.addLabel("Epoch", cc.xy(3, row));
		builder.addLabel("Duration", cc.xy(5, row));

		for (Epoch e : d_pm.getBean().getEpochs()) {
			row = buildEpoch(layout, builder, cc, row, e);
		}
		return builder.getPanel();

	}

	private int buildEpoch(FormLayout layout, PanelBuilder builder,	CellConstraints cc, int row, Epoch e) {
		PresentationModel<Epoch> epochModel = d_pmf.getModel(e);
		EpochDurationPresentation edpm = new EpochDurationPresentation(e);
		
		LayoutUtil.addRow(layout);
		row += 2;
		
		final JLabel epochLabel = BasicComponentFactory.createLabel(epochModel.getModel(Epoch.PROPERTY_NAME));
		final JLabel epochDurationLabel = BasicComponentFactory.createLabel(
				new PropertyAdapter<EpochDurationPresentation>(edpm, EpochDurationPresentation.PROPERTY_LABEL, true));
		JButton button = new NoteViewButton(d_parent, "Epoch: " + e.toString(), e.getNotes());
		builder.add(button, cc.xy(1, row));
		builder.add(epochLabel, cc.xy(3, row));
		builder.add(epochDurationLabel, cc.xy(5, row));
		
		return row;
	}
}

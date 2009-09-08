package org.drugis.addis.gui;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;
import org.drugis.common.gui.OkCancelDialog;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class AddIndicationDialog extends OkCancelDialog {
	private Domain d_domain;
	private Indication d_indication;
	private Main d_main;
	
	public AddIndicationDialog(Main frame, Domain domain) {
		super(frame, "Add Indication");
		setModal(true);
		d_main = frame;
		d_domain = domain;
		d_indication = new Indication(0L, "");
		IndicationView view = new IndicationView(new PresentationModel<Indication>(d_indication), d_okButton);
		getUserPanel().add(view.buildPanel());
		pack();
		d_okButton.setEnabled(false);
		getRootPane().setDefaultButton(d_okButton);
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void commit() {
		d_domain.addIndication(d_indication);
		setVisible(false);
		d_main.leftTreeFocusOnIndication(d_indication);
	}
}

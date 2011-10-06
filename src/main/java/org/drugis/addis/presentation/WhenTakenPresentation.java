package org.drugis.addis.presentation;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.WhenTaken;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;

public class WhenTakenPresentation extends PresentationModel<WhenTaken> {
	private static final long serialVersionUID = 3332262569759324845L;
	private final ObservableList<Epoch> d_epochs;

	public WhenTakenPresentation(WhenTaken bean, ObservableList<Epoch> epochs) {
		super(bean); // To the rescue!
		d_epochs = epochs;
		
		d_epochs.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				update();
			}

			public void intervalAdded(ListDataEvent e) {
			}
			
			public void intervalRemoved(ListDataEvent e) {
				update();
			}

			private void update() {
				if (!d_epochs.contains(getBean().getEpoch())) {
					getBean().setEpoch(null);
				}
			}
			
		});
	}

	public DurationPresentation<WhenTaken> getOffsetPresentation() {
		return new DurationPresentation<WhenTaken>(getBean());
	}
}

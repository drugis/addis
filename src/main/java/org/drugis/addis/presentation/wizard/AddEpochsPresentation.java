package org.drugis.addis.presentation.wizard;

import java.util.List;

import org.drugis.addis.entities.Epoch;
import org.drugis.addis.entities.Note;

import com.jgoodies.binding.list.ObservableList;

public class AddEpochsPresentation extends AddListItemsPresentation<Epoch> {

	public AddEpochsPresentation(ObservableList<Epoch> list, String itemName, int minElements) {
		super(list, itemName, minElements);
	}

	@Override
	public List<Note> getNotes(Epoch t) {
		return t.getNotes();
	}
	
	@Override
	public Epoch createItem() {
		return new Epoch(nextItemName(), null);
	}

}

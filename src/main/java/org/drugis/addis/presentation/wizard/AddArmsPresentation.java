package org.drugis.addis.presentation.wizard;

import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Note;

import com.jgoodies.binding.list.ObservableList;

public class AddArmsPresentation extends AddListItemsPresentation<Arm> {

	public AddArmsPresentation(ObservableList<Arm> list, String itemName, int minElements) {
		super(list, itemName, minElements);
	}

	@Override
	public List<Note> getNotes(Arm t) {
		return t.getNotes();
	}
	
	@Override
	public Arm createItem() {
		return new Arm(nextItemName(), 0);
	}

}

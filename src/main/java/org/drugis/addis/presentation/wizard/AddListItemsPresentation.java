package org.drugis.addis.presentation.wizard;

import java.util.List;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.TypeWithName;
import org.drugis.common.beans.AbstractObservable;

import com.jgoodies.binding.list.ObservableList;

public abstract class AddListItemsPresentation<T extends TypeWithName> extends AbstractObservable {

	private static final String PROPERTY_LIST = "list";

	protected ObservableList<T> d_list;
	protected final String d_itemName;
	protected final int d_minElements;
	protected int d_itemsCreated;

	public AddListItemsPresentation(ObservableList<T> list, String itemName, int minElements) {
		d_list = list;
		d_itemName = itemName;
		d_minElements = minElements;
		d_itemsCreated = 0;
	}

	public abstract T createItem();
	public abstract List<Note> getNotes(T t);


	public ObservableList<T> getList() {
		return d_list;
	}

	public void setList(ObservableList<T> list) {
		ObservableList<T> oldVal = d_list;
		d_list = list;
		d_itemsCreated = d_list.size();
		firePropertyChange(PROPERTY_LIST, oldVal, d_list);
	}

	public String getItemName() {
		return d_itemName;
	}

	public int getMinElements() {
		return d_minElements;
	}

	public String nextItemName() {
		return getItemName() + " " + (++d_itemsCreated);
	}

}
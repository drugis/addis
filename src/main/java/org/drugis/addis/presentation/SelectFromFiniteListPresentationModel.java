package org.drugis.addis.presentation;

import java.util.List;

import com.jgoodies.binding.value.ValueModel;

public interface SelectFromFiniteListPresentationModel<T> {
	/**
	 * Remove one of the slots
	 * @param idx
	 */
	public void removeSlot(int idx);

	/**
	 * Add an additional slot.
	 */
	public void addSlot();

	/**
	 * The number of slots.
	 */
	public int countSlots();

	/**
	 * The slot at index idx
	 */
	public TypedHolder<T> getSlot(int idx);

	/**
	 * Whether or not it is possible to add more options to the list.
	 */
	public boolean hasAddOptionDialog();

	/**
	 * Show a dialog for adding more options to the list, and use the new option in slot idx.
	 */
	public void showAddOptionDialog(int idx);

	/**
	 * Whether more slots can be added.
	 */
	public ValueModel getAddSlotsEnabledModel();

	/**
	 * Whether input is complete.
	 */
	public ValueModel getInputCompleteModel();

	/**
	 * The title for this step.
	 */
	public String getTitle();

	/**
	 * The title for this step.
	 */
	public String getDescription();

	/**
	 * A name for the type of options we are dealing with.
	 */
	public String getTypeName();

	/**
	 * A list of options to select from.
	 */
	ListHolder<T> getOptions();

	public List<TypedHolder<T>> getSlots();
}

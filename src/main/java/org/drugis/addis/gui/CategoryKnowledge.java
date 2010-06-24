package org.drugis.addis.gui;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;

import com.jgoodies.binding.value.ValueModel;


public interface CategoryKnowledge {
	
	/**
	 * Get the type name (singular) of entities in this category.
	 */
	public String getSingular();
	
	/**
	 * Get the type name (plural) of entities in this category.
	 */
	public String getPlural();

	/**
	 * Get the filename for the entity icon.
	 */
	public String getIconName();
	
	/**
	 * Get the filename for the entity "new" icon.
	 */
	public String getNewIconName();

	/**
	 * Get the entity mnemonic (keyboard shortcut).
	 */
	public char getMnemonic();
	
	public JDialog getAddDialog(Main main, Domain domain, ValueModel selectionModel);
}

package org.drugis.addis.gui;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.common.gui.ViewBuilder;

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
	
	/**
	 * Builds the creation dialog for this category.
	 */
	public JDialog getAddDialog(Main main, Domain domain, ValueModel selectionModel);
	
	/**
	 * Whether the category should get a toolbar button.
	 */
	public boolean isToolbarCategory();
	
	/**
	 * What to show for the category
	 */
	public ViewBuilder getCategoryViewBuilder(Main main, Domain domain);
	
	/**
	 * What to show for a specific entity 
	 */
	public ViewBuilder getEntityViewBuilder(Main main, Domain domain, Entity entity);
}

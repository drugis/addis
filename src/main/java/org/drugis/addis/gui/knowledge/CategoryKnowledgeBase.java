package org.drugis.addis.gui.knowledge;

import javax.swing.JDialog;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.gui.CategoryKnowledge;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.ValueModel;

public abstract class CategoryKnowledgeBase implements CategoryKnowledge {
	private final String d_singular;
	private final String d_plural;
	private final String d_iconName;
	
	public CategoryKnowledgeBase(String singular, String iconName) {
		this(singular, singular + "s", iconName);
	}
	
	public CategoryKnowledgeBase(String singular, String plural, String iconName) {
		d_singular = singular;
		d_plural = plural;
		d_iconName = iconName;
	}

	public String getPlural() {
		return d_plural;
	}

	public String getSingular() {
		return d_singular;
	}
	
	public String getIconName() {
		return d_iconName;
	}
	
	public String getNewIconName() {
		return d_iconName;
	}
	
	public char getMnemonic() {
		return getSingular().toLowerCase().charAt(0);
	}
	
	public JDialog getAddDialog(Main main, Domain domain, ValueModel selectionModel) {
		return null;
	}
}

package org.drugis.addis.gui.knowledge;

import java.util.Arrays;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.gui.CategoryKnowledge;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.builder.EntitiesNodeView;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.common.gui.ViewBuilder;

public abstract class CategoryKnowledgeBase implements CategoryKnowledge {
	private final String d_singular;
	private final String d_plural;
	private final String d_iconName;
	private final Class<? extends Entity> d_entityClass;
	
	public CategoryKnowledgeBase(String singular, String iconName, Class<? extends Entity> entityClass) {
		this(singular, capitalize(singular) + "s", iconName, entityClass);
	}
	
	public CategoryKnowledgeBase(String singular, String plural, String iconName,
			Class<? extends Entity> entityClass) {
		d_singular = singular;
		d_plural = plural;
		d_iconName = iconName;
		d_entityClass = entityClass;
	}

	public String getPlural() {
		return d_plural;
	}

	public String getSingular() {
		return d_singular;
	}
	
	public String getSingularCapitalized() {
		return capitalize(d_singular);
	}
	
	private static String capitalize(String in) {
		return Character.toUpperCase(in.charAt(0)) + in.substring(1);
	}
	
	public String getIconName() {
		return d_iconName;
	}
	
	public String getNewIconName() {
		return d_iconName;
	}
	
	public char getMnemonic() {
		return getSingularCapitalized().toLowerCase().charAt(0);
	}
	
	public boolean isToolbarCategory() {
		return false;
	}
	
	private ViewBuilder buildEntityTable(ListHolder<? extends Entity> entities,
			String[] formatter, String title, PresentationModelFactory pmf, Main main) {
	
		EntitiesNodeView view = new EntitiesNodeView(
				Arrays.asList(formatter), entities, main, title, pmf);
		return view;
	}
	
	public ViewBuilder getCategoryViewBuilder(Main main, Domain domain) {
		return buildEntityTable(domain.getCategoryContentsModel(domain.getCategory(getEntityClass())),
				getShownProperties(), getPlural(), main.getPresentationModelFactory(), main);
	}
	
	protected Class<? extends Entity> getEntityClass() {
		return d_entityClass;
	}

	protected String[] getShownProperties() {
		return new String[] {};
	}
	
	abstract public ViewBuilder getEntityViewBuilder(Main main, Domain domain,
			final Entity entity);
}

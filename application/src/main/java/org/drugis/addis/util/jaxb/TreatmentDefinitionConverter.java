package org.drugis.addis.util.jaxb;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TreatmentDefinition;

public class TreatmentDefinitionConverter {
	
	public static TreatmentDefinition load(org.drugis.addis.entities.data.TreatmentDefinition t, final Domain domain) {
		TreatmentDefinition td = new TreatmentDefinition();
		for(Object category : t.getRichCategoryOrTrivialCategory()) { 
			if(category instanceof org.drugis.addis.entities.data.TrivialCategory) {
				org.drugis.addis.entities.data.TrivialCategory trivial = (org.drugis.addis.entities.data.TrivialCategory) category; 
				td.getContents().add(Category.createTrivial(JAXBConvertor.findNamedItem(domain.getDrugs(), trivial.getDrug())));
			} else if(category instanceof org.drugis.addis.entities.data.TreatmentCategoryRef) {
				final org.drugis.addis.entities.data.TreatmentCategoryRef rich = (org.drugis.addis.entities.data.TreatmentCategoryRef) category; 
				TreatmentCategorization tc = CollectionUtils.find(domain.getTreatmentCategorizations(), new Predicate<TreatmentCategorization>() {
					public boolean evaluate(TreatmentCategorization object) {
						Drug drug = JAXBConvertor.findNamedItem(domain.getDrugs(), rich.getDrug());
						return object.getName().equals(rich.getName()) && object.getDrug().equals(drug);
					}
				});
				Category c = CollectionUtils.find(tc.getCategories(), new Predicate<Category>() {
					public boolean evaluate(Category object) {
						return object.getName().equals(rich.getCategoryName());
					}
				});
				td.getContents().add(c);
			}
		}
		return td;
	}
	
	
	public static org.drugis.addis.entities.data.TreatmentDefinition save(TreatmentDefinition t) {
		org.drugis.addis.entities.data.TreatmentDefinition td = new org.drugis.addis.entities.data.TreatmentDefinition();
		for(Category category : t.getContents()) {
			if(category.isTrivial()) { 
				org.drugis.addis.entities.data.TrivialCategory trivial = new org.drugis.addis.entities.data.TrivialCategory();
				trivial.setDrug(category.getDrug().getName());
				td.getRichCategoryOrTrivialCategory().add(trivial);
			}
			else {
				org.drugis.addis.entities.data.TreatmentCategoryRef rich = new org.drugis.addis.entities.data.TreatmentCategoryRef();
				rich.setDrug(category.getDrug().getName());
				rich.setName(category.getCategorization().getName());
				rich.setCategoryName(category.getName());
				td.getRichCategoryOrTrivialCategory().add(rich);
			}
		}
		return td; 
	}
}

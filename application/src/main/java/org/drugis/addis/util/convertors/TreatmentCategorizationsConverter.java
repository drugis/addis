package org.drugis.addis.util.convertors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.UnknownDose;
import org.drugis.addis.entities.data.Edge;
import org.drugis.addis.entities.data.Node;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.DecisionTree;
import org.drugis.addis.entities.treatment.DecisionTreeEdge;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.DoseQuantityChoiceNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.TypeEdge;
import org.drugis.addis.util.JAXBConvertor;
import org.drugis.addis.util.JAXBConvertor.ConversionException;
import org.drugis.common.EqualsUtil;


public class TreatmentCategorizationsConverter {
	
	static final DualHashBidiMap<String, Class<?>> s_beanClasses;
	static { 
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put(FlexibleDose.class.getSimpleName(), FlexibleDose.class);
		map.put(FixedDose.class.getSimpleName(), FixedDose.class);
		map.put(UnknownDose.class.getSimpleName(), UnknownDose.class);
		s_beanClasses = new DualHashBidiMap<String, Class<?>>(Collections.unmodifiableMap(map));
	}

	public static TreatmentCategorization load(org.drugis.addis.entities.data.TreatmentCategorization t, Domain domain) throws ConversionException {
		String name = t.getName();
		Drug drug = JAXBConvertor.findNamedItem(domain.getDrugs(), t.getDrug().getName());
		DoseUnit unit = JAXBConvertor.convertDoseUnit(t.getUnit(), domain);
		
		TreatmentCategorization tc = new TreatmentCategorization(name, drug, unit, false);
		
		for (org.drugis.addis.entities.data.Category category : t.getCategory()) {
			tc.addCategory(new Category(category.getName()));
		}
		
		convertDecisionTree(t.getDecisionTree(), tc.getDecisionTree(), t, tc, domain);
		
		return tc;
	}

	private static void convertDecisionTree(
			org.drugis.addis.entities.data.DecisionTree src,
			DecisionTree dest,
			org.drugis.addis.entities.data.TreatmentCategorization srcTc, 
			TreatmentCategorization destTc,
			Domain domain) throws ConversionException {
		convertSubtree(dest, destTc, src.getRootNode(), dest.getRoot());
	}

	private static void convertSubtree(DecisionTree dest,
			TreatmentCategorization destTc,
			org.drugis.addis.entities.data.Node srcParent,
			DecisionTreeNode destParent)
			throws ConversionException {
		for (Edge e : srcParent.getEdge()) {
			DecisionTreeEdge edge = convertEdge(e);
			DecisionTreeNode child = convertNode(e.getNode(), destTc);
			dest.addChild(edge, destParent, child);
			convertSubtree(dest, destTc, e.getNode(), child);
		}
	}

	private static DecisionTreeNode convertNode(
			Node node, 
			TreatmentCategorization destTc) throws ConversionException {
		if (node instanceof org.drugis.addis.entities.data.ChoiceNode) {
			org.drugis.addis.entities.data.ChoiceNode choiceNode = (org.drugis.addis.entities.data.ChoiceNode) node;
			return new DoseQuantityChoiceNode(s_beanClasses.get(choiceNode.getBeanClass()), choiceNode.getPropertyName(), destTc.getDoseUnit());
		} else if (node instanceof org.drugis.addis.entities.data.LeafNode) {
			org.drugis.addis.entities.data.LeafNode leafNode = (org.drugis.addis.entities.data.LeafNode) node;
			Category destCat = null; 
			for(Category cat : destTc.getCategories()) { 
				 if(leafNode.getCategory() != null && EqualsUtil.equal(cat.getName(), leafNode.getCategory().getName())) { 
					 destCat = cat;
				 }
			}
			return new LeafNode(destCat); // if destCat is null, the LeafNode equals to an ExcludeNode
		}
		throw new ConversionException("Illegal node type (" + node.getClass() + ")");
	}

	private static DecisionTreeEdge convertEdge(Edge e) throws ConversionException {
		if (e instanceof org.drugis.addis.entities.data.RangeEdge) {
			org.drugis.addis.entities.data.RangeEdge rangeEdge = (org.drugis.addis.entities.data.RangeEdge) e;
			return new RangeEdge(
					rangeEdge.getRangeLowerBound(),
					rangeEdge.isIsRangeLowerBoundOpen(),
					rangeEdge.getRangeUpperBound(),
					rangeEdge.isIsRangeUpperBoundOpen());
		} else if (e instanceof org.drugis.addis.entities.data.TypeEdge) {
			org.drugis.addis.entities.data.TypeEdge typeEdge = (org.drugis.addis.entities.data.TypeEdge) e;
			return new TypeEdge(s_beanClasses.get(typeEdge.getBeanClass()));
		}
		throw new ConversionException("Illegal edge type (" + e.getClass() + ")");
	}

	public static org.drugis.addis.entities.data.TreatmentCategorization save(TreatmentCategorization t) {
		org.drugis.addis.entities.data.TreatmentCategorization tc = new org.drugis.addis.entities.data.TreatmentCategorization();
		tc.setName(t.getName());
		tc.setDrug(JAXBConvertor.nameReference(t.getDrug().getName()));
		tc.setUnit(JAXBConvertor.convertDoseUnit(t.getDoseUnit()));
		for (Category category : t.getCategories()) {
			tc.getCategory().add(convertCategory(category));
		}
		tc.setDecisionTree(convertDecisionTree(t.getDecisionTree()));
		return tc;
	}

	private static org.drugis.addis.entities.data.Category convertCategory(Category category) {
		org.drugis.addis.entities.data.Category c = new org.drugis.addis.entities.data.Category();
		c.setName(category.getName());
		return c;
	}

	private static org.drugis.addis.entities.data.DecisionTree convertDecisionTree(DecisionTree tree) {
		org.drugis.addis.entities.data.DecisionTree t = new org.drugis.addis.entities.data.DecisionTree();
		t.setRootNode((org.drugis.addis.entities.data.ChoiceNode)convertNodes(tree.getRoot(), tree));
		return t;
	}

	private static org.drugis.addis.entities.data.Node convertNodes(DecisionTreeNode parent, DecisionTree tree) {
		org.drugis.addis.entities.data.Node node = convertNode(parent);
		List<DecisionTreeEdge> outEdges = new ArrayList<DecisionTreeEdge>(tree.getOutEdges(parent));
		Collections.sort(outEdges, new DecisionTreeEdgeComparator());
		for(DecisionTreeEdge e : outEdges) { 
			node.getEdge().add(convertEdge(tree, e));
		}
		return node;
	}

	private static org.drugis.addis.entities.data.Edge convertEdge(DecisionTree tree, DecisionTreeEdge edge) {
		if(edge instanceof RangeEdge) { 
			RangeEdge rangeEdge = (RangeEdge) edge;
			org.drugis.addis.entities.data.RangeEdge e = new org.drugis.addis.entities.data.RangeEdge();
			e.setRangeLowerBound(rangeEdge.getLowerBound());
			e.setIsRangeLowerBoundOpen(rangeEdge.isLowerBoundOpen());
			e.setRangeUpperBound(rangeEdge.getUpperBound());
			e.setIsRangeUpperBoundOpen(rangeEdge.isUpperBoundOpen());
			e.setNode(convertNodes(tree.getEdgeTarget(edge), tree));
			return e; 
		} else if (edge instanceof TypeEdge) { 
			TypeEdge typeEdge = (TypeEdge) edge;
			org.drugis.addis.entities.data.TypeEdge e = new org.drugis.addis.entities.data.TypeEdge();
			e.setBeanClass(s_beanClasses.getKey(typeEdge.getType()));
			e.setNode(convertNodes(tree.getEdgeTarget(edge), tree));
			return e;
		}
		throw new IllegalArgumentException("Cannot convert " + edge + " to XML, its type " + edge.getClass() + " is incompatible");
	}

	private static org.drugis.addis.entities.data.Node convertNode(DecisionTreeNode node) {
		if(node instanceof ChoiceNode) {
			ChoiceNode choiceNode = (ChoiceNode) node; 
			org.drugis.addis.entities.data.ChoiceNode n = new org.drugis.addis.entities.data.ChoiceNode();
			n.setBeanClass(s_beanClasses.getKey(choiceNode.getBeanClass()));
			n.setPropertyName(choiceNode.getPropertyName());
			return n; 
		} else if (node instanceof LeafNode) {
			LeafNode leafNode = (LeafNode) node; 
			org.drugis.addis.entities.data.LeafNode n = new org.drugis.addis.entities.data.LeafNode();
			if(leafNode.getCategory() != null) { 
				n.setCategory(JAXBConvertor.nameReference(leafNode.getName()));
			}
			return n;
		}
		throw new IllegalArgumentException("Cannot convert " + node + " to XML, its type " + node.getClass() + " is incompatible");
	}
}

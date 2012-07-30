package org.drugis.addis.gui.renderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.drugis.addis.entities.treatment.ChoiceNode;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.presentation.wizard.DosedDrugTreatmentWizardPresentation.CategorySpecifiers;

public class CategoryComboboxRenderer implements ListCellRenderer {
	private boolean d_alternate;
	private ListCellRenderer d_defaultRenderer;

	public CategoryComboboxRenderer(boolean hasPrevious) {
		d_defaultRenderer = new JComboBox().getRenderer();
		d_alternate = hasPrevious; 
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		// Pre-processing 
		boolean postprocess = false;
		if(value instanceof ChoiceNode) {
			postprocess = true;
			String property = StringUtils.lowerCase(value.toString());
			if(d_alternate) {
				value = "Consider " + property;
			} else { 
				value = "Consider " + property + " first";
			}
		}
		
		Component c = d_defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		//Post-processing 
		if(	postprocess
			|| value instanceof CategorySpecifiers 
			|| (value instanceof LeafNode && value.toString().equals(LeafNode.NAME_EXCLUDE))) {
			c.setFont(c.getFont().deriveFont(Font.BOLD));
		}
		return c;
	} 
	
}
package org.drugis.addis.presentation;

import java.util.SortedSet;

import org.apache.commons.math.random.ValueServer;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Indication;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation {
	
	@SuppressWarnings("serial")
	private class IndicationHolder extends AbstractValueModel {

		private Indication d_indication = null;
		
		public Object getValue() {
			return d_indication;
		}

		public void setValue(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
			
			Indication oldValue = d_indication;
			d_indication = (Indication) newValue;
				
			fireValueChange(oldValue, d_indication);
		}
		
	}
	private Domain d_domain;
	private IndicationHolder d_valueHolder;
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_valueHolder = new IndicationHolder();		
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_valueHolder; 
	}
	
}

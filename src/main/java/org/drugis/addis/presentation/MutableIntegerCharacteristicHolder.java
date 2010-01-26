package org.drugis.addis.presentation;


import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Study;

	@SuppressWarnings("serial")
	public class MutableIntegerCharacteristicHolder extends MutableCharacteristicHolder {

		public MutableIntegerCharacteristicHolder(Study bean,
				BasicStudyCharacteristic characteristic) {
			super(bean, characteristic);
		}
		
		public void setValue(String value) {
				super.setValue(Integer.parseInt((String) value)); 
		}
		
		@Override
		public String getValue() {
			return super.getValue().toString();
		}
		
	}
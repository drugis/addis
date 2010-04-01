package org.drugis.addis.util;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class EnumXMLFormat<T extends Enum<T>>  extends XMLFormat<T> {
	
	@SuppressWarnings("unchecked")
	public EnumXMLFormat(){
		super((Class<T>) Enum.class);
	}
	
	public T newInstance(Class<T> enumClass, InputElement ie) throws XMLStreamException {
		String selectedOption = ie.getAttribute(enumClass.getSimpleName()).toString();
		
		for (T curOption : enumClass.getEnumConstants())
			if (selectedOption.equals(curOption.toString()))
				return curOption;
		return null;
	}
	public boolean isReferenceable() {
		return false;
	}
	public void read(InputElement ie, T enumInstance) throws XMLStreamException {
	}
	
	public void write(T enumInstance, OutputElement oe) throws XMLStreamException {
		oe.setAttribute(enumInstance.getClass().getSimpleName() ,enumInstance.toString());
	}
}

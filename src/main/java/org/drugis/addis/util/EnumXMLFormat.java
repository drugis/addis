package org.drugis.addis.util;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

@SuppressWarnings("unchecked")
public class EnumXMLFormat<T extends Enum>  extends XMLFormat<T> {
	
	public EnumXMLFormat(Class<T> c){
		super( c);
	}
	
	public T newInstance(Class<T> enumClass, InputElement ie) throws XMLStreamException {
		String selectedOption = ie.getAttribute("value").toString();
		
		return (T) Enum.valueOf(enumClass, selectedOption);
	}
	public boolean isReferenceable() {
		return false;
	}
	public void read(InputElement ie, T enumInstance) throws XMLStreamException {
	}
	
	public void write(T enumInstance, OutputElement oe) throws XMLStreamException {
		oe.setAttribute("value" ,enumInstance.name());
	}
}

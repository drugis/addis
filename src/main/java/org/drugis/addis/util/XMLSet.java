package org.drugis.addis.util;
import java.util.ArrayList;
import java.util.Collection;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


public class XMLSet<T> {
	private Collection<T> d_set;
	private String d_typeName;
	
	
	public XMLSet(Collection<T> set, String typeName) {
		d_set = set;
		d_typeName = typeName;
	}
	
	public Collection<T> getSet() {
		return d_set;
	}
	
	public String getTypeName() {
		return d_typeName;
	}
	
	@SuppressWarnings("unused")
	private static final XMLFormat<XMLSet> XML = new XMLFormat<XMLSet>(XMLSet.class) {		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		@Override
		public XMLSet newInstance(Class<XMLSet> cls, InputElement ie) throws XMLStreamException {
			//System.out.println("XMLSet::newInstance");
			return new XMLSet(new ArrayList(),"unknown"); // FIXME
		}
		
		@Override
		public void read(InputElement ie, XMLSet wrappedSet) throws XMLStreamException {
			//System.out.println("XMLSet::read");
			while (ie.hasNext()) {
				wrappedSet.getSet().add(ie.getNext());
			}
		}
		
		@Override
		public void write(XMLSet wrappedSet, OutputElement oe) throws XMLStreamException {
			//System.out.println("XMLSet::XMLFormat::write " + wrappedSet.getSet());
			for (Object o : wrappedSet.getSet()) {
				oe.add(o);
			}
		
		}		
	};				
}

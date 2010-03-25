package org.drugis.addis.util;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Entity;


public class XMLSet<T extends Entity> {
	private Set<T> d_set;
	private String d_typeName;
	
	
	public XMLSet(Set<T> set, String typeName) {
		d_set = set;
		d_typeName = typeName;
	}
	
	public Set<T> getSet() {
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
			return new XMLSet(new TreeSet(),"unknown");
		}
		
		@Override
		public void read(InputElement ie, XMLSet wrappedSet) throws XMLStreamException {
			//System.out.println("XMLSet::read");
			while (ie.hasNext()) {
				wrappedSet.getSet().add(ie.<Entity>getNext());
			}
		}
		@Override
		public void write(XMLSet wrappedSet, OutputElement oe) throws XMLStreamException {
			//System.out.println("XMLSet::XMLFormat::write " + wrappedSet.getSet());
			for (Object o : wrappedSet.getSet()) {
				Entity e = (Entity) o;
//				System.out.println(e);
				Type t = e.getClass();
				oe.add(e,wrappedSet.getTypeName());
			}
		
		}		
	};				
}

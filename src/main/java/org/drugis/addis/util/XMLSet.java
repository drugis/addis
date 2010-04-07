package org.drugis.addis.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class XMLSet<T> {
	private Collection<T> d_list;
	private String d_typeName;
	
	public XMLSet(Collection<T> list, String typeName) {
		d_list = list;
		d_typeName = typeName;
	}
	
	public List<T> getList() {
		return (List<T>) d_list;
	}
	
	public Collection<T> getCollection() {
		return d_list;
	}
	
	public Set<T> getSet() {
		return new TreeSet<T>(d_list);
	}
	
	public String getTypeName() {
		return d_typeName;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	private static final XMLFormat<XMLSet> XML = new XMLFormat<XMLSet>(XMLSet.class) {		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		@Override
		public XMLSet newInstance(Class<XMLSet> cls, InputElement ie) throws XMLStreamException {
			//System.out.println("XMLSet::newInstance");
			System.out.print("");
			if(cls==null)
				System.out.println("");;
			return new XMLSet(new ArrayList(),"unknown"); // FIXME
		}
		
		@Override
		public void read(InputElement ie, XMLSet wrappedSet) throws XMLStreamException {
			//System.out.println("XMLSet::read");
			while (ie.hasNext()) {
				wrappedSet.getList().add(ie.getNext());
			}
		}
		
		@Override
		public void write(XMLSet wrappedSet, OutputElement oe) throws XMLStreamException {
			//System.out.println("XMLSet::XMLFormat::write " + wrappedSet.getSet());
			for (Object o : wrappedSet.getCollection()) {
				oe.add(o);
			}
		}		
	};				
}

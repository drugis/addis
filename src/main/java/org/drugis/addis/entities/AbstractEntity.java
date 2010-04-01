package org.drugis.addis.entities;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLSet;
import org.drugis.common.ObserverManager;

import com.jgoodies.binding.beans.BeanUtils;

public abstract class AbstractEntity implements Entity, Serializable {
	private static final long serialVersionUID = -3889001536692466540L;
	
	transient private ObserverManager d_om;
	
	public AbstractEntity() {
		init();
	}
	
	protected void init() {
		d_om = new ObserverManager(this);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		init();
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		d_om.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		d_om.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		d_om.removePropertyChangeListener(listener);
	}
	
	public abstract Set<? extends Entity> getDependencies();
	
	public String[] getXmlExclusions() {
		return null;
	}
	
	protected static final XMLFormat<Entity> XML = new XMLFormat<Entity>(Entity.class) {
		
		@Override
		public Entity newInstance(Class<Entity> cls, InputElement ie) throws XMLStreamException {
			try {
				System.out.println("Entities::AbstractEntity::XMLFormat->  trying to run empty constructor for: "+cls);
				return (Entity) cls.getConstructors()[0].newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}
		@Override
		public boolean isReferenceable() {
			return true;
		}
		
		@Override
		public void read(InputElement ie, Entity i) throws XMLStreamException {
			System.out.println("\nstarting reading");
			PropertyDescriptor[] properties = null;
			try {
				properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();
				System.out.println("reading properties");
				for(int p = 0; p < properties.length; ++p){
					if (propertyIsExcluded(i, properties[p].getName()))
						continue;
					System.out.print("AbstractEntity::XMLFormat: inspecting " + properties[p].getName() + ", class is " + properties[p].getPropertyType());	
					if (properties[p].getPropertyType().equals(String.class)) {
						System.out.println(" as String");
						BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), null));
					} else if (properties[p].getPropertyType().equals(Long.class)) {
						System.out.println(" as Long");
						BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), 0l));
					} else if (properties[p].getPropertyType().equals(int.class) || properties[p].getPropertyType().equals(Integer.class)) {
						System.out.println(" as Integer");
						BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), 0));
					} else if (properties[p].getPropertyType().equals(Double.class)) {
						System.out.println(" as Double");
						BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), 0.0));
					}  else{
						System.out.println(" .. didnt read as leaf");
					}

				}
				System.out.println("attribures read, now others");

				for(int p = 0; p < properties.length; ++p){
					if (propertyIsExcluded(i, properties[p].getName()))
						continue;

					System.out.print("AbstractEntity::XMLFormat: inspecting " + properties[p].getName() + ", class is " + properties[p].getPropertyType());

					if (properties[p].getPropertyType().isEnum()) {
						System.out.println(" as Enum of type "+properties[p].getPropertyType());
						BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName(),properties[p].getPropertyType()));
					} else if (Entity.class.isAssignableFrom(properties[p].getPropertyType()) ) {
						System.out.println(" as Entity");
						Object parsedVal = ie.get(properties[p].getName());
						BeanUtils.setValue(i, properties[p], parsedVal);
						System.out.println(parsedVal);
					}	else if (Map.class.isAssignableFrom(properties[p].getPropertyType())) {
						System.out.println(" as Map");
						BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName()));
					} else if (properties[p].getPropertyType().equals(String[].class)) {
						XMLSet<String> xmlSet = ((XMLSet<String>) ie.get(properties[p].getName(),XMLSet.class));
						String[] retrievedVal = new String[xmlSet.getSet().size()];
						int index=0;
						for (Object o : xmlSet.getSet())
							retrievedVal[index++] = ((String) o);		
						System.out.println(" as string array: "+retrievedVal);
						BeanUtils.setValue(i, properties[p], retrievedVal);
					} else if (List.class.isAssignableFrom(properties[p].getPropertyType())) {
						System.out.println(" as List");
						XMLSet xmlSet = ((XMLSet) ie.get(properties[p].getName(),XMLSet.class));
						BeanUtils.setValue(i, properties[p], xmlSet.getSet());
					} else System.out.println(" Didnt read as node");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected boolean propertyIsExcluded(Entity e, String propertyName) {
			if (propertyName.equals("dependencies") || propertyName.equals("class") || propertyName.equals("xmlExclusions"))
				return true;
			
			System.out.println("checking exclusion: "+e.getXmlExclusions());
			if (e.getXmlExclusions() == null)
				return false;
			
			System.out.println("checking exclusion2: "+e.getXmlExclusions());
			for (String s : e.getXmlExclusions()) {
				if (s.equals(propertyName))
					return true;
			}
			return false;
		}
		
		@Override
		public void write(Entity i, OutputElement oe) throws XMLStreamException {
			System.out.println("\nGoing to try to write Entity " + i);				

			try {
				PropertyDescriptor[] properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();
				System.out.println("Starting to write attributes");

				for(int p = 0; p < properties.length; ++p){
					if(propertyIsExcluded(i, properties[p].getName()))
						continue;
					//						if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
					Object value = BeanUtils.getValue(i, properties[p]);
					System.out.print("(attributes) inspecting "+properties[p].getName() + ", value is: " + value + ", class is " + value.getClass());	
					if (! ((value instanceof Enum) || (value instanceof Entity) || (value instanceof String[]) || value instanceof List || value instanceof Set || value instanceof Map) ) {
						System.out.print("  writing ");
						oe.setAttribute(properties[p].getName(), value);
						System.out.println(".. done writing.");
					}
					else System.out.println(" not writing, no attribute ");
					//						}
				}

				System.out.println("done writing attributes, starting to write others");

				for(int p = 0; p < properties.length; ++p){
					if(propertyIsExcluded(i, properties[p].getName()))
						continue;
					Object value = BeanUtils.getValue(i, properties[p]);
					System.out.print("(others) inspecting "+properties[p].getName() + ", value is: " + value + ", class is " + value.getClass());
					if (value instanceof Enum) {
						System.out.println("writing as as Enum "+properties[p].getName());
						//oe.setAttribute(properties[p].getName(),value);
						oe.add( (Enum) value, properties[p].getName(), ((Enum) value).getDeclaringClass());
					} else if (value instanceof Entity){ 
						System.out.println("writing as Entity");
						oe.add(value, properties[p].getName());
					} else if (value instanceof String[]) {// categorical variable
						System.out.println("writing as String[]");
						ArrayList<String> stringList = new ArrayList<String>();
						for (String s : (String[]) value)
							stringList.add(s);
						oe.add(new XMLSet<String>(stringList,""),properties[p].getName(), XMLSet.class);
					} else if (value instanceof List) { // ADE list
						System.out.println("writing as List");
						oe.add(new XMLSet( (List)value, ""),properties[p].getName(), XMLSet.class);
					} else if (value instanceof Set) {
						System.out.println("writing as Set");
						oe.add(new XMLSet( (Set)value, ""),properties[p].getName(), XMLSet.class);
					} else if (value instanceof Map) {
						System.out.println("writing as Map");
						oe.add(value);
					}
					// FIXME: Map
					else System.out.println("Not writing, not known other. (maybe attribute?)");

				}		
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
			System.out.println("<<");
		}
	};
}

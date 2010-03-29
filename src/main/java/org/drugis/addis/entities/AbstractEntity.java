package org.drugis.addis.entities;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

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

			PropertyDescriptor[] properties = null;
			try {
				properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();
				
				for(int p = 0; p < properties.length; ++p){
					if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
//						System.out.println("AbstractEntity::XMLFormat: reading " + properties[p].getName());
						if (properties[p].getPropertyType().equals(String.class)) {
//							System.out.println("as String");
							BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), null));
						} else if (properties[p].getPropertyType().equals(Long.class)) {
//							System.out.println("as Long");
							BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), 0l));
						} else{
//							System.out.println(".. didnt read");
						}
					}
				}
				System.out.println("attribures read, now others");
				
				for(int p = 0; p < properties.length; ++p){
					if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
//						System.out.println("AbstractEntity::XMLFormat: reading " + properties[p].getName());
						if (properties[p].getPropertyType().isEnum()) {
//							System.out.println("as Enum");
							BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName()));
						} else if (properties[p].getPropertyType().equals(Entity.class)) {
//							System.out.println("as Entity");
							BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName()));
						} else {
//							System.out.println("Didnt read");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void write(Entity i, OutputElement oe) throws XMLStreamException {
			try {
				PropertyDescriptor[] properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();

				for(int p = 0; p < properties.length; ++p) {
					if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
						Object value = BeanUtils.getValue(i, properties[p]);
//						System.out.println("Going to write " + value);
						if (! ((value instanceof Enum) || (value instanceof Entity)) ) {
//							System.out.println("As attribute " + value.getClass());
							oe.setAttribute(properties[p].getName(), value);
						}
					}
				}

				for(int p = 0; p < properties.length; ++p){
					if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
						Object value = BeanUtils.getValue(i, properties[p]);
//						System.out.println("Going to write " + value);
						if (value instanceof Enum) {
//							System.out.println("As Enum");
							//oe.setAttribute(properties[p].getName(),value);
							oe.add(value, properties[p].getName());
						} else if (value instanceof Entity){
//							System.out.println("As Object");
							oe.add(value, properties[p].getName());
						}
					}
				}		
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

		}
	};
}

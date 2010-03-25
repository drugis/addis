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

import org.drugis.addis.entities.OutcomeMeasure.Direction;
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
						if (properties[p].getPropertyType().equals(String.class)) {
							BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), null));
						} else if (properties[p].getPropertyType().equals(Long.class)) {
							BeanUtils.setValue(i, properties[p], ie.getAttribute(properties[p].getName(), 0l));
						} else if (properties[p].getPropertyType().equals(Direction.class)) {
							BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName()));
						} else if (properties[p].getPropertyType().equals(Entity.class)) {
							BeanUtils.setValue(i, properties[p], ie.get(properties[p].getName()));
						} else {
							System.err.println("AbstractEntity-> For type "+properties[p].getPropertyType()+" not recognized");
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
						System.out.println("Going to write " + value);
						if (! ((value instanceof Enum) || (value instanceof Entity)) ) {
							System.out.println("As attribute " + value.getClass());
							oe.setAttribute(properties[p].getName(), value);
						}
					}
				}

				for(int p = 0; p < properties.length; ++p){
					if (!(properties[p].getName().equals("class") || properties[p].getName().equals("dependencies"))) {
						Object value = BeanUtils.getValue(i, properties[p]);
						System.out.println("Going to write " + value);
						if (value instanceof Enum) {
							System.out.println("As Enum");
							oe.add(value, properties[p].getName());
						} else if (value instanceof Entity){
							System.out.println("As Object");
							oe.add(value, properties[p].getName());
						}
					}
						//oe.add(BeanUtils.getValue(i, properties[p]), properties[p].getName());
				}		
//				System.out.println("beaninf: "+Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors()[3].getPropertyType());
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

		}
	};
}

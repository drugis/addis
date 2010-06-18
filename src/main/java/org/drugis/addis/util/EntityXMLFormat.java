/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Entity;

import com.jgoodies.binding.beans.BeanUtils;

@SuppressWarnings("unchecked")
public class EntityXMLFormat extends XMLFormat<Entity>
{	
	static final Set<Class> NODES = new HashSet<Class>(); 

	public EntityXMLFormat() {
		super(Entity.class);
		// These classes should not be treated as properties, but as nodes.
		Collections.addAll(NODES, Enum.class, Entity.class, String[].class, List.class, Set.class, Map.class);
	}

	
	@Override
	public Entity newInstance(Class<Entity> cls, InputElement ie) throws XMLStreamException {
		try {
			for (Constructor<?> c : cls.getConstructors()) {
				if (c.getParameterTypes().length == 0) {
					return (Entity)c.newInstance();
				}
			}
			throw new IllegalStateException("No zero-argument constructor for " + cls);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
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
			for(PropertyDescriptor p : properties) {
				if (propertyIsExcluded(i, p.getName()))
					continue;

				if (p.getPropertyType().equals(String.class)) {
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), null));
				} else if (p.getPropertyType().equals(Long.class)) {
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0l));
				} else if (p.getPropertyType().equals(int.class) || p.getPropertyType().equals(Integer.class)) {
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0));
				} else if (p.getPropertyType().equals(Double.class)) {
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0.0));
				}
			}

			for(PropertyDescriptor p : properties){
				
				if (propertyIsExcluded(i, p.getName()))
					continue;

				String propertyName = p.getName();

				if (p.getPropertyType().isEnum()) {
					BeanUtils.setValue(i, p, ie.get(p.getName(),p.getPropertyType()));
				} else if (Entity.class.isAssignableFrom(p.getPropertyType()) ) {
					Object parsedVal = ie.get(p.getName());
					BeanUtils.setValue(i, p, parsedVal);
				}	else if (Map.class.isAssignableFrom(p.getPropertyType())) {
					BeanUtils.setValue(i, p, ie.get(p.getName(), HashMap.class));
				} else if (p.getPropertyType().equals(String[].class)) {
					ArrayList<String> list = ((ArrayList<String>) ie.get(p.getName(),ArrayList.class));
					String[] retrievedVal = new String[list.size()];
					int index=0;
					for (String s : list)
						retrievedVal[index++] = s;		
					BeanUtils.setValue(i, p, retrievedVal);
				} else if (List.class.isAssignableFrom(p.getPropertyType())) {
					List retrList = ie.get(propertyName,ArrayList.class);
					if (retrList != null)
						BeanUtils.setValue(i, p, retrList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean propertyIsExcluded(Entity e, String propertyName) {
		if (propertyName.equals("dependencies") || propertyName.equals("class") || propertyName.equals("xmlExclusions"))
			return true;

		if (e.getXmlExclusions() == null)
			return false;

		for (String s : e.getXmlExclusions()) {
			if (s.equals(propertyName))
				return true;
		}
		return false;
	}
	
	private boolean classIsNode(Class clsToCompare) {
		boolean isNode = false;
		for (Class<?> nodeCls : NODES) {
			if (nodeCls.isAssignableFrom(clsToCompare))
				isNode = true;
		}
		return isNode;			
	}

	@Override
	public void write(Entity i, OutputElement oe) throws XMLStreamException {
		try {
			PropertyDescriptor[] properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();
			
			for(int p = 0; p < properties.length; ++p){
				
				if(propertyIsExcluded(i, properties[p].getName()))
					continue;

				Object value = BeanUtils.getValue(i, properties[p]);
				if (value != null && !classIsNode(value.getClass())) {
					oe.setAttribute(properties[p].getName(), value);
				}
			}

			for(int p = 0; p < properties.length; ++p){
				
				String propertyName = properties[p].getName();
				if(propertyIsExcluded(i, propertyName))
					continue;

				Object value = BeanUtils.getValue(i, properties[p]);
				
				if (value instanceof Enum) {
					oe.add( (Enum) value, properties[p].getName(), ((Enum) value).getDeclaringClass());
				} else if (value instanceof Entity){ 
					oe.add(value, properties[p].getName());
				} else if (value instanceof String[]) {// categorical variable
					ArrayList<String> stringList = new ArrayList<String>();
					for (String s : (String[]) value)
						stringList.add(s);
					oe.add(stringList,properties[p].getName(), ArrayList.class);
				} else if (value instanceof List) { // ADE list
					oe.add(new ArrayList<Object>((List) value), propertyName, ArrayList.class);
				} else if (value instanceof HashMap) {
					oe.add((HashMap)value, properties[p].getName(), HashMap.class);
				}
			}		
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}
}
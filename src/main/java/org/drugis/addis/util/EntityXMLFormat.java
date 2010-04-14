package org.drugis.addis.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
			for(PropertyDescriptor p : properties) {
				if (propertyIsExcluded(i, p.getName()))
					continue;

				System.out.print("AbstractEntity::XMLFormat: inspecting " + p.getName() + ", class is " + p.getPropertyType());	
				if (p.getPropertyType().equals(String.class)) {
					System.out.println(" as String");
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), null));
				} else if (p.getPropertyType().equals(Long.class)) {
					System.out.println(" as Long");
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0l));
				} else if (p.getPropertyType().equals(int.class) || p.getPropertyType().equals(Integer.class)) {
					System.out.println(" as Integer");
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0));
				} else if (p.getPropertyType().equals(Double.class)) {
					System.out.println(" as Double");
					BeanUtils.setValue(i, p, ie.getAttribute(p.getName(), 0.0));
				}  else{
					System.out.println(" .. didnt read as leaf");
				}

			}
			System.out.println("attribures read, now others");

			for(PropertyDescriptor p : properties){
				if (propertyIsExcluded(i, p.getName()))
					continue;

				// This is an unfortunate bugfix for the javolution use of the keyword "end....."
				String propertyName = p.getName();
				if(propertyName.equals("endpoints"))
					propertyName = "results";

				System.out.print("AbstractEntity::XMLFormat: inspecting " + p.getName() + ", class is " + p.getPropertyType());

				if (p.getPropertyType().isEnum()) {
					System.out.println(" as Enum of type "+p.getPropertyType());
					BeanUtils.setValue(i, p, ie.get(p.getName(),p.getPropertyType()));
				} else if (Entity.class.isAssignableFrom(p.getPropertyType()) ) {
					System.out.println(" as Entity");
					Object parsedVal = ie.get(p.getName());
					BeanUtils.setValue(i, p, parsedVal);
					System.out.println(parsedVal);
				}	else if (Map.class.isAssignableFrom(p.getPropertyType())) {
					System.out.println(" as Map");
					BeanUtils.setValue(i, p, ie.get(p.getName(), HashMap.class));
				} else if (p.getPropertyType().equals(String[].class)) {
					XMLSet<String> xmlSet = ((XMLSet<String>) ie.get(p.getName(),XMLSet.class));
					String[] retrievedVal = new String[xmlSet.getList().size()];
					int index=0;
					for (Object o : xmlSet.getList())
						retrievedVal[index++] = ((String) o);		
					System.out.println(" as string array: "+retrievedVal);
					BeanUtils.setValue(i, p, retrievedVal);
				} else if (List.class.isAssignableFrom(p.getPropertyType())) {
					List retrList = ie.get(propertyName,ArrayList.class);
					BeanUtils.setValue(i, p, retrList);
				} else System.out.println(" Didnt read as node");

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
		System.out.println("\nGoing to try to write Entity " + i);				

		try {
			PropertyDescriptor[] properties = Introspector.getBeanInfo(i.getClass()).getPropertyDescriptors();
			System.out.println("Starting to write attributes");

			for(int p = 0; p < properties.length; ++p){
				if(propertyIsExcluded(i, properties[p].getName()))
					continue;

				Object value = BeanUtils.getValue(i, properties[p]);
				System.out.print("(attributes) inspecting "+properties[p].getName() + ", value is: " + value + ", class is " + value.getClass());	
				if (!classIsNode(value.getClass())) {
					System.out.print("  writing ");
					oe.setAttribute(properties[p].getName(), value);
					System.out.println(".. done writing.");
				}
				else System.out.println(" not writing, no attribute ");
			}

			System.out.println("done writing attributes, starting to write others");

			for(int p = 0; p < properties.length; ++p){
				if(propertyIsExcluded(i, properties[p].getName()))
					continue;

				// This is an unfortunate bugfix for the javolution use of the keyword "end....."
				String propertyName = properties[p].getName();
				if(propertyName.equals("endpoints"))
					propertyName = "results";

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
					oe.add(new XMLSet<String>(stringList),properties[p].getName(), XMLSet.class);
				} else if (value instanceof List) { // ADE list
					System.out.println("writing "+propertyName+" as List");
					oe.add(new ArrayList<Object>((List) value), propertyName, ArrayList.class);
				} else if (value instanceof Set) {
					System.out.println("writing as Set");
					oe.add(new XMLSet( (Set)value),properties[p].getName(), XMLSet.class);
				} else if (value instanceof Map) {
					System.out.println("writing as Map");
					oe.add((HashMap) value, properties[p].getName(), HashMap.class);
				}
				else System.out.println("Not writing, not known other. (maybe attribute?)");
			}		
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}
}
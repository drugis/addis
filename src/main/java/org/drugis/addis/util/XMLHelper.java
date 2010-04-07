package org.drugis.addis.util;

import java.io.InputStream;
import java.io.StringReader;

import org.drugis.addis.entities.DomainData;

import javolution.io.AppendableWriter;
import javolution.text.TextBuilder;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.XMLReferenceResolver;
import javolution.xml.stream.XMLStreamException;

public class XMLHelper {

	public static <T> String toXml(T obj, Class<T> cls) throws XMLStreamException {	     
	    TextBuilder xml = TextBuilder.newInstance();
	    AppendableWriter out = new AppendableWriter().setOutput(xml);
		XMLObjectWriter writer = new XMLObjectWriter().setOutput(out).setBinding(new AddisBinding());
		writer.setReferenceResolver(new XMLReferenceResolver());		
		writer.setIndentation("\t");
		if (cls.equals(DomainData.class))
			writer.write(obj, "addis-data", cls);
		else
			writer.write(obj, cls.getCanonicalName(), cls);
		writer.close();
		return xml.toString();
	}

	public static <T> T fromXml(String xml) throws XMLStreamException {	     
		StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(sreader).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.<T>read();
	}
	
	public static <T> T fromXml(InputStream xmlStream) throws XMLStreamException {	     
		//StringReader sreader = new StringReader(xml);
		XMLObjectReader reader = new XMLObjectReader().setInput(xmlStream).setBinding(new AddisBinding());
		reader.setReferenceResolver(new XMLReferenceResolver());
		return reader.<T>read();
	}

}

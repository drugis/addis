/**
 * 
 */
package org.drugis.addis.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Study;

@SuppressWarnings("unchecked")
public final class HashMapXMLFormat extends XMLFormat<HashMap> {
	public HashMapXMLFormat() {
		super(HashMap.class);
	}

	@Override
	public boolean isReferenceable() {
		return false;
	}


	@Override
	public void read(InputElement ie, HashMap s) throws XMLStreamException {

		while (ie.hasNext()) {
			Entry<Study.MeasurementKey,Measurement> measEntry = ie.get("measurement", Entry.class);
			if(measEntry!= null)
				s.put(measEntry.getKey(), measEntry.getValue());
			Entry<Object, Note> noteEntry = ie.get("note", Entry.class);
			if(noteEntry != null)
				s.put(noteEntry.getKey(), noteEntry.getValue());
		}
	}

	@Override
	public void write(HashMap map, OutputElement oe) throws XMLStreamException {

		if (map.entrySet().isEmpty())
			return;

		Iterator iterator = map.entrySet().iterator();
		Entry value = (Entry) iterator.next();

		if (value.getValue() instanceof Measurement) {
			Map<Study.MeasurementKey, Measurement> measurementMap = (Map<Study.MeasurementKey, Measurement>) map;
			for(Map.Entry<Study.MeasurementKey, Measurement> e: measurementMap.entrySet()){
				oe.add(e,"measurement",Entry.class);
			}
		} else if (value.getValue() instanceof Note){
			Map<Object, Note> noteMap = (Map<Object, Note>) map;
			for(Map.Entry<Object, Note> e: noteMap.entrySet()){
				oe.add(e,"note",Entry.class);
			}
		}
	}
}
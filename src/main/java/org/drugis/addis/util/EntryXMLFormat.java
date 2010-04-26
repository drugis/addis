/**
 * 
 */
package org.drugis.addis.util;

import java.util.Map.Entry;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
@SuppressWarnings("unchecked")
public final class EntryXMLFormat extends XMLFormat<Entry> {
	public EntryXMLFormat() {
		super(Entry.class);
	}

	class MyEntry implements Entry {
		Object key, value;	

		public Object setKey(Object key) {
			this.key = key;
			return key;
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public Object setValue(Object value) {
			this.value = value;
			return value;
		}
	}

	public Entry newInstance(Class<Entry> cls, InputElement ie) throws XMLStreamException {
		return new MyEntry();
	}

	@Override
	public void read(javolution.xml.XMLFormat.InputElement ie, Entry entry)
	throws XMLStreamException {
		
		Object key = ie.get("key");
		if (key != null) {
			String text = ie.get("noteText", String.class);
			Source src = ie.get("noteSrc", Source.class);
			((MyEntry) entry).setKey(key);
			((MyEntry) entry).setValue(new Note(src, text));
		} else {
			Entity outcomeMeasure = (Entity) ie.get("outcomeMeasure");
			Arm arm = (Arm) ie.get("arm",Arm.class);
			Measurement measurement = ie.get("measurement");

			Study.MeasurementKey mk = new Study.MeasurementKey(outcomeMeasure, arm);
			((MyEntry) entry).setKey(mk);
			((MyEntry) entry).setValue(measurement);
		}
	}

	public void write(Entry e,
			javolution.xml.XMLFormat.OutputElement oe)
	throws XMLStreamException {

		if (e.getValue() instanceof Measurement) {
			Entry<Study.MeasurementKey, Measurement> entry = (Entry<Study.MeasurementKey, Measurement>) e;
			oe.add((OutcomeMeasure) entry.getKey().getOutcomeM(), "outcomeMeasure");
			oe.add(entry.getKey().getArm(), "arm", Arm.class);
			oe.add(entry.getValue(), "measurement");
		} else if (e.getValue() instanceof Note){
			oe.add(e.getKey(),"key");
			oe.add( ((Note) e.getValue()).getText(), "noteText", String.class );
			oe.add( ((Note) e.getValue()).getSource(), "noteSrc", Source.class );
		}
	}
}
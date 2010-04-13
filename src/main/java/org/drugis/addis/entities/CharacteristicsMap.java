package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class CharacteristicsMap extends MapBean<Characteristic, Object> {
	private static final long serialVersionUID = -6003644367870072126L;
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	protected static XMLFormat<CharacteristicsMap> XMLMap = new XMLFormat<CharacteristicsMap>(CharacteristicsMap.class) {
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(InputElement xml,	CharacteristicsMap obj) throws XMLStreamException {
			for(BasicStudyCharacteristic c : BasicStudyCharacteristic.values()){
				Object value = xml.get(c.toString(),c.getValueType());
				if(value != null)
					obj.put(c, value );	
			}
		}

		@Override
		public void write(CharacteristicsMap map, OutputElement xml) throws XMLStreamException {	
			for(BasicStudyCharacteristic c : BasicStudyCharacteristic.values())
					xml.add( map.get(c), c.toString());
		}
	};	
}

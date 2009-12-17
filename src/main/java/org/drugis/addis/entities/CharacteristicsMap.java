package org.drugis.addis.entities;

import java.util.Collections;
import java.util.Set;

public class CharacteristicsMap extends MapBean<Characteristic, Object> {
	private static final long serialVersionUID = -6003644367870072126L;
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
}

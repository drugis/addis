package nl.rug.escher.addis.entities;

import java.util.Set;

public interface Entity {
	public Set<Entity> getDependencies();
}

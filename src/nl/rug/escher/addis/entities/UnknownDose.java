package nl.rug.escher.addis.entities;

public class UnknownDose extends Dose {
	private static final long serialVersionUID = -1264950512657687181L;
	
	@Override
	public void setQuantity(Double q) {
	}
	
	@Override
	public void setUnit(SIUnit u) {
	}

	@Override
	public String toString() {
		return "Unknown Dose";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof UnknownDose) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
}

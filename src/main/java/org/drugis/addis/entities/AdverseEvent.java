package org.drugis.addis.entities;

public class AdverseEvent extends AbstractVariable implements OutcomeMeasure {
	private static final long serialVersionUID = -1026622949185265860L;

	public AdverseEvent(String name, Variable.Type type) {
		super(name, type);
	}

	public Direction getDirection() {
		return Direction.LOWER_IS_BETTER;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AdverseEvent) {
			return super.equals(o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (d_name != null) {
			return d_name.hashCode() + 7;
		}
		return 0;
	}
}
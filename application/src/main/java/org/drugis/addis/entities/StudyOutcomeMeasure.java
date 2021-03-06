/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.entities;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.EqualsUtil;
import org.drugis.common.beans.GuardedObservableList;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

@SuppressWarnings("serial")
public class StudyOutcomeMeasure<T extends Variable> extends ObjectWithNotes<T> {
	public class EmptyVariable extends AbstractVariable {
		private T d_dummy;

		@SuppressWarnings("unchecked")
		protected EmptyVariable(String name, VariableType type) {
			super(name, type);
			try {
				d_dummy = (T) d_class.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException("Cannot create dummy variable for StudyOutcomeMeasure, unrecognized type " + d_class);
			}
		}

		T getDummy() {
			return d_dummy;
		}
	}

	public static final String PROPERTY_IS_PRIMARY = "isPrimary";
	public static final String PROPERTY_HAS_PLACEHOLDER = "hasPlaceholder";

	protected static final String PROPERTY_WHEN_TAKEN_CHANGED = "whenTakenChanged";
	private ValueHolder<EmptyVariable> d_placeholder = new ModifiableHolder<EmptyVariable>(null);

	private Boolean d_isPrimary = false;
	private ObservableList<WhenTaken> d_whenTaken = new GuardedObservableList<WhenTaken>(new ArrayListModel<WhenTaken>(),
			new Predicate<WhenTaken>() {
				public boolean evaluate(WhenTaken wt) {
					return wt != null && wt.isCommitted();
				}
			});

	private final Class<? extends Variable> d_class;

	public StudyOutcomeMeasure(Class<T> cls) {
		this((T)null, cls);
	}

	public StudyOutcomeMeasure(T obj) {
		this(obj, obj.getClass());
	}

	private StudyOutcomeMeasure(T obj, Class<? extends Variable> cls) {
		super(obj);
		d_class = cls;
		d_whenTaken.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				firePropertyChange(PROPERTY_WHEN_TAKEN_CHANGED, false, true);
			}
			public void intervalAdded(ListDataEvent e) {
				firePropertyChange(PROPERTY_WHEN_TAKEN_CHANGED, false, true);
			}
			public void contentsChanged(ListDataEvent e) {
				firePropertyChange(PROPERTY_WHEN_TAKEN_CHANGED, false, true);
			}
		});
	}

	public StudyOutcomeMeasure(T obj, WhenTaken whenTaken) {
		this(obj);
		if (whenTaken != null) {
			d_whenTaken.add(whenTaken);
		}
	}

	@Override
	public T getValue() {
		if (super.getValue() == null) {
			return initializePlaceholder();
		}
		return super.getValue();
	}

	@Override
	public void setValue(Object newValue) {
		super.setValue(newValue);

		d_placeholder.setValue(null);
		firePropertyChange(PROPERTY_HAS_PLACEHOLDER, true, false);
	}

	private T initializePlaceholder() {
		if (d_placeholder.getValue() == null) {
			d_placeholder.setValue(new EmptyVariable("", null));
			firePropertyChange(PROPERTY_HAS_PLACEHOLDER, false, true);
		}
		return d_placeholder.getValue().getDummy();
	}

	public boolean hasPlaceholder() {
		return d_placeholder.getValue() != null;
	}

	public T getPlaceholder() {
		return d_placeholder.getValue().getDummy();
	}

	@Override
	public StudyOutcomeMeasure<T> clone() {
		StudyOutcomeMeasure<T> clone = new StudyOutcomeMeasure<T>(getValue());
		clone.setIsPrimary(getIsPrimary());
		clone.getNotes().addAll(getNotes());
		for (WhenTaken wt : getWhenTaken()) {
			WhenTaken wtClone = wt.clone();
			wtClone.commit();
			clone.getWhenTaken().add(wtClone);
		}
		return clone;
	}

	public Boolean getIsPrimary() {
		return d_isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		Boolean oldValue = new Boolean(d_isPrimary);
		d_isPrimary = isPrimary;
		firePropertyChange(PROPERTY_IS_PRIMARY, oldValue, d_isPrimary);
	}

	public ObservableList<WhenTaken> getWhenTaken() {
		return d_whenTaken;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StudyOutcomeMeasure<?>) {
			StudyOutcomeMeasure<?> other = (StudyOutcomeMeasure<?>) o;
			return  EqualsUtil.equal(getValue(), other.getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder(17, 37).
	    	       append(d_class).
	    	       append(d_isPrimary).
	    	       append(d_whenTaken).
	    	       toHashCode();
	}

	@Override
	public String toString() {
		return (d_isPrimary ? "primary measure: " : "secondary measure: ") +
				(getValue() != null ? getValue().getName() : "???") + " " + d_whenTaken + " " + getNotes();
	}

	public Class<? extends Variable> getValueClass() {
		return d_class;
	}
}
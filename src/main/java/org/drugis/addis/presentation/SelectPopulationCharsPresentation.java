/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.gui.Main;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class SelectPopulationCharsPresentation
extends SelectFromFiniteListPresentationImpl<PopulationCharacteristic> {
	public SelectPopulationCharsPresentation(ListHolder<PopulationCharacteristic> options, Main main) {
		super(options, "Population Baseline Characteristics", "Select Population Baseline Characteristics",
			"Please select the appropriate population baseline characteristics.", main);
		d_addSlotsEnabled = new AddSlotsEnabledModel();
	}
	
	@Override
	public void showAddOptionDialog(int idx) {
		d_main.showAddPopulationCharacteristicDialog(getSlot(idx));
	}
	
	public class AddSlotsEnabledModel extends AbstractValueModel implements PropertyChangeListener {
		public AddSlotsEnabledModel() {
			d_options.addValueChangeListener(this);
			SelectPopulationCharsPresentation.this.addPropertyChangeListener(this);
		}
		
		public Object getValue() {
			return addSlotsEnabled();
		}

		private boolean addSlotsEnabled() {
			return addSlotsEnabled(d_slots.size(), d_options.getValue().size());
		}
		
		private boolean addSlotsEnabled(int slots, int options) {
			return slots < options;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("AddSlotsEnabledModel is read-only");
		}

		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_options) {
				boolean oldVal = addSlotsEnabled(d_slots.size(), ((List)evt.getOldValue()).size());
				boolean newVal = addSlotsEnabled(d_slots.size(), ((List)evt.getNewValue()).size());
				fireValueChange(oldVal, newVal);
			} else if (evt.getSource() == SelectPopulationCharsPresentation.this) {
				boolean oldVal = addSlotsEnabled((Integer)evt.getOldValue(), d_options.getValue().size());
				boolean newVal = addSlotsEnabled((Integer)evt.getNewValue(), d_options.getValue().size());
				fireValueChange(oldVal, newVal);
			}
		}
	}
}

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

import javax.swing.table.AbstractTableModel;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.StudyCharacteristics;

import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class StudyCharTableModel extends AbstractTableModel {
	protected StudyListPresentationModel d_pm;
	private PresentationModelFactory d_pmf;
	
	public StudyCharTableModel(StudyListPresentationModel pm, PresentationModelFactory pmf) {
		d_pm = pm;
		d_pmf = pmf;
		for (Characteristic c : StudyCharacteristics.values()) {
			ValueModel vm = d_pm.getCharacteristicVisibleModel(c);
			vm.addValueChangeListener(new ValueChangeListener());
		}
		d_pm.getIncludedStudies().addValueChangeListener(new ValueChangeListener());
	}
		
	public int getColumnCount() {
		return getNoVisibleCharacteristics() + 1;
	}

	private int getNoVisibleCharacteristics() {
		int visible = 0;
		for (Characteristic c : StudyCharacteristics.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).booleanValue()) {
				visible++;
			}
		}
		return visible;
	}

	public int getRowCount() {
		return d_pm.getIncludedStudies().getValue().size();
	}

	/**
	 * @throws IndexOutOfBoundsException if row- or columnindex doesn't exist in the model
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("column index (" + columnIndex + ") out of bounds");
		}
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("row index (" + rowIndex + ") out of bounds");
		}
		
		if (columnIndex == 0) {
			return d_pm.getIncludedStudies().getValue().get(rowIndex);
		}
		Characteristic c = getCharacteristic(columnIndex);
		StudyPresentationModel spm = (StudyPresentationModel) d_pmf.getModel(d_pm.getIncludedStudies().getValue().get(rowIndex));
		return spm.getCharacteristicModel(c).getValue();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "Study ID";
		}
		return getCharacteristic(columnIndex).getDescription();
	}
	
	private Characteristic getCharacteristic(int columnIndex) {
		int idx = 0;
		for (Characteristic c: StudyCharacteristics.values()) {
			if (d_pm.getCharacteristicVisibleModel(c).getValue().equals(Boolean.TRUE)) {
				++idx;
			}
			if (idx == columnIndex) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException();
	}
	
	private class ValueChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			fireTableStructureChanged();
		}		
	}
}

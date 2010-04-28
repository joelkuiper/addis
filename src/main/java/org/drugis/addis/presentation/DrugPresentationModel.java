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

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class DrugPresentationModel extends PresentationModel<Drug> implements StudyListPresentationModel, LabeledPresentationModel {
	
	private CharacteristicVisibleMap d_charVisibleMap = new CharacteristicVisibleMap();
	private ListHolder<Study> d_studies;

	public DrugPresentationModel(Drug drug, ListHolder<Study> studies) {
		super(drug);
		d_studies = studies;		
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_charVisibleMap.get(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}

	public AbstractValueModel getLabelModel() {
		return new ValueHolder(getBean().getName());
	}
}

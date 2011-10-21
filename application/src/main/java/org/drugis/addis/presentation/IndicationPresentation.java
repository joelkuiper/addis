/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.common.beans.FilteredObservableList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class IndicationPresentation extends PresentationModel<Indication> implements LabeledPresentation, StudyListPresentation {
	public class LabelModel extends DefaultLabelModel {
		protected LabelModel()  {
			super(getBean());
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(Indication.PROPERTY_CODE)) {
				firePropertyChange("value", (evt.getOldValue() + " " + getBean().getName()), getValue());
			} else if (evt.getPropertyName().equals(Indication.PROPERTY_NAME)) {
				firePropertyChange("value", (getBean().getCode() + " " + evt.getOldValue()), getValue());
			}
		}
	}

	private CharacteristicVisibleMap d_charMap = new CharacteristicVisibleMap();
	private ObservableList<Study> d_studies;

	public IndicationPresentation(final Indication indication, ObservableList<Study> sortedSetModel) {
		super(indication);
		d_studies = new FilteredObservableList<Study>(sortedSetModel, new DomainImpl.IndicationFilter(indication));
	}

	public AbstractValueModel getLabelModel() {
		return new LabelModel();
	}

	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_charMap.get(c);
	}

	public ObservableList<Study> getIncludedStudies() {
		return d_studies;
	}
}
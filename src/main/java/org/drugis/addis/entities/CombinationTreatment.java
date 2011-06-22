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

package org.drugis.addis.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class CombinationTreatment extends AbstractEntity implements Activity {

	public static final String PROPERTY_TREATMENTS = "treatments";
	
	private ObservableList<TreatmentActivity> d_treatments = new ArrayListModel<TreatmentActivity>();

	@Override
	public Set<? extends Entity> getDependencies() {
		return new HashSet<Entity>(getDrugs());
	}

	@Override
	public String getDescription() {
		if(d_treatments.size() == 0) {
			return "No treatments.";
		}
		String out = "Combination treatment (";
		for(TreatmentActivity t : d_treatments) {
			out = out + t.getDescription().substring(t.getDescription().lastIndexOf('(') + 1, t.getDescription().lastIndexOf(')')) + "; " ;
		}
		return out.substring(0, out.length() - 2) + ")";	
	}

	public void addTreatment(Drug drug, AbstractDose dose) {
		TreatmentActivity ta = new TreatmentActivity(drug, dose);
		d_treatments.add(ta);
	}
	
	@Override
	protected CombinationTreatment clone() {
		CombinationTreatment clone = new CombinationTreatment();
		for(TreatmentActivity t : d_treatments) {
			clone.addTreatment(t.getDrug(),  t.getDose() == null ? null : t.getDose().clone());
		}
		return clone;
	}
	
	public List<Drug> getDrugs() {
		List<Drug> drugs = new ArrayList<Drug>();
		for(TreatmentActivity ta : d_treatments) {
			drugs.add(ta.getDrug());
		}
		return drugs;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CombinationTreatment) {
			CombinationTreatment other = (CombinationTreatment) obj;
			return EqualsUtil.equal(other.getTreatments(), getTreatments());
		}
		return false;	
	}

	public  List<AbstractDose> getDoses() {
		List<AbstractDose> doses = new ArrayList<AbstractDose>();
		for(TreatmentActivity ta : d_treatments) {
			doses.add(ta.getDose());
		}
		return doses;
	}
	
	public ObservableList<TreatmentActivity> getTreatments() {
		return d_treatments;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
}

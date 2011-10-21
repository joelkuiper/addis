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


public class DomainEvent {

	private Type d_type;
	
	public enum Type {
		STUDIES,
		DRUGS,
		ENDPOINTS,
		INDICATIONS,
		ANALYSES,
		VARIABLES,
		ADVERSE_EVENTS,
		BENEFITRISK_ANALYSIS
	}

	public DomainEvent(Type type) {
		d_type = type;
	} 
	
	public Type getType() {
		return d_type;
	}	
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DomainEvent)) {
			return false;
		}
		DomainEvent dom = (DomainEvent) other;
		return dom.getType().equals(getType());
	}
}
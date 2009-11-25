/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.entities.metaanalysis;

import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.common.Interval;

public class LogOddsRatio extends OddsRatio  {
	private static final long serialVersionUID = -9012075635937781733L;
	
	LogOddsRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}

	public Double getRelativeEffect() {
		return Math.log(super.getRelativeEffect());
	}

	@Override
	public Interval<Double> getConfidenceInterval() {
		throw new RuntimeException("log odds ratio doesnt have confidence interval"); 
	}		
}
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

package org.drugis.addis.entities;

public class RiskRatio extends AbstractRatio {

	public RiskRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(numerator, denominator);
	}

	@Override
	public String toString() {
		return "[" + d_baseline.toString() + "] / [" 
		+ d_subject.toString() + "]";
	}
	
	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public Double getSigma() { //NB: this is the LOG error
		if (!isDefined())
			return Double.NaN;

		return Math.sqrt((1.0 / (d_subject.getRate() + d_correction)) +
				(1.0 / (d_baseline.getRate() + d_correction)) -
				(1.0 / (d_subject.getSampleSize())) -
				(1.0 / (d_baseline.getSampleSize())));		
	}

	public String getName() {
		return "Risk ratio";
	}
	
	public Double getMu() {
		if (!isDefined())
			return Double.NaN;
		
		double expResult = ( (d_subject.getRate() + d_correction) / (d_subject.getSampleSize()) ) 
			/ ( (d_baseline.getRate() + d_correction) / (d_baseline.getSampleSize()) );
		
		return Math.log(expResult);
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}
}

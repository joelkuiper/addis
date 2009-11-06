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

package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class OddsRatioTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	private static final double s_meanNum = (double)s_effectNum / (double)(s_sizeNum - s_effectNum); 
	private static final double s_meanDen = (double)s_effectDen / (double)(s_sizeDen - s_effectDen);
	private static final double s_stdDevNum = s_meanNum / Math.sqrt(s_sizeNum);
	private static final double s_stdDevDen = s_meanDen / Math.sqrt(s_sizeDen);
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	OddsRatio d_ratio;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.RATE);
		PatientGroup pnum = new BasicPatientGroup(null,null,null,s_sizeNum);
		PatientGroup pden = new BasicPatientGroup(null,null,null,s_sizeDen);
		d_numerator = new BasicRateMeasurement(e, s_effectNum, pnum);		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, pden);
		d_ratio = new OddsRatio(d_denominator, d_numerator);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorThrowsException() {
		Endpoint e2 = new Endpoint("E2", Type.RATE);
		PatientGroup pnum = new BasicPatientGroup(null, null, null, s_sizeNum);
		RateMeasurement subject = new BasicRateMeasurement(e2, s_effectNum, pnum);
		new OddsRatio(d_denominator, subject);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(new Endpoint("E", Type.RATE), d_ratio.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(s_sizeNum + s_sizeDen, (int)d_ratio.getSampleSize());
	}
	
	@Test
	public void testGetRelativeEffect() {
		assertEquals(s_meanNum / s_meanDen, (double)d_ratio.getRelativeEffect(), 0.00001);
	}
	
	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(d_ratio.getSampleSize() - 2);
		double g = square(t * s_stdDevDen / s_meanDen);
		double q = d_ratio.getRelativeEffect();
		double sd = q / (1 - g) * Math.sqrt((1 - g) * square(s_stdDevNum) / square(s_meanNum) + 
				square(s_stdDevDen) / square(s_meanDen));
		double lower = q / (1 - g) - t * sd;
		double upper = q / (1 - g) + t * sd;
		
		Interval<Double> ci = d_ratio.getConfidenceInterval();
		assertEquals(lower, ci.getLowerBound(), 0.0001);
		assertEquals(upper, ci.getUpperBound(), 0.0001);
	}
	
	private double square(double d) {
		return d * d;
	}
	
	@Test
	public void testGetError() {
		double t = StudentTTable.getT(d_ratio.getSampleSize() - 2);
		double g = square(t * s_stdDevDen / s_meanDen);
		double q = d_ratio.getRelativeEffect();
		double sd = q / (1 - g) * Math.sqrt((1 - g) * square(s_stdDevNum) / square(s_meanNum) + 
				square(s_stdDevDen) / square(s_meanDen));
		
		assertEquals(sd, d_ratio.getError(), 0.00001);
	}
}

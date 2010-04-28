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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.RelativeEffect.AxisType;
import org.drugis.addis.treeplot.ForestPlot;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class ForestPlotPresentationTest {
	
	private static final double s_mean1 = 0.50;
	private static final double s_mean2 = 0.25;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baseSize = 41;
	
	private ForestPlotPresentation d_pm;
	private ContinuousMeasurement d_mBase1;
	private ContinuousMeasurement d_mSubj1;
	private BasicContinuousMeasurement d_mBase2;
	private BasicContinuousMeasurement d_mSubj2;
	private Study d_s2;
	private Study d_s1;
	private Endpoint d_endpoint;
	private Drug d_subject;
	private Drug d_baseline;
	
	@Before
	public void setUp() {
		d_s1 = new Study("X", new Indication(0L, ""));
		d_endpoint = new Endpoint("E", Variable.Type.CONTINUOUS);
		d_s1.addEndpoint(d_endpoint);
		d_baseline = new Drug("DrugA", "");
		d_subject = new Drug("DrugB", "");
		Arm pBase = new Arm(d_baseline, new FixedDose(10, SIUnit.MILLIGRAMS_A_DAY), s_baseSize);
		Arm pSubj = new Arm(d_subject,  new FixedDose(10, SIUnit.MILLIGRAMS_A_DAY), s_subjSize);
		d_s1.addArm(pBase);
		d_s1.addArm(pSubj);
		d_mBase1 = new BasicContinuousMeasurement(s_mean1, s_stdDev1, pBase.getSize());
		d_mSubj1 = new BasicContinuousMeasurement(s_mean2, s_stdDev2, pSubj.getSize());
		d_s1.setMeasurement(d_endpoint, pBase, d_mBase1);
		d_s1.setMeasurement(d_endpoint, pSubj, d_mSubj1);
		
		d_s2 = new Study("Y", new Indication(0L, ""));
		d_s2.addEndpoint(d_endpoint);
		Arm pBase2 = new Arm(d_baseline, new FixedDose(10, SIUnit.MILLIGRAMS_A_DAY), s_baseSize);
		Arm pSubj2 = new Arm(d_subject, new FixedDose(10, SIUnit.MILLIGRAMS_A_DAY), s_subjSize);
		d_s2.addArm(pBase2);
		d_s2.addArm(pSubj2);
		d_mBase2 = new BasicContinuousMeasurement(s_mean2, s_stdDev2, pBase2.getSize());
		d_mSubj2 = new BasicContinuousMeasurement(s_mean1, s_stdDev1, pSubj2.getSize());
		d_s2.setMeasurement(d_endpoint, pBase2, d_mBase2);
		d_s2.setMeasurement(d_endpoint, pSubj2, d_mSubj2);
		
		List<Study> studies = new ArrayList<Study>();
		studies.add(d_s1);
		studies.add(d_s2);
		d_pm = new ForestPlotPresentation(studies, d_endpoint, d_baseline, d_subject, MeanDifference.class, 
				new PresentationModelFactory(new DomainImpl()), null);
	}
	
	@Test
	public void testGetListedRelativeEffects() {
		assertEquals(2, d_pm.getNumRelativeEffects());
		assertRelativeEffectEqual(
				new MeanDifference(d_mBase1, d_mSubj1),
				d_pm.getRelativeEffectAt(0));
		assertRelativeEffectEqual(
				new MeanDifference(d_mBase2, d_mSubj2),
				d_pm.getRelativeEffectAt(1));
	}
	
	
	@Test
	public void testGetScale() {
		assertEquals( (int) Math.round(ForestPlot.BARWIDTH  / 2.0), (int) d_pm.getScale().getBin(0.0).bin);
		int expectedBin = (int) Math.round( (2 - 1.09) / (4.0 / (ForestPlot.BARWIDTH - 1)) ); 
		assertEquals(expectedBin + 1, (int) d_pm.getScale().getBin(-1.09).bin);
		assertTrue(!d_pm.getScale().getBin(-1.09).outOfBoundsMin);
		assertEquals(ForestPlot.BARWIDTH - expectedBin, (int) d_pm.getScale().getBin(1.09).bin);
		assertTrue(!d_pm.getScale().getBin(1.09).outOfBoundsMax);
	}
	
	@Test
	public void testGetRange() {
		// known intervals: "0.25 (-0.53, 1.03)" & "-0.25 (-1.09, 0.59)"
		Interval<Double> interval = d_pm.getRange();
		assertEquals(-2.0, interval.getLowerBound(), 0.01);
		assertEquals(2.0, interval.getUpperBound(), 0.01);	
	}
	
	@Test
	public void testGetDrugsLabel() {
		assertEquals("DrugA", d_pm.getLowValueFavorsDrug().toString());
		assertEquals("DrugB", d_pm.getHighValueFavorsDrug().toString());
	}
	
	@Test
	public void testGetStudiesLabel() {
		assertEquals("X", d_pm.getStudyLabelAt(0));
		assertEquals("Y", d_pm.getStudyLabelAt(1));
	}
	
	@Test
	public void testGetCIlabel() {
		// known intervals: "0.25 (-0.53, 1.03)" & "-0.25 (-1.09, 0.59)"
		String interval1 = "0.25 (-0.53, 1.03)";
		String interval2 = "-0.25 (-1.09, 0.59)";
		assertEquals(interval1, d_pm.getCIlabelAt(1).getLabelModel().getValue());
		assertEquals(interval2, d_pm.getCIlabelAt(0).getLabelModel().getValue());
	}
	
	@Test
	public void testGetScaleType() {
		assertEquals(AxisType.LINEAR, d_pm.getScaleType());
	}
	
	@Test
	public void testGetScaleZero() {
		assertEquals(151, (int)d_pm.getScale().getBin(0.0).bin);
	}
	
	@Test
	public void testGetDiamondSize() {
		for (Arm pg : d_s2.getArms()) {
			BasicMeasurement m = (BasicMeasurement)d_s2.getMeasurement(d_endpoint, pg);
			m.setSampleSize(m.getSampleSize() * 10);
		}
		List<Study> studies = new ArrayList<Study>();
		studies.add(d_s1);
		studies.add(d_s2);
		
		ForestPlotPresentation pm = new ForestPlotPresentation(studies, d_endpoint,
				d_baseline, d_subject,
				MeanDifference.class, new PresentationModelFactory(new DomainImpl()), null);
		assertEquals(5, pm.getDiamondSize(0));
		assertEquals(21, pm.getDiamondSize(1));
	}
	
	@Test
	public void testLogarithmic() {
		Interval<Double> logint = d_pm.niceIntervalLog(0.0624, 4.1);
		assertEquals(logint.getLowerBound(), 1D/32D, 0.001);
		assertEquals(logint.getUpperBound(), 8D, 0.001);
	}
	
	@Test
	public void testGetTickVals() {
		// known intervals: "0.25 (-0.53, 1.03)" & "-0.25 (-1.09, 0.59)"		
		List<String> tickVals = d_pm.getTickVals();
		assertEquals(3, tickVals.size());
		assertEquals("-2", tickVals.get(0));
		assertEquals("0", tickVals.get(1));
		assertEquals("2", tickVals.get(2));
	}
	
	@Test
	public void testGetTicks() {
		// known intervals: "0.25 (-0.53, 1.03)" & "-0.25 (-1.09, 0.59)"
		List<Integer> ticks = d_pm.getTicks();
		assertEquals(3, ticks.size());
		assertEquals(1, (int)ticks.get(0));
		assertEquals(151, (int)ticks.get(1));
		assertEquals(301, (int)ticks.get(2));
	}
	
	@Test
	public void testLabelsForLowerIsBetter() {
		d_endpoint.setDirection(Direction.LOWER_IS_BETTER);
		assertEquals("DrugB", d_pm.getLowValueFavorsDrug().toString());
		assertEquals("DrugA", d_pm.getHighValueFavorsDrug().toString());
	}
	
	private static void assertRelativeEffectEqual(RelativeEffect<?> expected,
			RelativeEffect<?> actual) {
		assertEquals(expected.getBaseline(), actual.getBaseline());
		assertEquals(expected.getSubject(), actual.getSubject());
		assertEquals(expected.getClass(), actual.getClass());
	}
}

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

package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Domain;
import nl.rug.escher.addis.entities.DomainImpl;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Entity;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.MetaAnalysis;
import nl.rug.escher.addis.entities.MetaStudy;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.PooledRateMeasurement;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.JUnitUtil;

import static org.easymock.EasyMock.*;
import org.junit.Before;
import org.junit.Test;

public class MetaStudyTest {

	private MetaStudy d_study;
	private MetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		Domain d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		d_analysis = new MetaAnalysis(TestData.buildEndpointHamd(), 
				new ArrayList<Study>(d_domain.getStudies()));		
		d_study = new MetaStudy("s", d_analysis);
	}
	
	@Test
	public void testGetDrugs() {
		assertEquals(d_analysis.getDrugs(), d_study.getDrugs());
	}
	
	@Test
	public void testGetEndpoints() {
		assertEquals(Collections.singleton(d_analysis.getEndpoint()), d_study.getEndpoints());
	}
	
	@Test
	public void testGetPatientGroups() {
		PooledRateMeasurement m1 = (PooledRateMeasurement) d_analysis.getPooledMeasurement(TestData.buildDrugFluoxetine());
		PooledRateMeasurement m2 = (PooledRateMeasurement) d_analysis.getPooledMeasurement(TestData.buildDrugParoxetine());
		
		List<PatientGroup> l = d_study.getPatientGroups();
		
		assertEquals(2, l.size());
		PatientGroup parox = l.get(0);
		PatientGroup fluox = l.get(1);
		
		if (parox.getDrug().equals(TestData.buildDrugFluoxetine())) {
			PatientGroup swap = parox;
			parox = fluox;
			fluox = swap;
		}
		
		assertEquals(TestData.buildDrugParoxetine(), parox.getDrug());
		assertEquals(TestData.buildDrugFluoxetine(), fluox.getDrug());
		
		assertEquals(m1.getSampleSize(), fluox.getSize());
		assertEquals(m2.getSampleSize(), parox.getSize());
	}
	
	@Test
	public void testGetDependencies() {
		List<Study> studies = d_study.getAnalysis().getStudies();
		Set<Entity> deps = new HashSet<Entity>(studies);
		for (Study s : studies) {
			deps.addAll(s.getDependencies());
		}
		assertEquals(deps, d_study.getDependencies());
	}
	
	@Test
	public void testGetMeasurement() {
		PatientGroup pg = d_study.getPatientGroups().get(0);
		Measurement expected = d_study.getAnalysis().getPooledMeasurement(pg.getDrug());
		Measurement value = d_study.getMeasurement(TestData.buildEndpointHamd(), pg);
		assertEquals(expected, value);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetMeasurementThrowsException() {
		d_study.setMeasurement(d_analysis.getEndpoint(), d_study.getPatientGroups().get(0),
				new BasicRateMeasurement(d_analysis.getEndpoint(), 10));
	}
	
	@Test
	public void testAddEndpoint() {
		Endpoint e = new Endpoint("epoint", Type.RATE);
		Collection<Endpoint> oldVal = new HashSet<Endpoint>(d_study.getEndpoints());						
		Collection<Endpoint> newVal = new HashSet<Endpoint>(d_study.getEndpoints());
		newVal.add(e);
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_study, Study.PROPERTY_ENDPOINTS,
				oldVal, newVal);
		d_study.addPropertyChangeListener(mock);
		d_study.addEndpoint(e);
		verify(mock);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDeleteMetaEndpointThrowsException() {
		d_study.deleteEndpoint(d_study.getEndpoints().iterator().next());
	}
	
	@Test
	public void testDeleteEndpoint() throws Exception {
		Endpoint e = new Endpoint("epoint", Type.RATE);
		Set<Endpoint> newVal = new HashSet<Endpoint>(d_study.getEndpoints());		
		d_study.addEndpoint(e);
		Set<Endpoint> oldVal = new HashSet<Endpoint>(d_study.getEndpoints());				
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_study, Study.PROPERTY_ENDPOINTS,
				oldVal, newVal);
		d_study.addPropertyChangeListener(mock);
		d_study.deleteEndpoint(e);
		verify(mock);
	}
	
}

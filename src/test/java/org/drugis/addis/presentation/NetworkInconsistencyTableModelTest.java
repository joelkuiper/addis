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

import java.util.Arrays;
import java.util.List;

import mocks.MockNetworkMetaAnalysis;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyParameter;
import org.junit.Before;
import org.junit.Test;

public class NetworkInconsistencyTableModelTest {

	private PresentationModelFactory d_pmf;
	private NetworkInconsistencyFactorsTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		
		NetworkMetaAnalysisPresentation pm = (NetworkMetaAnalysisPresentation) d_pmf.getModel(d_analysis);
		d_tableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) pm, d_pmf);
		pm.d_isModelConstructionFinished = true;
		
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(2, d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getInconsistencyFactors().size(), d_tableModel.getRowCount());
	}

	@Test
	public void testValueAt() {
		d_analysis.getInconsistencyModel().run();
		for(int x = 0; x < d_tableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
				InconsistencyParameter ip = d_analysis.getInconsistencyModel().getInconsistencyFactors().get(y);
				if(x == 0){
					assertEquals("Fluoxetine, Sertraline, Paroxetine", d_tableModel.getValueAt(y, x));
				} else if (x == 1){
					Estimate icModel = d_analysis.getInconsistencyModel().getInconsistency(ip);
					
					BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(icModel.getMean(), icModel.getStandardDeviation(), 0);
					ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
										(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
					assertEquals(pm.normConfIntervalString(), d_tableModel.getValueAt(y, x));
				}
			}
		}	
	}
	
	@Test
	public void testContinuousValueAt() {
		NetworkMetaAnalysis d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		NetworkInconsistencyFactorsTableModel d_contTableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) d_pmf.getModel(d_contAnalysis), d_pmf);
		d_contAnalysis.getInconsistencyModel().run();
		for(int x = 0; x < d_contTableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_contTableModel.getRowCount(); ++y) {
				InconsistencyParameter ip = d_contAnalysis.getInconsistencyModel().getInconsistencyFactors().get(y);
				if(x == 0){
					assertEquals("Fluoxetine, Sertraline, Paroxetine", d_contTableModel.getValueAt(y, x));
				} else if (x == 1){
					Estimate icModel = d_contAnalysis.getInconsistencyModel().getInconsistency(ip);
					
					BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(icModel.getMean(), icModel.getStandardDeviation(), 0);
					ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
										(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
					assertEquals(pm.normConfIntervalString(), d_contTableModel.getValueAt(y, x));
				}
			}
		}	
	}
	
	private NetworkMetaAnalysis buildMockContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard()});
		List<Drug> drugs = Arrays.asList(new Drug[] {ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine(), 
				ExampleData.buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}
}
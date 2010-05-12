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

package org.drugis.addis.entities.metaanalysis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RandomEffectMetaAnalysisRelativeEffect;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RiskRatio;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;

public class RandomEffectsMetaAnalysis extends AbstractMetaAnalysis {

	transient private double d_thetaDSL;
	transient private double d_SEThetaDSL;
	transient private Interval<Double> d_confidenceInterval;
	transient private double d_qIV;
	
	public static final String PROPERTY_INCLUDED_STUDIES_COUNT = "studiesIncluded";
	public static final String PROPERTY_FIRST_DRUG = "firstDrug";
	public static final String PROPERTY_SECOND_DRUG = "secondDrug";
	
	private RandomEffectsMetaAnalysis() {
		super();
	}
	
	/**
	 * @throws IllegalArgumentException if all studies don't measure the same indication OR
	 * if the list of studies is empty
	 */
	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<? extends Study> studies,
			Drug drug1, Drug drug2) 
	throws IllegalArgumentException {
		super(name, studies.get(0).getIndication(), om, studies, 
				Arrays.asList(new Drug[] {drug1, drug2}), getArmMap(studies, drug1, drug2));
		checkREDataConsistency(studies, drug1, drug2);
	}

	private void checkREDataConsistency(List<? extends Study> studies,
			Drug drug1, Drug drug2) {
		if (studies.size() == 0)
			throw new IllegalArgumentException("No studies in MetaAnalysis");
		for (Study s : studies)
			if (!(s.getDrugs().contains(drug1) && s.getDrugs().contains(drug2)))
				throw new IllegalArgumentException("Not all studies contain the drugs under comparison");
	}

	public RandomEffectsMetaAnalysis(String name, OutcomeMeasure om, List<StudyArmsEntry> studyArms)
	throws IllegalArgumentException {
		super(name, getIndication(studyArms), om, getStudies(studyArms), getDrugs(studyArms), getArmMap(studyArms));
		
		for (StudyArmsEntry s : studyArms){
			if(!s.getBase().getDrug().equals(getFirstDrug())){
				throw new IllegalArgumentException("Left drug not consistent over all studies");
			}
			if(!s.getSubject().getDrug().equals(getSecondDrug())){
				throw new IllegalArgumentException("Right drug not consistent over all studies");
			}
		}
	}
	

	public String getType() {
		return "DerSimonian-Laird Random Effects";
	}

	private static Map<Study, Map<Drug, Arm>> getArmMap(
			List<? extends Study> studies, Drug drug1, Drug drug2) {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();

		for (Study s : studies) {
			Arm arm1 = RelativeEffectFactory.findFirstArm(s, drug1);
			Arm arm2 = RelativeEffectFactory.findFirstArm(s, drug2);
			studyArms.add(new StudyArmsEntry(s, arm1, arm2));
		}
		
		return getArmMap(studyArms);
	}
	
	private static Map<Study, Map<Drug, Arm>> getArmMap(List<StudyArmsEntry> studyArms) {
		Map<Study, Map<Drug, Arm>> armMap = new HashMap<Study, Map<Drug, Arm>>();
		for (StudyArmsEntry sae : studyArms) {
			Map<Drug, Arm> drugMap = new HashMap<Drug, Arm>();
			drugMap.put(sae.getBase().getDrug(), sae.getBase());
			drugMap.put(sae.getSubject().getDrug(), sae.getSubject());
			armMap.put(sae.getStudy(), drugMap);
		}
		return armMap;
	}

	private static List<Drug> getDrugs(List<StudyArmsEntry> studyArms) {
		return Arrays.asList(new Drug[]{getFirstDrug(studyArms), getSecondDrug(studyArms)});
	}

	private static Drug getSecondDrug(List<StudyArmsEntry> studyArms) {
		return studyArms.get(0).getSubject().getDrug();
	}

	private static Drug getFirstDrug(List<StudyArmsEntry> studyArms) {
		return studyArms.get(0).getBase().getDrug();
	}

	private static List<? extends Study> getStudies(
			List<StudyArmsEntry> studyArms) {
		return StudyArmsEntry.getStudyList(studyArms);
	}

	private static Indication getIndication(List<StudyArmsEntry> studyArms) {
		return getStudies(studyArms).get(0).getIndication();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
	}	
	
	public Drug getFirstDrug() {
		return d_drugs.get(0);
	}
	
	public Drug getSecondDrug() {
		return d_drugs.get(1);
	}
	
	public List<StudyArmsEntry> getStudyArms() {
		return getStudyArms(false);
	}
	
	private List<StudyArmsEntry> getStudyArms(boolean drugsSwapped) {
		List<StudyArmsEntry> studyArms = new ArrayList<StudyArmsEntry>();
		for (Study s : getIncludedStudies()) {
			if (!drugsSwapped)
				studyArms.add(new StudyArmsEntry(s, getArm(s, getFirstDrug()), getArm(s, getSecondDrug())));
			else
				studyArms.add(new StudyArmsEntry(s, getArm(s, getSecondDrug()), getArm(s, getFirstDrug())));
		}
		return studyArms;
	}
	
	private void compute(Class<? extends RelativeEffect<?>> relEffClass, boolean drugsSwapped) {
		
		Class<? extends RelativeEffect<? extends Measurement>> type = relEffClass; 
		if (relEffClass == RiskRatio.class)
			type = LogRiskRatio.class;
		if (relEffClass == OddsRatio.class)
			type = LogOddsRatio.class;
		
		List<Double> weights = new ArrayList<Double>();
		List<Double> adjweights = new ArrayList<Double>();
		List<RelativeEffect<? extends Measurement>> relEffects = new ArrayList<RelativeEffect<? extends Measurement>>();
			
		for (int i=0; i<d_studies.size(); ++i ){
			RelativeEffect<? extends Measurement> re;
			re = RelativeEffectFactory.buildRelativeEffect(getStudyArms(drugsSwapped).get(i), d_outcome, type);
			relEffects.add(re);
		}
		
		// Calculate the weights.
		for (RelativeEffect<? extends Measurement> re : relEffects) {
			weights.add(1D / Math.pow(re.getError(),2));
		}
		
		// Calculate needed variables.
		double thetaIV = getThetaIV(weights, relEffects);
		d_qIV = getQIV(weights, relEffects, thetaIV);
		double tauSquared = getTauSquared(d_qIV, weights);
		
		// Calculated the adjusted Weights.
		for (RelativeEffect<? extends Measurement> re : relEffects) {
			adjweights.add(1 / (Math.pow(re.getError(),2) + tauSquared) );
		}
		
		d_thetaDSL = getThetaDL(adjweights, relEffects);
		d_SEThetaDSL = getSE_ThetaDL(adjweights);
			
		d_confidenceInterval = getConfidenceInterval();
		
		if ((type == LogRiskRatio.class) || (type == LogOddsRatio.class)) {
			d_thetaDSL = Math.exp(d_thetaDSL);
			d_confidenceInterval = new Interval<Double>(Math.exp(d_confidenceInterval.getLowerBound()),Math.exp(d_confidenceInterval.getUpperBound()));
		}
	}
	
	private Interval<Double> getConfidenceInterval() {	
		double Z95percent = StudentTTable.getT(Integer.MAX_VALUE);
		double lower = d_thetaDSL - Z95percent * d_SEThetaDSL;
		double upper = d_thetaDSL + Z95percent * d_SEThetaDSL;
		return new Interval<Double>(lower, upper);
	}
	
	private double getSE_ThetaDL(List<Double> adjweights) {
		return 1.0 / (Math.sqrt(computeSum(adjweights)));
	}

	private double getThetaDL(List<Double> adjweights, List<RelativeEffect<? extends Measurement>> relEffects) {
		double numerator = 0;
		for (int i=0; i < adjweights.size(); ++i) {
			numerator += adjweights.get(i) * relEffects.get(i).getRelativeEffect();
		}
		
		return numerator / computeSum(adjweights);
	}
	
	private double getTauSquared(double Q, List<Double> weights) {
		double k = weights.size();
		double squaredWeightsSum = 0;
		for (int i=0;i<weights.size();i++) {
			squaredWeightsSum += Math.pow(weights.get(i),2);
		}
		
		double num = Q - (k - 1);
		double denum = computeSum(weights) - (squaredWeightsSum / computeSum(weights));
		return Math.max(num / denum, 0);
	}
	
	private double getQIV(List<Double> weights, List<RelativeEffect<? extends Measurement>> relEffects, double thetaIV) {
		double sum = 0;
		for (int i=0; i < weights.size(); ++i) {
			sum += weights.get(i) * Math.pow(relEffects.get(i).getRelativeEffect() - thetaIV,2);
		}
		return sum;
	}
	
	private double getThetaIV(List<Double> weights, List<RelativeEffect<? extends Measurement>> relEffects) {
		assert(weights.size() == relEffects.size());
		
		// Calculate the sums
		double sumWeightRatio = 0D;
			
		for (int i=0; i < weights.size(); ++i) {
			sumWeightRatio += weights.get(i) * relEffects.get(i).getRelativeEffect();
		}
		
		return sumWeightRatio / computeSum(weights);
	}	
	
	protected double computeSum(List<Double> weights) {
		double weightSum = 0;
		for (int i=0; i < weights.size(); ++i) {
			weightSum += weights.get(i);
		}
		return weightSum;
	}
	
	public RelativeEffect<Measurement> getRelativeEffect(Drug d1, Drug d2, Class<? extends RelativeEffect<?>> type) {
		// check if drugs make sense
		List<Drug> askedDrugs = Arrays.asList(new Drug[]{d1,d2});
		if (!d_drugs.containsAll(askedDrugs))
			throw new IllegalArgumentException(d_name + " compares drugs " + d_drugs + " but " + askedDrugs + " were asked");
		
		// return measurement
		if(d1.equals(getFirstDrug()))
			compute(type, false);
		else 
			compute(type, true);
		
		return new RandomEffects(d_confidenceInterval, d_thetaDSL, d_totalSampleSize, d_SEThetaDSL, d_qIV);
	}
		
	public RandomEffectMetaAnalysisRelativeEffect<Measurement> getRelativeEffect(Class<? extends RelativeEffect<?>> type) {
		compute(type, false);
		return new RandomEffects(d_confidenceInterval, d_thetaDSL, d_totalSampleSize, d_SEThetaDSL, d_qIV);		
	}

	private class RandomEffects extends MetaAnalysisRelativeEffect<Measurement> implements RandomEffectMetaAnalysisRelativeEffect<Measurement> {
		public double t_qIV;

		public RandomEffects(Interval<Double> confidenceInterval, double relativeEffect, 
				int totalSampleSize, double stdDev, double qIV) {
			super(confidenceInterval, relativeEffect, totalSampleSize, stdDev);
			t_qIV = qIV;
		}
		
		public double getHeterogeneity() {
			return t_qIV;
		}
		
		public double getHeterogeneityI2() {
			int k = getIncludedStudies().size();
			return Math.max(0, 100* ((t_qIV - (k-1)) / t_qIV ) );
		}
	}
	
	protected static final XMLFormat<RandomEffectsMetaAnalysis> NETWORK_XML = 
		new XMLFormat<RandomEffectsMetaAnalysis>(RandomEffectsMetaAnalysis.class) {
		@Override
		public RandomEffectsMetaAnalysis newInstance(Class<RandomEffectsMetaAnalysis> cls, InputElement xml) {
			return new RandomEffectsMetaAnalysis();
		}

		@Override
		public void read(InputElement arg0, RandomEffectsMetaAnalysis arg1) throws XMLStreamException {
			XML.read(arg0, arg1);
		}

		@Override
		public void write(RandomEffectsMetaAnalysis arg0, OutputElement arg1) throws XMLStreamException {
			XML.write(arg0, arg1);
		}
	};
}


package org.drugis.addis.entities.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.AbstractEntity;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.relativeeffect.Distribution;

public class StudyBenefitRiskAnalysis extends AbstractEntity implements BenefitRiskAnalysis<Arm> {
	public static String PROPERTY_STUDY = "study";
	private Study d_study;
	private final String d_name;
	private final Indication d_indication;
	private final List<OutcomeMeasure> d_criteria;
	private final List<Arm> d_alternatives;
	
	public StudyBenefitRiskAnalysis(String name, Indication indication, Study study, 
			List<OutcomeMeasure> criteria, List<Arm> alternatives) {
		d_name = name;
		d_indication = indication;
		d_study = study;
		d_criteria = Collections.unmodifiableList(criteria);
		d_alternatives = Collections.unmodifiableList(alternatives);
	}

	@Override
	public Set<? extends Entity> getDependencies() {
		Set <Entity> deps = new HashSet<Entity>(d_study.getDependencies());
		deps.add(d_study);
		return deps;
	}

	public List<Arm> getAlternatives() {
		return d_alternatives;
	}

	public Indication getIndication() {
		return d_indication;
	}

	public Distribution getMeasurement(Arm alternative, OutcomeMeasure criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return d_name;
	}

	public List<OutcomeMeasure> getOutcomeMeasures() {
		return d_criteria;
	}

	public int compareTo(BenefitRiskAnalysis<?> o) {
		if (o == null)
			return 1;
		return d_name.compareTo(o.getName());
	}

	public Study getStudy() {
		return d_study;
	}

}

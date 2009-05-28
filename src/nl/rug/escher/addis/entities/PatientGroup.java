package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.jgoodies.binding.beans.Model;

@PersistenceCapable
public class PatientGroup extends Model {
	private Study d_study;
	private Integer d_size;
	private Drug d_drug;
	private Dose d_dose;
	private List<BasicMeasurement> d_measurements = new ArrayList<BasicMeasurement>();
	
	public static final String PROPERTY_STUDY = "study";
	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";
	public static final String PROPERTY_MEASUREMENTS = "measurements";
	public static final String PROPERTY_LABEL = "label";
	
	@Persistent
	public Study getStudy() {
		return d_study;
	}
	
	public void setStudy(Study study) {
		Study oldVal = d_study;
		d_study = study;
		firePropertyChange(PROPERTY_STUDY, oldVal, d_study);
	}
	
	@Persistent
	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		String oldLabel = getLabel();
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}
	
	@Persistent
	public Dose getDose() {
		return d_dose;
	}
	
	public void setDose(Dose dose) {
		String oldLabel = getLabel();
		Dose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}

	@Persistent
	public List<BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurements(List<BasicMeasurement> measurements) {
		List<BasicMeasurement> oldVal = d_measurements;
		d_measurements = measurements;
		firePropertyChange(PROPERTY_MEASUREMENTS, oldVal, d_measurements);
	}
	
	public void addMeasurement(BasicMeasurement m) {
		List<BasicMeasurement> newVal = new ArrayList<BasicMeasurement>(d_measurements);
		newVal.add(m);
		m.setPatientGroup(this);
		setMeasurements(newVal);
	}
	
	/**
	 * Get Measurement by Endpoint.
	 * @param endpoint Endpoint to get measurement for.
	 * @return Measurement if Endpoint is measured, null otherwise.
	 */
	public BasicMeasurement getMeasurement(Endpoint endpoint) {
		for (BasicMeasurement m : d_measurements) {
			if (m.getEndpoint().equals(endpoint)) {
				return m;
			}
		}
		return null;
	}
	
	public String getLabel() {
		if (d_drug == null || d_dose == null) {
			return "INCOMPLETE";
		}
		return d_drug.toString() + " " + d_dose.toString();
	}
	
	@Override
	public String toString() {
		return "PatientGroup(" + d_drug + ", " + d_dose + ", " + d_size + ")";
	}

	@Persistent
	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}
}

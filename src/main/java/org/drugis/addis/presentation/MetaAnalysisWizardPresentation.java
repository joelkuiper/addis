package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation {
	
	@SuppressWarnings("serial") class IndicationHolder extends AbstractHolder<Indication> {
		@Override
		protected void cascade() {
			d_endpointHolder.unSet();
		}
		
		@Override
		protected void checkArgument(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointHolder extends AbstractHolder<Endpoint> {
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getEndpointSet().contains(newValue))
					throw new IllegalArgumentException("Endpoint not in the actual set!");
		}

		@Override
		protected void cascade() {
			d_firstDrugHolder.unSet();
			d_secondDrugHolder.unSet();
		}
	}
	
	@SuppressWarnings("serial")
	private class DrugHolder extends AbstractHolder<Drug> {
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getDrugSet().contains(newValue))
					throw new IllegalArgumentException("Drug not in the actual set!");
		}

		protected void cascade() {
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationListHolder extends AbstractListHolder<Indication> {
		@Override
		public List<Indication> getValue() {
			return new ArrayList<Indication>(getIndicationSet());
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointListHolder extends AbstractListHolder<Endpoint> implements PropertyChangeListener {
		public EndpointListHolder() {
			getIndicationModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Endpoint> getValue() {
			return new ArrayList<Endpoint>(getEndpointSet());
		}

		public void propertyChange(PropertyChangeEvent event) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener {
		public DrugListHolder() {
			getEndpointModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(getDrugSet());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class StudyListHolder extends AbstractListHolder<Study> implements PropertyChangeListener {
		public StudyListHolder() {
			getFirstDrugModel().addValueChangeListener(this);
			getSecondDrugModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Study> getValue() {
			return getStudyList();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			updateStudyList();
			fireValueChange(null, getValue());
		}
	}
		
	private Domain d_domain;
	private AbstractHolder<Indication> d_indicationHolder;
	private AbstractHolder<Endpoint> d_endpointHolder;
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;	
	private DrugHolder d_firstDrugHolder;
	private DrugHolder d_secondDrugHolder;
	private EndpointListHolder d_endpointListHolder;
	private DrugListHolder d_drugListHolder;
	private DefaultSelectableStudyListPresentationModel d_studyListPm;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private List<Study> d_studyList = new ArrayList<Study>();
	
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_indicationHolder = new IndicationHolder();
		d_endpointHolder = new EndpointHolder();
		d_firstDrugHolder = new DrugHolder();
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();		
		d_secondDrugHolder = new DrugHolder();
		d_endpointListHolder = new EndpointListHolder();
		d_drugListHolder = new DrugListHolder();
		d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getSecondDrug())) {
					d_secondDrugHolder.unSet();
				}									
			}
		});
		d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getFirstDrug())) {
					d_firstDrugHolder.unSet();
				}									
			}			
		});
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.addSelectionListener(d_metaAnalysisCompleteListener);
		d_firstDrugHolder.addPropertyChangeListener(d_metaAnalysisCompleteListener);
		d_secondDrugHolder.addPropertyChangeListener(d_metaAnalysisCompleteListener);		
	}
		
	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	public AbstractListHolder<Endpoint> getEndpointListModel() {
		return d_endpointListHolder;
	}
	
	public SortedSet<Endpoint> getEndpointSet() {
		TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();
		if (getIndication() != null) {
			for (Study s : d_domain.getStudies(getIndication()).getValue()) {
				endpoints.addAll(s.getEndpoints());
			}			
		}	
		return endpoints;
	}
	
	public ValueModel getEndpointModel() {
		return d_endpointHolder;
	}
	
	public AbstractListHolder<Drug> getDrugListModel() {
		return d_drugListHolder;
	}
	
	public SortedSet<Drug> getDrugSet() {
		SortedSet<Drug> drugs = new TreeSet<Drug>();
		if (getIndication() != null && getEndpoint() != null) {
			List<Study> studies = getStudiesEndpointAndIndication();
			for (Study s : studies) {
				drugs.addAll(s.getDrugs());
			}
		}
		return drugs;
	}

	private List<Study> getStudiesEndpointAndIndication() {
		List<Study> studies = new ArrayList<Study>(d_domain.getStudies(getEndpoint()).getValue());
		studies.retainAll(d_domain.getStudies(getIndication()).getValue());
		return studies;
	}

	private Indication getIndication() {
		return d_indicationHolder.getValue();
	}

	private Endpoint getEndpoint() {
		return d_endpointHolder.getValue();
	}
	
	public ValueModel getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ValueModel getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	public List<Study> getStudyList() {
		return d_studyList;
	}
	
	private void updateStudyList() {
		List<Study> studies = new ArrayList<Study>();
		if (getSecondDrug() != null && getFirstDrug() != null) {
			studies = getStudiesEndpointAndIndication();
			studies.retainAll(d_domain.getStudies(getFirstDrug()).getValue());
			studies.retainAll(d_domain.getStudies(getSecondDrug()).getValue());
		}
		d_studyList = studies;
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	public RandomEffectsMetaAnalysis getAnalysis() {
		Endpoint e = d_domain.getEndpoints().first();
		return new RandomEffectsMetaAnalysis("", e, d_domain.getStudies(e).getValue(), getFirstDrug(), getSecondDrug());
	}
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
	}
	
	@SuppressWarnings("serial")
	public class StudiesMeasuringValueModel extends AbstractValueModel implements PropertyChangeListener {
		
		public StudiesMeasuringValueModel() {
			// NB indication listening automatically via endpoint cascade
			d_endpointHolder.addValueChangeListener(this);			
		}

		public Object getValue() {
			return constructString();
		}

		private Object constructString() {
			String indVal = d_indicationHolder.getValue() != null ? d_indicationHolder.getValue().toString() : "";
			String endpVal = d_endpointHolder.getValue() != null ? d_endpointHolder.getValue().toString() : "";
			return "Studies measuring " + indVal + " on " + endpVal;
		}
		
		public void setValue(Object newValue) {
			throw new RuntimeException("value set not allowed");
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, constructString());
		}		
	}

	public StudyCharTableModel getStudyTableModel() {
		return new SelectableStudyCharTableModel(d_studyListPm);
	}

	public RandomEffectsMetaAnalysis createMetaAnalysis() {
		return new RandomEffectsMetaAnalysis("", (Endpoint)getEndpointModel().getValue(),
				new ArrayList<Study>(getSelectedStudyList()), getFirstDrug(), getSecondDrug());
	}
	
	public List<Study> getSelectedStudyList() {
		return d_studyListPm.getSelectedStudies();
	}

	public RandomEffectsMetaAnalysis saveMetaAnalysis(String name, RandomEffectsMetaAnalysis ma) throws EntityIdExistsException {
		ma.setName(name);
		d_domain.addMetaAnalysis(ma);
		return ma;
	}
	
	public ValueModel getMetaAnalysisCompleteModel() {
		return d_metaAnalysisCompleteListener;
	}
	
	@SuppressWarnings("serial")
	public class MetaAnalysisCompleteListener extends AbstractValueModel implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTYNAME_VALUE, null, getValue());
		}

		public Object getValue() {
			return new Boolean(!getSelectedStudyList().isEmpty());
		}

		public void setValue(Object newValue) {			
		}		
	}
}

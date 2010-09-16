/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.gui.wizard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM;
import org.drugis.addis.presentation.wizard.BenefitRiskWizardPM.BRAType;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.LayoutUtil;
import org.pietschy.wizard.InvalidStateException;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.DynamicModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class BenefitRiskWizard extends Wizard {

	public BenefitRiskWizard(Main parent, BenefitRiskWizardPM pm) {
		super(buildModel(pm, parent));
		getTitleComponent().setPreferredSize(new Dimension(750, 100));
		setPreferredSize(new Dimension(750, 750));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
	}

	private static WizardModel buildModel(final BenefitRiskWizardPM pm, Main frame) {
		DynamicModel wizardModel = new DynamicModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectStudyOrMetaAnalysisWizardStep(pm, frame));
		wizardModel.add(new SelectStudyWizardStep(pm, frame), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getAnalysisType().getValue() == BRAType.SINGLE_STUDY_TYPE;
			}
		});
		wizardModel.add(new SelectOutcomeMeasuresAndArmsWizardStep(pm, frame), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getAnalysisType().getValue() == BRAType.SINGLE_STUDY_TYPE;
			}
		});
		wizardModel.add(new SelectCriteriaAndAlternativesWizardStep(pm, frame), new Condition() {
			public boolean evaluate(WizardModel model) {
				return pm.getAnalysisType().getValue() == BRAType.SYNTHESYS_TYPE;
			}
		});
		
		return wizardModel;
	}
	
	private static class SelectStudyOrMetaAnalysisWizardStep extends PanelWizardStep {
		public SelectStudyOrMetaAnalysisWizardStep(BenefitRiskWizardPM pm, Main main){
			super("Select Study or Meta-analysis","In this step, you select the criteria (analyses on specific outcomemeasures) and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least two criteria and at least two alternatives must be included.");

			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.Y_AXIS));
			 JRadioButton MetaAnalysisButton = BasicComponentFactory.createRadioButton(pm.getAnalysisType(), BRAType.SYNTHESYS_TYPE, "Evidence synthesis");
			 JRadioButton StudyButton = BasicComponentFactory.createRadioButton(pm.getAnalysisType(), BRAType.SINGLE_STUDY_TYPE, "Single Study");

			 radioButtonPanel.add(MetaAnalysisButton);
		     radioButtonPanel.add(StudyButton);
		    
		     add(radioButtonPanel);
		    
		     setComplete(true);
		}
	}

	private static class SelectStudyWizardStep extends PanelWizardStep {
		public SelectStudyWizardStep(final BenefitRiskWizardPM pm, Main main){
			super("Select Study or Meta-analysis","test");
			
			JComboBox studyBox = AuxComponentFactory.createBoundComboBox(pm.getStudiesWithIndication(), pm.getStudyModel());
			add(studyBox);
			pm.getStudyModel().addValueChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setComplete(evt.getNewValue() != null);
				}
			});
		}
	}

	private static class SelectOutcomeMeasuresAndArmsWizardStep extends PanelWizardStep {
		private Main d_main;
		private BenefitRiskWizardPM d_pm;
	
		public SelectOutcomeMeasuresAndArmsWizardStep(BenefitRiskWizardPM pm, Main main) {
			super("Select OutcomeMeasures and Arms","test");
			// TODO : Add text
			d_main = main;
			d_pm = pm;			
		}
		
		@Override
		public void prepare() {
			this.removeAll();
			add(buildPanel());
			Bindings.bind(this, "complete", d_pm.getCompleteModel());
		}
		
		@Override
		public void applyState() throws InvalidStateException {
			saveAsAnalysis();
		}
		
		private void saveAsAnalysis() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
					"Input name for new analysis", 
					"Save analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					d_main.leftTreeFocus(d_pm.saveAnalysis(res));
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
							"There already exists an analysis with the given name, input another name",
							"Unable to save analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}
		private JPanel buildPanel() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildOutcomeMeasuresPane(d_pm), cc.xy(1, 1));
			builder.add(buildArmsPane(d_pm), cc.xy(3, 1));
			
			return builder.getPanel();
		}

		private Component buildOutcomeMeasuresPane(BenefitRiskWizardPM pm) {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel outcomeMeasuresLabel = new JLabel("Criteria");
			outcomeMeasuresLabel.setFont(
				outcomeMeasuresLabel.getFont().deriveFont(Font.BOLD));
			builder.add(outcomeMeasuresLabel, cc.xy(1, 1));
			int row = 1;
			for(OutcomeMeasure out : d_pm.getStudyModel().getValue().getOutcomeMeasures()){
				// Add outcome measure checkbox
				row += 2;
				LayoutUtil.addRow(layout);
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(d_pm.getOutcomeSelectedModel(out), out.getName());
				builder.add(checkBox, cc.xyw(1, row, 3));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}

		private Component buildArmsPane(BenefitRiskWizardPM pm) {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel alternativesLabel = new JLabel("Alternatives");
			alternativesLabel.setFont(alternativesLabel.getFont().deriveFont(Font.BOLD));
			builder.add(alternativesLabel, cc.xy(1, 1));
			
			int row = 1;
			for(Arm a : d_pm.getStudyModel().getValue().getArms() ){
				LayoutUtil.addRow(layout);
				ValueHolder<Boolean> selectedModel = d_pm.getArmSelectedModel(a);
				selectedModel.setValue(true);
				JCheckBox armCheckbox = BasicComponentFactory.createCheckBox(selectedModel, a.getDrug().getName());
				builder.add(armCheckbox, cc.xy(1, row += 2));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}		
	}

	private static class SelectCriteriaAndAlternativesWizardStep extends PanelWizardStep {
		private Main d_main;
		private BenefitRiskWizardPM d_pm;

		public SelectCriteriaAndAlternativesWizardStep(BenefitRiskWizardPM pm, Main main){
			super("Select Criteria and Alternatives","In this step, you select the criteria (analyses on specific outcomemeasures) and the alternatives (drugs) to include in the benefit-risk analysis. To perform the analysis, at least two criteria and at least two alternatives must be included.");//			JComboBox indBox = AuxComponentFactory.createBoundComboBox(pm.getIndicationListModel(), pm.getIndicationModel());
			d_main = main;
			d_pm = pm;
		}

		@Override
		public void prepare() {
			this.removeAll();
			add(buildPanel());
			Bindings.bind(this, "complete", d_pm.getCompleteModel());
		}
		
		@Override
		public void applyState() throws InvalidStateException {
			saveAsAnalysis();
		}

		private void saveAsAnalysis() throws InvalidStateException {
			String res = JOptionPane.showInputDialog(this.getTopLevelAncestor(),
					"Input name for new analysis", 
					"Save analysis", JOptionPane.QUESTION_MESSAGE);
			if (res != null) {
				try {
					d_main.leftTreeFocus(d_pm.saveAnalysis(res));
				} catch (EntityIdExistsException e) {
					JOptionPane.showMessageDialog(this.getTopLevelAncestor(), 
							"There already exists an analysis with the given name, input another name",
							"Unable to save analysis", JOptionPane.ERROR_MESSAGE);
					saveAsAnalysis();
				}
			} else {
				throw new InvalidStateException();
			}
		}

		private JPanel buildPanel() {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildCriteriaPane(d_pm), cc.xy(1, 1));
			builder.add(buildAlternativesPane(d_pm), cc.xy(3, 1));
			
			return builder.getPanel();
		}

		private Component buildCriteriaPane(BenefitRiskWizardPM pm) {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel criteriaLabel = new JLabel("Criteria");
			criteriaLabel.setFont(
				criteriaLabel.getFont().deriveFont(Font.BOLD));
			builder.add(criteriaLabel, cc.xy(1, 1));
			int row = 1;
			for(OutcomeMeasure out : d_pm.getOutcomesListModel().getValue()){
				if(d_pm.getMetaAnalyses(out).isEmpty())
					continue;

				// Add outcome measure checkbox
				row += 2;
				LayoutUtil.addRow(layout);
				JCheckBox checkBox = BasicComponentFactory.createCheckBox(d_pm.getOutcomeSelectedModel(out), out.getName());
				builder.add(checkBox, cc.xyw(1, row, 3));
				
				// Add radio-button panel
				row += 2;
				LayoutUtil.addRow(layout);
				builder.add(buildRadioButtonAnalysisPanel(out), cc.xy(3, row, CellConstraints.LEFT, CellConstraints.DEFAULT));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}

		private JPanel buildRadioButtonAnalysisPanel(OutcomeMeasure out) {
			// create the panel
			JPanel radioButtonPanel = new JPanel();
			radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel,BoxLayout.Y_AXIS));
			
			// Retrieve the valueModel to see whether we should enable the radio-buttons.
			ValueHolder<Boolean> enabledModel = d_pm.getOutcomeSelectedModel(out);
			
			// Add the radio buttons
			for(MetaAnalysis ma : d_pm.getMetaAnalyses(out)){
				ValueHolder<MetaAnalysis> selectedModel = d_pm.getMetaAnalysesSelectedModel(out);
				JRadioButton radioButton = AuxComponentFactory.createDynamicEnabledRadioButton(ma.getName(), ma, selectedModel, enabledModel);
				radioButtonPanel.add(radioButton);
			}
			return radioButtonPanel;
		}

		private Component buildAlternativesPane(BenefitRiskWizardPM pm) {
			FormLayout layout = new FormLayout(
					"left:pref, 3dlu, left:pref",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			
			JLabel alternativesLabel = new JLabel("Alternatives");
			alternativesLabel.setFont(alternativesLabel.getFont().deriveFont(Font.BOLD));
			builder.add(alternativesLabel, cc.xy(1, 1));
			
			int row = 1;
			for( Drug d : d_pm.getAlternativesListModel().getValue() ){
				LayoutUtil.addRow(layout);
				ValueHolder<Boolean> enabledModel  = d_pm.getAlternativeEnabledModel(d);
				ValueHolder<Boolean> selectedModel = d_pm.getAlternativeSelectedModel(d);
				
				JCheckBox drugCheckbox = AuxComponentFactory.createDynamicEnabledBoundCheckbox(d.getName(), enabledModel, selectedModel);
				builder.add(drugCheckbox, cc.xy(1, row += 2));
			}
			
			return AuxComponentFactory.createInScrollPane(builder, 350, 550);
		}
	}
}

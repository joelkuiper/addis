package org.drugis.addis.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.StudyGraph;
import org.drugis.addis.gui.builder.RandomEffectsMetaAnalysisView;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.wizard.MetaAnalysisWizardPresentation;
import org.drugis.common.gui.AuxComponentFactory;
import org.drugis.common.gui.ViewBuilder;
import org.pietschy.wizard.PanelWizardStep;
import org.pietschy.wizard.Wizard;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.StaticModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class MetaAnalysisWizard extends Wizard {
	
	public MetaAnalysisWizard(Main parent, MetaAnalysisWizardPresentation pm) {
		super(buildModel(pm, parent));
		setDefaultExitMode(Wizard.EXIT_ON_FINISH);
		setPreferredSize(new Dimension(950, 650));
	}
	
	private static WizardModel buildModel(MetaAnalysisWizardPresentation pm, Main frame) {
		StaticModel wizardModel = new StaticModel();
		wizardModel.add(new SelectIndicationWizardStep(pm));
		wizardModel.add(new SelectEndpointWizardStep(pm));
		wizardModel.add(new SelectDrugsWizardStep(pm, frame));
		SelectStudiesWizardStep selectStudiesStep = new SelectStudiesWizardStep(pm, frame);
		wizardModel.add(selectStudiesStep);
		Bindings.bind(selectStudiesStep, "complete", pm.getMetaAnalysisCompleteModel());
		wizardModel.add(new SelectArmsWizardStep(pm));
		wizardModel.add(new OverviewWizardStep(pm, frame));
		return wizardModel;
	}

	public static class OverviewWizardStep extends AbstractOverviewWizardStep<StudyGraphModel> {
		public OverviewWizardStep(MetaAnalysisWizardPresentation pm, Main frame) {
			super(pm, frame);
		}
		
		public void prepare() {
			removeAll();
			
			RandomEffectsMetaAnalysis analysis = (RandomEffectsMetaAnalysis)d_pm.createMetaAnalysis("");
			PresentationModel<RandomEffectsMetaAnalysis> pm = d_main.getPresentationModelFactory().getModel(analysis);
			ViewBuilder mav = new RandomEffectsMetaAnalysisView(
					(RandomEffectsMetaAnalysisPresentation)pm, d_main, true);
			add(mav.buildPanel());
			setComplete(true);
		}
	}

	public static class SelectDrugsWizardStep extends PanelWizardStep {
		MetaAnalysisWizardPresentation d_pm;
		Main d_frame;

		public SelectDrugsWizardStep(MetaAnalysisWizardPresentation pm, Main frame) {
			super("Select Drugs","Select the drugs to be used for meta analysis.");
			
			d_pm = pm;
			d_frame = frame;
					
			setLayout(new BorderLayout());
			    
			FormLayout layout = new FormLayout(
					"center:pref:grow",
					"p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			
			builder.add(buildSelectDrugsPanel(), cc.xy(1, 1));			
			builder.add(BasicComponentFactory.createLabel(d_pm.getStudiesMeasuringLabelModel()),
					cc.xy(1, 3));
			builder.setBorder(BorderFactory.createEmptyBorder());
			builder.add(buildStudiesGraph(), cc.xy(1, 5));
			
			JScrollPane sp = new JScrollPane(builder.getPanel());
			add(sp);
			sp.getVerticalScrollBar().setUnitIncrement(16);			
			
			Bindings.bind(this, "complete", d_pm.getMetaAnalysisCompleteModel());
		}
		
		private Component buildStudiesGraph() {
			StudyGraphModel pm = d_pm.getStudyGraphModel();
			StudyGraph panel = new StudyGraph(pm);
			return panel;
		}

		private JPanel buildSelectDrugsPanel() {
			FormLayout layout = new FormLayout(
					"center:pref, 3dlu, center:pref, 3dlu, center:pref",
					"p, 3dlu, p, 3dlu, p, 3dlu, p"
					);	
			
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			
			CellConstraints cc = new CellConstraints();
			builder.addLabel("First Drug",cc.xy(1, 1));
			builder.addLabel("Second Drug",cc.xy(5, 1));
						
			JComboBox firstDrugBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), d_pm.getFirstDrugModel());
			JComboBox secondDrugBox = AuxComponentFactory.createBoundComboBox(d_pm.getDrugListModel(), d_pm.getSecondDrugModel());
			
			builder.add(firstDrugBox,cc.xy(1, 3));
			builder.add(secondDrugBox,cc.xy(5, 3));
			builder.addLabel("VS",cc.xy(3, 3));
			JPanel panel = builder.getPanel();			
			
			return panel;
		}
	}	
}
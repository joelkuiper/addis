package org.drugis.addis.gui.builder;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel.PreferenceType;
import fi.smaa.jsmaa.gui.views.CardinalPreferencesView;
import fi.smaa.jsmaa.gui.views.DefaultScaleRenderer;
import fi.smaa.jsmaa.gui.views.OrdinalPreferencesView;
import fi.smaa.jsmaa.gui.views.ScaleRenderer;
import fi.smaa.jsmaa.model.CardinalPreferenceInformation;
import fi.smaa.jsmaa.model.OrdinalPreferenceInformation;
import fi.smaa.jsmaa.model.SMAAModel;

public class ModifiedPrefInfoView implements ViewBuilder {
	private PreferencePresentationModel model;
	private JComponent prefPanel;
	private ScaleRenderer renderer;
	
	public ModifiedPrefInfoView(PreferencePresentationModel model) {
		this.model = model;
		this.renderer = new DefaultScaleRenderer();
	}
	
	public ModifiedPrefInfoView(PreferencePresentationModel model, ScaleRenderer renderer) {
		this.model = model;
		this.renderer = renderer;
	}	

	@SuppressWarnings("serial")
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, left:pref:grow",
				"p, 3dlu, p");
		
		int fullWidth = 3;
		
		ValueModel preferenceTypeModel = model.getModel(PreferencePresentationModel.PREFERENCE_TYPE);

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		SelectionInList<PreferenceType> typeSelInList // FIXME: the only difference is here.
			= new SelectionInList<PreferenceType>(new PreferenceType[] { PreferenceType.MISSING, PreferenceType.ORDINAL }, preferenceTypeModel);
		

		JComboBox preferenceTypeBox = BasicComponentFactory.createComboBox(typeSelInList);
		builder.add(preferenceTypeBox, cc.xy(1, 1));

		builder.addLabel("Preference information", cc.xyw(3, 1, fullWidth-2));

		prefPanel = null;
		if (model.getPreferenceType() == PreferenceType.ORDINAL) {
			SMAAModel smodel = model.getBean();
			OrdinalPreferencesView oview = new OrdinalPreferencesView((OrdinalPreferenceInformation) smodel.getPreferenceInformation());
			oview.setScaleRenderer(renderer);
			prefPanel = oview.buildPanel();
		} else if (model.getPreferenceType() == PreferenceType.CARDINAL) {
			CardinalPreferencesView oview = new CardinalPreferencesView(
					(CardinalPreferenceInformation) model.getBean().getPreferenceInformation());
			oview.setScaleRenderer(renderer);			
			prefPanel = oview.buildPanel();
		}
		if (prefPanel != null) {
			builder.add(prefPanel, cc.xyw(1, 3, fullWidth));
		}
		
		preferenceTypeBox.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (prefPanel != null) {
					prefPanel.requestFocusInWindow();
				}
			}
		});
		
		return builder.getPanel();
	}
}
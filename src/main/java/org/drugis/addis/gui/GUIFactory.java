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

package org.drugis.addis.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drugis.addis.FileNames;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.gui.components.LinkLabel;
import org.drugis.addis.gui.components.StudiesTablePanel;
import org.drugis.addis.presentation.StudyListPresentationModel;
import org.drugis.common.ImageLoader;
import org.jdesktop.swingx.JXCollapsiblePane;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jidesoft.swing.JideButton;

public class GUIFactory {
	public static JButton createPlusButton(String toolTipText) {
		return createIconButton(FileNames.ICON_PLUS, toolTipText);
	}

	public static JButton createIconButton(String iconName, String toolTipText) {
		Icon icon = ImageLoader.getIcon(iconName);
		JButton button = new JButton(icon);
		button.setToolTipText(toolTipText);
		return button;
	}
	
	public static JPanel createCollapsiblePanel(JComponent innerComp) {
		JPanel topPane = new JPanel(new BorderLayout());
		JXCollapsiblePane pane = new JXCollapsiblePane();
		pane.setLayout(new BorderLayout());
		pane.setAnimated(true);
		pane.add(innerComp);
		
		 // get the built-in toggle action
		 Action toggleAction = pane.getActionMap().
		   get(JXCollapsiblePane.TOGGLE_ACTION);

		 // use the collapse/expand icons from the JTree UI
		 toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON,
				 ImageLoader.getIcon(FileNames.ICON_COLLAPSE));
		 toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON,
				 ImageLoader.getIcon(FileNames.ICON_EXPAND));
		
		topPane.add(pane, BorderLayout.CENTER);
		JideButton button = new JideButton(pane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
		button.setText("");
		topPane.add(button, BorderLayout.SOUTH);

		return topPane;
	}	

	public static JComponent createOutcomeMeasureLabelWithIcon(OutcomeMeasure e) {
		String fname = FileNames.ICON_STUDY;
		if (e instanceof Endpoint) {
			fname = FileNames.ICON_ENDPOINT;
		} if (e instanceof AdverseEvent) {
			fname = FileNames.ICON_ADVERSE_EVENT;
		}
		JLabel textLabel = null;
		Icon icon = ImageLoader.getIcon(fname);
		textLabel = new JLabel(e.getName(), icon, JLabel.CENTER);			
		
		Bindings.bind(textLabel, "text", 
				new PresentationModel<OutcomeMeasure>(e).getModel(OutcomeMeasure.PROPERTY_NAME));
		return textLabel;
	}

	public static JComponent buildStudyPanel(StudyListPresentationModel studies, Main parent) {
		JComponent studiesComp = null;
		if(studies.getIncludedStudies().getValue().isEmpty()) {
			studiesComp = new JLabel("No studies found.");
		} else {
			studiesComp = new StudiesTablePanel(studies, parent); 
		}
		return studiesComp;
	}	
	
	public static JLabel buildSiteLink() {
		return new LinkLabel("www.drugis.org", "http://drugis.org/");
	}

	public static JComboBox createDrugSelector(AbstractValueModel drugModel,
			Domain domain) {
		SelectionInList<Drug> drugSelectionInList =
			new SelectionInList<Drug>(
					new ArrayList<Drug>(domain.getDrugs()),
					drugModel);
		return BasicComponentFactory.createComboBox(drugSelectionInList);
	}
}

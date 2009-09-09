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

package org.drugis.addis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class NotEmptyValidator {
	
	private List<JComponent> d_fields = new ArrayList<JComponent>();
	private DocumentListener d_myTextListener = new MyTextListener();
	private ActionListener d_myActionListener = new ComboBoxListener();
	private JButton button;
	
	public NotEmptyValidator(JButton button) {
		this.button = button;
	}
	
	public void add(JComponent field) {
		d_fields.add(field);
		if (field instanceof JTextComponent) {
			((JTextComponent) field).getDocument().addDocumentListener(d_myTextListener);
		} else if (field instanceof JComboBox) {
			((JComboBox) field).addActionListener(d_myActionListener);
		}
		checkFieldsEmpty();
	}
	
	private void checkFieldsEmpty() {
		boolean empty = false;
		for (JComponent f : d_fields) {
			if (f instanceof JTextComponent){
				JTextComponent tf = (JTextComponent) f;
				if (tf.getText().length() == 0) {
					empty = true;
					break;
				}
			} else if (f instanceof JComboBox) {				
				if (((JComboBox) f).getSelectedItem() == null) {
					empty = true;
					break;
				}
			}
		}
		button.setEnabled(!empty);
	}	
	
	@SuppressWarnings("serial")
	private class ComboBoxListener extends AbstractAction {
		public void actionPerformed(ActionEvent arg0) {
			checkFieldsEmpty();
		}		
	}
	
	private class MyTextListener implements DocumentListener {
		public void changedUpdate(DocumentEvent arg0) {
			checkFieldsEmpty();			
		}

		public void insertUpdate(DocumentEvent arg0) {
			checkFieldsEmpty();
		}

		public void removeUpdate(DocumentEvent arg0) {
			checkFieldsEmpty();
		}
	}
}

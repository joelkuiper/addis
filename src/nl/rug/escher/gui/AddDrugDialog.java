package nl.rug.escher.gui;

import javax.swing.JFrame;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.Drug;

import com.jgoodies.binding.PresentationModel;

public class AddDrugDialog extends OkCancelDialog {
	private Domain d_domain;
	private Drug d_drug;
	
	public AddDrugDialog(JFrame frame, Domain domain) {
		super(frame, "Add Drug");
		d_domain = domain;
		d_drug = new Drug();
		DrugView view = new DrugView(new PresentationModel<Drug>(d_drug));
		getUserPanel().add(view.buildPanel());
		pack();
	}
	
	@Override
	protected void cancel() {
		setVisible(false);
	}
	
	@Override
	protected void commit() {
		d_domain.addDrug(d_drug);
		System.out.println("Drugs: " + d_domain.getDrugs());
		setVisible(false);
	}
}

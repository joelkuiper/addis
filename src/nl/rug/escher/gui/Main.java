package nl.rug.escher.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import nl.rug.escher.entities.Domain;
import nl.rug.escher.entities.DomainImpl;
import nl.rug.escher.entities.Drug;
import nl.rug.escher.entities.Endpoint;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

public class Main extends JFrame {
	private JMenuBar d_menuBar;
	private JComponent d_leftPanel;
	private JComponent d_rightPanel;
	
	private Domain d_domain;

	public Main() {
		super("Escher ADDIS");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	
		setPreferredSize(new Dimension(800, 500));
		initializeLookAndFeel();
		
		d_domain = new DomainImpl();
		
		initDefaultData();
	}

	private void initDefaultData() {
		d_domain.addEndpoint(buildDefaultEndpoint());
		d_domain.addEndpoint(buildDefaultEndpoint2());
		d_domain.addDrug(buildDefaultDrug1());
		d_domain.addDrug(buildDefaultDrug2());
	}
	
	private Drug buildDefaultDrug2() {
		Drug drug = new Drug();
		drug.setName("Paroxetine");
		return drug;
	}

	private Drug buildDefaultDrug1() {
		Drug drug = new Drug();
		drug.setName("Fluoxetine");
		return drug;
	}

	public static Endpoint buildDefaultEndpoint() {
		Endpoint endpoint = new Endpoint();
		endpoint.setName("HAM-D");
		endpoint.setDescription("Change from baseline in HAM-D total score (21 items)");
		return endpoint;
	}
	
	public static Endpoint buildDefaultEndpoint2() {
		Endpoint endpoint = new Endpoint();
		endpoint.setName("CGI Severity");
		endpoint.setDescription("Change from baseline CGI Severity of Illness score");
		return endpoint;
	}
	
	private void initializeLookAndFeel() {
		try {
			String osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				UIManager.setLookAndFeel(new WindowsLookAndFeel());
			} else  if (osName.startsWith("Mac")) {
				// do nothing, use the Mac Aqua L&f
			} else {
				try {
					UIManager.setLookAndFeel(new GTKLookAndFeel());
				} catch (Exception e) {
					UIManager.setLookAndFeel(new PlasticLookAndFeel());
				}
			}
		} catch (Exception e) {
			// Likely the Looks library is not in the class path; ignore.
		}
	}

	private void initMenu() {
		d_menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		
		fileMenu.add(initExitItem());
		d_menuBar.add(fileMenu);
		
		d_menuBar.add(createAddEndpointButton());
		d_menuBar.add(createAddStudyButton());
		d_menuBar.add(createAddDrugButton());
		
		add(d_menuBar, BorderLayout.NORTH);
	}

	private JComponent createAddEndpointButton() {
		JButton button = new JButton("Add Endpoint");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddEndpointDialog();
			}
		});
		
		return button;
	}
	
	private JComponent createAddStudyButton() {
		JButton button = new JButton("Add Study");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddStudyDialog();
			}
		});
		
		return button;
	}
	
	private JComponent createAddDrugButton() {
		JButton button = new JButton("Add Drug");
		button.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				showAddDrugDialog();
			}

		});
		
		return button;
	}
	
	private void showAddEndpointDialog() {
		AddEndpointDialog dialog = new AddEndpointDialog(this, d_domain);
		dialog.setVisible(true);
	}
	
	private void showAddStudyDialog() {
		AddStudyDialog dialog = new AddStudyDialog(this, d_domain);
		dialog.setVisible(true);
	}
	
	private void showAddDrugDialog() {
		AddDrugDialog dialog = new AddDrugDialog(this, d_domain);
		dialog.setVisible(true);
	}

	private JMenuItem initExitItem() {
		JMenuItem exitItem = new JMenuItem("Exit");
		
		exitItem.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		
		exitItem.setMnemonic('e');
		
		return exitItem;
	}
	
	private void exit() {
		System.exit(0);
	}
	
	public void initComponents() {
		initMenu();
		initPanel();
	}
	
	private void initPanel() {
		JSplitPane pane = new JSplitPane();
		
		initLeftPanel();
		pane.setLeftComponent(d_leftPanel);
		
		initRightPanel();
		pane.setRightComponent(d_rightPanel);
		
		add(pane);
	}

	private void initLeftPanel() {
		DomainTreeModel model = new DomainTreeModel(d_domain);
		JTree tree = new JTree(model);
		tree.setRootVisible(false);
		tree.expandPath(new TreePath(new Object[]{model.getRoot(), model.getEndpointsNode()}));
		d_leftPanel = tree;
	}
	
	private void initRightPanel() {
		JPanel panel = new JPanel();
		d_rightPanel = new JScrollPane(panel);
	}

	public static void main(String[] args) {
		Main frame = new Main();
		frame.initComponents();
		frame.pack();
		frame.setVisible(true);
	}
}
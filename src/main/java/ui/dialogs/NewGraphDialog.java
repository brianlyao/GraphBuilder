package ui.dialogs;

import context.GBContext;
import graph.Graph;
import graph.GraphConstraint;
import ui.GBFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Dialog prompting the user to start a new graph. The user specifies the
 * type of graph they intend to create.
 *
 * @author Brian Yao
 */
public class NewGraphDialog extends JDialog {

	private static final long serialVersionUID = 6102194091921014637L;

	private static final int GAP = 20;

	private static final String NEW_FILE_TITLE = "GraphBuilder: New File";
	private static final String CHANGE_CONSTRAINTS_TITLE = "GraphBuilder: Modify Constraints";

	private static final String[] GRAPH_TYPES = {"Graph"};

	private static final int OK_OPTION = 1;
	private static final int CANCEL_OPTION = 2;

	private JPanel graphTypePanel;
	private JComboBox<String> graphTypeComboBox;

	private JPanel graphPanel;

	private JPanel constraintPanel;
	private JRadioButton simple;
	private JRadioButton multigraph;

	private JPanel weightPanel;
	private JRadioButton unweighted;
	private JRadioButton weighted;

	private JPanel edgePanel;
	private JRadioButton onlyUndirected;
	private JRadioButton onlyDirected;
	private JRadioButton mixed;

	private JPanel relevantPanel;

	private JPanel buttonPanel;
	private JButton okButton;
	private JButton cancelButton;

	private int constraints;
	private int option;

	/**
	 * @param g The GBFrame this dialog is displayed alongside.
	 */
	public NewGraphDialog(GBFrame g) {
		super(g, NEW_FILE_TITLE);

		setSize(800, 500);
		setVisible(false);
		setResizable(false);
		setLayout(new GridBagLayout());

		graphTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		graphTypePanel.setBorder(BorderFactory.createTitledBorder("Select Structure Type"));
		graphTypeComboBox = new JComboBox<String>(GRAPH_TYPES);
		graphTypeComboBox.setEditable(false);
		graphTypeComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				CardLayout cl = (CardLayout) relevantPanel.getLayout();
				cl.show(relevantPanel, (String) e.getItem());
			}

		});

		graphTypePanel.add(new JLabel("Choose the structure you want to create:"));
		graphTypePanel.add(graphTypeComboBox);

		// Create the panel with edge behavior settings
		constraintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		constraintPanel.setBorder(BorderFactory.createTitledBorder("Constraints (Edge Behavior)"));
		constraintPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		ButtonGroup constraintGroup = new ButtonGroup();
		simple = new JRadioButton("Simple Graph");
		simple.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.GRAPH_TYPE_MASK) | GraphConstraint.SIMPLE;
				}
			}

		});

		multigraph = new JRadioButton("Multigraph");
		multigraph.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.GRAPH_TYPE_MASK) | GraphConstraint.MULTIGRAPH;
				}
			}

		});
		constraintGroup.add(simple);
		constraintGroup.add(multigraph);

		constraintPanel.add(simple);
		constraintPanel.add(multigraph);

		// Create the panel with edge type settings
		edgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		edgePanel.setBorder(BorderFactory.createTitledBorder("Allowed Edge Types"));
		edgePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		ButtonGroup edgeGroup = new ButtonGroup();
		onlyUndirected = new JRadioButton("Only Undirected");
		onlyUndirected.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.EDGE_BEHAVIOR_MASK) | GraphConstraint.UNDIRECTED;
				}
			}

		});

		onlyDirected = new JRadioButton("Only Directed");
		onlyDirected.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.EDGE_BEHAVIOR_MASK) | GraphConstraint.DIRECTED;
				}
			}

		});

		mixed = new JRadioButton("Both");
		mixed.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.EDGE_BEHAVIOR_MASK) | GraphConstraint.EDGE_BEHAVIOR_MASK;
				}
			}

		});

		edgeGroup.add(onlyUndirected);
		edgeGroup.add(onlyDirected);
		edgeGroup.add(mixed);
		edgePanel.add(onlyUndirected);
		edgePanel.add(onlyDirected);
		edgePanel.add(mixed);

		// Create the panel with weighted edge settings
		weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		weightPanel.setBorder(BorderFactory.createTitledBorder("Weight Settings"));
		weightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		ButtonGroup weightGroup = new ButtonGroup();
		unweighted = new JRadioButton("Unweighted");
		unweighted.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.WEIGHT_MASK) | GraphConstraint.UNWEIGHTED;
				}
			}

		});

		weighted = new JRadioButton("Numerical Weights");
		weighted.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					constraints = (constraints & ~GraphConstraint.WEIGHT_MASK) | GraphConstraint.WEIGHTED;
				}
			}

		});

		weightGroup.add(unweighted);
		weightGroup.add(weighted);
		weightPanel.add(unweighted);
		weightPanel.add(weighted);

		// Create the panel for regular graphs
		graphPanel = new JPanel();
		graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));

		Box constraintBox = Box.createHorizontalBox();
		constraintBox.add(Box.createHorizontalStrut(GAP));
		constraintBox.add(constraintPanel);
		constraintBox.add(Box.createHorizontalStrut(GAP));

		Box edgeBox = Box.createHorizontalBox();
		edgeBox.add(Box.createHorizontalStrut(GAP));
		edgeBox.add(edgePanel);
		edgeBox.add(Box.createHorizontalStrut(GAP));

		Box weightBox = Box.createHorizontalBox();
		weightBox.add(Box.createHorizontalStrut(GAP));
		weightBox.add(weightPanel);
		weightBox.add(Box.createHorizontalStrut(GAP));

		graphPanel.add(Box.createVerticalStrut(GAP));
		graphPanel.add(constraintBox);
		graphPanel.add(Box.createVerticalStrut(GAP));
		graphPanel.add(edgeBox);
		graphPanel.add(Box.createVerticalStrut(GAP));
		graphPanel.add(weightBox);
		graphPanel.add(Box.createVerticalStrut(GAP));

		// Create the main panel for displaying structure-specific options
		relevantPanel = new JPanel(new CardLayout());
		relevantPanel.setBorder(BorderFactory.createTitledBorder("Structure-specific Settings"));
		relevantPanel.add(graphPanel, GRAPH_TYPES[0]);

		// Create the panel with ok and cancel buttons
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setAlignmentX(RIGHT_ALIGNMENT);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				option = OK_OPTION;
				dispose();
			}

		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				option = CANCEL_OPTION;
				dispose();
			}

		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		// Initialize with default settings
		graphTypeComboBox.setSelectedIndex(0);
		simple.setSelected(true);
		onlyUndirected.setSelected(true);
		unweighted.setSelected(true);

		// Add panels in order
		GridBagConstraints graphTypeConstraints = new GridBagConstraints();
		GridBagConstraints relevantPanelConstraints = new GridBagConstraints();
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();

		graphTypeConstraints.gridx = 0;
		graphTypeConstraints.gridy = 0;
		graphTypeConstraints.gridwidth = 1;
		graphTypeConstraints.gridheight = 1;
		graphTypeConstraints.weightx = 1;
		graphTypeConstraints.weighty = 0.15;
		add(graphTypePanel, graphTypeConstraints);

		relevantPanelConstraints.gridx = 0;
		relevantPanelConstraints.gridy = 1;
		relevantPanelConstraints.gridwidth = 1;
		relevantPanelConstraints.gridheight = 1;
		relevantPanelConstraints.weightx = 1;
		relevantPanelConstraints.weighty = 0.75;
		add(relevantPanel, relevantPanelConstraints);

		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 2;
		buttonPanelConstraints.gridwidth = 1;
		buttonPanelConstraints.gridheight = 1;
		buttonPanelConstraints.weightx = 1;
		buttonPanelConstraints.weighty = 0.1;
		add(buttonPanel, buttonPanelConstraints);

//		pack();
		setLocationRelativeTo(g);
	}

	/**
	 * Display a dialog for selecting the type of graph the user would like
	 * to create from scratch. Return the kind of graph chosen in the form of
	 * an integer bit field.
	 *
	 * @param parent The parent component, or null if there is none.
	 * @return The constraints on the graph, in bit field form.
	 */
	public static Integer getConstraints(GBFrame parent) {
		NewGraphDialog dialog = new NewGraphDialog(parent);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
		int constraints = dialog.constraints;
		int option = dialog.option;
		dialog.setVisible(false);
		dialog.dispose();
		if (option == OK_OPTION) {
			return constraints;
		} else {
			return null;
		}
	}

	/**
	 * Change the constraints from the current constraints.
	 *
	 * @param parent     The parent GBFrame.
	 * @param oldContext The context we are changing from.
	 * @return The new constraints, or null if the user pressed cancel.
	 */
	public static Integer modifyConstraints(GBFrame parent, GBContext oldContext) {
		NewGraphDialog dialog = new NewGraphDialog(parent);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setTitle(CHANGE_CONSTRAINTS_TITLE);

		// Pre-select the current constraints
		Graph oldGraph = oldContext.getGraph();
		if (oldGraph.hasConstraint(GraphConstraint.SIMPLE)) {
			dialog.simple.setSelected(true);
		} else {
			dialog.multigraph.setSelected(true);
		}
		boolean undirected = oldGraph.hasConstraint(GraphConstraint.UNDIRECTED);
		boolean directed = oldGraph.hasConstraint(GraphConstraint.DIRECTED);
		if (undirected && directed) {
			dialog.mixed.setSelected(true);
		} else if (directed) {
			dialog.onlyDirected.setSelected(true);
		} else {
			dialog.onlyUndirected.setSelected(true);
		}
		if (oldGraph.hasConstraint(GraphConstraint.WEIGHTED)) {
			dialog.weighted.setSelected(true);
		} else {
			dialog.unweighted.setSelected(true);
		}

		dialog.setVisible(true);
		int constraints = dialog.constraints;
		int option = dialog.option;
		dialog.setVisible(false);
		dialog.dispose();
		if (option == OK_OPTION) {
			// TODO: Check to see whether the current graph violates new constraints
			return constraints;
		} else {
			return null;
		}
	}

}

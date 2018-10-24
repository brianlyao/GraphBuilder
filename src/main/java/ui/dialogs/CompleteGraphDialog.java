package ui.dialogs;

import actions.GenerateRadialGraphAction;
import graph.Graph;
import graph.GraphConstraint;
import graph.GraphFactory;
import graph.components.gb.GBGraph;
import ui.GBFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The dialog used to generate complete graphs. Built using WindowBuilder.
 *
 * @author Brian Yao
 */
public class CompleteGraphDialog extends JDialog {

	private static final long serialVersionUID = 4902159817122953301L;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public CompleteGraphDialog(final GBFrame g) {
		super(g, "Complete Graph");

		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(g);

		setSize(153, 122);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel numNodesLabel = new JLabel("Number of nodes:");

		SpinnerModel spinnerModel = new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1);
		final JSpinner spinner = new JSpinner();
		spinner.setModel(spinnerModel);

		contentPanel.add(numNodesLabel);
		contentPanel.add(spinner);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
				Graph completeGraph = GraphFactory.completeGraph((int) spinner.getValue(), constraints);
				GenerateRadialGraphAction genAction = new GenerateRadialGraphAction(new GBGraph(g.getContext(), completeGraph));
				genAction.perform();
				g.getContext().pushReversibleAction(genAction, true, false);
				dispose();
			}

		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener($ -> this.dispose());

		getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
	}

}

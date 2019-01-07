package ui.dialogs;

import actions.AddRadialGraphAction;
import graph.Graph;
import graph.GraphConstraint;
import graph.GraphFactory;
import graph.components.gb.GBGraph;
import org.javatuples.Pair;
import ui.GBFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The dialog used to generate complete graphs. Built using WindowBuilder.
 *
 * @author Brian Yao
 */
public class CompleteGraphDialog extends JDialog {

	private static final long serialVersionUID = 4902159817122953301L;

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

		JPanel contentPanel = new JPanel();
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
		okButton.addActionListener($ -> {
			Pair<Graph, Integer> result = GraphFactory.completeGraph((int) spinner.getValue(),
																	 g.getContext().getNextId());
			g.getContext().setNextId(result.getValue1());

			AddRadialGraphAction genAction = new AddRadialGraphAction(new GBGraph(g.getContext(), result.getValue0()));
			genAction.perform();
			g.getContext().pushReversibleAction(genAction, true, false);

			dispose();
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener($ -> this.dispose());

		getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
	}

}

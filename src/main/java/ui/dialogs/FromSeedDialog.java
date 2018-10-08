//package ui.dialogs;
//
//import actions.GenerateRadialGraphAction;
//import graph.Graph;
//import graph.GraphFactory;
//import ui.GUI;
//
//import javax.swing.*;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.LayoutStyle.ComponentPlacement;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class FromSeedDialog extends JDialog {
//
//	private static final long serialVersionUID = -3110971321314034418L;
//
//	private final JPanel contentPanel = new JPanel();
//	private JTextField textField;
//
//	/**
//	 * Create the dialog.
//	 */
//	public FromSeedDialog(final GUI g) {
//		super(g, "Generate Graph From Seed");
//
//		setResizable(false);
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		setVisible(true);
//		setLocationRelativeTo(g);
//
//		setBounds(100, 100, 284, 231);
//		getContentPane().setLayout(new BorderLayout());
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		getContentPane().add(contentPanel, BorderLayout.CENTER);
//
//		JLabel lblNumberOfNodes = new JLabel("Number of nodes:");
//
//		final JSpinner spinner = new JSpinner();
//
//		JLabel lblDensity = new JLabel("Density (%):");
//
//		JSlider slider = new JSlider();
//
//		final JSpinner spinner_1 = new JSpinner();
//
//		JLabel lblSeed = new JLabel("Seed:");
//
//		textField = new JTextField();
//		textField.setColumns(10);
//
//		JLabel lblEdgeTypes = new JLabel("Edge types:");
//
//		final JCheckBox chckbxUndirected = new JCheckBox("Undirected");
//
//		final JCheckBox chckbxDirected = new JCheckBox("Directed");
//
//		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
//		gl_contentPanel.setHorizontalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addGap(14)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
//						.addComponent(lblEdgeTypes)
//						.addGroup(gl_contentPanel.createSequentialGroup()
//							.addComponent(lblSeed)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//						.addGroup(gl_contentPanel.createSequentialGroup()
//							.addComponent(lblDensity)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(slider, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE))
//						.addGroup(gl_contentPanel.createSequentialGroup()
//							.addComponent(lblNumberOfNodes)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
//						.addGroup(gl_contentPanel.createSequentialGroup()
//							.addGap(10)
//							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
//								.addComponent(chckbxDirected)
//								.addComponent(chckbxUndirected))))
//					.addContainerGap(23, Short.MAX_VALUE))
//		);
//		gl_contentPanel.setVerticalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
//						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(lblNumberOfNodes))
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
//						.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(lblDensity)
//						.addComponent(slider, 0, 0, Short.MAX_VALUE))
//					.addPreferredGap(ComponentPlacement.UNRELATED)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
//						.addComponent(lblSeed)
//						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addPreferredGap(ComponentPlacement.UNRELATED)
//					.addComponent(lblEdgeTypes)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(chckbxUndirected)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(chckbxDirected)
//					.addContainerGap(11, Short.MAX_VALUE))
//		);
//		contentPanel.setLayout(gl_contentPanel);
//		JPanel buttonPane = new JPanel();
//		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		getContentPane().add(buttonPane, BorderLayout.SOUTH);
//
//		JButton okButton = new JButton("OK");
//		okButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int seed = textField.getText().hashCode();
//				int nodeCount = (int) spinner.getValue();
//				double density = (int) spinner_1.getValue() / 100.0;
//				boolean allowUndirected = chckbxUndirected.isSelected();
//				boolean allowDirected = chckbxDirected.isSelected();
//
//				Graph fromSeed = GraphFactory.fromSeed(g.getContext(), seed,
//						nodeCount, allowUndirected, allowDirected, density);
//				GenerateRadialGraphAction genAction = new GenerateRadialGraphAction(g.getContext(), fromSeed);
//				genAction.perform();
//				g.getContext().pushReversibleAction(genAction, true, false);
//				dispose();
//			}
//
//		});
//
//		JButton cancelButton = new JButton("Cancel");
//		cancelButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				dispose();
//			}
//
//		});
//
//		getRootPane().setDefaultButton(okButton);
//		buttonPane.add(okButton);
//		buttonPane.add(cancelButton);
//	}
//}

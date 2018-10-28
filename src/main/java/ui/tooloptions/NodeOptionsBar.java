package ui.tooloptions;

import graph.components.gb.GBNode;
import ui.GBFrame;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The option bar for the Node tool.
 *
 * @author Brian Yao
 */
public class NodeOptionsBar extends ToolOptionsBar {

	private static final long serialVersionUID = -7728960366670401765L;

	private static final int DEFAULT_NODE_RADIUS = 30;

	// Interface components for the node tool
	private JLabel circleRadiusLabel;
	private JTextField circleRadiusTextField;
	private JSlider circleRadiusSlider;
	private JLabel circleFillColorLabel;
	private BufferedImage circleFillColorImage;
	private ImageIcon circleFillColorIcon;
	private JButton circleFillColorButton;
	private Color circleFillColor;
	private JLabel circleBorderColorLabel;
	private BufferedImage circleBorderColorImage;
	private ImageIcon circleBorderColorIcon;
	private JButton circleBorderColorButton;
	private Color circleBorderColor;
	private JLabel circleTextColorLabel;
	private BufferedImage circleTextColorImage;
	private ImageIcon circleTextColorIcon;
	private JButton circleTextColorButton;
	private Color circleTextColor;

	/**
	 * @param g The GBFrame object this bar will appear in.
	 */
	public NodeOptionsBar(GBFrame g) {
		super(g);

		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		circleRadiusLabel = new JLabel("Node Radius (px):");
		circleRadiusTextField = new JTextField(3);
		circleRadiusTextField.setText(String.valueOf(DEFAULT_NODE_RADIUS));
		circleRadiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, DEFAULT_NODE_RADIUS);
		circleRadiusSlider.setPaintTicks(true);
		circleRadiusSlider.setMinorTickSpacing(10);
		circleRadiusSlider.setMajorTickSpacing(50);
		circleRadiusSlider.setPaintLabels(true);

		circleRadiusTextField.addActionListener($ -> {
			try {
				int newRadius = Integer.parseInt(circleRadiusTextField.getText());
				if (newRadius < circleRadiusSlider.getMinimum() || newRadius > circleRadiusSlider.getMaximum()) {
					JOptionPane.showMessageDialog(null, String.format("Must be an integer between %d and %d inclusive.",
																	  circleRadiusSlider.getMinimum(),
																	  circleRadiusSlider.getMaximum()), "Out of range",
												  JOptionPane.WARNING_MESSAGE);
				} else {
					circleRadiusSlider.setValue(newRadius);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Radius must be a well-formatted integer.", "Invalid integer",
											  JOptionPane.WARNING_MESSAGE);
			}
		});

		circleRadiusSlider.addChangeListener($ -> circleRadiusTextField.setText(
			String.valueOf(circleRadiusSlider.getValue()))
		);

		// Initialize selected properties in the options panel
		circleFillColor = GBNode.DEFAULT_FILL_COLOR;
		circleBorderColor = GBNode.DEFAULT_BORDER_COLOR;
		circleTextColor = GBNode.DEFAULT_TEXT_COLOR;

		circleFillColorLabel = new JLabel("Fill Color:");
		circleFillColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ImageUtils.fillImage(circleFillColorImage, circleFillColor);
		circleFillColorIcon = new ImageIcon(circleFillColorImage);
		circleFillColorButton = new JButton(circleFillColorIcon);
		circleFillColorButton.addActionListener($ -> {
			Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Fill Color", circleFillColor);
			if (c != null) {
				circleFillColor = c;
				ImageUtils.fillImage(circleFillColorImage, c);
			}
		});

		circleBorderColorLabel = new JLabel("Border Color:");
		circleBorderColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ImageUtils.fillImage(circleBorderColorImage, circleBorderColor);
		circleBorderColorIcon = new ImageIcon(circleBorderColorImage);
		circleBorderColorButton = new JButton(circleBorderColorIcon);
		circleBorderColorButton.addActionListener($ -> {
			Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Border Color", circleBorderColor);
			if (c != null) {
				circleBorderColor = c;
				ImageUtils.fillImage(circleBorderColorImage, c);
			}
		});

		circleTextColorLabel = new JLabel("Text Color:");
		circleTextColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ImageUtils.fillImage(circleTextColorImage, circleTextColor);
		circleTextColorIcon = new ImageIcon(circleTextColorImage);
		circleTextColorButton = new JButton(circleTextColorIcon);
		circleTextColorButton.addActionListener($ -> {
			Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Text Color", circleTextColor);
			if (c != null) {
				circleTextColor = c;
				ImageUtils.fillImage(circleTextColorImage, c);
			}
		});

		this.add(circleRadiusLabel);
		this.add(circleRadiusTextField);
		this.add(circleRadiusSlider);
		this.addSeparator();
		this.add(circleFillColorLabel);
		this.add(circleFillColorButton);
		this.addSeparator();
		this.add(circleBorderColorLabel);
		this.add(circleBorderColorButton);
		this.addSeparator();
		this.add(circleTextColorLabel);
		this.add(circleTextColorButton);
	}

	/**
	 * @return the current radius selected in the node options.
	 */
	public int getCurrentRadius() {
		return circleRadiusSlider.getValue();
	}

	/**
	 * @return the fill, border, and text colors currently selected.
	 */
	public Color[] getCurrentCircleColors() {
		return new Color[]{circleFillColor, circleBorderColor, circleTextColor};
	}

}

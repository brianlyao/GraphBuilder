package ui.tooloptions;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tool.Tool;
import ui.GUI;

/** The option bar for the Node tool. */
public class NodeOptionsBar extends ToolOptionsBar {

	private static final long serialVersionUID = -7728960366670401765L;
	
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
	 * @param g The GUI object this bar will appear in.
	 */
	public NodeOptionsBar(GUI g) {
		super(g);
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		circleRadiusLabel = new JLabel("Node Radius (px):");
		circleRadiusTextField = new JTextField(3);
		circleRadiusTextField.setText("30");
		circleRadiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 30);
		circleRadiusSlider.setPaintTicks(true);
		circleRadiusSlider.setMinorTickSpacing(10);
		circleRadiusSlider.setMajorTickSpacing(50);
		circleRadiusSlider.setPaintLabels(true);
		circleRadiusTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					int newRadius = Integer.parseInt(circleRadiusTextField.getText());
					if(newRadius < circleRadiusSlider.getMinimum() || newRadius > circleRadiusSlider.getMaximum())
						JOptionPane.showMessageDialog(null,
							String.format("Must be an integer between %d and %d inclusive.", circleRadiusSlider.getMinimum(), circleRadiusSlider.getMaximum()),
							"Out of range", JOptionPane.WARNING_MESSAGE);
					else
						circleRadiusSlider.setValue(newRadius);
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, "Radius must be a well-formatted integer.", "Invalid integer", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		circleRadiusSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				circleRadiusTextField.setText(String.valueOf(circleRadiusSlider.getValue()));
			}
		});
		
		//Initialize selected properties in the options panel
		circleFillColor = Color.WHITE;
		circleBorderColor = Color.BLACK;
		circleTextColor = Color.BLACK;
		circleFillColorLabel = new JLabel("Fill Color:");
		circleFillColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ToolOptionsBar.fillImage(circleFillColorImage, circleFillColor);
		circleFillColorIcon = new ImageIcon(circleFillColorImage);
		circleFillColorButton = new JButton(circleFillColorIcon);
		circleFillColorButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Fill Color", circleFillColor);
				if(c != null){
					circleFillColor = c;
					ToolOptionsBar.fillImage(circleFillColorImage, c);
				}
			}
		});
		circleBorderColorLabel = new JLabel("Border Color:");
		circleBorderColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ToolOptionsBar.fillImage(circleBorderColorImage, circleBorderColor);
		circleBorderColorIcon = new ImageIcon(circleBorderColorImage);
		circleBorderColorButton = new JButton(circleBorderColorIcon);
		circleBorderColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Border Color", circleBorderColor);
				if(c != null){
					circleBorderColor = c;
					ToolOptionsBar.fillImage(circleBorderColorImage, c);
				}
			}
		});
		circleTextColorLabel = new JLabel("Text Color:");
		circleTextColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		ToolOptionsBar.fillImage(circleTextColorImage, circleTextColor);
		circleTextColorIcon = new ImageIcon(circleTextColorImage);
		circleTextColorButton = new JButton(circleTextColorIcon);
		circleTextColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(NodeOptionsBar.this.getGUI(), "Choose Text Color", circleTextColor);
				if(c != null){
					circleTextColor = c;
					ToolOptionsBar.fillImage(circleTextColorImage, c);
				}
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
	
	public int getCurrentRadius() {
		if(getGUI().getCurrentTool() == Tool.NODE)
			return circleRadiusSlider.getValue();
		return -1;
	}
	
	public Color[] getCurrentCircleColors() {
		if(getGUI().getCurrentTool() == Tool.NODE)
			return new Color[] {circleFillColor, circleBorderColor, circleTextColor};
		return null;
	}
	
}

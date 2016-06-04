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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ToolOptionsPanel extends JToolBar {
	
	private GUI gui;
	
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
	
	private JLabel lineWeightLabel;
	private JTextField lineWeightTextField;
	private JSlider lineWeightSlider;
	private JLabel lineColorLabel;
	private BufferedImage lineColorImage;
	private ImageIcon lineColorIcon;
	private JButton lineColorButton;
	private Color lineColor;
	
	private String choose;
	
	public ToolOptionsPanel(GUI g, Tool t){
		super();
		gui = g;
		if(t == Tool.NODE){
			this.setLayout(new FlowLayout(FlowLayout.LEADING));
			circleRadiusLabel = new JLabel("Node Radius (px):");
			circleRadiusTextField = new JTextField(3);
			circleRadiusTextField.setText("20");
			circleRadiusSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 20);
			circleRadiusSlider.setPaintTicks(true);
			circleRadiusSlider.setMinorTickSpacing(10);
			circleRadiusSlider.setMajorTickSpacing(50);
			circleRadiusSlider.setPaintLabels(true);
			circleRadiusTextField.addActionListener(new ActionListener(){
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
			circleRadiusSlider.addChangeListener(new ChangeListener(){
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
			fillImage(circleFillColorImage, circleFillColor);
			circleFillColorIcon = new ImageIcon(circleFillColorImage);
			circleFillColorButton = new JButton(circleFillColorIcon);
			circleFillColorButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color c = JColorChooser.showDialog(gui, "Choose Fill Color", circleFillColor);
					if(c != null){
						circleFillColor = c;
						fillImage(circleFillColorImage, c);
					}
				}
			});
			circleBorderColorLabel = new JLabel("Border Color:");
			circleBorderColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
			fillImage(circleBorderColorImage, circleBorderColor);
			circleBorderColorIcon = new ImageIcon(circleBorderColorImage);
			circleBorderColorButton = new JButton(circleBorderColorIcon);
			circleBorderColorButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color c = JColorChooser.showDialog(gui, "Choose Border Color", circleBorderColor);
					if(c != null){
						circleBorderColor = c;
						fillImage(circleBorderColorImage, c);
					}
				}
			});
			circleTextColorLabel = new JLabel("Text Color:");
			circleTextColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
			fillImage(circleTextColorImage, circleTextColor);
			circleTextColorIcon = new ImageIcon(circleTextColorImage);
			circleTextColorButton = new JButton(circleTextColorIcon);
			circleTextColorButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color c = JColorChooser.showDialog(gui, "Choose Text Color", circleTextColor);
					if(c != null){
						circleTextColor = c;
						fillImage(circleTextColorImage, c);
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
		}else if(t == Tool.LINE){
			choose = "Choose Line Color";
			this.setLayout(new FlowLayout(FlowLayout.LEADING));
			lineWeightLabel = new JLabel("Line Weight: ");
			lineWeightTextField = new JTextField(3);
			lineWeightTextField.setText("2");
			lineWeightSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 2);
			lineWeightSlider.setPaintTicks(true);
			lineWeightSlider.setMinorTickSpacing(5);
			lineWeightSlider.setPaintLabels(true);
			lineWeightTextField.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try{
						int newWeight = Integer.parseInt(circleRadiusTextField.getText());
						if(newWeight < lineWeightSlider.getMinimum() || newWeight > lineWeightSlider.getMaximum())
							JOptionPane.showMessageDialog(null,
								String.format("Must be an integer between %d and %d inclusive.", lineWeightSlider.getMinimum(), lineWeightSlider.getMaximum()),
								"Out of range", JOptionPane.WARNING_MESSAGE);
						else
							lineWeightSlider.setValue(newWeight);
					}catch(Exception e){
						JOptionPane.showMessageDialog(null, "Radius must be a well-formatted integer.", "Invalid integer", JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			lineWeightSlider.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					lineWeightTextField.setText(String.valueOf(lineWeightSlider.getValue()));
				}
			});
			lineColorLabel = new JLabel("Line Color: ");
			lineColor = Color.BLACK;
			lineColorImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
			fillImage(lineColorImage, lineColor);
			lineColorIcon = new ImageIcon(lineColorImage);
			lineColorButton = new JButton(lineColorIcon);
			lineColorButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color c = JColorChooser.showDialog(gui, choose, lineColor);
					if(c != null){
						lineColor = c;
						fillImage(lineColorImage, c);
					}
				}
			});
			this.add(lineWeightLabel);
			this.add(lineWeightTextField);
			this.add(lineWeightSlider);
			this.addSeparator();
			this.add(lineColorLabel);
			this.add(lineColorButton);
		}
	}
	
	public void lineToArrow(){
		lineWeightLabel.setText("Arrow Weight: ");
		lineColorLabel.setText("Arrow Color: ");
		choose = "Choose Arrow Color";
	}
	
	public void arrowToLine(){
		lineWeightLabel.setText("Line Weight: ");
		lineColorLabel.setText("Line Color: ");
		choose = "Choose Line Color";
	}
	
	public int getCurrentRadius(){
		return circleRadiusSlider.getValue();
	}
	
	public Color[] getCurrentCircleColors(){
		return new Color[] {circleFillColor, circleBorderColor, circleTextColor};
	}
	
	public int getCurrentLineWeight(){
		return lineWeightSlider.getValue();
	}
	
	public Color getCurrentLineColor(){
		return lineColor;
	}
	
	private void fillImage(BufferedImage b, Color c){
		for(int i = 0 ; i < b.getWidth() ; i++)
			for(int j = 0 ; j < b.getHeight() ; j++)
				b.setRGB(i, j, c.getRGB());
	}
}

package ui;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** An instance is an interface for displaying the properties of a component. */
public class PropertiesGUI extends JPanel {
	
	private static final long serialVersionUID = -7343299437427405123L;

//	private JLabel generalSelection;
//	private JLabel generalSelectionID;
//	private JLabel generalNodeLocation;
//	private JLabel generalNodeRadius;
//	private JLabel generalNodeEdges;
//	private JButton generalNodeEdgesAll;
//	private JLabel generalNodeIndegree;
//	private JLabel generalNodeOutdegree;
//	private JLabel generalArrowFrom;
//	private JLabel generalArrowTo;
	
	private JTabbedPane propertiesCirclePane;
	private JPanel generalCircleTab;	
	private JPanel appearanceCircleTab;
	private JPanel descriptionCircleTab;
	
	public PropertiesGUI(){
		
//		generalCircleTab = new JPanel();
//		GroupLayout gl = new GroupLayout(generalCircleTab);
//		gl.setAutoCreateGaps(true);
//		generalSelection = new JLabel("Selection Type:");
//		generalSelectionID = new JLabel("Selection ID:");
//		generalNodeLocation = new JLabel("Location:");
//		generalNodeRadius = new JLabel("Radius:");
//		generalNodeEdges = new JLabel("Edges:");
//		generalNodeEdgesAll = new JButton("See Edges");
//		generalNodeIndegree = new JLabel("Indegree:");
//		generalNodeOutdegree = new JLabel("Outdegree:");
//		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
//				.addComponent(generalSelection)
//				.addComponent(generalSelectionID)
//				.addComponent(generalNodeLocation)
//				.addComponent(generalNodeRadius)
//				.addGroup(gl.createSequentialGroup()
//						.addComponent(generalNodeEdges)
//						.addComponent(generalNodeEdgesAll))
//				.addComponent(generalNodeIndegree)
//				.addComponent(generalNodeOutdegree));
//		gl.setVerticalGroup(gl.createSequentialGroup()
//				.addComponent(generalSelection)
//				.addComponent(generalSelectionID)
//				.addComponent(generalNodeLocation)
//				.addComponent(generalNodeRadius)
//				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
//						.addComponent(generalNodeEdges)
//						.addComponent(generalNodeEdgesAll))
//				.addComponent(generalNodeIndegree)
//				.addComponent(generalNodeOutdegree));
//		
//		generalCircleTab.setLayout(gl);
		
//		appearanceCircleTab = new JPanel();
//		descriptionCircleTab = new JPanel();
		
		//Initialize and fill out the tabbed pane
		propertiesCirclePane = new JTabbedPane();
		propertiesCirclePane.addTab("General", generalCircleTab);
		propertiesCirclePane.addTab("Appearance", appearanceCircleTab);
		propertiesCirclePane.addTab("Description", descriptionCircleTab);
	}

}

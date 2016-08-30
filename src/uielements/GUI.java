package uielements;
import java.awt.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.*;

import keybindings.KeyboardShortcutActions;
import tool.Tool;
import uielements.tooloptions.EdgeOptionsBar;
import uielements.tooloptions.NodeOptionsBar;
import context.GraphBuilderContext;

public class GUI extends JFrame {
	
	private static final long serialVersionUID = -8275121379599770074L;
	private static final String VERSION = "0.1.1";

//	private final int PANE_PADDING = 10;
	
	private MenuBar menuBar; // The menu bar
	
	private ToolBar toolBar; // The tool bar
	
	//Tool option bars 
	private NodeOptionsBar nodeOptions;
	private EdgeOptionsBar edgeOptions;
	private JPanel toolOptions; // This panel will hold all tool option bars
	
	private JScrollPane panelEditorScroll; // The scrolling pane containing the editor
	
	private Editor editor; // The main panel workspace
	
	private JPanel panelProperties;
	
	private JPanel panelStatus;
	
	private KeyboardShortcutActions keyActions; // Object with all keyboard shortcuts and corresponding actions
	
	private Tool currentTool; // The tool currently being used
	
	private GraphBuilderContext context; // The context for the entire program
	
	public GUI() {
		super("Graph Builder " + VERSION);
		
		context = new GraphBuilderContext(this);
		
		// Initialize and set menu bar
		menuBar = new MenuBar();
		setJMenuBar(menuBar);
		
		// Initialize toolbar
		toolBar = new ToolBar(context);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		
		// Initialize the key bindings and their actions
		keyActions = new KeyboardShortcutActions(context);
		
		//Initialize and fill out the options panel
		toolOptions = new JPanel();
		toolOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
		nodeOptions = new NodeOptionsBar(this);
		edgeOptions = new EdgeOptionsBar(this);
		
		//Set JFrame properties
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024, 768);
		setVisible(true);
		setLayout(new GridBagLayout());
		
		//Manage the layout constraints
		GridBagConstraints tbargbc = new GridBagConstraints();
		GridBagConstraints toptgbc = new GridBagConstraints();
		GridBagConstraints editorgbc = new GridBagConstraints();
		GridBagConstraints propgbc = new GridBagConstraints();
		GridBagConstraints statusgbc = new GridBagConstraints();
		
		tbargbc.gridx = 0;
		tbargbc.gridy = 0;
		tbargbc.gridwidth = 1;
		tbargbc.weightx = 1;
		tbargbc.weighty = 0.01;
		tbargbc.fill = GridBagConstraints.HORIZONTAL;
		add(toolBar, tbargbc);
		
		toptgbc.gridx = 0;
		toptgbc.gridy = 1;
		toptgbc.gridwidth = 1;
		toptgbc.weightx = 1;
		toptgbc.weighty = 0.01;
		toptgbc.fill = GridBagConstraints.HORIZONTAL;
		add(toolOptions, toptgbc);
		
		editorgbc.gridx = 0;
		editorgbc.gridy = 2;
		editorgbc.weightx = 1;
		editorgbc.weighty = 1;
		editorgbc.gridheight = 2;
		editorgbc.insets = new Insets(9, 9, 9, 9);
		editorgbc.fill = GridBagConstraints.BOTH;
		
		propgbc.gridx = 1;
		propgbc.gridy = 2;
		propgbc.weightx = 0.1;
		propgbc.weighty = 0.8;
		propgbc.insets = new Insets(2, 2, 2, 2);
		propgbc.fill = GridBagConstraints.BOTH;
		
		statusgbc.gridx = 1;
		statusgbc.gridy = 3;
		statusgbc.weightx = 0.1;
		statusgbc.weighty = 0.4;
		statusgbc.insets = new Insets(2, 2, 2, 2);
		statusgbc.fill = GridBagConstraints.BOTH;
		
		//Initialize and set up the main editor panel
		editor = new Editor(this);
		panelEditorScroll = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelEditorScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JScrollBar horiz = panelEditorScroll.getHorizontalScrollBar();
		JScrollBar vert = panelEditorScroll.getVerticalScrollBar();
		horiz.setValue((horiz.getMaximum() + horiz.getVisibleAmount() - horiz.getMinimum())/2);
		vert.setValue((vert.getMaximum()  + vert.getVisibleAmount() - vert.getMinimum())/2);
		add(panelEditorScroll, editorgbc);
		
		//Initialize and set up the properties panel, used when someone right clicks an object and clicks properties
		panelProperties = new JPanel();
		Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		panelProperties.setBorder(BorderFactory.createTitledBorder(lowerEtched, "Properties"));

//		panelProperties.add(propertiesCirclePane, BorderLayout.CENTER);
		
		// Add properties panel
		add(panelProperties, propgbc);
		
		panelStatus = new JPanel();
		panelStatus.setBorder(lowerEtched);
		add(panelStatus, statusgbc);
		revalidate();
	}
	
	/** 
	 * The procedure when a tool button is pressed. Updates the current tool and the button appearance.
	 *
	 * @param t The Tool we want to change to.
	 */
	public void updateTool(Tool t) {
		HashMap<Tool, JButton> toolButtons = toolBar.getToolButtons();
		HashMap<Tool, ImageIcon[]> toolIcons = toolBar.getToolIcons();
		if(currentTool != null) {
			if(currentTool != t) {
				toolButtons.get(currentTool).setIcon(toolIcons.get(currentTool)[0]);
				toolButtons.get(t).setIcon(toolIcons.get(t)[1]);
				currentTool = t;
			} else {
				toolButtons.get(t).setIcon(toolIcons.get(t)[0]);
				currentTool = null;
			}
		} else {
			toolButtons.get(t).setIcon(toolIcons.get(t)[1]);
			currentTool = t;
		}
	}
	
	/**
	 * Procedure to change the tool options bar (to the options bar for the specified tool).
	 *
	 * @param t The tool whose option bar we want to change to.
	 */
	public void changeToolOptionsBar(Tool t) {
		toolOptions.removeAll();
		if(currentTool == Tool.NODE) {
			toolOptions.add(nodeOptions);
		} else if(currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) {
			toolOptions.add(edgeOptions);
		}
		toolOptions.repaint();
		toolOptions.revalidate();
	}
	
//	public void displayProperties(GraphComponent g){
//		String type = "";
//		JPanel panel = new JPanel();
//		if(g instanceof Circle){
//			type = "Circle";
//			generalNodeLocation.setText("Location: %d, %d");
//			generalNodeRadius.setText("Radius: %d");
//			generalNodeEdges.setText("Number of Edges: %d");
//			generalNodeEdgesAll.setText("See Edges");
//			generalNodeIndegree.setText("Indegree: %d");
//			generalNodeOutdegree.setText("Outdegree: %d");
//			panel.add(propertiesCirclePane, BorderLayout.CENTER);;
//		}
//		generalSelection.setText(type);
//		generalSelectionID.setText(String.valueOf(g.getID()));
//		JFrame props = new JFrame(String.format("%s Properties: \"%d\"", type, g.getID()));
//		int result = JOptionPane.showConfirmDialog(props, panel);
//		if(result == JOptionPane.OK_OPTION){
//			//TODO
//		}
//	}
	
	/**
	 * Get the editor panel on this GUI.
	 * 
	 * @return The Editor object on this GUI.
	 */
	public Editor getEditor() {
		return editor;
	}
	
	/**
	 * Get the context associated with this GUI.
	 * 
	 * @return This GUI's context.
	 */
	public GraphBuilderContext getContext() {
		return context;
	}
	
	/**
	 * Get the node option tool bar; each GUI should have only one.
	 * 
	 * @return The node option tool bar.
	 */
	public NodeOptionsBar getNodeOptionsBar() {
		return nodeOptions;
	}
	
	/**
	 * Get the edge option tool bar; each GUI should have only one.
	 * 
	 * @return The edge option tool bar.
	 */
	public EdgeOptionsBar getEdgeOptionsBar() {
		return edgeOptions;
	}
	
	/**
	 * Get the scrolling pane containing the editor panel.
	 * 
	 * @return This GUI's scroll pane.
	 */
	public JScrollPane getScrollPane() {
		return panelEditorScroll;
	}
	
	/**
	 * Get the panel containing all the tool option bars.
	 * 
	 * @return The panel for tool option bars.
	 */
	public JPanel getToolOptionsPanel() {
		return toolOptions;
	}
	
	/**
	 * Get the tool currently being used.
	 * 
	 * @return The current Tool.
	 */
	public Tool getCurrentTool() {
		return currentTool;
	}
	
}

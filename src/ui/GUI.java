package ui;

import graph.Graph;
import graph.GraphConstraint;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import components.Node;
import keybindings.KeyActions;
import logger.Logger;
import structures.OrderedPair;
import tool.Tool;
import ui.dialogs.GridSettingsDialog;
import ui.dialogs.NewGraphDialog;
import ui.menus.MenuBar;
import ui.tooloptions.EdgeOptionsBar;
import ui.tooloptions.NodeOptionsBar;
import util.FileUtils;
import context.GraphBuilderContext;

/**
 * The main window in which the program runs.
 * 
 * @author Brian Yao
 */
public class GUI extends JFrame {
	
	private static final long serialVersionUID = -8275121379599770074L;
	private static final String VERSION = "0.1.6";
	
	public static final String DEFAULT_TITLE = "GraphBuilder " + VERSION;
	
	private static final String DEFAULT_FILENAME = "Untitled";
	
	private MenuBar menuBar; // The menu bar
	
	private ToolBar toolBar; // The tool bar
	
	private GridSettingsDialog gridSettingsDialog;
	private NewGraphDialog newGraphDialog;
	
	//Tool option bars 
	private NodeOptionsBar nodeOptions;
	private EdgeOptionsBar edgeOptions;
	private JPanel toolOptions; // This panel will hold all tool option bars
	
	private JScrollPane panelEditorScroll; // The scrolling pane containing the editor
	
	private Editor editor; // The main panel workspace
	
	private JPanel panelProperties;
	
	private JPanel panelStatus;
	
	private JFileChooser fileChooser;
	
	private Tool currentTool; // The tool currently being used
	
	private GraphBuilderContext context; // The context for the entire program
	
	public GUI(GraphBuilderContext initialContext) {
		super();
		
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				Logger.writeEntry(Logger.INFO, "Nimbus look and feel not found; backing off to system look and feel."); 
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e2) {
				Logger.writeEntry(Logger.INFO, "Unable to identify system look and feel.");
			}
		}
		
		// Set context
		context = initialContext;
		context.setGUI(this);
		
		// Initialize and set menu bar
		menuBar = new MenuBar(this);
		setJMenuBar(menuBar);
		
		// Initialize dialogs
		gridSettingsDialog = new GridSettingsDialog(this);
		newGraphDialog = new NewGraphDialog(this);
		
		// Initialize toolbar
		toolBar = new ToolBar(context);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		
		// Initialize the key bindings and their actions
		KeyActions.initialize(this);
		
		// Initialize and customize the file chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Graph Builder Files", "gbf"));
		
		// Initialize and fill out the options panel
		toolOptions = new JPanel();
		toolOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
		nodeOptions = new NodeOptionsBar(this);
		edgeOptions = new EdgeOptionsBar(this);
		
		// Set JFrame properties
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLayout(new GridBagLayout());
		
		// Perform a certain procedure when the window is closed (with the X button in the top right)
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				FileUtils.exitProcedure(GUI.this.context);
			}
			
		});
		
		// Manage the layout constraints
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
		
		// Initialize and set up the main editor panel
		editor = new Editor(this);
		panelEditorScroll = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelEditorScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(panelEditorScroll, editorgbc);
		
		// Initialize and set up the properties panel, used when someone right clicks an object and clicks properties
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
		
		// By default, start with the Select tool
		updateTool(Tool.SELECT);
		updateByConstraint();
		
		// Add all nodes to the editor panel
		for (Node n : context.getNodes()) {
			editor.add(n.getNodePanel());
		}
		
		// Update GUI title
		if (context.existsOnDisk()) {
			setTitle(DEFAULT_TITLE + " - " + FileUtils.getBaseName(context.getCurrentlyLoadedFile()));
		} else {
			setTitle(DEFAULT_TITLE + " - " + DEFAULT_FILENAME);
		}
		context.setAsSaved();
		
		// Show GUI
		setVisible(true);
		
		// Set the initial scrolling positions to the center
		final JScrollBar horiz = panelEditorScroll.getHorizontalScrollBar();
		final JScrollBar vert = panelEditorScroll.getVerticalScrollBar();
		if (horiz != null) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					horiz.setValue((horiz.getMaximum() - horiz.getVisibleAmount()) / 2);
				}
				
			});
		}
		if (vert != null) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					vert.setValue((vert.getMaximum() - vert.getVisibleAmount()) / 2);
				}
				
			});
		}
	}
	
	/** 
	 * The procedure when a tool button is pressed. Updates the current tool and the button appearance.
	 *
	 * @param t The Tool we want to change to.
	 */
	public void updateTool(Tool t) {
		HashMap<Tool, JButton> toolButtons = toolBar.getToolButtons();
		HashMap<Tool, OrderedPair<ImageIcon>> toolIcons = toolBar.getToolIcons();
		if (currentTool != null) {
			if (currentTool != t) {
				toolButtons.get(currentTool).setIcon(toolIcons.get(currentTool).getFirst());
				toolButtons.get(t).setIcon(toolIcons.get(t).getSecond());
				currentTool = t;
			} else {
				toolButtons.get(t).setIcon(toolIcons.get(t).getFirst());
				currentTool = null;
			}
		} else {
			toolButtons.get(t).setIcon(toolIcons.get(t).getSecond());
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
		if (currentTool == Tool.NODE) {
			toolOptions.add(nodeOptions);
		} else if (currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) {
			toolOptions.add(edgeOptions);
		}
		toolOptions.repaint();
		toolOptions.revalidate();
	}
	
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
	 * Get the center of the viewport in editor coordinates (relative to the
	 * top left corner of the actual editor panel, not the scroll pane).
	 * 
	 * @return The viewport center.
	 */
	public Point getEditorCenter() {
		int viewportWidth = panelEditorScroll.getViewport().getWidth();
		int viewportHeight = panelEditorScroll.getViewport().getHeight();
		Point topLeft = panelEditorScroll.getViewport().getViewPosition();
		
		return new Point(topLeft.x + viewportWidth / 2, topLeft.y + viewportHeight / 2);
	}
	
	/**
	 * Make changes to certain parts of the GUI according to the current
	 * set of constraints in the context graph.
	 */
	public void updateByConstraint() {
		Graph currentGraph = context.getGraph();
		Map<Tool, JButton> toolToButton = toolBar.getToolButtons();
		toolToButton.get(Tool.DIRECTED_EDGE).setEnabled(currentGraph.hasConstraint(GraphConstraint.DIRECTED));
		toolToButton.get(Tool.EDGE).setEnabled(currentGraph.hasConstraint(GraphConstraint.UNDIRECTED));
	}
	
	/**
	 * Updates the context of this interface to the specified context.
	 * 
	 * @param newContext The context to replace the existing one.
	 */
	public void updateContext(GraphBuilderContext newContext) {
		Logger.writeEntry(Logger.INFO, String.format("Switching context to %s.", newContext.getCurrentlyLoadedFile()));
		context = newContext;
		context.setGUI(this);
		editor.clearState();
		updateByConstraint();
		KeyActions.initialize(this); // Re-initialize key bindings with correct context
		menuBar.updateContext(newContext);
		if (newContext.getCurrentlyLoadedFile() == null) {
			this.setTitle(DEFAULT_TITLE + " - " + DEFAULT_FILENAME);
		} else {
			this.setTitle(DEFAULT_TITLE + " - " + FileUtils.getBaseName(newContext.getCurrentlyLoadedFile()));
		}
		context.setAsSaved();
	}
	
	/**
	 * Get the menu bar object on this GUI.
	 * 
	 * @return The menu bar.
	 */
	public MenuBar getMainMenuBar() {
		return menuBar;
	}
	
	/**
	 * Get the grid settings dialog.
	 * 
	 * @return The grid settings dialog.
	 */
	public GridSettingsDialog getGridSettingsDialog() {
		return gridSettingsDialog;
	}
	
	/**
	 * Get the "new file" dialog.
	 * 
	 * @return The new file dialog.
	 */
	public NewGraphDialog getNewGraphDialog() {
		return newGraphDialog;
	}
	
	/**
	 * Get a file chooser object for this frame.
	 * 
	 * @return A JFileChooser.
	 */
	public JFileChooser getFileChooser() {
		return fileChooser;
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

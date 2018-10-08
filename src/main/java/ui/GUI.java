package ui;

import context.GBContext;
import graph.Graph;
import graph.GraphConstraint;
import graph.components.gb.GBNode;
import keybindings.KeyActions;
import logger.Logger;
import lombok.Getter;
import structures.OrderedPair;
import tool.Tool;
import ui.dialogs.GridSettingsDialog;
import ui.dialogs.NewGraphDialog;
import ui.menus.MenuBar;
import ui.tooloptions.EdgeOptionsBar;
import ui.tooloptions.NodeOptionsBar;
import util.FileUtils;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

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

	// Dialogs, if they are being displayed
	@Getter
	private GridSettingsDialog gridSettingsDialog;
	@Getter
	private NewGraphDialog newGraphDialog;

	private MenuBar menuBar; // The menu bar
	private ToolBar toolBar; // The tool bar

	//Tool option bars
	@Getter
	private NodeOptionsBar nodeOptionsBar;
	@Getter
	private EdgeOptionsBar edgeOptionsBar;
	@Getter
	private JPanel toolOptionsPanel; // This panel will hold all tool option bars

	private JScrollPane panelEditorScroll; // The scrolling pane containing the editor

	@Getter
	private Editor editor; // The main panel workspace

	private JPanel panelProperties;

	private JPanel panelStatus;

	@Getter
	private JFileChooser fileChooser;

	@Getter
	private Tool currentTool; // The tool currently being used

	@Getter
	private GBContext context; // The context for the current instance of the program

	public GUI(GBContext initialContext) {
		super();

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
		toolOptionsPanel = new JPanel();
		toolOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		nodeOptionsBar = new NodeOptionsBar(this);
		edgeOptionsBar = new EdgeOptionsBar(this);

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
		add(toolOptionsPanel, toptgbc);

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
		for (GBNode n : context.getGbNodes()) {
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
			SwingUtilities.invokeLater(() -> horiz.setValue((horiz.getMaximum() - horiz.getVisibleAmount()) / 2));
		}
		if (vert != null) {
			SwingUtilities.invokeLater(() -> vert.setValue((vert.getMaximum() - vert.getVisibleAmount()) / 2));
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
	 * Procedure to change the tool options bar (to the options bar for the
	 * tool currently being used).
	 */
	public void changeToolOptionsBar() {
		toolOptionsPanel.removeAll();
		if (currentTool == Tool.NODE) {
			toolOptionsPanel.add(nodeOptionsBar);
		} else if (currentTool == Tool.EDGE || currentTool == Tool.DIRECTED_EDGE) {
			toolOptionsPanel.add(edgeOptionsBar);
		}
		toolOptionsPanel.repaint();
		toolOptionsPanel.revalidate();
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
	public void updateContext(GBContext newContext) {
		Logger.writeEntry(Logger.INFO, String.format("Switching context to %s.", newContext.getCurrentlyLoadedFile()));
		context = newContext;
		context.setGUI(this);
		editor.clearState();
		updateByConstraint();
		KeyActions.initialize(this); // Re-initialize key bindings with correct context
		menuBar.updateWithNewContext();
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
	 * Get the scrolling pane containing the editor panel.
	 *
	 * @return This GUI's scroll pane.
	 */
	public JScrollPane getScrollPane() {
		return panelEditorScroll;
	}

}

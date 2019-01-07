package ui;

import context.GBContext;
import graph.Graph;
import graph.GraphConstraint;
import config.KeyActions;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * The main window in which the program runs.
 *
 * @author Brian Yao
 */
public class GBFrame extends JFrame {

	private static final long serialVersionUID = -8275121379599770074L;
	private static final String VERSION = "0.1.6";
	private static final String GRAPH_BUILDER_FILES = "Graph Builder Files";

	public static final String DEFAULT_FILENAME = "Untitled";
	public static final String DEFAULT_TITLE = "GraphBuilder " + VERSION;
	public static final String GRAPH_BUILDER_FILE_EXTENSION = "gbf";

	// Dialogs, if they are being displayed
	@Getter
	private GridSettingsDialog gridSettingsDialog;
	@Getter
	private NewGraphDialog newGraphDialog;

	private MenuBar menuBar; // The menu bar
	private ToolBar toolBar; // The tool bar

	// Tool option bars
	@Getter
	private NodeOptionsBar nodeOptionsBar;
	@Getter
	private EdgeOptionsBar edgeOptionsBar;
	@Getter
	private JPanel toolOptionsPanel; // This panel will hold all tool option bars

	@Getter
	private JScrollPane scrollPane; // The scrolling pane containing the editor
	@Getter
	private Editor editor; // The main panel workspace

	@Getter
	private JFileChooser fileChooser;

	@Getter
	private Tool currentTool; // The tool currently being used

	@Getter
	private GBContext context; // The context for the current instance of the program

	public GBFrame(GBContext initialContext) {
		// Set context
		context = initialContext;
		context.setGUI(this);

		// Initialize dialogs
		gridSettingsDialog = new GridSettingsDialog(this);
		newGraphDialog = new NewGraphDialog(this);

		// Initialize toolbar
		toolBar = new ToolBar(this);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		// Initialize the key bindings and their actions
		KeyActions.initialize(this);

		// Initialize and customize the file chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(new FileNameExtensionFilter(GRAPH_BUILDER_FILES, GRAPH_BUILDER_FILE_EXTENSION));

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
				FileUtils.exitProcedure(GBFrame.this.context);
			}

		});

		// Manage the layout constraints
		GridBagConstraints tbargbc = new GridBagConstraints();
		GridBagConstraints toptgbc = new GridBagConstraints();
		GridBagConstraints editorgbc = new GridBagConstraints();

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

		// Initialize and set up the main editor panel
		editor = new Editor(this);
		scrollPane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(scrollPane, editorgbc);

		// Initialize and set menu bar
		menuBar = new MenuBar(this);
		setJMenuBar(menuBar);

		revalidate();

		// By default, start with the Select tool
		updateTool(Tool.SELECT);
		updateByConstraint();

		// Add all node panels to the editor panel
		context.getGbNodes().forEach(gn -> editor.add(gn.getNodePanel()));

		// Update GBFrame title
		setTitle(FileUtils.getGuiTitle(this));
		context.setAsSaved();

		// Show GBFrame
		setVisible(true);

		// Set the initial scrolling positions to the center
		final JScrollBar horiz = scrollPane.getHorizontalScrollBar();
		final JScrollBar vert = scrollPane.getVerticalScrollBar();
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
		Map<Tool, JButton> toolButtons = toolBar.getToolButtons();
		Map<Tool, OrderedPair<ImageIcon>> toolIcons = toolBar.getToolIcons();
		if (currentTool != null) {
			if (currentTool != t) {
				if (toolIcons.containsKey(currentTool)) {
					toolButtons.get(currentTool).setIcon(toolIcons.get(currentTool).getFirst());
				}
				if (toolIcons.containsKey(t)) {
					toolButtons.get(t).setIcon(toolIcons.get(t).getSecond());
				}
			}
		} else if (toolIcons.containsKey(t)) {
			toolButtons.get(t).setIcon(toolIcons.get(t).getSecond());
		}

		currentTool = t;
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
		int viewportWidth = scrollPane.getViewport().getWidth();
		int viewportHeight = scrollPane.getViewport().getHeight();
		Point topLeft = scrollPane.getViewport().getViewPosition();

		return new Point(topLeft.x + viewportWidth / 2, topLeft.y + viewportHeight / 2);
	}

	/**
	 * Make changes to certain parts of the GBFrame according to the current
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
		setTitle(FileUtils.getGuiTitle(this));
		context.setAsSaved();
	}

	/**
	 * Get the menu bar object on this GBFrame.
	 *
	 * @return The menu bar.
	 */
	public MenuBar getMainMenuBar() {
		return menuBar;
	}

}

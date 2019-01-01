package ui.menus;

import actions.SwitchToolAction;
import actions.edit.*;
import actions.file.New;
import actions.file.Open;
import actions.file.Save;
import actions.file.SaveAs;
import algorithms.BFS;
import algorithms.Cycles;
import algorithms.DFS;
import graph.components.Node;
import graph.path.Cycle;
import keybindings.KeyActions;
import structures.EditorData;
import tool.Tool;
import ui.GBFrame;
import ui.dialogs.CompleteGraphDialog;
import util.FileUtils;
import util.StructureUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * The main menu bar which appears at the top of the GBFrame.
 *
 * @author Brian Yao
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = -7109662156036502356L;

	private GBFrame gui;

	private JMenu file;
	private JMenu edit;
	private JMenu view;
	private JMenu graph;
	private JMenu tools;
	private JMenu help;

	private JMenuItem newFile;
	private JMenuItem openFile;
	private JMenuItem saveFile;
	private JMenuItem saveAsFile;
	private JMenuItem exit;

	private JMenuItem undo;
	private JMenuItem redo;
	private JMenuItem copy;
	private JMenuItem copyFull;
	private JMenuItem duplicate;
	private JMenuItem duplicateFull;
	private JMenuItem paste;
	private JMenuItem cut;
	private JMenuItem cutFull;
	private JMenuItem delete;
	private JMenuItem selectAll;

	private JMenuItem grid;

	private JMenu search;
	private JMenuItem bfs;
	private JMenuItem dfs;
	private JMenuItem bfsUndirected;
	private JMenuItem dfsUndirected;
	private JMenuItem classify;

	private JMenu path;
	private JMenuItem dijkstra;
	private JMenuItem findCycle;


	private JMenu generate;
	private JMenuItem completeGraph;
	private JMenuItem fromSeed;

	public MenuBar(final GBFrame g) {
		super();

		gui = g;

		// Initialize and fill menu bar
		file = new JMenu("File");
		edit = new JMenu("Edit");
		view = new JMenu("View");
		graph = new JMenu("Graph");
		tools = new JMenu("Tools");
		help = new JMenu("Help");

		// Fill "File" menu
		newFile = new JMenuItem("New");
		newFile.setAccelerator(KeyActions.NEW);
		newFile.setToolTipText("Create a new blank graph.");
		openFile = new JMenuItem("Open");
		openFile.setAccelerator(KeyActions.OPEN);
		openFile.setToolTipText("Open a saved GraphBuilder file for editing.");
		saveFile = new JMenuItem("Save");
		saveFile.setAccelerator(KeyActions.SAVE);
		saveFile.setToolTipText("Save the changes made to the graph.");
		saveAsFile = new JMenuItem("Save As");
		saveAsFile.setAccelerator(KeyActions.SAVE_AS);
		saveAsFile.setToolTipText("Save the graph as it is now to a new file.");
		exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyActions.EXIT);
		exit.setToolTipText("Exit GraphBuilder.");

		file.add(newFile);
		file.add(openFile);
		file.add(saveFile);
		file.add(saveAsFile);
		file.add(exit);

		// Fill "Edit" menu
		undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyActions.UNDO);
		undo.setEnabled(false);
		undo.setToolTipText("Undo the last change.");
		redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyActions.REDO);
		redo.setEnabled(false);
		redo.setToolTipText("Perform the last undone change.");
		copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyActions.COPY);
		copy.setEnabled(false);
		copy.setToolTipText("Copy the selected graph components to the clipboard.");
		copyFull = new JMenuItem("Copy full subgraph");
		copyFull.setAccelerator(KeyActions.COPY_FULL);
		copyFull.setEnabled(false);
		copyFull.setToolTipText("Copy the subgraph induced by the selected nodes.");
		duplicate = new JMenuItem("Duplicate");
		duplicate.setAccelerator(KeyActions.DUPLICATE);
		duplicate.setEnabled(false);
		duplicate.setToolTipText("Duplicate the selection without copying it to the clipboard.");
		duplicateFull = new JMenuItem("Duplicate full subgraph");
		duplicateFull.setAccelerator(KeyActions.DUPLICATE_FULL);
		duplicateFull.setEnabled(false);
		duplicateFull.setToolTipText("Duplicate the subgraph induced by the selected nodes.");
		paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyActions.PASTE);
		paste.setEnabled(false);
		paste.setToolTipText("Paste the contents of the clipboard.");
		cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyActions.CUT);
		cut.setEnabled(false);
		cut.setToolTipText("Delete and add the selected components to the clipboard.");
		cutFull = new JMenuItem("Cut full subgraph");
		cutFull.setAccelerator(KeyActions.CUT_FULL);
		cutFull.setEnabled(false);
		cutFull.setToolTipText("Cut the subgraph induced by the selected nodes.");
		delete = new JMenuItem("Delete");
		delete.setAccelerator(KeyActions.BACKSPACE);
		delete.setEnabled(false);
		delete.setToolTipText("Delete the selected components.");
		selectAll = new JMenuItem("Select All");
		selectAll.setAccelerator(KeyActions.SELECT_ALL);
		selectAll.setToolTipText("Select all components in the graph.");

		edit.add(undo);
		edit.add(redo);
		edit.addSeparator();
		edit.add(copy);
		edit.add(copyFull);
		edit.add(duplicate);
		edit.add(duplicateFull);
		edit.add(paste);
		edit.add(cut);
		edit.add(cutFull);
		edit.add(delete);
		edit.addSeparator();
		edit.add(selectAll);

		// Fill "View" menu
		grid = new JMenuItem("Grid");
		grid.setToolTipText("View or change grid settings such as grid snapping.");
		grid.addActionListener(e -> gui.getGridSettingsDialog().showDialog());

		view.add(grid);

		// Fill "Graph" menu
		search = new JMenu("Search");
		search.setEnabled(false);
		bfs = new JMenuItem("BFS");
		bfs.setToolTipText("Highlight nodes searched using Breadth First Search starting from the selected nodes.");
		dfs = new JMenuItem("DFS");
		dfs.setToolTipText("Highlight nodes searched using Depth First Search starting from the selected nodes.");
		bfsUndirected = new JMenuItem("BFS Undirected");
		bfsUndirected.setToolTipText("Same as BFS, but ignore the directionality of edges while searching.");
		dfsUndirected = new JMenuItem("DFS Undirected");
		dfsUndirected.setToolTipText("Same as DFS, but ignore the directionality of edges while searching.");

		EditorData editorData = gui.getEditor().getData();
		bfs.addActionListener($ -> {
			Set<Node> selectedNodes = StructureUtils.toNodes(editorData.getSelectedNodes());
			Set<Node> traversed = BFS.exploreAll(selectedNodes, true);
			editorData.addHighlights(StructureUtils.toGbNodes(traversed));
			gui.getEditor().repaint();
		});

		dfs.addActionListener($ -> {
			Set<Node> selectedNodes = StructureUtils.toNodes(editorData.getSelectedNodes());
			Set<Node> traversed = DFS.exploreAll(selectedNodes, true);
			editorData.addHighlights(StructureUtils.toGbNodes(traversed));
			gui.getEditor().repaint();
		});

		bfsUndirected.addActionListener($ -> {
			Set<Node> selectedNodes = StructureUtils.toNodes(editorData.getSelectedNodes());
			Set<Node> traversed = BFS.exploreAll(selectedNodes, false);
			editorData.addHighlights(StructureUtils.toGbNodes(traversed));
			gui.getEditor().repaint();
		});

		dfsUndirected.addActionListener($ -> {
			Set<Node> selectedNodes = StructureUtils.toNodes(editorData.getSelectedNodes());
			Set<Node> traversed = DFS.exploreAll(selectedNodes, false);
			editorData.addHighlights(StructureUtils.toGbNodes(traversed));
			gui.getEditor().repaint();
		});

		search.add(bfs);
		search.add(dfs);
		search.add(bfsUndirected);
		search.add(dfsUndirected);

		path = new JMenu("Path");
		dijkstra = new JMenuItem("Dijkstra");
		dijkstra.setToolTipText("Perform Dijkstra's shortest path algorithm. After clicking this menu item, click " +
									"the start and destination nodes in your graph. The shortest path (if any) from " +
									"the start to the end will be highlighted.");
		findCycle = new JMenuItem("Find Cycle");
		findCycle.setToolTipText("Find an arbitrary cycle in the graph. The cycle must follow edge directionality. " +
									 "If a cycle exists, it will be highlighted.");

		dijkstra.addActionListener(new SwitchToolAction(gui.getContext(), Tool.SHORTEST_PATH));
		findCycle.addActionListener($ -> {
			Cycle cycle = Cycles.findCycle(gui.getContext().getGraph());
			if (cycle != null) {
				gui.getEditor().getData().addHighlights(StructureUtils.toGbNodes(cycle.getNodes()));
				gui.getEditor().getData().addHighlights(StructureUtils.toGbEdges(cycle.getEdges()));
				gui.getEditor().repaint();
			}
		});

		path.add(dijkstra);
		path.add(findCycle);

		classify = new JMenuItem("Classify");
		generate = new JMenu("Generate");

		completeGraph = new JMenuItem("Complete Graph");
		completeGraph.setToolTipText("Generate a complete graph with the prompted number of nodes.");
		completeGraph.addActionListener($ -> new CompleteGraphDialog(gui));

		fromSeed = new JMenuItem("From Seed");

		generate.add(completeGraph);
		generate.add(fromSeed);

		graph.add(search);
		graph.add(path);
		graph.add(classify);
		graph.add(generate);

		add(file);
		add(edit);
		add(view);
		add(graph);
		add(tools);
		add(help);

		// Add action listeners
		updateWithNewContext();
	}

	/**
	 * Sets the enabled state of the undo menu item.
	 *
	 * @param enabled Whether undo is possible.
	 */
	public void setUndoEnabled(boolean enabled) {
		undo.setEnabled(enabled);
	}

	/**
	 * Sets the enabled state of the redo menu item.
	 *
	 * @param enabled Whether redo is possible.
	 */
	public void setRedoEnabled(boolean enabled) {
		redo.setEnabled(enabled);
	}

	/**
	 * Update the actions associated with menu items to occur in the context
	 * of the GBFrame (the context likely has just been changed).
	 */
	public void updateWithNewContext() {
		removeAllActionListeners(
			newFile, openFile, saveFile, saveAsFile, exit, undo, redo, copy, copyFull, duplicate,
			duplicateFull, paste, cut, cutFull, delete, duplicate, duplicateFull
		);

		newFile.addActionListener(new New(gui.getContext()));
		openFile.addActionListener(new Open(gui.getContext()));
		saveFile.addActionListener(new Save(gui.getContext()));
		saveAsFile.addActionListener(new SaveAs(gui.getContext()));
		exit.addActionListener(e -> FileUtils.exitProcedure(gui.getContext()));

		undo.addActionListener(new Undo(gui.getContext()));
		redo.addActionListener(new Redo(gui.getContext()));
		copy.addActionListener(new Copy(gui.getContext(), false));
		copyFull.addActionListener(new Copy(gui.getContext(), true));
		duplicate.addActionListener(new PushDuplicate(gui.getContext(), false));
		duplicateFull.addActionListener(new PushDuplicate(gui.getContext(), true));
		paste.addActionListener(new PushPaste(gui.getContext()));
		cut.addActionListener(new PushCut(gui.getContext(), false));
		cutFull.addActionListener(new PushCut(gui.getContext(), true));
		delete.addActionListener(new PushDelete(gui.getContext()));
		duplicate.addActionListener(new PushDuplicate(gui.getContext(), false));
		duplicateFull.addActionListener(new PushDuplicate(gui.getContext(), true));
	}

	/**
	 * Update the enabled/disabled state of menu items depending on empty/non-empty selection.
	 */
	public void updateWithSelection() {
		boolean somethingSelected = !gui.getEditor().getData().selectionsEmpty();
		copy.setEnabled(somethingSelected);
		cut.setEnabled(somethingSelected);
		delete.setEnabled(somethingSelected);
		duplicate.setEnabled(somethingSelected);

		boolean nodeSelected = !gui.getEditor().getData().getSelectedNodes().isEmpty();
		copyFull.setEnabled(nodeSelected);
		duplicateFull.setEnabled(nodeSelected);
		cutFull.setEnabled(nodeSelected);

		search.setEnabled(nodeSelected);
	}

	/**
	 * Update the enabled/disabled state of menu items depending on empty/non-empty clipboard.
	 */
	public void updateWithCopy() {
		paste.setEnabled(!gui.getContext().getClipboard().isEmpty());
	}

	/**
	 * Remove all action listeners from the list of buttons.
	 *
	 * @param buttons The list of buttons to remove action listeners from.
	 */
	private static void removeAllActionListeners(AbstractButton... buttons) {
		for (AbstractButton button : buttons) {
			for (ActionListener listener : button.getActionListeners()) {
				button.removeActionListener(listener);
			}
		}
	}

}

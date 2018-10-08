package ui.menus;

import actions.edit.*;
import actions.file.New;
import actions.file.Open;
import actions.file.Save;
import actions.file.SaveAs;
import algorithms.Traversals;
import graph.components.Node;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import keybindings.KeyActions;
import org.javatuples.Pair;
import structures.UOPair;
import ui.GUI;
import ui.dialogs.CompleteGraphDialog;
import util.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main menu bar which appears at the top of the GUI.
 *
 * @author Brian Yao
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = -7109662156036502356L;

	private GUI gui;

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

	private JMenuItem grid;

	private JMenuItem traverse;
	private JMenuItem traverseUndirected;
	private JMenuItem classify;
	private JMenuItem generate;

	private JMenuItem completeGraph;
	private JMenuItem fromSeed;

	public MenuBar(final GUI g) {
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
		openFile = new JMenuItem("Open");
		openFile.setAccelerator(KeyActions.OPEN);
		saveFile = new JMenuItem("Save");
		saveFile.setAccelerator(KeyActions.SAVE);
		saveAsFile = new JMenuItem("Save As");
		saveAsFile.setAccelerator(KeyActions.SAVE_AS);
		exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyActions.EXIT);

		file.add(newFile);
		file.add(openFile);
		file.add(saveFile);
		file.add(saveAsFile);
		file.add(exit);

		// Fill "Edit" menu
		undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyActions.UNDO);
		undo.setEnabled(false);
		redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyActions.REDO);
		redo.setEnabled(false);
		copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyActions.COPY);
		copy.setEnabled(false);
		copyFull = new JMenuItem("Copy full subgraph");
		copyFull.setAccelerator(KeyActions.COPY_FULL);
		copyFull.setEnabled(false);
		duplicate = new JMenuItem("Duplicate");
		duplicate.setAccelerator(KeyActions.DUPLICATE);
		duplicate.setEnabled(false);
		duplicateFull = new JMenuItem("Duplicate full subgraph");
		duplicateFull.setAccelerator(KeyActions.DUPLICATE_FULL);
		duplicateFull.setEnabled(false);
		paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyActions.PASTE);
		paste.setEnabled(false);
		cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyActions.CUT);
		cut.setEnabled(false);
		cutFull = new JMenuItem("Cut full subgraph");
		cutFull.setAccelerator(KeyActions.CUT_FULL);
		cutFull.setEnabled(false);
		delete = new JMenuItem("Delete");
		delete.setAccelerator(KeyActions.BACKSPACE);
		delete.setEnabled(false);

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

		// Fill "View" menu
		grid = new JMenuItem("Grid");
		grid.addActionListener(e -> gui.getGridSettingsDialog().showDialog());

		view.add(grid);

		// Fill "Graph" menu
		traverse = new JMenuItem("Traverse selected");
		traverse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Node> selectedNodes = gui.getEditor().getSelections().getValue0().stream()
					.map(GBNode::getNode)
					.collect(Collectors.toSet());
				Set<Node> traversed = Traversals.depthFirstSearchAll(selectedNodes, true);
				gui.getEditor().addSelections(traversed.stream().map(Node::getGbNode).collect(Collectors.toSet()));
				gui.getEditor().repaint();
			}

		});

		traverseUndirected = new JMenuItem("Traverse selected ignoring direction");
		traverseUndirected.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Node> selectedNodes = gui.getEditor().getSelections().getValue0().stream()
					.map(GBNode::getNode)
					.collect(Collectors.toSet());
				Set<Node> traversed = Traversals.depthFirstSearchAll(selectedNodes, false);
				gui.getEditor().addSelections(traversed.stream().map(Node::getGbNode).collect(Collectors.toSet()));
				gui.getEditor().repaint();
			}

		});

		classify = new JMenuItem("Classify");
		generate = new JMenu("Generate");

		completeGraph = new JMenuItem("Complete Graph");
		completeGraph.addActionListener(ignored -> new CompleteGraphDialog(gui));

		fromSeed = new JMenuItem("From Seed");

		generate.add(completeGraph);
		generate.add(fromSeed);

		graph.add(traverse);
		graph.add(traverseUndirected);
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
	 * of the GUI (the context likely has just been changed).
	 */
	public void updateWithNewContext() {
		removeAllActionListeners(new AbstractButton[]{
			newFile, openFile, saveFile, saveAsFile, exit, undo, redo, copy, copyFull, duplicate,
			duplicateFull, paste, cut, cutFull, delete, duplicate, duplicateFull
		});

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
		Pair<Set<GBNode>, Map<UOPair<GBNode>, List<GBEdge>>> selections = gui.getEditor().getSelections();
		boolean somethingSelected = !selections.getValue0().isEmpty() || !selections.getValue1().isEmpty();
		copy.setEnabled(somethingSelected);
		cut.setEnabled(somethingSelected);
		delete.setEnabled(somethingSelected);
		duplicate.setEnabled(somethingSelected);

		boolean nodeSelected = !selections.getValue0().isEmpty();
		copyFull.setEnabled(nodeSelected);
		duplicateFull.setEnabled(nodeSelected);
		cutFull.setEnabled(nodeSelected);
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
	private static void removeAllActionListeners(AbstractButton[] buttons) {
		for (AbstractButton button : buttons) {
			for (ActionListener listener : button.getActionListeners()) {
				button.removeActionListener(listener);
			}
		}
	}

}

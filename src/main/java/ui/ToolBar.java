package ui;

import lombok.Getter;
import structures.OrderedPair;
import tool.Tool;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The main tool bar at the top of the GBFrame.
 *
 * @author Brian Yao
 */
public class ToolBar extends JToolBar {

	private static final long serialVersionUID = -4276539066793201017L;

	@Getter
	private Map<Tool, OrderedPair<ImageIcon>> toolIcons;
	@Getter
	private Map<Tool, JButton> toolButtons;
	
	private GBFrame frame;

	//Tool bar (right below menu bar)
	private JButton selectButton;
	private JButton edgeSelectButton;
	private JButton nodeButton;
	private JButton arrowButton;
	private JButton lineButton;
	private JButton panButton;

	//Icons for tool bar
	private final ImageIcon selectIcon;
	private final ImageIcon edgeSelectIcon;
	private final ImageIcon nodeIcon;
	private final ImageIcon arrowIcon;
	private final ImageIcon lineIcon;
	private final ImageIcon panIcon;
	private final ImageIcon selectSelectedIcon;
	private final ImageIcon edgeSelectSelectedIcon;
	private final ImageIcon nodeSelectedIcon;
	private final ImageIcon arrowSelectedIcon;
	private final ImageIcon lineSelectedIcon;
	private final ImageIcon panSelectedIcon;
	// Selected icons have 50% less brightness

	public ToolBar(GBFrame gui) {
		frame = gui;

		//Initialize icons for tools
		selectIcon = new ImageIcon("img/iconSelect.png");
		edgeSelectIcon = new ImageIcon("img/iconEdgeSelect.png");
		nodeIcon = new ImageIcon("img/iconCircle.png");
		arrowIcon = new ImageIcon("img/iconArrow.png");
		lineIcon = new ImageIcon("img/iconLine.png");
		panIcon = new ImageIcon("img/iconPan.png");
		selectSelectedIcon = new ImageIcon("img/iconSelectSelected.png");
		edgeSelectSelectedIcon = new ImageIcon("img/iconEdgeSelectSelected.png");
		nodeSelectedIcon = new ImageIcon("img/iconCircleSelected.png");
		arrowSelectedIcon = new ImageIcon("img/iconArrowSelected.png");
		lineSelectedIcon = new ImageIcon("img/iconLineSelected.png");
		panSelectedIcon = new ImageIcon("img/iconPanSelected.png");

		// Hash tools to both their icons
		toolIcons = new HashMap<>();
		toolIcons.put(Tool.SELECT, new OrderedPair<>(selectIcon, selectSelectedIcon));
		toolIcons.put(Tool.EDGE_SELECT, new OrderedPair<>(edgeSelectIcon, edgeSelectSelectedIcon));
		toolIcons.put(Tool.NODE, new OrderedPair<>(nodeIcon, nodeSelectedIcon));
		toolIcons.put(Tool.DIRECTED_EDGE, new OrderedPair<>(arrowIcon, arrowSelectedIcon));
		toolIcons.put(Tool.EDGE, new OrderedPair<>(lineIcon, lineSelectedIcon));
		toolIcons.put(Tool.PAN, new OrderedPair<>(panIcon, panSelectedIcon));

		// Initialize tool buttons and add listeners to them.
		selectButton = new JButton(selectIcon);
		selectButton.setToolTipText("<html>Select Tool: Use the left mouse button to select and drag nodes." +
										"<br>Keyboard Shortcut: S</html>");
		selectButton.addActionListener($ -> {
			frame.updateTool(Tool.SELECT);
			frame.changeToolOptionsBar();
		});

		edgeSelectButton = new JButton(edgeSelectIcon);
		edgeSelectButton.setToolTipText("<html>Edge Select Tool: Use the left mouse button to select the closest" +
											"edge.<br>Keyboard Shortcut: R</html>");
		edgeSelectButton.addActionListener($ -> {
			frame.updateTool(Tool.EDGE_SELECT);
			frame.changeToolOptionsBar();
		});

		nodeButton = new JButton(nodeIcon);
		nodeButton.setToolTipText("<html>Node Tool: Places a new node.<br>Keyboard shortcut: N</html>");
		nodeButton.addActionListener($ -> {
			frame.updateTool(Tool.NODE);
			frame.changeToolOptionsBar();
		});

		arrowButton = new JButton(arrowIcon);
		arrowButton.setToolTipText("<html>Directed Edge Tool: Draws a new directed edge between two nodes. Select" +
									   "two nodes in succession to draw.<br>Keyboard shortcut: D</html>");
		arrowButton.addActionListener($ -> {
			frame.updateTool(Tool.DIRECTED_EDGE);
			frame.changeToolOptionsBar();
		});

		lineButton = new JButton(lineIcon);
		lineButton.setToolTipText("<html>Edge Tool: Draws a new edge between two nodes. Select two nodes in" +
									  "succession to draw.<br>Keyboard shortcut: E</html>");
		lineButton.addActionListener($ -> {
			frame.updateTool(Tool.EDGE);
			frame.changeToolOptionsBar();
		});

		panButton = new JButton(panIcon);
		panButton.setToolTipText("<html>Pan Tool: Allows you to pan the workspace by dragging the left mouse click." +
									 "<br>Keyboard shortcut: P</html>");
		panButton.addActionListener($ -> {
			frame.updateTool(Tool.PAN);
			frame.changeToolOptionsBar();
		});

		//Add buttons to the toolbar
		add(selectButton);
		add(edgeSelectButton);
		add(nodeButton);
		add(lineButton);
		add(arrowButton);
		add(panButton);

		//Initialize and fill mapping from tools to the corresponding button
		toolButtons = new HashMap<>();
		toolButtons.put(Tool.SELECT, selectButton);
		toolButtons.put(Tool.EDGE_SELECT, edgeSelectButton);
		toolButtons.put(Tool.NODE, nodeButton);
		toolButtons.put(Tool.DIRECTED_EDGE, arrowButton);
		toolButtons.put(Tool.EDGE, lineButton);
		toolButtons.put(Tool.PAN, panButton);
	}

}

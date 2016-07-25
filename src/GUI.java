import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class GUI extends JFrame {
	
	private class ToolShortcut {
		
		// Indicates whether the button needs to be pressed down prior to the final key
		private boolean ctrlNeeded;
		private boolean shiftNeeded;
		private boolean altNeeded;
		
		private int keyCode; // The final keystroke which completes the shortcut
		
		public ToolShortcut(boolean ctrl, boolean shift, boolean alt, int key){
			ctrlNeeded = ctrl;
			shiftNeeded = shift;
			altNeeded = alt;

			keyCode = key;
		}
		
		public ToolShortcut(int key){
			this(false, false, false, key);
		}
		
		public boolean[] getReqs(){
			return new boolean[] {ctrlNeeded, shiftNeeded, altNeeded};
		}
		
		public int getFinalKeyCode(){
			return keyCode;
		}
		
		public int hashCode(){
			return ("" + ctrlNeeded + shiftNeeded + altNeeded + keyCode).hashCode();
		}
		
		public boolean equals(Object obj){
			ToolShortcut other = (ToolShortcut) obj;
			return keyCode == other.keyCode && ctrlNeeded == other.ctrlNeeded && shiftNeeded == other.shiftNeeded && altNeeded == other.altNeeded;
		}
		
	}
	
	private final int PANE_PADDING = 10;
//	private PriorityQueue<Node> order;
	private int currentID;
	
	//Menu bar
	private JMenuBar menuBar;
	private JMenu file;
	private JMenu edit;
	private JMenu tools;
	private JMenu help;
	
	//Tool bar (right below menu bar)
	private JToolBar toolbar;
	private JButton selectButton;
	private JButton edgeSelectButton;
	private JButton circleButton;
	private JButton arrowButton;
	private JButton lineButton;
	private JButton panButton;
	
	//Icons for tool bar
	private final ImageIcon selectIcon;
	private final ImageIcon edgeSelectIcon;
	private final ImageIcon circleIcon;
	private final ImageIcon arrowIcon;
	private final ImageIcon lineIcon;
	private final ImageIcon panIcon;
	private final ImageIcon selectSelectedIcon;
	private final ImageIcon edgeSelectSelectedIcon;
	private final ImageIcon circleSelectedIcon;
	private final ImageIcon arrowSelectedIcon;
	private final ImageIcon lineSelectedIcon;
	private final ImageIcon panSelectedIcon;
	// Selected icons have 50% less brightness
	
	private Tool currentTool;
	private HashMap<Tool, JButton> toolButtons;
	private HashMap<Tool, ImageIcon[]> toolIcons;
	private HashMap<ToolShortcut, Tool> toolShortcuts;
	
	private boolean controlPressed;
	private boolean altPressed;
	private boolean shiftPressed;
	
	private Circle linePoint;
	private Circle arrowPoint;
	
	//Tool options bar 
	private final ToolOptionsPanel nodeOptions;
	private ToolOptionsPanel lineOptions;
	private JPanel toolOptions;
//	private JLabel circleRadiusLabel;
//	private JTextField circleRadiusTextField;
//	private JSlider circleRadiusSlider;
//	private JLabel circleFillColorLabel;
//	private BufferedImage circleFillColorImage;
//	private ImageIcon circleFillColorIcon;
//	private JButton circleFillColorButton;
//	private Color circleFillColor;
//	private JLabel circleBorderColorLabel;
//	private BufferedImage circleBorderColorImage;
//	private ImageIcon circleBorderColorIcon;
//	private JButton circleBorderColorButton;
//	private Color circleBorderColor;
//	private JLabel circleTextColorLabel;
//	private BufferedImage circleTextColorImage;
//	private ImageIcon circleTextColorIcon;
//	private JButton circleTextColorButton;
//	private Color circleTextColor;
	
	//The currently selected item
	private GraphComponent selection;
	
	private JScrollPane panelEditorScroll;
	
	//The main panel workspace!
	private Editor panelEditor;
	
	private JPanel panelProperties;
	
	private JPanel panelStatus;
	
	private JTabbedPane propertiesCirclePane;
	private JPanel generalCircleTab;	
	private JPanel appearanceCircleTab;
	private JPanel descriptionCircleTab;
	
	private JLabel generalSelection;
	private JLabel generalSelectionID;
	private JLabel generalNodeLocation;
	private JLabel generalNodeRadius;
	private JLabel generalNodeEdges;
	private JButton generalNodeEdgesAll;
	private JLabel generalNodeIndegree;
	private JLabel generalNodeOutdegree;
	private JLabel generalArrowFrom;
	private JLabel generalArrowTo;
	
	
	
	private HashSet<Circle> circles;
	private HashSet<Line> edges;
	private HashMap<Circle, HashMap<Circle, HashSet<Line>>> edgeMap = new HashMap<>();
	
	public GUI(){
		super("Graph Builder 1.0");
		currentID = 0;
		
		//Initialize and fill menu bar
		menuBar = new JMenuBar();
		file = new JMenu("File");
		edit = new JMenu("Edit");
		tools = new JMenu("Tools");
		help = new JMenu("Help");
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(tools);
		menuBar.add(help);
		setJMenuBar(menuBar);
		
		//Initialize toolbar
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		
		//Initialize icons for tools
		selectIcon = new ImageIcon("img/iconSelect.png");
		edgeSelectIcon = new ImageIcon("img/iconEdgeSelect.png");
		circleIcon = new ImageIcon("img/iconCircle.png");
		arrowIcon = new ImageIcon("img/iconArrow.png");
		lineIcon = new ImageIcon("img/iconLine.png");
		panIcon = new ImageIcon("img/iconPan.png");
		selectSelectedIcon = new ImageIcon("img/iconSelectSelected.png");
		edgeSelectSelectedIcon = new ImageIcon("img/iconEdgeSelectSelected.png");
		circleSelectedIcon = new ImageIcon("img/iconCircleSelected.png");
		arrowSelectedIcon = new ImageIcon("img/iconArrowSelected.png");
		lineSelectedIcon = new ImageIcon("img/iconLineSelected.png");
		panSelectedIcon = new ImageIcon("img/iconPanSelected.png");
		
		//Hash tools to both their icons
		toolIcons = new HashMap<>();
		toolIcons.put(Tool.SELECT, new ImageIcon[] {selectIcon, selectSelectedIcon});
		toolIcons.put(Tool.EDGE_SELECT, new ImageIcon[] {edgeSelectIcon, edgeSelectSelectedIcon});
		toolIcons.put(Tool.NODE, new ImageIcon[] {circleIcon, circleSelectedIcon});
		toolIcons.put(Tool.ARROW, new ImageIcon[] {arrowIcon, arrowSelectedIcon});
		toolIcons.put(Tool.LINE, new ImageIcon[] {lineIcon, lineSelectedIcon});
		toolIcons.put(Tool.PAN, new ImageIcon[] {panIcon, panSelectedIcon});
		
		//Initialize tool buttons and add listeners to them.
		selectButton = new JButton(selectIcon);
		selectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.SELECT);
				changeToolOptions(Tool.SELECT);
			}
		});
		selectButton.setToolTipText("Select Tool: Use the left mouse button to select and drag circles.");
		edgeSelectButton = new JButton(edgeSelectIcon);
		edgeSelectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.EDGE_SELECT);
				changeToolOptions(Tool.EDGE_SELECT);
			}
		});
		edgeSelectButton.setToolTipText("Edge Select Tool: Use the left mouse button to select the closest edge.");
		circleButton = new JButton(circleIcon);
		circleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.NODE);
				changeToolOptions(Tool.NODE);
			}
		});
		circleButton.setToolTipText("Circle Tool: Places a new circle.\nKeyboard shortcut: C");
		arrowButton = new JButton(arrowIcon);
		arrowButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.ARROW);
				changeToolOptions(Tool.ARROW);
			}
		});
		arrowButton.setToolTipText("Directed Edge Tool: Draws a new directed edge between two nodes. Select two nodes in succession to draw.\nKeyboard shortcut: A");
		lineButton = new JButton(lineIcon);
		lineButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.LINE);
				changeToolOptions(Tool.LINE);
			}
		});		
		lineButton.setToolTipText("Edge Tool: Draws a new edge between two nodes. Select two nodes in succession to draw.\nKeyboard shortcut: L");
		panButton = new JButton(panIcon);
		panButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionForToolButtons(Tool.PAN);
				changeToolOptions(Tool.LINE);
			}
		});
		panButton.setToolTipText("Pan Tool: Allows you to pan the workspace by dragging the left mouse click.\nKeyboard shortcut: P");
		
		//Obviously, these are not pressed by default
		controlPressed = false;
		shiftPressed = false;
		altPressed = false;
		
		//Fill mapping from shortcut key to the appropriate tool, and add key listener for shortcuts
		toolShortcuts = new HashMap<>();
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_S), Tool.SELECT);
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_E), Tool.EDGE_SELECT);
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_C), Tool.NODE);
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_A), Tool.ARROW);
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_L), Tool.LINE);
		toolShortcuts.put(new ToolShortcut(KeyEvent.VK_P), Tool.PAN);
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				switch(keyCode){
					case KeyEvent.VK_SHIFT:
						shiftPressed = true;
						break;
					case KeyEvent.VK_CONTROL:
						controlPressed = true;
						break;
					case KeyEvent.VK_ALT:
						altPressed = true;
						break;
					default:
						Tool t = toolShortcuts.get(new ToolShortcut(controlPressed, shiftPressed, altPressed, keyCode));
						actionForToolButtons(t);
						changeToolOptions(t);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				int keyCode = arg0.getKeyCode();
				switch(keyCode){
					case KeyEvent.VK_SHIFT:
						shiftPressed = false;
						break;
					case KeyEvent.VK_CONTROL:
						controlPressed = false;
						break;
					case KeyEvent.VK_ALT:
						altPressed = false;
						break;
					default:
				}
			}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		//Add buttons to the toolbar
		toolbar.add(selectButton);
		toolbar.add(edgeSelectButton);
		toolbar.add(circleButton);
		toolbar.add(lineButton);
		toolbar.add(arrowButton);
		toolbar.add(panButton);
		
		//Initialize and fill mapping from tools to the corresponding button
		toolButtons = new HashMap<>();
		toolButtons.put(Tool.SELECT, selectButton);
		toolButtons.put(Tool.EDGE_SELECT, edgeSelectButton);
		toolButtons.put(Tool.NODE, circleButton);
		toolButtons.put(Tool.ARROW, arrowButton);
		toolButtons.put(Tool.LINE, lineButton);
		toolButtons.put(Tool.PAN, panButton);
		
		//Initialize and fill out the options panel
		toolOptions = new JPanel();
		toolOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
		nodeOptions = new ToolOptionsPanel(this, Tool.NODE);
		lineOptions = new ToolOptionsPanel(this, Tool.LINE);
		
		//Set JFrame properties
		circles = new HashSet<>(); //Set of all circles in the editor
		edges = new HashSet<>();
		edgeMap = new HashMap<>();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1024, 768);
		setVisible(true);
		setLayout(new GridBagLayout());
		
		//Manage the layout constraints
		GridBagConstraints tbar = new GridBagConstraints();
		GridBagConstraints topt = new GridBagConstraints();
		GridBagConstraints editor = new GridBagConstraints();
		GridBagConstraints prop = new GridBagConstraints();
		GridBagConstraints status = new GridBagConstraints();
		
		tbar.gridx = 0;
		tbar.gridy = 0;
		tbar.gridwidth = 1;
		tbar.weightx = 1;
		tbar.weighty = 0.01;
		tbar.fill = GridBagConstraints.HORIZONTAL;
		add(toolbar, tbar);
		
		topt.gridx = 0;
		topt.gridy = 1;
		topt.gridwidth = 1;
		topt.weightx = 1;
		topt.weighty = 0.01;
		topt.fill = GridBagConstraints.HORIZONTAL;
		add(toolOptions, topt);
		
		editor.gridx = 0;
		editor.gridy = 2;
		editor.weightx = 1;
		editor.weighty = 1;
		editor.gridheight = 2;
		editor.insets = new Insets(9, 9, 9, 9);
		editor.fill = GridBagConstraints.BOTH;
		
		prop.gridx = 1;
		prop.gridy = 2;
		prop.weightx = 0.1;
		prop.weighty = 0.8;
		prop.insets = new Insets(2, 2, 2, 2);
		prop.fill = GridBagConstraints.BOTH;
		
		status.gridx = 1;
		status.gridy = 3;
		status.weightx = 0.1;
		status.weighty = 0.4;
		status.insets = new Insets(2, 2, 2, 2);
		status.fill = GridBagConstraints.BOTH;
		
		//Initialize and set up the main editor panel
		panelEditor = new Editor(this);
		panelEditorScroll = new JScrollPane(panelEditor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelEditorScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JScrollBar horiz = panelEditorScroll.getHorizontalScrollBar();
		JScrollBar vert = panelEditorScroll.getVerticalScrollBar();
		horiz.setValue((horiz.getMaximum() + horiz.getVisibleAmount() - horiz.getMinimum())/2);
		vert.setValue((vert.getMaximum()  + vert.getVisibleAmount() - vert.getMinimum())/2);
		add(panelEditorScroll, editor);
		
		//Initialize and set up the properties panel, used when someone right clicks an object and clicks properties
		panelProperties = new JPanel();
		Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		panelProperties.setBorder(BorderFactory.createTitledBorder(lowerEtched, "Properties"));
		
		generalCircleTab = new JPanel();
		GroupLayout gl = new GroupLayout(generalCircleTab);
		gl.setAutoCreateGaps(true);
		generalSelection = new JLabel("Selection Type:");
		generalSelectionID = new JLabel("Selection ID:");
		generalNodeLocation = new JLabel("Location:");
		generalNodeRadius = new JLabel("Radius:");
		generalNodeEdges = new JLabel("Edges:");
		generalNodeEdgesAll = new JButton("See Edges");
		generalNodeIndegree = new JLabel("Indegree:");
		generalNodeOutdegree = new JLabel("Outdegree:");
		gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(generalSelection)
				.addComponent(generalSelectionID)
				.addComponent(generalNodeLocation)
				.addComponent(generalNodeRadius)
				.addGroup(gl.createSequentialGroup()
						.addComponent(generalNodeEdges)
						.addComponent(generalNodeEdgesAll))
				.addComponent(generalNodeIndegree)
				.addComponent(generalNodeOutdegree));
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(generalSelection)
				.addComponent(generalSelectionID)
				.addComponent(generalNodeLocation)
				.addComponent(generalNodeRadius)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(generalNodeEdges)
						.addComponent(generalNodeEdgesAll))
				.addComponent(generalNodeIndegree)
				.addComponent(generalNodeOutdegree));
		
		generalCircleTab.setLayout(gl);
		
		appearanceCircleTab = new JPanel();
		descriptionCircleTab = new JPanel();
		
		//Initialize and fill out the tabbed pane
		propertiesCirclePane = new JTabbedPane();
		propertiesCirclePane.addTab("General", generalCircleTab);
		propertiesCirclePane.addTab("Appearance", appearanceCircleTab);
		propertiesCirclePane.addTab("Description", descriptionCircleTab);
		panelProperties.add(propertiesCirclePane, BorderLayout.CENTER);
//		panelProperties.addComponentListener(new ComponentListener(){
//			@Override
//			public void componentHidden(ComponentEvent arg0) {}
//			@Override
//			public void componentMoved(ComponentEvent arg0) {}
//			@Override
//			public void componentResized(ComponentEvent arg0) {
//				Rectangle newBounds = ((JPanel) arg0.getSource()).getBounds();
//				propertiesPane.setBounds(newBounds.x + PANE_PADDING, newBounds.y + PANE_PADDING,
//										newBounds.width - PANE_PADDING, newBounds.height - PANE_PADDING);
//				generalTab.setBounds(newBounds.x + PANE_PADDING, newBounds.y + PANE_PADDING,
//						newBounds.width - PANE_PADDING, newBounds.height - PANE_PADDING);
//				appearanceTab.setBounds(newBounds.x + PANE_PADDING, newBounds.y + PANE_PADDING,
//						newBounds.width - PANE_PADDING, newBounds.height - PANE_PADDING);
//				descriptionTab.setBounds(newBounds.x + PANE_PADDING, newBounds.y + PANE_PADDING,
//						newBounds.width - PANE_PADDING, newBounds.height - PANE_PADDING);
//			}
//			@Override
//			public void componentShown(ComponentEvent arg0) {}
//		});
		
		add(panelProperties, prop);
		
		panelStatus = new JPanel();
		panelStatus.setBorder(lowerEtched);
		add(panelStatus, status);
		revalidate();
	}
	
	/** The procedure when a tool button is pressed. Updates the current tool and the button appearance. */
	private void actionForToolButtons(Tool t){
		if(currentTool != null){
			if(currentTool != t){
				toolButtons.get(currentTool).setIcon(toolIcons.get(currentTool)[0]);
				toolButtons.get(t).setIcon(toolIcons.get(t)[1]);
				currentTool = t;
			}else{
				toolButtons.get(t).setIcon(toolIcons.get(t)[0]);
				currentTool = null;
			}
		}else{
			toolButtons.get(t).setIcon(toolIcons.get(t)[1]);
			currentTool = t;
		}
	}
	
	private void changeToolOptions(Tool t){
		toolOptions.removeAll();
		if(currentTool != null && currentTool != Tool.SELECT && currentTool != Tool.PAN){
			if(currentTool == Tool.NODE)
				toolOptions.add(nodeOptions);
			else if(currentTool == Tool.LINE){
				lineOptions.arrowToLine();
				toolOptions.add(lineOptions);
			}else if(currentTool == Tool.ARROW){
				lineOptions.lineToArrow();
				toolOptions.add(lineOptions);
			}
		}
		toolOptions.repaint();
		toolOptions.revalidate();
	}
	
	public void addCircle(Circle c){
		c.setID(currentID++);
		circles.add(c);
		panelEditor.add(c);
		panelEditor.repaint();
		panelEditor.revalidate();
	}
	
	public void removeCircle(Circle c){
		circles.remove(c);
		Iterator<Line> lineit = edges.iterator();
		while(lineit.hasNext())
			if(lineit.next().hasEndpoint(c))
				lineit.remove();
		if(edgeMap.keySet().contains(c)){
			edgeMap.remove(c);
		}else{
			Iterator<Circle> it = edgeMap.keySet().iterator();
			while(it.hasNext()){
				if(edgeMap.get(it.next()).keySet().contains(c))
					it.remove();
			}
		}
		panelEditor.remove(c);
		panelEditor.repaint();
		panelEditor.revalidate();
		if(linePoint == c)
			linePoint = null;
	}
	
	public void addEdge(Line l){
		edges.add(l);
		Circle[] ends = l.getEndpoints();
		boolean first = edgeMap.containsKey(ends[0]);
		boolean second = edgeMap.containsKey(ends[1]);
		if(first && edgeMap.get(ends[0]).containsKey(ends[1])){
			edgeMap.get(ends[0]).get(ends[1]).add(l);
		}else if(second && edgeMap.get(ends[1]).containsKey(ends[0])){
			edgeMap.get(ends[1]).get(ends[0]).add(l);
		}else if(first){
			edgeMap.get(ends[0]).put(ends[1], new HashSet<Line>());
			edgeMap.get(ends[0]).get(ends[1]).add(l);
		}else if(second){
			edgeMap.get(ends[1]).put(ends[0], new HashSet<Line>());
			edgeMap.get(ends[1]).get(ends[0]).add(l);
		}else{
			edgeMap.put(ends[0], new HashMap<Circle, HashSet<Line>>());
			edgeMap.get(ends[0]).put(ends[1], new HashSet<Line>());
			edgeMap.get(ends[0]).get(ends[1]).add(l);
		}
	}
	
	public void displayProperties(GraphComponent g){
		String type = "";
		JPanel panel = new JPanel();
		if(g instanceof Circle){
			type = "Circle";
			generalNodeLocation.setText("Location: %d, %d");
			generalNodeRadius.setText("Radius: %d");
			generalNodeEdges.setText("Number of Edges: %d");
			generalNodeEdgesAll.setText("See Edges");
			generalNodeIndegree.setText("Indegree: %d");
			generalNodeOutdegree.setText("Outdegree: %d");
			panel.add(propertiesCirclePane, BorderLayout.CENTER);;
		}
		generalSelection.setText(type);
		generalSelectionID.setText(String.valueOf(g.getID()));
		JFrame props = new JFrame(String.format("%s Properties: \"%d\"", type, g.getID()));
		int result = JOptionPane.showConfirmDialog(props, panel);
		if(result == JOptionPane.OK_OPTION){
			//TODO
		}
	}
	
	public JScrollPane getScrollPane(){
		return panelEditorScroll;
	}
	
	public HashSet<Circle> getCircles(){
		return circles;
	}
	
	public HashSet<Line> getEdges(){
		return edges;
	}
	
	public HashMap<Circle, HashMap<Circle, HashSet<Line>>> getEdgeMap(){
		return edgeMap;
	}
	
	public int currentID(){
		return currentID;
	}
	
	public GraphComponent getSelection(){
		return selection;
	}
	
	public Tool getCurrentTool(){
		return currentTool;
	}
	
	public Circle getLinePoint(){
		return linePoint;
	}
	
	public void setLinePoint(Circle c){
		linePoint = c;
	}
	
	public int getCurrentRadius(){
		if(currentTool == Tool.NODE)
			return nodeOptions.getCurrentRadius();
		return -1;
	}
	
	public Color[] getCurrentCircleColors(){
		if(currentTool == Tool.NODE)
			return nodeOptions.getCurrentCircleColors();
		return null;
	}
	
	public int getCurrentLineWeight(){
		if(currentTool == Tool.LINE || currentTool == Tool.ARROW)
			return lineOptions.getCurrentLineWeight();
		return -1;
	}
	
	public Color getCurrentLineColor(){
		if(currentTool == Tool.LINE || currentTool == Tool.ARROW)
			return lineOptions.getCurrentLineColor();
		return null;
	}
	
	public void setSelection(GraphComponent gc){
		selection = gc;
	}
}

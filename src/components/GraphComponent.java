package components;

import javax.swing.JPanel;

import context.GraphBuilderContext;

/** Abstract class for a component of the graph the user can interact with. */
public abstract class GraphComponent extends JPanel {

	private static final long serialVersionUID = -7725155749149231451L;
	
	private int id;
	private boolean selected;
	private int priority;
	
	private GraphBuilderContext context;
	
	public GraphComponent(GraphBuilderContext ctxt) {
		id = ctxt.getNextIDAndInc();
		ctxt.getIdMap().put(id, this);
		
		this.priority = 0;
		this.context = ctxt;
	}
	
	public GraphComponent(GraphBuilderContext ctxt, int id) {
		this.id = id;
		ctxt.getIdMap().put(id, this);
		
		this.priority = 0;
		this.context = ctxt;
	}
	
	public GraphComponent(int priority) {
		this.priority = priority;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		context.getIdMap().remove(this.id);
		this.id = id;
		context.getIdMap().put(this.id, this);
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public GraphBuilderContext getContext() {
		return context;
	}
	
}

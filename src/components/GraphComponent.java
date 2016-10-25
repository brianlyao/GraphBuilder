package components;

import context.GraphBuilderContext;

/** Abstract class for a component of the graph. */
public abstract class GraphComponent {
	
	private int id; // The unique id of this component
	private boolean selected; // Whether this component is selected
	private int priority; // Unused priority field
	
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

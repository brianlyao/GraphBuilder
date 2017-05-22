package components;

import context.GraphBuilderContext;

/**
 * An abstract class for a component of the graph.
 * 
 * @author Brian
 */
public abstract class GraphComponent {
	
	private int id; // The unique id of this component
	private boolean selected; // Whether this component is selected
//	private int priority; // Unused priority field
	
	private GraphBuilderContext context;
	
	/**
	 * @param ctxt The context in which this component is initialized.
	 */
	public GraphComponent(GraphBuilderContext ctxt) {
		id = ctxt.getNextIdAndInc();
		ctxt.getIdMap().put(id, this);
		selected = false;
		
//		this.priority = 0;
		this.context = ctxt;
	}
	
	/**
	 * @param ctxt The context in which this component is initialized.
	 * @param id   The id assigned to this component.
	 */
	public GraphComponent(GraphBuilderContext ctxt, int id) {
		this.id = id;
		ctxt.getIdMap().put(id, this);
		selected = false;
		
//		this.priority = 0;
		this.context = ctxt;
	}
	
	/**
	 * Get the ID associated with this graph component.
	 * 
	 * @return The integer ID.
	 */
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		context.getIdMap().remove(this.id);
		this.id = id;
		context.getIdMap().put(this.id, this);
	}
	
	/**
	 * Get whether this component is currently selected.
	 * 
	 * @return True iff this component is selected.
	 */
	public boolean getSelected() {
		return selected;
	}
	
	/**
	 * Set the selected state of this component.
	 * 
	 * @param b A true if we want to mark this component as selected, false otherwise.
	 */
	public void setSelected(boolean b) {
		selected = b;
	}
	
	/**
	 * Get the context in which this component exists.
	 * 
	 * @return The relevant context.
	 */
	public GraphBuilderContext getContext() {
		return context;
	}
	
	/**
	 * Serialize this graph component into a string with relevant data.
	 * 
	 * @return The serialized graph component.
	 */
	public abstract String toStorageString();
	
}

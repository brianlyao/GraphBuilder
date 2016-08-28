package components;
import javax.swing.JPanel;

/** Abstract class for a component of the graph the user can interact with. */
public abstract class GraphComponent extends JPanel {

	private static final long serialVersionUID = -7725155749149231451L;
	
	private static int idpool = 0;
	private int id;
	private boolean selected;
	private int priority;
	
	public GraphComponent() {
		id = idpool++;
		this.priority = 0;
	}
	
	public GraphComponent(int priority) {
		id = idpool++;
		this.priority = priority;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public static int getIdPool() {
		return idpool;
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
	
}

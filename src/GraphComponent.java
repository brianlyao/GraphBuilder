import javax.swing.JPanel;


public abstract class GraphComponent extends JPanel{
	private static int idpool = 0;
	private int id;
	private boolean selected;
	private int priority;
	
	public GraphComponent(){
		id = idpool++;
		this.priority = 0;
	}
	
	public GraphComponent(int priority){
		id = idpool++;
		this.priority = priority;
	}
	
	public abstract void displayProperties();
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public boolean getSelected(){
		return selected;
	}
	
	public void setSelected(boolean b){
		selected = b;
	}
	
	public int getPriority(){
		return priority;
	}
}

import java.awt.Color;


public enum Preferences {
	SELECTION_COLOR(Color.BLUE),
	LINE_START_COLOR(new Color(115, 30, 136)),
	LINE_END_COLOR(new Color(40, 145, 77)),
	ARROW_START_COLOR(new Color(219, 54, 36)),
	ARROW_END_COLOR(new Color(41, 52, 184)),
	CIRCLE_PREVIEW_COLOR(new Color(128, 128, 128)),
	EDGE_SPREAD_ANGLE(Math.toRadians(12)),
	PAN_SENSITIVITY(1.0);
	
	private Object data;
	private Preferences(Object o){
		data = o;
	}
	
	public Object getData(){
		return data;
	}
	
	public void setData(Object o){
		data = o;
	}
}

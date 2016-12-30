package preferences;
import java.awt.Color;

/**
 * An enum for holding user preferences. Currently, these are hard-coded, but eventually
 * they will be moved to a configuration file.
 * 
 * @author Brian
 */
public enum Preferences {
	
	LOG_FILE_PATH("log.txt"),
	SELECTION_COLOR(Color.BLUE),
	LINE_START_COLOR(new Color(115, 30, 136)),
	LINE_END_COLOR(new Color(40, 145, 77)),
	ARROW_START_COLOR(new Color(219, 54, 36)),
	ARROW_END_COLOR(new Color(41, 52, 184)),
	CIRCLE_PREVIEW_COLOR(new Color(128, 128, 128)),
	EDGE_SPREAD_ANGLE(Math.toRadians(15)),
	SELF_EDGE_SUBTENDED_ANGLE(Math.toRadians(45)),
	SELF_EDGE_ARC_ANGLE(Math.toRadians(110)),
	EDGE_PREVIEW_COLOR(new Color(170, 170, 170)),
	EDGE_SELECT_PREVIEW_COLOR(new Color(170, 170, 170)),
	EDGE_SELECT_SQUARE_SIZE(7),
	EDGE_SELECT_SQUARE_COLOR(new Color(230, 53, 54)),
	PAN_SENSITIVITY(1.0);
	
	private Object data;
	
	Preferences(Object o) {
		data = o;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object o) {
		data = o;
	}
	
}

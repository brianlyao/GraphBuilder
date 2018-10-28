package preferences;

import java.awt.Color;

/**
 * A class for holding user preferences. Currently, these are hard-coded, but eventually
 * they will be moved to a configuration file.
 *
 * @author Brian Yao
 */
public final class Preferences {

	public static final String LOG_FILE_PATH = "log.txt";

	public static final Color SELECTION_COLOR = Color.BLUE;
	public static final Color HIGHLIGHT_COLOR = Color.RED;
	public static final Color EDGE_BASE_POINT_COLOR = new Color(202, 38, 255);
	public static final Color EDGE_SECOND_POINT_COLOR = new Color(0, 255, 112);

	public static final double ARROW_TIP_SCALE_FACTOR = 5.0;

	public static final Color PREVIEW_COLOR = new Color(128, 128, 128);
	public static final Color EDGE_SELECT_PREVIEW_COLOR = Color.LIGHT_GRAY;

	public static final double EDGE_SPREAD_ANGLE = Math.toRadians(15);
	public static final double SELF_EDGE_SUBTENDED_ANGLE = Math.toRadians(45);
	public static final double SELF_EDGE_ARC_ANGLE = Math.toRadians(110);

	public static final double PAN_SENSITIVITY = 1.0;

	public static final Color ACTION_COLOR = Color.ORANGE;

}

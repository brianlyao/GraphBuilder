package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** A class of utility methods for image drawing. */
public class ImageUtils {
	
	/**
	 * Fill a buffered image with the specified color c.
	 * 
	 * @param b The image to fill.
	 * @param c
	 */
	public static void fillImage(BufferedImage b, Color c) {
		Graphics2D g2d = b.createGraphics();
		g2d.setPaint(c);
		g2d.fillRect(0, 0, b.getWidth(), b.getHeight());
	}
	
}

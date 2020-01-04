package lx.ledgeo.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import henning.leddriverj.util.Log;

public class MapUtil {
	// TODO Add ramp blue
	public static final int[] COLOR_NONE = { 0, 0, 0, 0 }; // : 0
	public static final int[] COLOR_SOLID = { 0, 255, 0, 3 }; // : 3
	public static final int[] COLOR_SPIKE = { 255, 0, 0, 2 }; // : 2
	public static final int[] COLOR_INVALID_SOLID = { 255, 255, 0, 4 };
	public static final int[] COLOR_JUMP = { 255, 165, 0, -1 }; // : -1
	public static final int[] COLOR_SCALE1 = { 0, 0, 20, -2 }; // : -2
	public static final int[] COLOR_SCALE2 = { 0, 0, 40, -3 }; // : -3
	public static final int[] COLOR_SCALE3 = { 0, 0, 60, -4 }; // : -4
	public static final int[] COLOR_FINISH = { 20, 0, 0, Integer.MIN_VALUE };
	public static final int[][] COLORS_MAP;
	static {
		COLORS_MAP = new int[][] { COLOR_NONE, COLOR_SOLID, COLOR_SPIKE, COLOR_JUMP, COLOR_SCALE1, COLOR_SCALE2,
				COLOR_SCALE3, COLOR_FINISH };
	}

	public static int[][] loadFromFile(InputStream i) throws IOException {
		Log.info("Parsing map...", "MapUtil");
		BufferedImage img = ImageIO.read(i);
		int width = img.getWidth();
		int height = img.getHeight();
		int[][] map = new int[height][width];
		for (int Y = 0; Y < height; Y++) {
			for (int X = 0; X < width; X++) {
				map[height - Y - 1][X] = getType(img, X, Y);
			}
		}
		Log.info("Parsing completed.", "MapUtil");
		return map;
	}

	public static int[][] loadFromFile(File f) throws IOException {
		if (f.exists()) {
			FileInputStream fi = new FileInputStream(f);
			return loadFromFile(fi);
		} else {
			Log.error("Unable to locate file " + f.toString(), "MapUtil");
			return null;
		}
	}

	public static int[][] loadMap(String mapName) throws IOException {
		InputStream ios = MapUtil.class.getResourceAsStream("/lx/ledgeo/maps/" + mapName + ".png");
		if (ios != null) {
			return loadFromFile(ios);
		} else {
			throw new IOException("Unable to locate map");
		}
	}

	public static int getType(BufferedImage img, int x, int y) {
		int rgbv = img.getRGB(x, y);
		int[] rgb = new int[3];
		rgb[2] = rgbv & 0xFF;
		rgb[1] = (rgbv >> 8) & 0xFF;
		rgb[0] = (rgbv >> 16) & 0xFF;
		for (int C = 0; C < COLORS_MAP.length; C++) {
			if (matches(rgb, COLORS_MAP[C])) {
				return COLORS_MAP[C][3];
			}
		}
		Log.warn("Invalid input color " + Arrays.toString(rgb), "MapUtil");
		return 3;
	}

	public static boolean matches(int[] color1, int[] color2) {
		for (int C = 0; C < 3; C++) {
			if (!(color1[C] - 10 < color2[C] && color1[C] + 10 > color2[C])) {
				return false;
			}
		}
		return true;
	}

}
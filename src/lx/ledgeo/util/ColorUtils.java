package lx.ledgeo.util;

public class ColorUtils {
	
	public static int[] copy(int[] color)	{
		return color.clone();
	}
	public static int[] invert(int[] color)	{
		int[] newcolor = new int[3];
		for (int C = 0;C < 3;C++)	{
			newcolor[C] = 255 - color[C];
		}
		return newcolor;
	}
	public static int[] multiply(int[] color,float factor)	{
		color[0] = (int) (color[0] * factor);
		color[1] = (int) (color[1] * factor);
		color[2] = (int) (color[2] * factor);
		return color;
	}
	
}

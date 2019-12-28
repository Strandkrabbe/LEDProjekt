package lx.ledgeo;

import java.io.IOException;

import henning.leddriverj.BoardController;

public class Main {
	private static final int[] red = {255,0 ,0};
	private static final int[] gre = {0,255,0};
	private static final int[] blu = {0,0,255};
	private static final int[] yel = {255,255,0};
	private static final int[] ora = {255,165,0};
	private static final int[] pur = {160,32,240};
	private static int[][][] rgb = {
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		blu,	yel,	blu,	{},		{},		{},		{},		{},		red,	{}},
			{blu,	ora,	{},		{},		{},		red,	{},		{},		yel,	gre,	yel,	{},		{},		red,	{},		red,	ora,	red},
			{ora,	blu,	{},		{},		red,	red	,	red,	{},		blu,	yel,	blu,	{},		red,	ora,	red,	red,	red,	red},
			}
	;
	private static int[][][] rgb2 = {
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
			{{},	{},		{},		{},		{},		{},		{},		{},		blu,	yel,	blu,	{},		{},		{},		{},		{},		red,	{}},
			{blu,	blu,	{},		{},		{},		red,	{},		{},		yel,	gre,	yel,	{},		{},		red,	{},		red,	ora,	red},
			{blu,	blu,	{},		{},		red,	ora	,	red,	{},		blu,	yel,	blu,	{},		red,	ora,	red,	red,	red,	red},
			}
	;
	
	
	public static void main(String[] args) throws IOException {
		BoardController b = new BoardController(18, 10);
		b.getController().clear();
		b.getController().useBrightnessMod(true);
		for (int Y = 0;Y < rgb2.length;Y++)	{
			for (int X = 0;X < rgb2[Y].length;X++)	{
				if (rgb2[Y][X] == null || rgb2[Y][X].length == 0)	{
					rgb2[Y][X] = new int[3];
				}
			}
		}
		b.setColors(rgb2);
		b.update();
		b.close();
	}
	
}

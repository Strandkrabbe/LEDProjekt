package lx.ledgeo;

import java.awt.event.KeyEvent;
import java.io.IOException;

import henning.leddriverj.BoardController;
import henning.leddriverj.LEDController;
import lx.ledgeo.draw.RootContainer;
import lx.ledgeo.input.InputProvider;
import lx.ledgeo.input.KeyInputFrame;

public class Main {
//	private static final int[] red = {255,0 ,0};
//	private static final int[] gre = {0,255,0};
//	private static final int[] blu = {0,0,255};
//	private static final int[] yel = {255,255,0};
//	private static final int[] ora = {255,165,0};
//	private static final int[] pur = {160,32,240};
//	private static int[][][] rgb = {
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		blu,	yel,	blu,	{},		{},		{},		{},		{},		red,	{}},
//			{blu,	ora,	{},		{},		{},		red,	{},		{},		yel,	gre,	yel,	{},		{},		red,	{},		red,	ora,	red},
//			{ora,	blu,	{},		{},		red,	red	,	red,	{},		blu,	yel,	blu,	{},		red,	ora,	red,	red,	red,	red},
//			}
//	;
//	private static int[][][] rgb2 = {
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{},		{}},
//			{{},	{},		{},		{},		{},		{},		{},		{},		blu,	yel,	blu,	{},		{},		{},		{},		{},		red,	{}},
//			{blu,	blu,	{},		{},		{},		red,	{},		{},		yel,	gre,	yel,	{},		{},		red,	{},		red,	ora,	red},
//			{blu,	gre,	{},		{},		red,	ora	,	red,	{},		blu,	yel,	blu,	{},		red,	ora,	red,	red,	red,	red},
//			}
//	;
//	
//	
//	public static void main(String[] args) throws IOException {
//		BoardController b = new BoardController(18, 10);
//		b.getController().clear();
//		b.getController().useBrightnessMod(true);
//		for (int Y = 0;Y < rgb2.length;Y++)	{
//			for (int X = 0;X < rgb2[Y].length;X++)	{
//				if (rgb2[Y][X] == null || rgb2[Y][X].length == 0)	{
//					rgb2[Y][X] = new int[3];
//				}
//			}
//		}
//		b.setColors(rgb2);
//		b.update();
//		b.close();
//	}
	
	private static RootContainer root;
	
	public static void main(String[] args) throws IOException {
		InputProvider input = new KeyInputFrame();
		root = new RootContainer(18, 10);
		root.setPosition(0, 0);
		BoardController board = root.getBoard();
		board.reset();
		LEDController ctl = board.getController();
		ctl.setAlpha(2);
		ctl.useBrightnessMod(true);
		Game game = new Game();
		root.add(game);
		boolean start = false;
		while (!start)	{
			if (input.hasKey())	{
				if (input.getLastKey() == KeyEvent.VK_S)
					start = true;
			}
		}
		game.start("map0", Skin.getDefaultSkin(), input);
		root.close();
		input.close();
	}
	
	public static void draw()	{
		root.draw();
	}
	
}

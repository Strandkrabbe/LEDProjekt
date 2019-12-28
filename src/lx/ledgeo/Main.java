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
//	private static final int[] blu = {0,0,255};
	private static int[][][] rgb = {
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}},
			{blu,blu,{},{},{},{},{},red,{},{},red,{},{},gre,{},{},{},{}},
			{blu,blu,{},{},{},{},red,red,red,red,red,red,{},gre,{},{},{},{}}
	};
	
	public static void main(String[] args) throws IOException {
		BoardController b = new BoardController(18, 10);
		b.setColors(rgb);
		b.update();
	}
	
}

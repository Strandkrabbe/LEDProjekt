package lx.ledgeo;

import java.io.IOException;
import henning.leddriverj.util.Log;
import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.ColorMode;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.draw.Image;
import lx.ledgeo.util.MapUtil;

public class Map extends BasicDrawable {

	// Current game position
	private int currentXPos = 0;
	private int currentYPos = 0; // 0 is default height, the bottom left corner is defined as 0,0
	private int level[][] = null;
	private Image noMapImg;
	private String mapName = "*";

	private int[] color_solid = { 0, 255, 0 };
	private int[] color_invalid_solid = { 255, 0, 255};
	private int[] color_spike = { 255, 0, 0 };
	private int[] color_playerblock = { 0, 0, 255 }; // Was is das??
	private int[] color_jump = { 255, 225, 0 };
	private int[] color_scale1 = { 10, 250, 180 };
	private int[] color_scale2 = { 10, 250, 180 };
	private int[] color_scale3 = { 10, 250, 180 };
	private int[] color_star = { 255,179 , 0 };
	private int[] color_deco = { 255,0 ,0 };
	private int[] color_gravity = {255,0,255};

	private int[] color_finish = { 255, 255, 255 };

	public Map() {
		this.noMapImg = new Image();
		this.noMapImg.load("no-map.png");
		this.noMapImg.setVisible(false);
		this.noMapImg.setPosition(0, 0);
		this.noMapImg.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
		this.setPosition(0, 0);
		this.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
		this.setVisible(true);
		this.setBackgrounColor(null);
	}

	public void loadMap(String name) {
		this.mapName = name;
		try {
			this.level = MapUtil.loadMap(name);
		} catch (IOException e) {
			this.level = null;
			Log.error("No map found at /lx/ledgeo/maps/" + name.toString() + ".png", "Map");
			Log.error(e);
		}
	}
	public void setStarDone(int x,int y)	{
		this.level[y][x] = 0;
	}

	public int getLevel(int x, int y) {
		if (y < 0 || y >= this.level.length)
			return 2;
		int[] row = level[y];
		if (x < 0)
			return 0;
		if (x >= row.length)
			return Integer.MIN_VALUE;
		return row[x];
	}

	public boolean isMapLoaded() {
		return this.level != null;
	}

	public int getFinishX() {
		return this.level[0].length;
	}
	public int getLevelHeight()	{
		return this.level.length;
	}

	@Override
	public boolean draw(DrawingArea a) {
		if (!super.draw(a)) {
			return false;
		}
		this.noMapImg.setVisible(this.level == null);
		a.drawInverse(noMapImg);
		if (this.level != null) {
			for (int Y = 0; Y < this.getHeight(); Y++) {
				for (int X = 0; X < this.getWidth(); X++) {
					int levelx = X + currentXPos;
					int levely = Y + currentYPos;
					if (levelx < 0 || levely < 0 || levely >= this.level.length)
						continue;
					int blocktype;
					if (levelx >= this.level[0].length) {
						blocktype = Integer.MIN_VALUE;
					} else {
						blocktype = this.level[Y + currentYPos][X + currentXPos];
					}
					int[] color;
					switch (blocktype) { // Time based annimations here TODO
					case 0:
						color = new int[] { 0, 0, 0 };
						break;
					case 1:
						color = color_playerblock;
						break;
					case 2:
						color = color_spike;
						break;
					case 3:
						color = color_solid;
						break;
					case 4:
						color = color_invalid_solid;
						break;
					case -1:
						color = color_jump;
						break;
					case -2:
						color = color_scale1;
						break;
					case -3:
						color = color_scale2;
						break;
					case -4:
						color = color_scale3;
						break;
					case -5:
						color = color_star;
						break;
					case -6:
						color = color_deco;
						break;
					case -7:
					case -8:
						color = color_gravity;
						break;
					case Integer.MIN_VALUE:
						color = color_finish;
						break;
					default:
						color = new int[] { 255, 255, 0 };
					}
					a.setColor(color);
					a.set(X, Y);
				}
			}
		}
		return true;
	}

	@Override
	public ColorMode getColorMode() {
		return ColorMode.REPLACE_NONEZERO;
	}

	public int getCurrentXPos() {
		return currentXPos;
	}

	public void setCurrentXPos(int currentXPos) {
		this.currentXPos = currentXPos;
	}

	public int getCurrentYPos() {
		return currentYPos;
	}

	public void setCurrentYPos(int currentYPos) {
		this.currentYPos = currentYPos;
	}
	
	public String getName()	{
		return this.mapName;
	}
	
//	private boolean isCollectingStar()	{
//		
//	}

}
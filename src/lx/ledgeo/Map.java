package lx.ledgeo;

import java.io.IOException;

import henning.leddriverj.util.Log;
import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.ColorMode;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.draw.Image;
import lx.ledgeo.util.MapUtil;

public class Map extends BasicDrawable {
	// 0 Nichts  0 0 0
	// 1 Spieler 0 0 255
	// 2 Spike 	 255 0 0
	// 3 Block	 0 255 0
	// -1 Jump	 255 165 0
	// -2 Scale 1 0 0 20
	// -3 Scale 2 0 0 40
	// -4 Scale 3 0 0 60
	
	// Current game position
	private int currentXPos = 0;
	private int currentYPos = 0;	// 0 is default height, the bottom left corner is defined as 0,0
	private int level[][] = null;
	private Image noMapImg;
	
	private int[] color_solid = {0,255,0};
	private int[] color_spike = {255,0,0};
	private int[] color_playerblock = {0,0,255}; // Was is das??
	private int[] color_jump = {255,265,0};
	private int[] color_scale1 = {10,250,180};
	private int[] color_scale2 = {10,250,180};
	private int[] color_scale3 = {10,250,180};
	
	public Map()	{
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
		try {
			this.level = MapUtil.loadMap(name);
		} catch (IOException e) {
			this.level = null;
			Log.error("No map found at /lx/ledgeo/maps/" + name.toString() + ".png", "Map");
			Log.error(e);
		}
	}

	public int getLevel(int x, int y) {
		return level[y][x];
	}
	
	public boolean isMapLoaded()	{
		return this.level != null;
	}
	
	@Override
	public boolean draw(DrawingArea a) {
		if (!super.draw(a))	{
			return false;
		}
		this.noMapImg.setVisible(this.level == null);
		a.drawInverse(noMapImg);
		if (this.level != null)	{
			for (int Y = 0;Y < this.getHeight();Y++)	{
				for (int X = 0;X < this.getWidth();X++)	{
					int levelx = X + currentXPos;
					int levely = Y + currentYPos;
					if (levelx < 0 || levely < 0)
						continue;
					int blocktype = this.level[Y + currentYPos][X + currentXPos];
					int[] color;
					switch (blocktype) {		// Time based annimations here TODO
					case 0:
						color = new int[] {0,0,0};
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
					default:
						color = new int[] {255,255,0};
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

}

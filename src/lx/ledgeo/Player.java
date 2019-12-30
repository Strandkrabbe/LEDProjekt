package lx.ledgeo;

import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.DrawingArea;

public class Player extends BasicDrawable {
	private static final int[] blu = { 0, 0, 255 };
	private static final int[][][] DEFAULT_SKIN = {
			// TODO
	};

//	private int posiX = 4;
//	private int posiY = 1;

	private int scale = 2;
	private int[][][] skin;
	private int njump = 2;
	
	public Player()	{
		
	}

//	public int getPosiX() {		// Doubled by position attributes of BasicDrawable
//		return posiX;
//	}
//
//	public void setPosiX(int posiX) {
//		this.posiX = posiX;
//	}
//
//	public int getPosiY() {
//		return posiY;
//	}
//
//	public void setPosiY(int posiY) {
//		this.posiY = posiY;
//	}

	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	@Override
	public int getHeight() {
		return this.scale;
	}
	@Override
	public int getWidth() {
		return this.scale;
	}
	
	@Override
	public boolean draw(DrawingArea a) {
		if (!super.draw(a))
			return false;
		return true;
	}

}

package lx.ledgeo;

import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.DrawingArea;

public class Player extends BasicDrawable {

	private int scale = 2;
	
	private Skin skin;
	
	public Player(Skin s)	{
		this.skin = s;
	}
	public Player()	{
		this(Skin.getDefaultSkin());
	}

	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public void setSkin(Skin s)	{
		this.skin = s;
	}
	public synchronized Skin getSkin()	{
		return this.skin;
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
	public synchronized boolean draw(DrawingArea a) {
		if (!super.draw(a))
			return false;
		a.draw(skin.getAnimatedSkin(), 0, 0);
		return true;
	}

}

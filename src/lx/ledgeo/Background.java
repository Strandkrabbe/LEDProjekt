package lx.ledgeo;

import henning.leddriverj.draw.BasicDrawable;
import henning.leddriverj.draw.ColorMode;

public abstract class Background extends BasicDrawable {

	public Background() {
		this.setPosition(0, 0);
		this.setSize(Game.GAME_WIDTH, Game.GAME_HEIGHT);
		this.setVisible(true);
		this.setBackgroundColor(null);
	}

	@Override
	public ColorMode getColorMode() {
		return ColorMode.REPLACE;
	}

	/**
	 * @param mapPos 0.0 to below 1.0
	 */
	public abstract void setMapPosition(double mapPos);

	public static Background getByName(int[] color) {
		return new ColorBackground(color);
	}
	// May add loadable background, from picture, which position is dependent on the
	// relative position of the map

}
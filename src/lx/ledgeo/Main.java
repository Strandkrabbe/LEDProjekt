package lx.ledgeo;

import java.io.IOException;
import henning.leddriverj.BoardController;
import henning.leddriverj.LEDController;
import lx.ledgeo.draw.RootContainer;
import lx.ledgeo.input.InputProvider;
import lx.ledgeo.input.KeyInputFrame;

public class Main {

	private static RootContainer root;

	public static void main(String[] args) throws IOException {
		InputProvider input = new KeyInputFrame();
		try	{
			root = new RootContainer(18, 10);
			root.setPosition(0, 0);
			BoardController board = root.getBoard();
			board.reset();
			LEDController ctl = board.getController();
			ctl.setAlpha(3);
			ctl.useBrightnessMod(true);
			Menu menu = new Menu(input);
			root.add(menu);
			Game game = new Game();
			root.add(game);
			boolean active = true;
			while (active) {
				String mapName = menu.levelAuswahl();
				if (mapName == null) {
					active = false;
				} else {
					game.start(mapName, Skin.getDefaultSkin(), input);
				}
			}
		}	finally	{
			root.close();
			input.close();
		}
	}

	public static void draw() {
		root.draw();
	}

}
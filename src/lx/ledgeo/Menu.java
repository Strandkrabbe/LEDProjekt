package lx.ledgeo;

import java.awt.event.KeyEvent;
import henning.leddriverj.util.Log;
import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.draw.Image;
import lx.ledgeo.input.InputProvider;

public class Menu extends BasicDrawable {
	private int auswahlFeld = 1;
	private Image menu;
	private InputProvider input;
	public static boolean star  = false;
	public static boolean stars =false;
	public Menu(InputProvider input) {
		this.input = input;
		this.menu = new Image();
		this.menu.load("Menu.png");
		this.menu.setVisible(true);
		this.menu.setPosition(0, 0);
		this.menu.setSize(18, 10);
		this.setPosition(0, 0);
		this.setSize(18, 10);
		this.setVisible(false);
	}
		

	@Override
	public boolean draw(DrawingArea a) {
		if (!super.draw(a)) {
			return false;
		}
		int[] Cursor = new int[] { 0, 255, 255 };
		a.draw(menu);
		a.setColor(Cursor);
		int x = 2;
		int y = 0;	
		while (auswahlFeld > 6) {
			auswahlFeld -= 6;
		}
		switch (auswahlFeld) {
		case 1:
			x = 2;
			y = 0;
			break;
		case 2:
			x = 6;
			y = 0;
			break;
		case 3:
			x = 10;
			y = 0;
			break;
		case 4:
			x = 2;
			y = 4;
			break;
		case 5:
			x = 6;
			y = 4;
			break;
		case 6:
			x = 10;
			y = 4;
			break;
		}
		if(!stars || !star) {
		a.drawRect(x, y, 5, 5);
		}
		return true;
	}
	
	public String levelAuswahl() {
		auswahlFeld = 1;
		String mapName = "Level 1";
		boolean ausgewahlt = false;
		this.setVisible(true);
		try {
			while (!ausgewahlt) {
				int nk = this.input.getLastKey();
				if (nk == KeyEvent.VK_ESCAPE) {
					Log.info("Game stopped: ESC", "Menu");
					return null;
				}
				if (nk == KeyEvent.VK_R) {
					star=!star;
					if(star) {
					this.menu.load("Menu_star.png");}
					else {
						this.menu.load("Menu.png");
						stars=!stars;
					}
				}
					
				if (nk == KeyEvent.VK_SPACE || nk == KeyEvent.VK_ENTER) {
					switch (auswahlFeld) {
					case 1:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level1")) {
								this.menu.load("Level1_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level1_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level1";
						}
						break;
					case 2:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level2")) {
								this.menu.load("Level2_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level2_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level2";
						}
						break;
					case 3:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level3")) {
								this.menu.load("Level3_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level3_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level3";
						}
						break;
					case 4:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level4")) {
								this.menu.load("Level4_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level4_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level4";
						}
						break;
					case 5:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level5")) {
								this.menu.load("Level5_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level5_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level5";
						}
						break;
					case 6:
						if(star) {
							if(ScoreManager.getInstance().getStarEarned("Level6")) {
								this.menu.load("Level6_Star_Got.png");
								stars=!stars;
							}else
							{
								this.menu.load("Level6_Star.png");
								stars=!stars;
							}
						}
						else {
						mapName = "Level6";
						}
						break;
					}
					if(!star) {
					Log.info("Game Start:Level " + mapName, "Game");
					return mapName;
					}
				}
				switch (nk) {
				case KeyEvent.VK_UP:
					auswahlFeld += 3;
					break;
				case KeyEvent.VK_DOWN:
					auswahlFeld -= 3;
					if (auswahlFeld < 1) {
						auswahlFeld = 6 + auswahlFeld;
					}
					break;
				case KeyEvent.VK_RIGHT:
					auswahlFeld++;
					break;
				case KeyEvent.VK_LEFT:
					auswahlFeld--;
					if (auswahlFeld == 0) {
						auswahlFeld = 6;
					}
					break;
				}
				Main.draw();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			return mapName;
		} finally {
			this.setVisible(false);
		}
	}
}
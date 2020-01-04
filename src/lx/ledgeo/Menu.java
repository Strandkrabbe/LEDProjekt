package lx.ledgeo;

import java.awt.event.KeyEvent;

import henning.leddriverj.util.Log;
import lx.ledgeo.draw.BasicDrawable;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.draw.Image;
import lx.ledgeo.input.InputProvider;

public class Menu extends BasicDrawable
{
	private int auswahlFeld = 1;
	private Image menu;
	private InputProvider input;
	public Menu()	{
		this.menu = new Image();
		this.menu.load("Menu.png");
		this.menu.setVisible(true);
		this.menu.setPosition(0, 0);
		this.menu.setSize(18,10);
	}
	@Override
	
	public boolean draw(DrawingArea a) 
	{
		if(!super.draw(a))
		{
			return false;
		}
		int[] Cursor = new int[] {0,255,255};
		a.draw(menu);
		a.setColor(Cursor);
		int x=2;
		int y=0;
		while(auswahlFeld>6) 
		{
			auswahlFeld-=6;
		}
		switch(auswahlFeld)
		{
			case 1:
				x=2;
				y=0;
				break;
			case 2:
				x=7;
				y=0;
				break;
			case 3:
				x=12;
				y=0;
				break;
			case 4:
				x=2;
				y=5;
				break;
			case 5:
				x=7;
				y=5;
				break;
			case 6:
				x=12;
				y=5;
		}
		a.drawRect(x, y, 5, 5);
		return true;
	}
	public String levelAuswahl()
	{
		auswahlFeld = 1;
		boolean ausgewahlt = false;
		while(!ausgewahlt)	
		{
				int nk = this.input.getLastKey();
				if (nk == KeyEvent.VK_SPACE || nk == KeyEvent.VK_ENTER)
				{
					String mapName = "";

					Log.info("Game Start:Level "+mapName, "Game");
					return mapName;
				}
				switch(nk) 
				{
					case KeyEvent.VK_UP:
						auswahlFeld += 3; 
						break;
					case KeyEvent.VK_DOWN:
						auswahlFeld -= 3;
						break;
					case KeyEvent.VK_RIGHT:
						auswahlFeld++;	
						break;
					case KeyEvent.VK_LEFT:
						auswahlFeld--;
						break;
				}	
		}
		return "Fehler";
	}
}
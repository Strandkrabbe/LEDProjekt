package lx.ledgeo;

public class Game 
{
	private Player player;
	private Map map;
	
	public boolean binIchTot()
	{
		for(int i = 0;i<player.getScale();i++)
		{
			for(int x = 0;x<player.getScale();x++)
			{
				if(map.getLevel(player.getPosiX()+i, player.getPosiY()+x) > 0)
				{
				return true;
				}
			}
		}
		return false;
	}
	public boolean unnormalerSprung()
	{
		if(map.getLevel(player.getPosiX(), player.getPosiY()-1) == -1)
		{
			return true;
		}
		return false;
	}
	public void Sprung()
	{
		
	}
	public void formAendern()
	{
		if(map.getLevel(player.getPosiX(), player.getPosiY()-1) == -2)
		{
			player.setScale(1);
		}
		else if(map.getLevel(player.getPosiX(), player.getPosiY()-1) == -3)
		{
			player.setScale(2);
		}
		else if(map.getLevel(player.getPosiX(), player.getPosiY()-1) == -4)
		{
			player.setScale(3);
		}
	}
}

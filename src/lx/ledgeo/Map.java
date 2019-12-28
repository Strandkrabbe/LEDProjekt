package lx.ledgeo;

import java.io.File;

public class Map 
{
// 0 Nichts  0 0 0
// 1 Spieler 0 0 255
// 2 Spike 	 255 0 0
// 3 Block	 0 255 0 
// -1 Jump	 255 165 0
// -2 Scale 1 0 0 20
// -3 Scale 2 0 0 40
// -4 Scale 3 0 0 60
private int Aktuell = 0; 
private int level[][];
public void loadMap(File f)
{
	
}
public int getLevel(int x, int b) {
	return level[x][b];
}



}

package lx.ledgeo;

import java.io.File;

public class Map 
{
// 0 Nichts 
// 1 Spieler
// 2 Spike
// 3 Block
// -1 Jump
// -2 Scale 1
// -3 Scale 2
// -4 Scale 3
private int Aktuell = 0; 
private int level[][];
public void loadMap(File f)
{
	
}
public int getLevel(int x, int b) {
	return level[x][b];
}



}

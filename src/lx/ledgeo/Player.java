package lx.ledgeo;

public class Player 
{
private static final int[] blu = {0,0,255};

private int posiX = 4;
private int posiY = 1;

private int scale = 2;

private int njump =2;

public int getPosiX() {
	return posiX;
}

public void setPosiX(int posiX) {
	this.posiX = posiX;
}

public int getPosiY() {
	return posiY;
}

public void setPosiY(int posiY) {
	this.posiY = posiY;
}

public int getScale() {
	return scale;
}

public void setScale(int scale) {
	this.scale = scale;
}

public static int[] getBlu() {
	return blu;
	}

}

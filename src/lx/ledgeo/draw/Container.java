package lx.ledgeo.draw;

import java.util.LinkedList;
import java.util.List;

public class Container extends BasicDrawable {
	
	private List<Drawable> elements;
	
	public Container()	{
		this.elements = new LinkedList<>();
	}
	
	public synchronized void add(Drawable d)	{
		if (!this.elements.contains(d))
			this.elements.add(d);
	}
	public synchronized void remove(Drawable d)	{
		this.elements.remove(d);
	}
	public List<Drawable> getAll()	{
		return this.elements;
	}
	public synchronized void removeAll()	{
		this.elements.clear();
	}
	
	@Override
	public synchronized boolean draw(DrawingArea a) {
		if (!super.draw(a))
			return false;
		int[][][] buffer = new int[this.getHeight()][this.getWidth()][3];
		for (Drawable d : elements)	{
			DrawingArea ddraw = new DrawingArea(d.getWidth(), d.getHeight());
			boolean vis = d.draw(ddraw);
			if (vis)	{
				int DX = d.getX();
				int DY = d.getY();
				int xe = DX + d.getWidth();
				int ye = DY + d.getHeight();
				int[][][] ddrawBuffer = ddraw.getArea();
				for (int Y = Math.max(DY,0);Y < ye && Y < buffer.length;Y++)	{
					for (int X = Math.max(DX, 0);X < xe && X < buffer[Y].length;X++)	{
						int DDBX = X - DX;	// Positions in ddrawbuffer
						int DDBY = Y - DY;
						if (ddrawBuffer == null || ddrawBuffer[DDBY][DDBX].length == 0)
							ddrawBuffer[DDBY][DDBX] = new int[3];	// TODO should be not needed
						switch (d.getColorMode()) {
						case ADD:
							for (int C = 0;C < 3;C++)	{
								buffer[Y][X][C] += ddrawBuffer[DDBY][DDBX][C];
							}
							break;
						case MAX:
							for (int C = 0;C < 3;C++)	{
								buffer[Y][X][C] = Math.max(ddrawBuffer[DDBY][DDBX][C],buffer[Y][X][C]);
							}
							break;
						case REPLACE:
							buffer[Y][X] = ddrawBuffer[DDBY][DDBX];
						case REPLACE_NONEZERO:
							if (ddrawBuffer[DDBY][DDBX][0] != 0 || ddrawBuffer[DDBY][DDBX][1] != 0 || ddrawBuffer[DDBY][DDBX][2] != 0)	{
								buffer[Y][X] = ddrawBuffer[DDBY][DDBX];
							}
						default:
							break;
						}
					}
				}
			}
		}
		a.draw(buffer, 0, 0);
		return true;
	}
	
}

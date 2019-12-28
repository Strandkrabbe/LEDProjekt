package lx.ledgeo.draw;

import java.util.List;

public class Container extends BasicDrawable {
	
	private List<Drawable> elements;
	
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
				int xe = d.getX() + d.getWidth();
				int ye = d.getY() + d.getHeight();
				int[][][] ddrawBuffer = ddraw.getArea();
				for (int Y = Math.max(d.getY(),0);Y < ye && Y < buffer.length;Y++)	{
					for (int X = Math.max(d.getX(), 0);X < xe && X < buffer[Y].length;X++)	{
						if (ddrawBuffer[Y][X].length == 0)
							ddrawBuffer[Y][X] = new int[3];	// TODO should be not needed
						switch (d.getColorMode()) {
						case ADD:
							for (int C = 0;C < 3;C++)	{
								buffer[Y][X][C] += ddrawBuffer[Y][X][C];
							}
							break;
						case MAX:
							for (int C = 0;C < 3;C++)	{
								buffer[Y][X][C] = Math.max(ddrawBuffer[Y][X][C],buffer[Y][X][C]);
							}
							break;
						case REPLACE:
							buffer[Y][X] = ddrawBuffer[Y][X];
						case REPLACE_NONEZERO:
							if (ddrawBuffer[Y][X][0] != 0 || ddrawBuffer[Y][X][1] != 0 || ddrawBuffer[Y][X][2] != 0)	{
								buffer[Y][X] = ddrawBuffer[Y][X];
							}
						default:
							break;
						}
					}
				}
			}
		}
		return true;
	}
	
}

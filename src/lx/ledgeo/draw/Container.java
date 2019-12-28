package lx.ledgeo.draw;

import java.util.List;

public class Container extends BasicDrawable {
	
	private List<Drawable> elements;
	
	public void add(Drawable d)	{
		if (!this.elements.contains(d))
			this.elements.add(d);
	}
	public void remove(Drawable d)	{
		this.elements.remove(d);
	}
	public List<Drawable> getAll()	{
		return this.elements;
	}
	public void removeAll()	{
		this.elements.clear();
	}
	
	@Override
	public boolean draw(DrawingArea a) {
		super.draw(a);
		
		return true;
	}
	
}

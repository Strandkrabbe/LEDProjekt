package lx.ledgeo.input;

import java.io.Closeable;

public interface InputProvider extends Closeable {
	
	public boolean hasKey();
	public int getLastKey();
	
}

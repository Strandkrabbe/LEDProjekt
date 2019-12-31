package lx.ledgeo;

import java.util.ArrayList;
import java.util.List;

public class Skin {
	
	public static final int[] COLOR_BLUE = {0,0,255};
	public static final int[] COLOR_LIGHTBLUE = {80,120,255};
	public static final int[] COLOR_GREEN = {80,255,160};
	public static final int[] COLOR_ORANGE = {255,210,0};
	
	private final List<int[][][]> skins;
	private long cycleDuration = 2000L;
	
	public Skin(List<int[][][]> skin,long cd)	{
		this.skins = skin;
		this.cycleDuration = cd;
	}
	
	public int[][][] getStaticSkin()	{
		return this.skins.get(0);
	}
	public int[][][] getAnimatedSkin()	{
		long currentTime = System.currentTimeMillis();
		currentTime %= cycleDuration;
		currentTime /= cycleDuration/((long)this.skins.size());
		if (currentTime >= this.skins.size())
			currentTime = this.skins.size() - 1;
		return this.skins.get((int)currentTime);
	}
	
	public static Skin getDefaultSkin()	{
		int[][][] defskin0 = new int[][][] {
			{COLOR_LIGHTBLUE,COLOR_ORANGE,COLOR_LIGHTBLUE},
			{COLOR_ORANGE,COLOR_LIGHTBLUE,COLOR_ORANGE},
			{COLOR_LIGHTBLUE,COLOR_ORANGE,COLOR_LIGHTBLUE}
		};
		int[][][] defskin1 = new int[][][] {
			{COLOR_ORANGE,COLOR_LIGHTBLUE,COLOR_ORANGE},
			{COLOR_LIGHTBLUE,COLOR_ORANGE,COLOR_LIGHTBLUE},
			{COLOR_ORANGE,COLOR_LIGHTBLUE,COLOR_ORANGE}
		};
		List<int[][][]> l = new ArrayList<>();
		l.add(defskin0);
		l.add(defskin1);
		return new Skin(l, 2000L);
	}
	
}

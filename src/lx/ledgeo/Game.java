package lx.ledgeo;

import lx.ledgeo.draw.Container;

public class Game extends Container {
	
	public static final int GAME_WIDTH = 18;
	public static final int GAME_HEIGHT = 10;
	public static final double ACCELERATION_GRAVITY = 0.2;
	public static final double VELOCITY_JUMP = 1.0;
	public static final double VELOCITY_JUMP_LONG = 1.2;
	
	private Player player;
	private Map map;
	private double exactPlayerX = 0.0;	// scale: [pixel]		// player draw pos ceil/floor?
	private double exactPlayerY = 0.0;
	private double velocityX = 0.5;		// scale: [pixel/tick]	// Has to be below (or equal to) 1!!
	private double velocityY = 0.0;
	private double gravity = ACCELERATION_GRAVITY;
	private boolean running = false;
	
	public Game()	{
		this.map = new Map();
		this.player = new Player();
		this.setPosition(0, 0);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.add(map);
		this.add(player);
	}
	
	public void loadMap(String s)	{
		if (!running)	{
			this.map.loadMap(s);
			// Try to find a matching background here
		}
	}
	
	private int xToInt(double x)	{
		return (int) x;
	}
	private int yToInt(double y)	{
		return (int) y;
	}

	boolean onGround(double x,double y) {
		if (map.getLevel(xToInt(x),yToInt(x) - 1) == 3) {
			return true;
		}
		return false;
	}

	boolean isDead(double X,double Y) {
		int PX = xToInt(X);
		int PY = yToInt(Y);
		for (int i = 0; i < player.getScale(); i++) {
			for (int x = 0; x < player.getScale(); x++) {
				if (map.getLevel(PX + x, PY + i) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	void bigJump() {
		if (map.getLevel(player.getPosiX(), player.getPosiY() - 1) == -1) {
			if (gravity < 0)	{
				this.velocityY = VELOCITY_JUMP_LONG;
			}	else	{
				this.velocityY = VELOCITY_JUMP_LONG * -1.0;
			}
		}
	}

	public void jump() {	// Called by extrenal method (f.e. key listener)
		if (this.onGround(exactPlayerX, exactPlayerY))	{
			if (gravity < 0)	{
				this.velocityY = VELOCITY_JUMP;
			}	else	{
				this.velocityY = VELOCITY_JUMP * -1.0;
			}
		}
	}
	
	/**
	 * @return true if successful, false if dead
	 */
	public boolean move()	{
		this.changeSize();
		this.bigJump();
		double tmpVelY = velocityY;
		boolean negative = tmpVelY < 0;
		tmpVelY = Math.abs(tmpVelY);
		while (tmpVelY > 1.0)	{
			if (isDead(exactPlayerX,exactPlayerY))	{
				return false;
			}
			double comp = Math.min(1.0, tmpVelY);
			if (onGround(exactPlayerX,exactPlayerY) && ((negative && gravity < 0) || !negative && gravity > 0))	{
				tmpVelY = 0.0;
				velocityY = 0.0;
				exactPlayerY = ((int) exactPlayerY);	// Ensure player is not floating f.e. 0.5 above ground
			}
			if (negative)
				exactPlayerY -= comp;
			else
				exactPlayerY += comp;
			tmpVelY -= comp;
		}
		exactPlayerX += velocityX;
		
		return true;
	}

	void changeSize() {
		if (map.getLevel(player.getPosiX(), player.getPosiY() - 1) == -2) {
			player.setScale(1);
		} else if (map.getLevel(player.getPosiX(), player.getPosiY() - 1) == -3) {
			player.setScale(2);
		} else if (map.getLevel(player.getPosiX(), player.getPosiY() - 1) == -4) {
			player.setScale(3);
		}
	}
	
	public void start()	{
		
	}
	public void run()	{	// Run method (main method of game)
		
	}

}

package lx.ledgeo;

import lx.ledgeo.draw.Container;
import lx.ledgeo.draw.DrawingArea;

public class Game extends Container {
	
	public static final int GAME_WIDTH = 18;
	public static final int GAME_HEIGHT = 10;
	public static final double ACCELERATION_GRAVITY = -0.2;
	public static final double VELOCITY_JUMP = 1.0;
	public static final double VELOCITY_JUMP_LONG = 1.2;
	public static final double VELOCITY_LIMIT = 2.0;
	
	private Player player;
	private Map map;
	private double exactPlayerX = 0.0;	// scale: [pixel]		// player draw pos ceil/floor?
	private double exactPlayerY = 0.0;
	private double velocityX = 0.5;		// scale: [pixel/tick]	// Has to be below (or equal to) 1!!
	private double velocityY = 0.0;
	private double applyVelocityY = Double.NaN;
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
	
	// Converts the real player position to the position on the map used to identify a block type
	private int xToInt(double x)	{
		return (int) x;
	}
	private int yToInt(double y)	{
		return (int) y;
	}

	boolean onGround(double x,double y) {
		if (map.getLevel(xToInt(x),yToInt(x) + ((int)Math.signum(this.gravity))) == 3) {
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
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		if (map.getLevel(X, Y - 1) == -1) {
			if (gravity < 0)	{
				this.newVelocity(VELOCITY_JUMP_LONG);
			}	else	{
				this.newVelocity(VELOCITY_JUMP_LONG * -1.0);
			}
		}
	}

	public void jump() {	// Called by extrenal method (f.e. key listener)
		if (this.onGround(exactPlayerX, exactPlayerY))	{
			if (gravity < 0)	{
				this.newVelocity(VELOCITY_JUMP);
			}	else	{
				this.newVelocity(VELOCITY_JUMP * -1.0);
			}
		}
	}
	
	private void applyVelocityChanges()	{
		if (!Double.isNaN(applyVelocityY))	{
			this.velocityY = applyVelocityY;
		}
	}
	private void newVelocity(double d)	{	// This might be less realistic then adding velocities but more predictable for us and the player
		if (Double.isNaN(applyVelocityY))	{
			this.applyVelocityY = d;
		}	else	{
			if (Math.abs(d) > Math.abs(this.applyVelocityY))	{
				this.applyVelocityY = d;
			}
		}
	}
	
	/**
	 * @return true if successful, false if dead
	 */
	public boolean move()	{
		if (!onGround(exactPlayerX, exactPlayerY))	{
			this.velocityY += this.gravity;
		}
		if (this.velocityY > VELOCITY_LIMIT)	{
			this.velocityY = VELOCITY_LIMIT;
		}
		if (this.velocityY < VELOCITY_LIMIT * -1.0)	{
			this.velocityY = VELOCITY_LIMIT * -1.0;
		}
		if (this.velocityX > VELOCITY_LIMIT)	{	// -- Not needed yet but eventually later
			this.velocityX = VELOCITY_LIMIT;
		}
		if (this.velocityX < VELOCITY_LIMIT * -1.0)	{
			this.velocityX = VELOCITY_LIMIT * -1.0;	// --
		}
		double tmpVelY = velocityY;
		boolean negative = tmpVelY < 0;
		tmpVelY = Math.abs(tmpVelY);
		while (tmpVelY > 0.9)	{	// Prevent glitching by skipping more then 1.0 blocks -> calculate every block move
									// 0.9 to prevent rounding issues
									// !!! A block might be computed twice !!!
			this.bigJump();
			this.changeSize();
			if (isDead(exactPlayerX,exactPlayerY))	{
				return false;
			}
			double comp = Math.min(0.9, tmpVelY);
			if (onGround(exactPlayerX,exactPlayerY) && ((negative && gravity < 0) || !negative && gravity > 0))	{
				tmpVelY = 0.0;
				this.velocityY = 0.0;
				exactPlayerY = ((int) exactPlayerY);	// Ensure player is not floating f.e. 0.5 above ground
			}
			if (negative)
				exactPlayerY -= comp;
			else
				exactPlayerY += comp;
			tmpVelY -= comp;
		}
		exactPlayerX += velocityX;	// If it causes problems we might want to calculate new x in more steps by dividing it up for every step in
									// y calculation
		this.applyVelocityChanges();
		return true;
	}

	void changeSize() {
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		if (map.getLevel(X, Y - 1) == -2) {
			player.setScale(1);
		} else if (map.getLevel(X, Y - 1) == -3) {
			player.setScale(2);
		} else if (map.getLevel(X, Y - 1) == -4) {
			player.setScale(3);
		}
	}
	
	@Override
	public synchronized boolean draw(DrawingArea a) {
		this.map.setCurrentXPos(xToInt(exactPlayerX + 5));
		this.map.setCurrentYPos(Math.max(yToInt(exactPlayerY) - 5,0));
		this.player.setPosition(xToInt(exactPlayerX) - this.map.getCurrentXPos(), yToInt(exactPlayerY) - map.getCurrentYPos());
		return super.draw(a);
	}
	
	public void start()	{
		
	}
	public void run()	{	// Run method (main method of game)
		
	}

}

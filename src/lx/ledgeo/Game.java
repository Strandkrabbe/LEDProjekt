package lx.ledgeo;

import java.awt.event.KeyEvent;

import henning.leddriverj.util.Log;
import lx.ledgeo.draw.Container;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.input.InputProvider;
import lx.ledgeo.util.ArrayUtils;

public class Game extends Container {
	
	public static final int GAME_WIDTH = 18;
	public static final int GAME_HEIGHT = 10;
	public static final double ACCELERATION_GRAVITY = -0.35;
	public static final double VELOCITY_JUMP = 1.6;
	public static final double VELOCITY_JUMP_LONG = 1.85;
	public static final double VELOCITY_LIMIT_ABS = 2.5;
	public static final long TICK_DURATION = 150;
	
	public static final byte RETURN_RUN = 0;
	public static final byte RETURN_DEAD = 1;
	public static final byte RETURN_FINISH = 2;
	
	private Player player;
	private Map map;
	private double exactPlayerX = 0.0;	// scale: [pixel]		// player draw pos ceil/floor?
	private double exactPlayerY = 0.0;
	private double velocityX = 0.5;		// scale: [pixel/tick]	// Has to be below (or equal to) 1!!
	private double velocityY = 0.0;
	private double applyVelocityY = Double.NaN;
	private double gravity = ACCELERATION_GRAVITY;
	private InputProvider input;
	private boolean running = false;
	
	public Game()	{
		this.map = new Map();
		this.player = new Player();
		this.setPosition(0, 0);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.add(map);
		this.add(player);
		this.setVisible(false);
		this.reset();
	}
	private void reset()	{
		this.exactPlayerX = 0.0;
		this.exactPlayerY = 1.0;
		this.velocityX = 0.5;
		this.velocityY = 0.0;
		this.applyVelocityY = Double.NaN;
		this.running = false;
		this.gravity = ACCELERATION_GRAVITY;
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
	
	@SuppressWarnings("unused")
	private int xToInt(double x,boolean up)	{
		if (up)	{
			return (int) Math.ceil(x);
		}	else	{
			return (int) x;
		}
	}
	
	@SuppressWarnings("unused")
	private int yToInt(double y,boolean up)	{
		if (up)	{
			return (int) Math.ceil(y);
		}	else	{
			return (int) y;
		}
	}

	boolean onGround(double x,double y) {
		if (y < 1.0)
			return true;
		for (int XA = 0;XA < this.player.getScale();XA++)	{
			int type = map.getLevel(xToInt(x,false) + XA,yToInt(y) + ((gravity < 0) ? -1 : this.player.getScale()));
			if (type != 0 && type != 2) {
				return true;
			}
		}
//		return map.getLevel(xToInt(x,true) + this.player.getScale() - 1,yToInt(x) + gravity < 0 ? -1 : this.player.getScale()) == 3;
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
	boolean isFinished(double x,double y)	{
		int PX = xToInt(exactPlayerX) + this.player.getScale() - 1;
		int PY = yToInt(exactPlayerY);
		return PX >= this.map.getFinishX() || this.map.getLevel(PX, PY) == Integer.MIN_VALUE;
	}

	void bigJump() {
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		if (map.getLevel(X, Y - 1) == -1 || map.getLevel(X, Y) == -1) {
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
			this.applyVelocityY = Double.NaN;
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
	public byte move()	{
		if (!onGround(exactPlayerX, exactPlayerY))	{
			this.velocityY += this.gravity;
		}
		if (this.velocityY > VELOCITY_LIMIT_ABS)	{
			this.velocityY = VELOCITY_LIMIT_ABS;
		}
		if (this.velocityY < VELOCITY_LIMIT_ABS * -1.0)	{
			this.velocityY = VELOCITY_LIMIT_ABS * -1.0;
		}
		if (this.velocityX > VELOCITY_LIMIT_ABS)	{	// -- Not needed yet but eventually later
			this.velocityX = VELOCITY_LIMIT_ABS;
		}
		if (this.velocityX < VELOCITY_LIMIT_ABS * -1.0)	{
			this.velocityX = VELOCITY_LIMIT_ABS * -1.0;	// --
		}
		double tmpVelY = velocityY;
		boolean negative = tmpVelY < 0;
		tmpVelY = Math.abs(tmpVelY);
		do	{	// Prevent glitching by skipping more then 1.0 blocks -> calculate every block move
									// 0.9 to prevent rounding issues
									// !!! A block might be computed twice !!!
			this.bigJump();
			this.changeSize();
			if (isDead(exactPlayerX,exactPlayerY))	{
				return RETURN_DEAD;
			}
			if (isFinished(exactPlayerX, exactPlayerY))	{
				return RETURN_FINISH;
			}
			double comp = Math.min(0.9, tmpVelY);
			if (onGround(exactPlayerX,exactPlayerY) && ((negative && gravity < 0) || !negative && gravity > 0))	{
				tmpVelY = 0.0;
				comp = 0.0;
				this.velocityY = 0.0;
				exactPlayerY = ((int) exactPlayerY);	// Ensure player is not floating f.e. 0.5 above ground
			}
			if (negative)
				exactPlayerY -= comp;
			else
				exactPlayerY += comp;
			tmpVelY -= comp;
		} while (tmpVelY > 0.9);
		exactPlayerX += velocityX;	// If it causes problems we might want to calculate new x in more steps by dividing it up for every step in
									// y calculation
		this.applyVelocityChanges();
		return RETURN_RUN;
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
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		this.map.setCurrentXPos(X - 4);
		this.map.setCurrentYPos(Math.max(Y - 5,0));
		this.player.setPosition(X - this.map.getCurrentXPos(),Y - map.getCurrentYPos());
		boolean vis = super.draw(a);
		if (!vis)
			return false;
		int[][][] buffer = ArrayUtils.copy3(a.getArea());
		a.drawInverse(buffer);
		return true;
	}
	
	public void start(String mapName,Skin skin,InputProvider in)	{
		Log.info("Starting game...", "Game");
		this.input = in;
		this.map.loadMap(mapName);
		this.player.setSkin(skin);
		this.reset();
		this.setVisible(true);
		if (!this.map.isMapLoaded())	{
			try {
				Thread.sleep(7500);
			} catch (InterruptedException e) {}
		}	else	{
			run();
		}
	}
	private void run()	{	// Run method (main method of game)
		Log.debug("Executing loop...", "Game");
		this.running = true;
		this.player.setVisible(true);
		while (running)	{
			while (this.input.hasKey())	{
				int nk = this.input.getLastKey();
				if (nk == KeyEvent.VK_UP || nk == KeyEvent.VK_SPACE || nk == KeyEvent.VK_W)	{
					this.jump();
				}
				if (nk == KeyEvent.VK_ESCAPE)	{
					this.running = false;
					Log.info("Game stopped: ESC", "Game");
				}
			}
			byte re = this.move();
			if (re == RETURN_DEAD)	{
				this.running = false;
				this.player.setVisible(false);
				Log.info("Game neded: DEAD", "Game");
				// TODO Enable endscreen here
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
			if (re == RETURN_FINISH)	{
				this.running = false;
				this.player.setVisible(false);
				Log.info("Game ended: FINISH", "Game");
				// TODO enable endscreen 2 here
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
			Main.draw();
			try {
				Thread.sleep(TICK_DURATION);
			} catch (InterruptedException e) {
				Log.error("Game interrupted");
				this.running = false;
				return;
			}
		}
		this.setVisible(false);
		Log.debug("Exiting loop", "Game");
	}
	public void stop()	{
		Log.info("Game ended: PROG_STOP", "Game");
		this.running = false;
	}

}

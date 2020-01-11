package lx.ledgeo;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import henning.leddriverj.util.Log;
import lx.ledgeo.draw.Container;
import lx.ledgeo.draw.DrawingArea;
import lx.ledgeo.draw.Image;
import lx.ledgeo.input.InputProvider;
import lx.ledgeo.util.ArrayUtils;
import lx.ledgeo.util.ColorUtils;

import static lx.ledgeo.ActionTypes.*;

@SuppressWarnings("unused")
public class Game extends Container {
	// integrate (-bx + a) from 0 to 32 = 0.25 and integrate (-bx + a) from 0 to 16 = 4.1
	// integrate (-0.0341797x + a) from 0 to 16 = 5.5 

	public static final int GAME_WIDTH = 18;
	public static final int GAME_HEIGHT = 10;
	public static final double ACCELERATION_GRAVITY = -0.0316547;
	public static final double VELOCITY_JUMP = 0.504688;
	public static final double VELOCITY_JUMP_LONG = 0.592188;
	public static final double VELOCITY_LIMIT_ABS = 1.2287;
	public static final long TICK_DURATION = 60;
	public static final double NON_LINEAR_VELOCITY_EXP = 1.0;
	public static final int FRAMES_PER_TICK = 1;
	
	public static final byte RETURN_RUN = 0;
	public static final byte RETURN_DEAD = 1;
	public static final byte RETURN_FINISH = 2;
	
	private static final int[] PROGRESSBAR_BACK = {70,70,70};
	private static final int[] PROGRESSBAR_CURRENT = {0,255,0};
	private static final int[] PROGRESSBAR_HIGHSCORE = {0,255,255};

	private Player player;
	private Map map;
	private Background background;
	private GameConfig gc;
	private double exactPlayerX; // scale: [pixel] // player draw pos ceil/floor? // Set at reset
	private double exactPlayerY;
	private double velocityX; // scale: [pixel/tick] // Has to be below (or equal to) 1!!
	private double velocityY;
	private double applyVelocityY = Double.NaN;
	private double gravity = ACCELERATION_GRAVITY;
	private InputProvider input;
	private boolean running = false;
	private Image endscreenLose;
	private Image endscreenWin;

	public Game() {
		this.endscreenLose = new Image();
		this.endscreenWin = new Image();
		this.endscreenLose.load("lose.png");
		this.endscreenWin.load("Win.png");
		this.endscreenLose.setPosition(0, 0);
		this.endscreenLose.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.endscreenWin.setPosition(0, 0);
		this.endscreenWin.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.endscreenLose.setVisible(false);
		this.endscreenWin.setVisible(false);
		this.map = new Map();
		this.player = new Player();
		this.setPosition(0, 0);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.add(map);
		this.add(player);
		this.setVisible(false);
		this.reset();
	}

	private void reset() {
		this.exactPlayerX = 0.0;
		this.exactPlayerY = 1.0;
		this.velocityX = 0.25;
		this.velocityY = 0.0;
		this.player.setScale(2);
		this.applyVelocityY = Double.NaN;
		this.running = false;
		this.gravity = ACCELERATION_GRAVITY;
		this.endscreenLose.setVisible(false);
		this.endscreenWin.setVisible(false);
	}

	public void loadMap(String s) {
		if (!running) {
			this.map.loadMap(s);
			this.gc = new GameConfig();
			try {
				this.gc.load(s);
			} catch (IOException e) {
				Log.error("Failed to load config for " + s, "Game");
				Log.error(e);
			}
			if (background != null)
				this.remove(this.background);
//			int[] bgcolor = ColorUtils.invert(this.player.getSkin().getMainColor());
//			ColorUtils.multiply(bgcolor, 0.16f);
			int[] bgcolor = new int[] {0,0,50};
			this.background = Background.getByName(bgcolor);
			this.add(this.background, 0);
		}
	}

	// Converts the real player position to the position on the map used to identify
	// a block type
	private int xToInt(double x) {
		return gravity < 0 ? (int) x : ((int) Math.ceil(x));
	}

	private int yToInt(double y) {
		return gravity < 0 ? (int) y : ((int) Math.ceil(y));
	}

	private int xToInt(double x, boolean up) {
		if (up) {
			return (int) Math.ceil(x);
		} else {
			return (int) x;
		}
	}

	private int yToInt(double y, boolean up) {
		if (up) {
			return (int) Math.ceil(y);
		} else {
			return (int) y;
		}
	}

	boolean onGround(double x, double y) {
		if (y < 1.0)
			return true;
		for (int XA = 0; XA < this.player.getScale(); XA++) {
			int type = map.getLevel(xToInt(x, false) + XA, yToInt(y) + ((gravity < 0) ? -1 : this.player.getScale()));
			if (type != 0 && type != 2 && type != -6 && type != -5) {
				return true;
			}
		}
//		return map.getLevel(xToInt(x,true) + this.player.getScale() - 1,yToInt(x) + gravity < 0 ? -1 : this.player.getScale()) == 3;
		return false;
	}
	boolean oneAboveGround(double x,double y)	{
		for (int XA = 0;XA < this.player.getScale();XA++)	{	// For better playability
			int type = map.getLevel(xToInt(x,false) + XA,yToInt(y) + ((gravity < 0) ? -2 : this.player.getScale() + 1));
			if (type != 0 && type != 2 && type != -6 && type != -5) {
				return true;
			}
		}
		return false;
	}

	boolean isDead(double X, double Y) {
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

	boolean isFinished(double x, double y) {
		int PX = xToInt(exactPlayerX) + this.player.getScale() - 1;
		return PX >= this.map.getFinishX() || this.isToutchingBlock(Integer.MIN_VALUE);
	}

	void bigJump() {
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		if (map.getLevel(X, Y - 1) == -1 || map.getLevel(X, Y) == -1) {
			if (gravity < 0) {
				this.newVelocity(VELOCITY_JUMP_LONG);
			} else {
				this.newVelocity(VELOCITY_JUMP_LONG * -1.0);
			}
		}
	}

	public void jump() { // Called by extrenal method (f.e. key listener)
		if (this.onGround(exactPlayerX, exactPlayerY)) {
			if (gravity < 0) {
				this.newVelocity(VELOCITY_JUMP);
			} else {
				this.newVelocity(VELOCITY_JUMP * -1.0);
			}
		}
	}

	private void applyVelocityChanges() {
		if (!Double.isNaN(applyVelocityY)) {
			this.velocityY = applyVelocityY;
			this.applyVelocityY = Double.NaN;
		}
	}

	private void newVelocity(double d) { // This might be less realistic then adding velocities but more predictable for
											// us and the player
		if (Double.isNaN(applyVelocityY)) {
			this.applyVelocityY = d;
		} else {
			if (Math.abs(d) > Math.abs(this.applyVelocityY)) {
				this.applyVelocityY = d;
			}
		}
	}

	/**
	 * @return true if successful, false if dead
	 */
	public byte move() {
		if (!onGround(exactPlayerX, exactPlayerY)) {
			this.velocityY += this.gravity;
		}
		if (this.velocityY > VELOCITY_LIMIT_ABS) {
			this.velocityY = VELOCITY_LIMIT_ABS;
		}
		if (this.velocityY < VELOCITY_LIMIT_ABS * -1.0) {
			this.velocityY = VELOCITY_LIMIT_ABS * -1.0;
		}
		if (this.velocityX > VELOCITY_LIMIT_ABS) { // -- Not needed yet but eventually later
			this.velocityX = VELOCITY_LIMIT_ABS;
		}
		if (this.velocityX < VELOCITY_LIMIT_ABS * -1.0) {
			this.velocityX = VELOCITY_LIMIT_ABS * -1.0; // --
		}
		double tmpVelY = velocityY;
		boolean negative = tmpVelY < 0;
		tmpVelY = Math.abs(tmpVelY);
		tmpVelY = Math.pow(tmpVelY, NON_LINEAR_VELOCITY_EXP); // Square for better curve
		do { // Prevent glitching by skipping more then 1.0 blocks -> calculate every block
				// move
				// 0.9 to prevent rounding issues
				// !!! A block might be computed twice !!!
			this.bigJump();
			this.changeGravity();
			this.changeSize();
			this.collectStar();
			if (isDead(exactPlayerX, exactPlayerY)) {
				return RETURN_DEAD;
			}
			if (isFinished(exactPlayerX, exactPlayerY)) {
				return RETURN_FINISH;
			}
			double comp = Math.min(0.9, tmpVelY);
			if (onGround(exactPlayerX, exactPlayerY) && ((negative && gravity < 0) || !negative && gravity > 0)) {
				tmpVelY = 0.0;
				comp = 0.0;
				this.velocityY = 0.0;
				exactPlayerY = ((int) exactPlayerY);	// Ensure player is not floating f.e. 0.5 above ground
			}	else if (oneAboveGround(exactPlayerX, exactPlayerY) && ((negative && gravity < 0) || !negative && gravity > 0))	{
				tmpVelY = 0.0;
				comp = 0.0;
				this.velocityY = 0.0;
				exactPlayerY = ((int) exactPlayerY) + Math.signum(this.gravity);	// Ensure player is not floating
			}
			if (negative)
				exactPlayerY -= comp;
			else
				exactPlayerY += comp;
			tmpVelY -= comp;
		} while (tmpVelY > 0.9);
		exactPlayerX += velocityX; // If it causes problems we might want to calculate new x in more steps by
									// dividing it up for every step in
									// y calculation
		this.applyVelocityChanges();
		if (this.gc != null)
			this.gc.apply(xToInt(exactPlayerX));
		return RETURN_RUN;
	}

	void changeSize() {
		if (this.isToutchingBlock(-2)) {
			player.setScale(1);
		} else if (this.isToutchingBlock(-3)) {
			player.setScale(2);
		} else if (this.isToutchingBlock(-4)) {
			player.setScale(3);
		}
	}
	
	void changeGravity()	{
		if (this.isToutchingBlock(-7))	{
			this.gravity = ACCELERATION_GRAVITY * -1;
		}
		if (this.isToutchingBlock(-8))	{
			this.gravity = ACCELERATION_GRAVITY;
		}
	}
	
	private void collectStar()	{
		if (this.isInBlock(-5))	{
			Log.info("Collected star for " + this.map.getName(),"Game");
			ScoreManager.getInstance().setStarEarned(this.map.getName(), true);
			int x = this.xToInt(exactPlayerX);
			int y = this.yToInt(exactPlayerY);
			for (int YA = 0;YA < this.player.getScale();YA++)	{
				for (int XA = 0;XA < this.player.getScale();XA++)	{
					if (this.map.getLevel(x + XA, y + YA) == -5)	{
						this.map.setStarDone(x + XA, y + YA);
					}
				}
			}
		}
	}
	
	private boolean isOnBlock(int type)	{
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		int offsetY = (this.gravity < 0 ? -1 : this.player.getScale());
//		for (int XA = 0;XA < this.player.getScale();XA++)	{
//			if (this.map.getLevel(X + XA, Y + offsetY) == type)
//				return true;
//		}
//		return false;
		return this.map.getLevel(X, Y + offsetY) == type;
	}
	private boolean isInBlock(int type)	{
		int X = xToInt(exactPlayerX);
		int Y = yToInt(exactPlayerY);
		for (int XA = 0;XA < this.player.getScale();XA++)	{
			for (int YA = 0;YA < this.player.getScale();YA++)	{
				if (this.map.getLevel(X + XA, Y + YA) == type)
					return true;
			}
		}
		return false;
	}
	private boolean isToutchingBlock(int type)	{
		return this.isOnBlock(type) || this.isInBlock(type);
	}

	@Override
	public synchronized boolean draw(DrawingArea a) {
		if (this.isVisible())	{
			int X = xToInt(exactPlayerX);
			int Y = yToInt(exactPlayerY);
			this.map.setCurrentXPos(X - 5);
			this.map.setCurrentYPos(Math.min(Math.max(Y - 5, 0),this.map.getLevelHeight() - 10));
			this.player.setPosition(X - this.map.getCurrentXPos(), Y - map.getCurrentYPos());
			if (background != null)
				this.background.setMapPosition(((double) X) / (this.map.getFinishX() + 1));
		}
		boolean vis = super.draw(a);
		if (!vis)
			return false;
		a.setColor(PROGRESSBAR_BACK);
		a.drawLine(0, 0, 0, 9);
		double highscore = ScoreManager.getInstance().getMapProgress(this.map.getName());
		a.setColor(PROGRESSBAR_HIGHSCORE);
		a.drawLine(0, 0, 0,(int) (highscore*10));
		a.setColor(PROGRESSBAR_CURRENT);
		int upperEnd = (int)(exactPlayerX*10/map.getFinishX()) - 1;
		if (upperEnd >= 0)
			a.drawLine(0, 0,  0, upperEnd);
		int[][][] buffer = ArrayUtils.copy3(a.getArea());
		a.drawInverse(buffer);
		if (this.endscreenLose.isVisible())
			a.draw(endscreenLose);
		if (this.endscreenWin.isVisible())
			a.draw(endscreenWin);
		return true;
	}

	public void start(String mapName, Skin skin, InputProvider in) {
		Log.info("Starting game...", "Game");
		this.input = in;
		this.player.setSkin(skin);
		this.loadMap(mapName);
		this.reset();
		this.setVisible(true);
		if (!this.map.isMapLoaded()) {
			try {
				Thread.sleep(7500);
			} catch (InterruptedException e) {
			}
		} else {
			run(mapName,skin);
		}
	}

	private void run(String mapName,Skin skin) { // Run method (main method of game)
		Log.debug("Executing loop...", "Game");
		this.running = true;
		this.player.setVisible(true);
		boolean restart = false;
		while (running) {
			while (this.input.hasKey()) {
				int nk = this.input.getLastKey();
				if (nk == KeyEvent.VK_UP || nk == KeyEvent.VK_SPACE || nk == KeyEvent.VK_W) {
					this.jump();
				}
				if (nk == KeyEvent.VK_ESCAPE) {
					this.running = false;
					Log.info("Game stopped: ESC", "Game");
				}
			}
			byte re = this.move();
			if (re == RETURN_DEAD) {
				this.running = false;
				this.player.setVisible(false);
				Log.info("Game neded: DEAD", "Game");
				Main.draw();
				try {
					Thread.sleep(1000);
					this.endscreenLose.setVisible(true);
					restart = this.waitForESC();
				} catch (InterruptedException e) {
				}
			}
			if (re == RETURN_FINISH) {
				this.running = false;
				this.player.setVisible(false);
				Log.info("Game ended: FINISH", "Game");
				Main.draw();
				try {
					Thread.sleep(1000);
					this.endscreenWin.setVisible(true);
					restart = this.waitForESC();
				} catch (InterruptedException e) {
				}
			}
			try {
				long frame_duration = TICK_DURATION / FRAMES_PER_TICK;
				for (int C = 0; C < FRAMES_PER_TICK; C++) {
					Main.draw();
					Thread.sleep(frame_duration);
				}
			} catch (InterruptedException e) {
				Log.error("Game interrupted");
				this.running = false;
			}
		}
		float score = (float) (this.exactPlayerX/this.map.getFinishX());
		float lastScore = ScoreManager.getInstance().getMapProgress(mapName);
		if (score > lastScore)
			ScoreManager.getInstance().setMapProgress(mapName, score);
		this.setVisible(false);
		if (restart)
			this.start(mapName, skin, this.input);
		Log.debug("Exiting loop", "Game");
	}

	public void stop() {
		Log.info("Game ended: PROG_STOP", "Game");
		this.running = false;
	}

	private boolean waitForESC() throws InterruptedException {
		do	{
			Main.draw();
			if (this.input.hasKey())	{
				if (this.input.getLastKey() == KeyEvent.VK_ESCAPE)	{
					return false;
				}	else	{
					return true;
				}
			}
			Thread.sleep(TICK_DURATION);
		} while (true);
	}
	
	// Load game cnf files for color and skin changes
	private class GameConfig	{
		
		private List<ActionList> lists;
		
		public GameConfig()	{
			this.lists = new LinkedList<>();
		}
		
		public void load(String mapName) throws IOException	{
			this.lists.clear();
			InputStream i = this.getClass().getResourceAsStream("/lx/ledgeo/maps/" + mapName + ".cnf");
			if (i == null)
				return;
			BufferedReader reader = new BufferedReader(new InputStreamReader(i, StandardCharsets.UTF_8));
			ActionList last = null;
			String line;
			while ((line = reader.readLine()) != null)	{
				if (line.isBlank())
					continue;
				if (line.startsWith(" "))	{
					line = line.substring(1);
					if (last != null)	{
						ActionTypes type;
						if (line.startsWith("+"))	{
							line = line.substring(1);
							type = ADD;
						}	else if (line.startsWith("="))	{
							line = line.substring(1);
							type = SET;
						}	else	{
							type = SET;
						}
						int equalsIndex = line.indexOf("=");
						String field = line.substring(0, equalsIndex);
						String value = line.substring(equalsIndex + 1);
						last.add(new ActionEntry(field, type, value));
					}	else	{
						Log.warn("Failed to read config line: entry before declaration of list", "GC");
					}
				}	else if (line.startsWith(":"))	{
					try	{
						line = line.substring(1);
						int toIndex = line.indexOf("-");
						if (toIndex < 0)	{
							int x = Integer.valueOf(line);
							last = new ActionList(x, x);
							this.lists.add(last);
						}	else	{
							int x1 = Integer.valueOf(line.substring(0,toIndex));
							int x2 = Integer.valueOf(line.substring(toIndex + 1));
							last = new ActionList(x1, x2);
							this.lists.add(last);
						}
					}	catch (IllegalArgumentException ex)	{
						Log.warn("Invalid list declaration" + line, "GC");
					}
				}
			}
			reader.close();
		}
		
		public void apply(int x)	{
			for (ActionList al : this.lists)	{
				al.apply(x);
			}
		}
		
	}
	private class ActionList	{
		
		public final int xstart;
		public final int xend;
		public final List<ActionEntry> actions;
		
		public ActionList(int x1,int x2)	{
			this.xstart = x1;
			this.xend = x2;
			this.actions = new LinkedList<Game.ActionEntry>();
		}
		
		public void add(ActionEntry e)	{
			this.actions.add(e);
		}
		public boolean apply(int x)	{
			if (x >= xstart && x <= xend)	{
				for (ActionEntry e : this.actions)	{
					e.apply();
				}
				return true;
			}
			return false;
		}
		
	}
	private class ActionEntry	{
		public final String fieldName;
		public final ActionTypes type;
		public final String value;
		
		public ActionEntry(String fname,ActionTypes type,String value)	{
			this.fieldName = fname;
			this.type = type;
			this.value = value;
		}
		
		public int[] asColor()	throws IllegalArgumentException	{
			String[] vs = this.value.split(",");
			if (vs.length < 3)
				return null;
			int[] i = new int[3];
			i[0] = Integer.valueOf(vs[0]);
			i[1] = Integer.valueOf(vs[1]);
			i[2] = Integer.valueOf(vs[2]);
			return i;
		}
		public int asInt()	throws IllegalArgumentException {
			return Integer.valueOf(this.value);
		}
		public float asFloat()	throws IllegalArgumentException	{
			return Float.valueOf(this.value);
		}
		
		public void apply()	{
			try	{
				switch (this.fieldName) {
				case "bg":
					if (background instanceof ColorBackground)
						if (type == ADD)	{
							ColorBackground cbg = ((ColorBackground)background);
							cbg.setBaseColor(ColorUtils.add(cbg.getBaseColor(), this.asColor()));
						}	else
							((ColorBackground)background).setBaseColor(this.asColor());
					break;
				case "csolid":
					if (type == ADD)
						ColorUtils.add(map.color_solid, this.asColor());
					else
						map.color_solid = this.asColor();
					break;
				case "cspike":
					if (type == ADD)
						ColorUtils.add(map.color_spike, this.asColor());
					else
						map.color_spike = this.asColor();
					break;
				case "cdeco":
					if (type == ADD)
						ColorUtils.add(map.color_deco, this.asColor());
					else
						map.color_deco = this.asColor();
					break;
				case "skin":
					player.setSkin(Skin.getByName(this.value));
					break;
				default:
					throw new IllegalArgumentException();
				}
			}	catch (IllegalArgumentException ex)	{
				Log.error("Invalid value or field " + this.fieldName + ":" + this.value, "GC");
				Log.error(ex);
			}
		}
	}

}
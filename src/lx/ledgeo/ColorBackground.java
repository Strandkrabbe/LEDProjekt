package lx.ledgeo;

import java.util.Random;

import henning.leddriverj.draw.DrawingArea;

public class ColorBackground extends Background {

	private float MAX_STEP = 0.125f;

	private int[] base_color;
	private float[][] current_state;
	private boolean[][] current_direction; // true: up; false: down
	private float[][] speed;

	private Random r;

	public ColorBackground(int[] color) {
		this.base_color = color.clone();
		this.current_state = new float[Game.GAME_HEIGHT][Game.GAME_WIDTH];
		this.speed = new float[Game.GAME_HEIGHT][Game.GAME_WIDTH];
		this.current_direction = new boolean[Game.GAME_HEIGHT][Game.GAME_WIDTH];
		this.r = new Random();
		for (int Y = 0; Y < Game.GAME_HEIGHT; Y++) {
			for (int X = 0; X < Game.GAME_WIDTH; X++) {
				float f = this.r.nextFloat();
				this.current_state[Y][X] = f;
				this.current_direction[Y][X] = f < 0.5;
				this.speed[Y][X] = this.r.nextFloat();
			}
		}
	}

	@Override
	public synchronized void setMapPosition(double mapPos) {
		for (int Y = 0; Y < Game.GAME_HEIGHT; Y++) {
			for (int X = 0; X < Game.GAME_WIDTH; X++) {
//				float change = MAX_STEP * this.r.nextFloat();
				float change = MAX_STEP * this.speed[Y][X];
				if (this.current_direction[Y][X]) {
					float newvalue = this.current_state[Y][X] + change;
					if (newvalue < 1.0f) {
						this.current_state[Y][X] = newvalue;
					} else {
						this.current_direction[Y][X] = false;
					}
				} else {
					float newvalue = this.current_state[Y][X] - change;
					if (newvalue > 0.0f) {
						this.current_state[Y][X] = newvalue;
					} else {
						this.current_direction[Y][X] = true;
					}
				}
			}
		}
	}

	@Override
	public synchronized boolean draw(DrawingArea a) {
		if (!super.draw(a)) {
			return false;
		}
		int[][][] buffer = a.getArea();
		for (int Y = 0; Y < Game.GAME_HEIGHT; Y++) {
			for (int X = 0; X < Game.GAME_WIDTH; X++) {
				buffer[Y][X][0] = (int) (this.current_state[Y][X] * this.base_color[0]);
				buffer[Y][X][1] = (int) (this.current_state[Y][X] * this.base_color[1]);
				buffer[Y][X][2] = (int) (this.current_state[Y][X] * this.base_color[2]);
			}
		}
		return true;
	}
	public synchronized void setBaseColor(int[] color)	{
		this.base_color = color;
	}
	public synchronized int[] getBaseColor()	{
		return this.base_color;
	}

}
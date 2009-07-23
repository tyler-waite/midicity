package midicity.gui;

import processing.core.PApplet;
import processing.core.PImage;

public class Overlay {
	private PImage[] frames;
	private int x;
	private int y;
	private int width;
	private int height;
	private int currentFrame = 0;

	public Overlay(PApplet parent, String framesDir, int x, int y, int width,
			int height) {
		super();
		this.frames = ImageLoader.getFrames(framesDir, false, parent);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void draw(PApplet parent) {
		PImage image = frames[currentFrame];
		parent.image(image, x, y, width, height);
		currentFrame++;
		currentFrame %= frames.length;
	}
}

package midicity.gui;

import midicity.MidiCityApplet;
import processing.core.PImage;

public class Floor {

	private MidiCityApplet parent;
	private PImage[] images;
	private int width;
	private int displacement = 0;
	private int height;
	private City city;
	private long firstTime;

	public Floor(MidiCityApplet parent, City city, PImage[] images, int width,
			int height) {
		this.parent = parent;
		this.city = city;
		this.images = images;
		this.width = width;
		this.height = height;
		this.firstTime = System.currentTimeMillis();
	}

	public void draw() {
		int time = (int) (System.currentTimeMillis() - firstTime);
		time *= city.scale * parent.speedFactor;
		displacement = (int) (time % (width));
		for (int y = -parent.height; y <= 2 * parent.height; y += height) {
			for (int x = -10 - displacement; x < 2 * parent.width; x += width) {
				PImage image = images[City.random.nextInt(images.length)];
				drawImageAt(image, x, y);
			}
		}
	}

	private void drawImageAt(PImage image, int x, int y) {
		drawImageAt(image, x, y, 1.0f);
	}

	private void drawImageAt(PImage image, int x, int y, float scale) {
		x = (int) (city.cos * x - city.sin * y);
		y = (int) (city.sin * x + city.cos * y);
		y -= (int) (scale * image.height);
		parent.image(image, x, y, scale * image.width, scale * image.height);
	}

}

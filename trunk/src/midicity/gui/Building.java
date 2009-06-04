package midicity.gui;

import midicity.MidiCityApplet;
import processing.core.PImage;

public class Building {
	PImage[] frames;
	int frame;
	int finalFrame;
	int x = 0;
	int y = 0;
	int w;
	int h;
	private MidiCityApplet parent;
	private float scale;

	/*
	 * Building(String framesDir, float initialPC, float finalPC, float scale) {
	 * this(loadFrames(framesDir), initialPC, finalPC, float scale); }
	 */
	Building(MidiCityApplet parent, PImage[] frames, float initialPC, float finalPC, float scale) {
		this.parent = parent;
		this.frames = frames;
		w = h = 0;
		for (PImage frame: frames) {
			if (frame.width>w) w = frame.width;
			if (frame.height>h) h = frame.height;
		}
		frame = pcToFrame(initialPC);
		finalFrame = pcToFrame(finalPC);
		this.scale = scale;
	}

	int pcToFrame(float pc) {
		int frameNumber = (int) (pc * (frames.length));
		return frameNumber;
	}

	void setPC(float pc) {
		System.out.println("setPC: " + pc);
		finalFrame = pcToFrame(pc);
	}

	void draw() {
		PImage image = frames[frame];
		parent.image(image, x, y + scale*(h - image.height), scale * image.width, scale*image.height);
		if (frame > finalFrame) {
			frame--;
		} else if (frame < finalFrame) {
			frame++;
		}
	}
}
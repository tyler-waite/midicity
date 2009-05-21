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

	/*
	 * Building(String framesDir, float initialPC, float finalPC, float scale) {
	 * this(loadFrames(framesDir), initialPC, finalPC, float scale); }
	 */
	Building(MidiCityApplet parent, PImage[] frames, float initialPC, float finalPC, float scale) {
		this.parent = parent;
		this.frames = frames;
		w = (int) (frames[0].width * scale);
		h = (int) (frames[0].height * scale);
		frame = pcToFrame(initialPC);
		finalFrame = pcToFrame(finalPC);
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
		parent.image(image, x, y, w, h);
		if (frame > finalFrame) {
			frame--;
		} else if (frame < finalFrame) {
			frame++;
		}
	}
}
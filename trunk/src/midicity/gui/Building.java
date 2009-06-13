package midicity.gui;

import midicity.MidiCityApplet;
import midicity.midi.Note;
import midicity.midi.NoteManager;
import processing.core.PImage;

public class Building {
	PImage[] frames;
	int frame;
	int finalFrame;
	int frameWidth;
	int frameHeight;
	MidiCityApplet parent;
	City city;
	Note note;
	private int sideWidth;

	Building(MidiCityApplet parent, PImage[] frames, float initialPC,
			float finalPC, City city, Note note) {
		this.parent = parent;
		this.frames = frames;
		frameWidth = frameHeight = 0;
		for (PImage frame : frames) {
			if (frame.width > frameWidth)
				frameWidth = frame.width;
			if (frame.height > frameHeight)
				frameHeight = frame.height;
		}
		sideWidth = frameWidth / 3;
		frame = pcToFrame(initialPC);
		finalFrame = pcToFrame(finalPC);
		this.city = city;
		this.note = note;
	}

	int pcToFrame(float pc) {
		int frameNumber = (int) (pc * (frames.length));
		return frameNumber;
	}

	void setPC(float pc) {
		finalFrame = pcToFrame(pc);
	}

	void draw() {
		int octave = note.pitch / NoteManager.NOTES_PER_OCTAVE;
		int y = parent.height - 200 * octave;
		int w = (int) (note.duration(parent.velocityDurFactor) * parent.speedFactor);
		int x = parent.width - 300 - w;
		int lastX = parent.width - 300 - sideWidth;

		long age = note.getAge(parent.velocityDurFactor);
		int ageDisplacement = (int) (age * parent.speedFactor);
		x -= ageDisplacement;
		lastX -= ageDisplacement;

		PImage image = frames[frame];

		while (x < lastX) {
			drawImageAt(image, x, y);
			x += sideWidth;
		}
		drawImageAt(image, lastX, y);

		if (frame > finalFrame) {
			frame--;
		} else if (frame < finalFrame) {
			frame++;
		}
	}

	private void drawImageAt(PImage image, int x, int y) {
		x = (int) (city.cos * x - city.sin * y);
		y = (int) (city.sin * x + city.cos * y);
		y += (int) (city.scale * (frameHeight - image.height));
		parent.image(image, x, y, city.scale * image.width, city.scale
				* image.height);
	}
}
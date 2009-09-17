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
	private PImage[] sidewalkFrames;
	private int sidewalkFrame = 0;
	int swFrameWidth;
	int swFrameHeight;
	int swSideWidth;

	Building(MidiCityApplet parent, PImage[] frames, PImage[] sidewalkFrames,
			float initialPC, float finalPC, City city, Note note) {
		this.parent = parent;
		this.frames = frames;
		this.sidewalkFrames = sidewalkFrames;
		frameWidth = frameHeight = 0;
		for (PImage frame : frames) {
			if (frame.width > frameWidth)
				frameWidth = frame.width;
			if (frame.height > frameHeight)
				frameHeight = frame.height;
		}
		sideWidth = frameWidth / 2;

		swFrameWidth = swFrameHeight = 0;
		for (PImage frame : sidewalkFrames) {
			if (frame.width > swFrameWidth)
				swFrameWidth = frame.width;
			if (frame.height > swFrameHeight)
				swFrameHeight = frame.height;
		}
		swSideWidth = swFrameHeight / 2;

		frame = pcToFrame(initialPC);
		finalFrame = pcToFrame(finalPC);
		this.city = city;
		this.note = note;
	}

	int pcToFrame(float pc) {
		int frameNumber = (int) Math.min((pc * (frames.length)),
				frames.length - 1);
		return frameNumber;
	}

	void setPC(float pc) {
		finalFrame = pcToFrame(pc);
	}

	void draw() {
		int octave = note.pitch / NoteManager.NOTES_PER_OCTAVE;
		int y = parent.height - parent.yOffset - parent.octaveSeparation
				* octave;
		int w = (int) (city.scale * note.duration(parent.velocityDurFactor) * parent.speedFactor);

		long age = note.getAge(parent.velocityDurFactor);
		int ageDisplacement = (int) (city.scale * age * parent.speedFactor);
		int x;
		int lastX;

		PImage sidewalk = sidewalkFrames[sidewalkFrame];
		// building

		x = parent.width + parent.xOffset - w;
		lastX = parent.width + parent.xOffset - (int) (city.scale * sideWidth);
		x -= ageDisplacement;
		lastX -= ageDisplacement;

		PImage image = frames[frame];

		while (x < lastX) {
			drawImageAt(image, x, y);
			x += city.scale * sideWidth;
		}
		drawImageAt(image, lastX, y);

		if (frame > finalFrame) {
			frame--;
		} else if (frame < finalFrame) {
			frame++;
		}

		// draw front sidewalk
		x = parent.width + parent.xOffset - w
				- (int) (city.scale * swSideWidth);
		lastX = parent.width + parent.xOffset;
		x -= ageDisplacement;
		lastX -= ageDisplacement;

		while (x < lastX) {
			drawImageAt(sidewalk, x, y, 1f);
			x += city.scale * swSideWidth;
		}
		drawImageAt(sidewalk, lastX, y, 1f);
		if (sidewalkFrame < sidewalkFrames.length - 1)
			sidewalkFrame++;
	}

	private void drawImageAt(PImage image, int x, int y) {
		drawImageAt(image, x, y, 1.0f);
	}

	private void drawImageAt(PImage image, int x, int y, float scale) {
		x = (int) (city.cos * x - city.sin * y);
		y = (int) (city.sin * x + city.cos * y);
		y -= (int) (scale * city.scale * image.height);
		parent.image(image, x, y, scale * city.scale * image.width, scale
				* city.scale * image.height);
	}
}
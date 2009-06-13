package midicity.gui;

import midicity.MidiCityApplet;
import midicity.midi.Note;
import processing.core.PApplet;

public class PianoRoll {

	private MidiCityApplet parent;

	public PianoRoll(MidiCityApplet parent) {
		this.parent = parent;
	}

	public void draw() {
		Note[] notes = parent.noteManager.notes();
		for (int i = 0; i < notes.length; i++) {
			Note note = notes[i];
			int y = parent.height - 4 * note.pitch;
			int w = (int) note.duration(parent.velocityDurFactor) / 20;
			int x = parent.width - w;
			int h = 6;
			int c = 2 * note.velocity;
			if (!note.on) {
				long age = System.currentTimeMillis() - note.endMillis;
				c = (int) PApplet.max(2 * note.velocity - age / 50, 0);
				x -= (int) age / 20;
			}
			int r = note.velocity % 3;
			if (r == 0)
				parent.fill(parent.color(c, 0, 0));
			if (r == 1)
				parent.fill(parent.color(0, c, 0));
			if (r == 2)
				parent.fill(parent.color(0, 0, c));
			parent.rect(x, y, w, h);
		}
	}

}

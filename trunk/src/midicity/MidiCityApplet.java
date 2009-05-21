/**
 * 
 */
package midicity;

import java.util.Arrays;

import midicity.gui.City;
import midicity.midi.Note;
import midicity.midi.NoteManager;
import processing.core.PApplet;
import themidibus.MidiBus;
import themidibus.MidiListener;
import fullscreen.FullScreen;

/**
 * @author mblech
 * 
 */
public class MidiCityApplet extends PApplet implements MidiListener {

	MidiBus myBus;
	FullScreen fs;

	public NoteManager noteManager;
	City city;

	public void setup() {
		// screen
		size(1024, 768);
		frameRate(30);

		// city
		// city = new City(8, "img/seagram");
		city = new City(this, 15, "resources/img/02 animeup", true, PI / 6);

		// currentNotes = new Note[OCTAVES];
		noteManager = new NoteManager(50);

		// midi
		myBus = new MidiBus(this);
		String[][] deviceList = MidiBus.returnList();
		for (int i = 0; i < deviceList.length; i++) {
			System.out.println(Arrays.toString(deviceList[i]));
			String type = deviceList[i][1];
			if (type.contains("Input")) {
				myBus.addInput(i);
			}
		}

		// fullscreen
		// fs = new FullScreen(this);
	}

	long frame = 0;

	public void draw() {
		frame++;
		background(0);
		// draw

		city.draw();

		/*
		 * Note[] notes = noteManager.notes(); for (int i = 0; i < notes.length;
		 * i++) { Note note = notes[i]; int y = height - 4 * note.pitch; int w =
		 * (int) note.duration() / 20; int x = width - w; int h = 6; int c = 2 *
		 * note.velocity; if (!note.on) { long age = System.currentTimeMillis()
		 * - note.endMillis; c = (int) max(2 * note.velocity - age / 50, 0); x
		 * -= (int) age / 20; } int r = note.velocity % 3; if (r == 0)
		 * fill(color(c, 0, 0)); if (r == 1) fill(color(0, c, 0)); if (r == 2)
		 * fill(color(0, 0, c)); rect(x, y, w, h); }
		 */
	}

	public void noteOn(int channel, int pitch, int velocity) {
		noteManager.noteOn(channel, pitch, velocity);
		city.noteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		noteManager.noteOff(channel, pitch, velocity);
	}

	public void controllerChange(int channel, int number, int value) {
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", MidiCityApplet.class.getName() });
	}
}

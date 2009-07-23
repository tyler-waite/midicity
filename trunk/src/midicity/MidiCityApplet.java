/**
 * 
 */
package midicity;

import java.util.Arrays;

import midicity.gui.City;
import midicity.gui.Overlay;
import midicity.gui.PianoRoll;
import midicity.midi.NoteManager;
import processing.core.PApplet;
import themidibus.MidiBus;
import themidibus.MidiListener;

/**
 * @author mblech
 * 
 */
public class MidiCityApplet extends PApplet implements MidiListener {

	MidiBus myBus;

	public NoteManager noteManager;
	City city;
	Overlay overlay;
	PianoRoll pianoRoll;
	public float speedFactor;
	public float velocityDurFactor;
	public int xOffset;
	public int yOffset;
	public int octaveSeparation;

	public void setup() {
		// screen
		size(1024, 768);
		frameRate(30);

		// speed / tempo factor
		speedFactor = 1.0f / 5;

		// velocity duration factor
		velocityDurFactor = 6000f / 128;

		// screen offset
		xOffset = -100;
		yOffset = -400;

		// inter-octave separation
		octaveSeparation = 200;

		// city
		city = new City(this, "img/buildings", false, PI / 6, 0.5f);

		overlay = new Overlay(this, "img/overlays/fog/fog_03", 0, 0, width, height);

		// piano roll
		// pianoRoll = new PianoRoll(this);

		// noteManager
		noteManager = new NoteManager(50);
		noteManager.addListener(city);

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
	}

	long frame = 0;

	public void draw() {
		frame++;
		background(0);
		// draw

		city.draw();
		overlay.draw(this);

		// pianoRoll.draw();

	}

	public void noteOn(int channel, int pitch, int velocity) {
		noteManager.noteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		noteManager.noteOff(channel, pitch, velocity);
	}

	public void controllerChange(int channel, int number, int value) {
		System.out.println("ctl(" + channel + ", " + number + ", " + value
				+ ")");
		if (number == 1) {
			float angle = ((float) value) / 128 * (float) Math.PI;
			city.setAngle(angle);
			System.out.println("new angle: " + angle);
		}
		if (number == 74) {
			xOffset = (int) (width * (2 * ((float) value) / 128 - 1));
			System.out.println("new x-offset: " + xOffset);
		}
		if (number == 71) {
			yOffset = (int) (height * (2 * ((float) value) / 128 - 1));
			System.out.println("new y-offset: " + yOffset);
		}
		if (number == 91) {
			octaveSeparation = (int) (height * (((float) value) / 128) / 2);
			System.out.println("new inter-octave separation: "
					+ octaveSeparation);
		}
		if (number == 93) {
			city.scale = (2 * (((float) value) / 128));
			System.out.println("new scale: " + city.scale);
		}
		if (number == 73) {
			speedFactor = 0.01f + (2 * (((float) value) / 128));
			System.out.println("new speed factor: " + speedFactor);
		}
	}

	public static void main(String[] args) {
		String[] newArgs = new String[args.length + 1];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		newArgs[args.length] = MidiCityApplet.class.getName();
		PApplet.main(newArgs);
	}
}
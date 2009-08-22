package midicity.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import midicity.MidiCityApplet;
import midicity.midi.Note;
import midicity.midi.NoteManager;
import midicity.midi.Util;
import processing.core.PApplet;
import processing.core.PImage;

public class City implements NoteManager.NoteListener {
	static Random random = new Random();

	LinkedList<Building[]> rows = new LinkedList<Building[]>();
	Map<String, PImage[]> frames;
	float angle;
	float sin;
	float cos;
	MidiCityApplet parent;
	public float scale;
	Map<Note, Building> buildings = new HashMap<Note, Building>();

	Map<String, PImage[]> sidewalkFrames;

	public City(MidiCityApplet parent, String framesDir,
			String sidewalkFramesDir, boolean reverse, float angle, float scale) {
		this.parent = parent;
		this.frames = ImageLoader.getAllFrames(framesDir, reverse, parent);
		this.sidewalkFrames = ImageLoader.getAllFrames(sidewalkFramesDir,
				false, parent);
		this.angle = angle;
		this.setAngle(angle);
		this.scale = scale;
	}

	public void setAngle(float angle) {
		this.sin = PApplet.sin(angle);
		this.cos = PApplet.cos(angle);
	}

	private static Comparator<Building> BUILDING_COMPARATOR = new Comparator<Building>() {
		public int compare(Building o1, Building o2) {
			int octave1 = o1.note.pitch / NoteManager.NOTES_PER_OCTAVE;
			int octave2 = o2.note.pitch / NoteManager.NOTES_PER_OCTAVE;
			int diff = octave2 - octave1;
			if (diff == 0) {
				diff = (int) (o1.note.startMillis - o2.note.startMillis);
			}
			return diff;
		}
	};

	public void draw() {
		Building[] buildings = null;
		// get buildings array
		synchronized (this.buildings) {
			buildings = this.buildings.values().toArray(
					new Building[this.buildings.size()]);
		}
		// sort to avoid overlap
		Arrays.sort(buildings, BUILDING_COMPARATOR);
		// draw each building
		for (Building building : buildings) {
			building.draw();
		}
	}

	public void noteOff(Note note, NoteManager noteManager) {
	}

	public void noteOn(Note note, NoteManager noteManager) {
		Building newBuilding = createBuilding(note, noteManager);
		synchronized (buildings) {
			buildings.put(note, newBuilding);
		}
	}

	private Building createBuilding(Note note, NoteManager noteManager) {
		PImage[] frames = selectFrames(note, noteManager);
		// note = height
		// int n = note.pitch % NoteManager.NOTES_PER_OCTAVE;
		// float finalPC = ((float) n) / NoteManager.NOTES_PER_OCTAVE;

		// velocity = height
		float finalPC = ((float) note.velocity) / 80;
		if (finalPC < 0)
			finalPC = 0;
		if (finalPC > 1)
			finalPC = 1;

		PImage[] sf = (PImage[]) this.sidewalkFrames.values().toArray()[random
				.nextInt(this.sidewalkFrames.size())];
		Building building = new Building(parent, frames, sf, 0, finalPC, this,
				note);
		return building;
	}

	private PImage[] selectFrames(Note note, NoteManager noteManager) {
		Collection<PImage[]> available = new HashSet<PImage[]>(frames.values());
		Note[] notes = noteManager.notes();
		float minDissonance = Float.MAX_VALUE;
		PImage[] minDisFrames = null;
		for (int i = notes.length - 1; i >= 0; i--) {
			if (minDissonance == 0.0f) {
				break;
			}
			Note note2 = notes[i];
			float dissonance = Util.getDissonance(note.pitch, note2.pitch);
			if (dissonance < minDissonance) {
				Building building = buildings.get(note2);
				if (building != null) {
					minDissonance = dissonance;
					minDisFrames = building.frames;
					available.remove(minDisFrames);
				}
			}
		}
		if (minDisFrames != null) {
			if (random.nextFloat() > 0.1f + 0.8f * minDissonance) {
				return minDisFrames;
			}
		}

		if (available.size() == 0) {
			available = frames.values();
		}
		PImage[][] allFrames = available
				.toArray(new PImage[available.size()][]);
		return allFrames[random.nextInt(allFrames.length)];
	}

	public void noteDied(Note note, NoteManager noteManager) {
		synchronized (buildings) {
			buildings.remove(note);
		}
	}
}
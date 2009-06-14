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
	int maxRows;
	Map<String, PImage[]> frames;
	float angle;
	float sin;
	float cos;
	MidiCityApplet parent;
	public float scale;
	Map<Note, Building> buildings = new HashMap<Note, Building>();

	public City(MidiCityApplet parent, int maxRows, String framesDir,
			boolean reverse, float angle, float scale) {
		this.parent = parent;
		this.maxRows = maxRows;
		this.frames = ImageLoader.getAllFrames(framesDir, reverse, parent);
		this.angle = angle;
		this.setAngle(angle);
		this.scale = scale;
	}

	public void setAngle(float angle) {
		this.sin = PApplet.sin(angle);
		this.cos = PApplet.cos(angle);
	}

	Building[] newRow(Building[] row) {
		rows.offer(row);
		while (rows.size() > maxRows) {
			rows.poll();
		}
		return row;
	}

	public void draw() {
		Building[] buildings = null;
		synchronized (this.buildings) {
			buildings = this.buildings.values().toArray(
					new Building[this.buildings.size()]);
		}
		Arrays.sort(buildings, new Comparator<Building>() {
			public int compare(Building o1, Building o2) {
				int octave1 = o1.note.pitch / NoteManager.NOTES_PER_OCTAVE;
				int octave2 = o2.note.pitch / NoteManager.NOTES_PER_OCTAVE;
				int diff = octave2 - octave1;
				if (diff == 0) {
					diff = (int) (o1.note.startMillis - o2.note.startMillis);
				}
				return diff;
			}
		});
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
		int n = note.pitch % NoteManager.NOTES_PER_OCTAVE;
		float finalPC = ((float) n) / NoteManager.NOTES_PER_OCTAVE;
		Building building = new Building(parent, frames, 0, finalPC, this, note);
		return building;
	}

	private PImage[] selectFrames(Note note, NoteManager noteManager) {
		Collection<PImage[]> available = new HashSet<PImage[]>(frames.values());
		Note[] notes = noteManager.notes();
		for (int i = notes.length - 1; i >= Math.max(0, notes.length - 4); i--) {
			Note note2 = notes[i];
			Building building = buildings.get(note2);
			if (building != null) {
				float dissonance = 0.2f + 0.8f * Util.getDissonance(
						note.pitch, note2.pitch);
				float rnd = random.nextFloat();
				if (rnd > dissonance) {
					return building.frames;
				} else {
					available.remove(building.frames);
				}
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
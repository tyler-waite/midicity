package midicity.gui;

import java.util.LinkedList;

import midicity.MidiCityApplet;
import midicity.midi.Note;
import midicity.midi.NoteManager;
import processing.core.PApplet;
import processing.core.PImage;

public class City {
	LinkedList<Building[]> rows = new LinkedList<Building[]>();
	int maxRows;
	PImage[] frames;
	float angle;
	float sin;
	float cos;
	MidiCityApplet parent;
	private float scale;

	public City(MidiCityApplet parent, int maxRows, String imgName,
			boolean reverse, float angle, float scale) {
		this.parent = parent;
		this.maxRows = maxRows;
		this.frames = ImageLoader.getFrames(imgName, reverse, parent);
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
		for (int octave = NoteManager.OCTAVES - 1; octave >= 0; octave--) {
			for (int rowN = rows.size() - 1; rowN >= 0; rowN--) {
				Building[] row = (Building[]) rows.get(rows.size() - 1 - rowN);
				Building building = row[octave];
				if (building != null) {
					float x = parent.width - 500 - 78 * rowN;
					x += 40 * octave;
					float y = 800 - 200 * octave;
					building.x = (int) (this.cos * x - this.sin * y);
					building.y = (int) (this.sin * x + this.cos * y);
					building.draw();
				}
			}
		}
	}

	public void noteOn(int channel, int pitch, int velocity) {
		Building[] newRow = new Building[NoteManager.OCTAVES];// newDefaultRow();
		newRow(newRow);
		Note[] notes = parent.noteManager.currentNotes();
		long time = System.currentTimeMillis();
		for (int i = 0; i < notes.length; i++) {
			Note note = notes[i];
			float age = ((float) (time - note.startMillis)) / note.velocity;
			if (age < 35) {
				int octave = note.pitch / NoteManager.NOTES_PER_OCTAVE;
				int n = note.pitch % NoteManager.NOTES_PER_OCTAVE;
				float finalPC = ((float) n) / NoteManager.NOTES_PER_OCTAVE;
				float initialPC = finalPC;
				if (pitch == note.pitch) {
					initialPC = 0;
				}
				Building building = newRow[octave];
				if (building == null) {
					building = new Building(parent, frames, initialPC, finalPC,
							this.scale);
					newRow[octave] = building;
				}
			}
		}
	}
}
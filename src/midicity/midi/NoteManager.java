package midicity.midi;

import static midicity.midi.Util.toNoteArray;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NoteManager {
	public static int MIDI_NOTES = 128;
	public static int NOTES_PER_OCTAVE = 12;
	public static int OCTAVES = (int) Math.ceil(((float) MIDI_NOTES)
			/ NOTES_PER_OCTAVE);

	Queue<Note>[] noteQueues;
	Queue<Note> notes;
	int maxOld;

	public NoteManager(int tempMO) {
		noteQueues = new Queue[MIDI_NOTES];
		for (int i = 0; i < noteQueues.length; i++) {
			noteQueues[i] = new LinkedList<Note>();
		}
		notes = new LinkedList<Note>();
		maxOld = tempMO;
	}

	public void noteOn(int channel, int pitch, int velocity) {
		Note note = new Note(channel, pitch, velocity);
		synchronized (noteQueues[pitch]) {
			noteQueues[pitch].offer(note);
		}
		synchronized (notes) {
			notes.offer(note);
			while (notes.size() >= maxOld) {
				notes.poll();
			}
		}
	}

	public void noteOff(int channel, int pitch, int velocity) {
		Note note;
		synchronized (noteQueues[pitch]) {
			note = (Note) noteQueues[pitch].poll();
		}
		if (note != null) {
			note.endMillis = System.currentTimeMillis();
			note.on = false;
		}
	}

	public Note[] currentNotes() {
		List<Note> l = new LinkedList<Note>();
		for (int i = 0; i < noteQueues.length; i++) {
			synchronized (noteQueues[i]) {
				l.addAll(noteQueues[i]);
			}
		}
		return toNoteArray(l);
	}

	public Note[] notes() {
		return toNoteArray(notes);
	}
}
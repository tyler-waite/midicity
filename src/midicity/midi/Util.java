package midicity.midi;

import java.util.Collection;

public class Util {

	public static Note[] toNoteArray(Collection<Note> l) {
		if (l == null) {
			return new Note[0];
		}
		synchronized (l) {
			Note[] notes = new Note[l.size()];
			int i = 0;
			for (Note note : l) {
				notes[i] = note;
				i++;
			}
			return notes;
		}
	}

	private static final float[] DISSONANCE = new float[] { 0.0f, 1.0f, 0.8f,
			0.6f, 0.4f, 0.2f, 1.0f, 0.1f, 0.4f, 0.5f, 0.6f, 0.3f };

	public static float getDissonance(int pitch1, int pitch2) {
		int min = Math.min(pitch1, pitch2);
		int max = Math.max(pitch1, pitch2);
		int interval = (max - min) % NoteManager.NOTES_PER_OCTAVE;
		return DISSONANCE[interval];
	}

}

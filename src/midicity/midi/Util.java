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

}

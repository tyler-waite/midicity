package midicity.midi;

public class Note {
	int channel;
	public int pitch;
	public int velocity;
	public long startMillis;
	public long endMillis;
	public boolean on;

	public Note(int tempC, int tempP, int tempV, long tempS, boolean tempOn) {
		channel = tempC;
		pitch = tempP;
		velocity = tempV;
		startMillis = tempS;
		endMillis = -1;
		on = tempOn;
	}

	Note(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, System.currentTimeMillis(), true);
	}

	public String toString() {
		return "Note(" + channel + ", " + pitch + ", " + velocity + ", "
				+ startMillis + ", " + endMillis + ")";
	}

	public long duration(float velocityDurFactor) {
		long now = System.currentTimeMillis();
		long endMillis = calculateEndMillis(velocityDurFactor, now);
		return endMillis - startMillis;
	}

	public long getAge(float velocityDurFactor) {
		long now = System.currentTimeMillis();
		long endMillis = calculateEndMillis(velocityDurFactor, now);
		return now - endMillis;
	}

	private long calculateEndMillis(float velocityDurFactor, long now) {
		long endMillis = now;
		if (!(this.endMillis == -1)) {
			endMillis = this.endMillis;
		}
		long maxEndMillis = this.startMillis
				+ (long) (velocityDurFactor * this.velocity);
		endMillis = Math.min(endMillis, maxEndMillis);
		return endMillis;
	}
}
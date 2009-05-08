import themidibus.*;

class Note {
  int channel;
  int pitch;
  int velocity;
  long startMillis;
  long endMillis;
  boolean on;
  Note(int tempC, int tempP, int tempV, long tempS, boolean tempOn) {
    channel = tempC;
    pitch = tempP;
    velocity = tempV;
    startMillis = tempS;
    endMillis = -1;
    on = tempOn;
  }
  Note(int channel, int pitch, int velocity) {
    this(channel, pitch, velocity, millis(), true);
  }
  String toString() {
    return "Note("+channel+", "+pitch+", "+velocity+", "+startMillis+", "+endMillis+")";
  }
  long duration() {
    if (endMillis == -1)
      return millis() - startMillis;
    else
      return endMillis - startMillis;
  }
}

class NoteManager {
  Queue[] noteQueues;
  Queue notes;
  int maxOld;
  NoteManager(int tempMO) {
    noteQueues = new Queue[MIDI_NOTES];
    for (int i = 0; i < noteQueues.length; i++) {
      noteQueues[i] = new LinkedList();
    }
    notes = new LinkedList();
    maxOld = tempMO;
  }
  void noteOn(int channel, int pitch, int velocity) {
    Note note = new Note(channel, pitch, velocity);
    synchronized (noteQueues[pitch]) {
      noteQueues[pitch].offer(note);
    }
    synchronized(notes) {
      notes.offer(note);
      while (notes.size()>=maxOld) {
        notes.poll();
      }
    }
  }
  void noteOff(int channel, int pitch, int velocity) {
    Note note;
    synchronized (noteQueues[pitch]) {
      note = (Note) noteQueues[pitch].poll();
    }
    if (note != null) {
      note.endMillis = millis();
      note.on = false;
    }
  }
  Note[] currentNotes() {
    java.util.List l = new LinkedList();
    for (int i = 0; i < noteQueues.length; i++) {
      synchronized (noteQueues[i]) {
        l.addAll(noteQueues[i]);
      }
    }
    return toNoteArray(l);
  }
  Note[] notes() {
    return toNoteArray(notes);
  }
}

Note[] toNoteArray(Collection l) {
  if (l == null) {
    return new Note[0];
  }
  Note[] notes = new Note[l.size()];
  int i = 0;
  synchronized (l) {
    for (Iterator it = l.iterator(); it.hasNext();) {
      Note note = (Note) it.next();
      notes[i] = note;
      i++;
    }
  }
  return notes;
}

PImage[] loadFrames(String framesDir) {
  File dir = new File(sketchPath + File.separator + "data" + File.separator + framesDir);
  String[] files = dir.list();
  Arrays.sort(files);
  PImage[] frames = new PImage[files.length];
  PImage im = null;
  for (int i = 0; i < files.length; i++) {
    String file = files[i];
    im = loadImage(framesDir + File.separator + file);
    frames[i] = im;
  }
  return frames;
}

class Building {
  PImage[] frames;
  int frame;
  int finalFrame;
  int x = 0;
  int y = 0;
  int w;
  int h;
  /*Building(String framesDir, float initialPC, float finalPC, float scale) {
   this(loadFrames(framesDir), initialPC, finalPC, float scale); 
  }*/
  Building(PImage[] frames, float initialPC, float finalPC, float scale) {
    this.frames = frames;
    w = int(frames[0].width*scale);
    h = int(frames[0].height*scale);
    frame = pcToFrame(initialPC);
    finalFrame = pcToFrame(finalPC);
  }
  
  int pcToFrame(float pc) {
    return int(pc * (frames.length-1));
  }
  
  void setPC(float pc) {
    finalFrame = pcToFrame(pc);
  }
  
  void draw() {
    PImage myMovie = frames[frame];
    image(myMovie, x, y, w, h);
    if (frame > finalFrame) {
      frame--;
    } else if (frame < finalFrame) {
      frame++;
    }
  }
}

class City {
  LinkedList rows = new LinkedList();
  int maxRows;
  PImage[] frames;
  City(int maxRows, String imgName) {
    this.maxRows = maxRows;
    this.frames = loadFrames(imgName);
  }
  Building[] newDefaultRow() {
    Building[] row = new Building[OCTAVES];
    for (int i = 0; i < OCTAVES; i++) {
      row[i] = new Building(frames, 0, 0, 0.3);
    }
    return newRow(row);
  }
  Building[] newRow(Building[] row) {
    rows.offer(row);
    while (rows.size()>maxRows) {
      rows.poll();
    }
    return row;
  }
  void draw() {
    for (int octave = OCTAVES - 1; octave >= 0; octave--) {
      for (int rowN = rows.size()-1; rowN >= 0; rowN--) {
        Building[] row = (Building[])rows.get(rows.size()-1-rowN);
        Building building = row[octave];
        if (building != null) {
          building.x = 200 - 50*rowN;
          building.y = 300 - 50*octave;
          building.draw();
        }
      }
    }
  }
  void noteOn(int channel, int pitch, int velocity) {
    Building[] newRow = new Building[OCTAVES];//newDefaultRow();
    newRow(newRow);
    Note[] notes = noteManager.currentNotes();
    for (int i = 0; i < notes.length; i++) {
      int octave = notes[i].pitch / NOTES_PER_OCTAVE;
      int n = notes[i].pitch % NOTES_PER_OCTAVE;
      Building building = newRow[octave];
      if (building == null) {
        building = new Building(frames, 0, 0, 0.3);
        newRow[octave] = building;
      }
      float f = float(n)/NOTES_PER_OCTAVE;
      building.setPC(0.2 + 0.8*f);
    }
  }
}

MidiBus myBus;

int INPUT_DEVICE_INDEX = 0;
int MIDI_NOTES = 128;
int NOTES_PER_OCTAVE = 12;
int OCTAVES = (int)Math.ceil(float(MIDI_NOTES)/NOTES_PER_OCTAVE);
NoteManager noteManager;
City city;

void setup() {
  println(OCTAVES);
  //screen
  size(400,400);
  frameRate(30);
  
  //city
  //building = new Building("img/seagram", 0, 1, 0.4);
  city = new City(10, "img/seagram");
  
  //currentNotes = new Note[OCTAVES];
  noteManager = new NoteManager(50);
  
  //midi
  myBus = new MidiBus(this);
  String[][] deviceList = MidiBus.returnList();
  for (int i = 0; i < deviceList.length; i++) {
    String type = deviceList[i][1];
    if (type.contains("Input")) {
      myBus.addInput(i);
    }
  }
}

long frame = 0;
void draw() {
  frame++;
  background(0);
  //draw
  
  city.draw();
  
  Note[] notes = noteManager.notes();
  for (int i = 0; i < notes.length; i++) {
    Note note = notes[i];
    int y = height - 4*note.pitch;
    int w = (int) note.duration()/20;
    int x = width - w;
    int h = 6;
    int c = 2*note.velocity;
    if (!note.on) {
      long age = millis() - note.endMillis;
      c = (int)max(2*note.velocity - age/50, 0);
      x -= (int)age/20;
    }
    int r = note.velocity % 3;
    if (r==0)
      fill(color(c, 0, 0));
    if (r==1)
      fill(color(0, c, 0));
    if (r==2)
      fill(color(0, 0, c));
    rect(x, y, w, h);
  }
}


void noteOn(int channel, int pitch, int velocity) {
  noteManager.noteOn(channel, pitch, velocity);
  city.noteOn(channel, pitch, velocity);
}

void noteOff(int channel, int pitch, int velocity) {
  noteManager.noteOff(channel, pitch, velocity);
}

void controllerChange(int channel, int number, int value) {
}

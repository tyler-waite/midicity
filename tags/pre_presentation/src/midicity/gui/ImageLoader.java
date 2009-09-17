package midicity.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageLoader {

	private static final Map<String, PImage[]> cache = new HashMap<String, PImage[]>();

	public static PImage[] loadFrames(String framesDir, boolean invert,
			PApplet parent) {
		long time = System.currentTimeMillis();
		File dir = new File(framesDir);
		String[] files = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".png");
			}
		});
		if (files == null || files.length == 0)
			throw new RuntimeException(new FileNotFoundException("no .pngs in "
					+ dir.getAbsolutePath()));
		Arrays.sort(files);
		if (invert) {
			int i = 0;
			int j = files.length - 1;
			while (i < j) {
				String temp = files[i];
				files[i] = files[j];
				files[j] = temp;
				i++;
				j--;
			}
		}
		PImage[] frames = new PImage[files.length];
		PImage im = null;
		for (int i = 0; i < files.length; i++) {
			String file = files[i];
			im = parent.loadImage(framesDir + File.separator + file);
			frames[i] = im;
		}
		time = System.currentTimeMillis() - time;
		System.out.println("loaded in " + time + "ms");
		return frames;
	}

	public static PImage[] getFrames(String framesDir, boolean invert,
			PApplet parent) {
		if (!cache.containsKey(framesDir))
			cache.put(framesDir, loadFrames(framesDir, invert, parent));
		return cache.get(framesDir);
	}

	public static Map<String, PImage[]> getAllFrames(String dir,
			boolean invert, PApplet parent) {
		Map<String, PImage[]> allFrames = new HashMap<String, PImage[]>();
		File dirFile = new File(dir);
		System.out.println("Loading frames from: " + dirFile.getAbsolutePath());
		String[] dirs = dirFile.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir.getAbsolutePath() + File.separator + name)
						.isDirectory();
			}
		});
		for (String name : dirs) {
			PImage[] frames = getFrames(dir + File.separator + name, invert,
					parent);
			if (frames.length > 0) {
				allFrames.put(name, frames);
			}
		}
		return allFrames;
	}

}

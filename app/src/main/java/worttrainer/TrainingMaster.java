package worttrainer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class TrainingMaster {
	private ArrayList<TrainingEntry> entries;
	private int currentEntry;
	private int completedEntries;
	private Random rand;

	public TrainingMaster(ArrayList<TrainingEntry> entries) {
		this.entries = entries;
		this.rand = new Random();
		this.completedEntries = 0;

		this.pickRandomEntry();
	}

	/**
	 * Guesses a word removes the entry from the list if correct,
	 * and picks a new entry regardless of correctness
	 * 
	 * @param word the word to be guessed (case-insensitive)
	 * @return whether the guess was correct
	 */
	public boolean guessWord(String word) {
		boolean correct = this.entries.get(currentEntry).guessWord(word);

		if (correct) {
			this.entries.remove(currentEntry);
			this.completedEntries++;
		}
		
		this.pickRandomEntry();

		return correct;
	}

	// TODO: fromState
	// TODO: saveState

	public URL getImage() {
		return entries.get(currentEntry).getImage();
	}

	public int getCompletedEntries() {
		return this.completedEntries;
	}

	public int getRemainingEntries() {
		return this.entries.size();
	}

	public int getIncorrectEntries() {
		int c = 0;
		for (TrainingEntry entry : this.entries) {
			c += entry.getTries();
		}
		return c;
	}

	private void pickRandomEntry() {
		if (this.entries.size() > 0) {
			this.currentEntry = this.rand.nextInt(this.entries.size());
		} else {
			this.currentEntry = -1;
		}
	}
}

package worttrainer;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingMaster implements Serializable {
	private ArrayList<TrainingEntry> entries;
	private int currentEntry;
	private int completedEntries;
	@JsonIgnore
	private Random rand;

	public TrainingMaster(ArrayList<TrainingEntry> entries) {
		this.entries = entries;
		this.rand = new Random();
		this.completedEntries = 0;

		this.pickRandomEntry();
	}

	@JsonCreator
	public TrainingMaster(
			@JsonProperty("entries") ArrayList<TrainingEntry> entries,
			@JsonProperty("currentEntry") int currentEntry,
			@JsonProperty("completedEntries") int completedEntries) {
		this.entries = entries;
		this.rand = new Random();
		this.completedEntries = completedEntries;
		this.currentEntry = currentEntry;
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

	@JsonIgnore
	public URL getImage() {
		return entries.get(currentEntry).getImage();
	}

	public int getCompletedEntries() {
		return this.completedEntries;
	}

	@JsonIgnore
	public int getRemainingEntries() {
		return this.entries.size();
	}

	@JsonIgnore
	public int getIncorrectEntries() {
		int c = 0;
		for (TrainingEntry entry : this.entries) {
			c += entry.getTries();
		}
		return c;
	}

	public ArrayList<TrainingEntry> getEntries(){
		return this.entries;
	}

	public int getCurrentEntry() {
		return this.currentEntry;
	}
	
	private void pickRandomEntry() {
		if (this.entries.size() > 0) {
			this.currentEntry = this.rand.nextInt(this.entries.size());
		} else {
			this.currentEntry = -1;
		}
	}
}

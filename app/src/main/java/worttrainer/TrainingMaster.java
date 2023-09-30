package worttrainer;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingMaster implements Serializable {
	private String name;
	private ArrayList<TrainingEntry> entries;
	private int currentEntry;
	private int completedEntries;
	@JsonIgnore
	private Random rand;

	public TrainingMaster(ArrayList<TrainingEntry> entries, String name) {
		this.rand = new Random();
		this.completedEntries = 0;
		this.name = name;
		
		if(this.name.equals("")){
			//special case - empty
			this.entries = new ArrayList<TrainingEntry>();
		}else{
			this.entries = entries;
		}
		
		this.pickRandomEntry();
	}

	@JsonCreator
	public TrainingMaster(
			@JsonProperty("entries") ArrayList<TrainingEntry> entries,
			@JsonProperty("currentEntry") int currentEntry,
			@JsonProperty("completedEntries") int completedEntries,
			@JsonProperty("name") String name) {
		this.entries = entries;
		this.rand = new Random();
		this.completedEntries = completedEntries;
		this.currentEntry = currentEntry;
		this.name = name;
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

	/**
	 * returns the image of the currently selected word
	 * 
	 * @return the url to the image
	 */
	@JsonIgnore
	public URL getImage() {
		if(currentEntry==-1)
			return null;
		return entries.get(currentEntry).getImage();
	}

	/**
	 * @return the amount of completed entries
	 */
	public int getCompletedEntries() {
		return this.completedEntries;
	}

	/**
	 * @return the amount of remaining entries
	 */
	@JsonIgnore
	public int getRemainingEntries() {
		return this.entries.size();
	}

	/**
	 * @return the amount of incorrect guesses
	 */
	@JsonIgnore
	public int getIncorrectEntries() {
		int c = 0;
		for (TrainingEntry entry : this.entries) {
			c += entry.getTries();
		}
		return c;
	}

	/**
	 * @return all remaining entries
	 */
	public ArrayList<TrainingEntry> getEntries(){
		return this.entries;
	}

	/**
	 * @return the currently selected entry's index
	 */
	public int getCurrentEntry() {
		return this.currentEntry;
	}

	/**
	 * @return the name of the current dataset
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * picks a random entry. internal function
	 */
	private void pickRandomEntry() {
		if (this.entries.size() > 0) {
			this.currentEntry = this.rand.nextInt(this.entries.size());
		} else {
			this.currentEntry = -1;
		}
	}
}

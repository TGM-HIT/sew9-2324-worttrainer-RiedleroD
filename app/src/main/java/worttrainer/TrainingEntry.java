package worttrainer;

import java.io.Serializable;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingEntry implements Serializable {
	private URL image;
	private String word;
	private int tries;

	@JsonCreator
	public TrainingEntry(
			@JsonProperty("image") URL image,
			@JsonProperty("word") String word,
			@JsonProperty("tries") int tries) {
		this.image = image;
		this.word = word.toLowerCase();
		this.tries = tries;
	}

	/**
	 * increments guesses, and checks for equality
	 * 
	 * @param word the word to guess
	 * @return whether the guess was correct
	 */
	public boolean guessWord(String word) {
		this.tries++;
		System.out.println(word + " vs. " + this.word);
		return word.toLowerCase().equals(this.word);
	}

	/**
	 * returns the image
	 * 
	 * @return the image
	 */
	public URL getImage() {
		return this.image;
	}

	/**
	 * returns the word
	 * 
	 * @return the word
	 */
	public String getWord() {
		return this.word;
	}

	/**
	 * returns the number of guesses
	 * 
	 * @return the number of guesses
	 */
	public int getTries() {
		return this.tries;
	}
}
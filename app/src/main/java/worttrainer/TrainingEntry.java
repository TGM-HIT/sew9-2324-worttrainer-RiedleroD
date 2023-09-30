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

	public boolean guessWord(String word) {
		this.tries++;
		System.out.println(word + " vs. " + this.word);
		return word.toLowerCase().equals(this.word);
	}

	public URL getImage() {
		return this.image;
	}

	public String getWord() {
		return this.word;
	}

	public int getTries() {
		return this.tries;
	}
}
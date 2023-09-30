package worttrainer;

import java.net.URL;

public class TrainingEntry {
	private URL image;
	private String word;
	private int tries;

	public TrainingEntry(URL image, String word) {
		this.image = image;
		this.word = word.toLowerCase();
		this.tries = 0;
	}

	public boolean guessWord(String word) {
		this.tries++;
		System.out.println(word+" vs. "+this.word);
		return word.toLowerCase().equals(this.word);
	}

	public URL getImage() {
		return this.image;
	}

	public int getTries() {
		return this.tries;
	}
}
package worttrainer;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class AppTest {
	@Test
	public void TrainingEntryModel() throws MalformedURLException {
		final String url = "https://riedler.wien/favicon.png";
		final String word = "riedler";
		final int tries = 12;

		TrainingEntry entry = new TrainingEntry(new URL(url), word, tries);

		assertEquals(url, entry.getImage().toString());
		assertEquals(word, entry.getWord());
		assertEquals(tries, entry.getTries());
	}

	@Test
	public void TrainingMasterModel() throws IOException {
		Map<String, WebReader> datasets = ConfigMaster.getSetting("datasets",
				new TypeReference<Map<String, WebReader>>() {
				});
		ArrayList<TrainingEntry> entries = datasets.get("Länderflaggen").parseSite();

		TrainingMaster tm = new TrainingMaster(entries, 3, 5, "gobbledygook");
		assertEquals(3, tm.getCurrentEntry());
		assertEquals(0, tm.getIncorrectEntries());
		assertEquals(5, tm.getCompletedEntries());
		assertEquals("gobbledygook", tm.getName());
		assertEquals(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Flag_of_Belgium_%28civil%29.svg/20px-Flag_of_Belgium_%28civil%29.svg.png",
				tm.getImage().toString());
		assertEquals(true,tm.guessWord("BelGieN"));
		assertEquals(0, tm.getIncorrectEntries());
		assertEquals(6, tm.getCompletedEntries());
		assertNotEquals(3, tm.getCurrentEntry());
		assertEquals(false,tm.guessWord("brahbrah"));
		assertEquals(1, tm.getIncorrectEntries());
		assertEquals(6, tm.getCompletedEntries());
		assertNotEquals(3, tm.getCurrentEntry());
	}

	@Test
	public void websiteParsing() throws IOException {
		WebReader wr = new WebReader(
				new URL("https://de.wikipedia.org/wiki/Liste_der_S%C3%A4ugetiere_%C3%96sterreichs"),
				".wikitable",
				":eq(0) img",
				":eq(1)");

		ArrayList<TrainingEntry> entries = wr.parseSite();
		assertEquals(9, entries.size());

		TrainingEntry entry = entries.get(0);
		assertEquals(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/EX_IUCN_3_1.svg/30px-EX_IUCN_3_1.svg.png",
				entry.getImage().toString());
		assertEquals("ausgestorben (engl. extinct)", entry.getWord());
	}
}

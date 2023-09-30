package worttrainer;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebReader implements Serializable {
	private URL url;
	private String cssSelector;
	private String imgCol;
	private String nameCol;

	@JsonCreator
	public WebReader(
			@JsonProperty("url") URL url,
			@JsonProperty("cssSelector") String cssSelector,
			@JsonProperty("imgCol") String imgCol,
			@JsonProperty("nameCol") String nameCol) {
		this.url = url;
		this.cssSelector = cssSelector;
		this.imgCol = imgCol;
		this.nameCol = nameCol;
	}

	public ArrayList<TrainingEntry> parseSite() {
		try {
			// getting document
			Document doc = Jsoup.connect(url.toString())
					.userAgent("Riedler says hi")
					.get();

			// extracting table, and cells containing the images and names
			Element table = doc.select(cssSelector).first();
			Elements imgElements = table.children().select("tbody>tr>td" + imgCol);
			Elements nameElements = table.children().select("tbody>tr>td" + nameCol);

			// checking if equal amounts of images and names were found
			if (imgElements.size() != nameElements.size()) {
				System.err.println(imgElements.size() + " != " + nameElements.size());
				System.err.print("\033[2m");
				int i = 0;
				boolean cont;
				do {
					cont = false;
					if (i < imgElements.size()) {
						System.err.println(imgElements.get(i));
						cont = true;
					}
					if (i < nameElements.size()) {
						System.err.println(nameElements.get(i));
						cont = true;
					}

					i++;
				} while (cont);
				System.err.print("\033[0m");
				throw new RuntimeException("Found different amounts of image cells and name cells");
			}

			ArrayList<TrainingEntry> result = new ArrayList<TrainingEntry>(imgElements.size());

			// iterating over cells & making TrainingEntries from them
			for (int i = 0; i < imgElements.size(); i++) {
				// getting src and name
				String imgSrc = imgElements.get(i).attr("src");
				String name = nameElements.get(i).text();

				// lots of url parsing
				String absImgSrc;
				if (imgSrc.startsWith("//")) {
					absImgSrc = "https:" + imgSrc;
				} else if (imgSrc.startsWith("/")) {
					absImgSrc = "https://" + url.getHost() + imgSrc;
				} else if (imgSrc.matches("^https?://.*$")) {
					absImgSrc = imgSrc;
				} else {
					throw new RuntimeException("Found unknown type of src url");
				}

				URL imgURL;

				try {
					imgURL = new URL(absImgSrc);
				} catch (MalformedURLException murle) {
					throw new RuntimeException(murle);
				}

				// saving the result :)
				result.add(new TrainingEntry(imgURL, name, 0));
			}

			return result;
		} catch (IOException e) {
			return null;
		}
	}
}

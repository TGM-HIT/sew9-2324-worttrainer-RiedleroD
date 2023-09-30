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

	/**
	 * Parses the website
	 * in hindsight, I should've split the "get website" and "parse website" parts into two.
	 * oh well.
	 * @return a list of training entries that correspond to the website and parsing options chosen
	 */
	public ArrayList<TrainingEntry> parseSite() {
		try {
			// getting document
			Document doc = Jsoup.connect(url.toString())
					.userAgent("Riedler says hi")
					.get();

			// extracting table, and cells containing the images and names
			Element table = doc.select(cssSelector).first();
			Elements rows = table.children().select("tbody>tr");

			ArrayList<TrainingEntry> result = new ArrayList<TrainingEntry>();

			// iterating over rows & making TrainingEntries from them
			for (int i = 0; i < rows.size(); i++) {
				//find image and name; only make entry if exactly one each exists
				Elements imgElements = rows.get(i).select(">td" + imgCol);
				Elements nameElements = rows.get(i).select(">td" + nameCol);
				if(imgElements.size()>1)
					System.err.println("found too many images in row "+i);
				if(nameElements.size()>1)
					System.err.println("found too many names in row "+i);
				if(imgElements.size()!=1 || nameElements.size()!=1)
					continue;
				
				// getting src and name
				String imgSrc = imgElements.first().attr("src");
				String name = nameElements.first().text();

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

package worttrainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class WebReader {
	public static ArrayList<TrainingEntry> parseSite(URL url, String cssSelector, String imgCol, String nameCol) {
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
				do{
					cont = false;
					if(i<imgElements.size()){
						System.err.println(imgElements.get(i));
						cont = true;
					}
					if(i<nameElements.size()){
						System.err.println(nameElements.get(i));
						cont = true;
					}
					
					i++;
				}while(cont);
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
				result.add(new TrainingEntry(imgURL, name));
			}

			return result;
		} catch (IOException e) {
			return null;
		}
	}
}

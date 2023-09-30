package worttrainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

import javax.swing.ImageIcon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.dirs.ProjectDirectories;

public class ConfigMaster {
	private static ObjectMapper mapper = new ObjectMapper();
	private final static ProjectDirectories projDirs =
		ProjectDirectories.from("wien", "riedler", "worttrainer");
	private final static File settingsFile = Paths.get(projDirs.configDir, "config.json").toFile();

	public static <T> void setSetting(String name, T obj) throws IOException {
		Map<String, T> map = getSettings();
		map.put(name, obj);
		mapper.writer().with(SerializationFeature.INDENT_OUTPUT).writeValue(settingsFile, map);
	}

	public static <T> T getSetting(String name) throws IOException {
		Map<String,T> map = getSettings();
		return map.get(name);
	}
	public static <T> T getSetting(String name, TypeReference<T> type) throws IOException {
		Map<String,T> map = getSettings();
		return mapper.convertValue(map.get(name),type);
	}

	private static <T> Map<String, T> getSettings() throws IOException {
		if (!settingsFile.exists()) {
			// copy settings from resources to config folder
			settingsFile.getParentFile().mkdirs();
			URL defaultSettingsURL = Thread.currentThread().getContextClassLoader().getResource("config.json");
			ReadableByteChannel rbc = Channels.newChannel(defaultSettingsURL.openStream());
			FileOutputStream fos = new FileOutputStream(settingsFile);
			fos.getChannel().transferFrom(rbc, 0, 1048576);// max 1MiB
			fos.flush();
			fos.close();
		}
		return mapper.readerFor(new TypeReference<Map<String, T>>() {}).readValue(settingsFile);
	}

	public static <T> void setCache(String name, T obj) throws IOException {
		File f = Paths.get(projDirs.cacheDir, name + ".json").toFile();
		f.getParentFile().mkdirs();
		mapper.writer().writeValue(f, obj);
	}
	
	public static <T> T getCache(String name) {
		return getCache(name, new TypeReference<T>(){});
	}

	public static <T> T getCache(String name, TypeReference<T> type) {
		File f = Paths.get(projDirs.cacheDir, name + ".json").toFile();
		try {
			return mapper.readerFor(type).readValue(f);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static ImageIcon cachedImage(URL url) throws IOException {
		byte[] hash = ByteBuffer.allocate(4).putInt(url.toString().hashCode()).array();
		String hashb64 = Base64.getEncoder().encodeToString(hash)
				.replace('/', '_')
				.replace('+', '-');
		File f = Paths.get(projDirs.cacheDir, "imgs", hashb64+".bin").toFile();
		
		if(!f.exists()){
			f.getParentFile().mkdirs();
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(f);
			fos.getChannel().transferFrom(rbc,0,1048576);//max 1MiB
			fos.flush();
			fos.close();
		}
		
		return new ImageIcon(f.toURI().toURL());
	}
}
package worttrainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
		Map<String, T> map;
		if(!settingsFile.exists()){
			settingsFile.getParentFile().mkdirs();
			map = new HashMap<String, T>();
		}else{
			map = mapper.readerFor(new TypeReference<Map<String, T>>() {})
				.readValue(settingsFile);
		}
		return map;
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
}
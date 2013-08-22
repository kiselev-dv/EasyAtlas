package ru.osm.dkiselev.atlasgenerator.configs;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.beust.jcommander.JCommander;

public class Config {

	public static final String COMMON_SECTION_NAME = "Common";

	private static Config instance = new Config();
	
	private Ini ini;
	
	private Config () {
		
	}

	public static synchronized void load(String[] args) throws ConfigurationException
	{
		instance = new Config();
		
		Options o = new Options();
		new JCommander(o, args);
		
		File f = new File(o.getConfigPath());
		
		if(!f.exists()) {
			throw new ConfigurationException("Config file " + o.getConfigPath() + " not found.");
		}
		
		instance.ini = new Ini();
		
		try {
			instance.ini.load(f);
		} catch (InvalidFileFormatException e) {
			throw new ConfigurationException("Failed to parse " + o.getConfigPath(), e);
		} catch (IOException e) {
			throw new ConfigurationException("Failed to read " + o.getConfigPath(), e);
		}
		
		String folder = f.getAbsolutePath();
		folder = folder.substring(0, folder.length() - f.getName().length());
		instance.ini.add(COMMON_SECTION_NAME, "CurentFolder", folder);
		
		if(o.getOverrides() != null)
		{
			for(Entry<String, String> override : o.getOverrides().entrySet()) {
				String iniPath = override.getKey();
				
				int pi = iniPath.indexOf('.');
				
				String section = COMMON_SECTION_NAME;
				String key = null;
				if(pi >= 0) {
					section = iniPath.substring(0, pi - 1);
					key = iniPath.substring(pi, iniPath.length() - 1);
				}
				else {
					key = iniPath.replace(".", "");
				}
				
				if(key != null) {
					instance.ini.add(section, key, override.getValue());
				}
			}
		}
	}
	
	public static String get(String section, String key) {
		return instance.ini.fetch(section, key);
	}

	public static <T extends Object> T get(String section, String key, Class<T> clazz) {
		return instance.ini.fetch(section, key, clazz);
	}
	
	public static Map<String, String> getMapByPrefix(String section, String prefix) {
		Section sec = instance.ini.get(section);
		Map<String, String> result = new HashMap<String, String>();
		for(String key : sec.keySet()) {
			if(key.startsWith(prefix)){
				result.put(key.replace(prefix, ""), sec.fetch(key));
			}
		}
		return result;
	}
	
	public static class ConfigurationException extends Exception {
		private static final long serialVersionUID = 7063757917780406201L;
		
		public ConfigurationException(String msg) {
			super(msg);
		}

		public ConfigurationException(String msg,
				Throwable cause) {
			super(msg, cause);
		}
	}
}

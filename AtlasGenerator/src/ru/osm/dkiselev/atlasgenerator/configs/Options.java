package ru.osm.dkiselev.atlasgenerator.configs;

import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

public class Options {
	
	@Parameter(names={"--config", "-c"}, description="Path to config file.", required = true)
	private String configPath;

	@DynamicParameter(names={"-o"}, description = "Override config file value. like -oCommon.Scale=500. " +
			"Common section name can be ommited like: -oScale=500")
	private Map<String, String> overrides;

	public String getConfigPath() {
		return configPath;
	}

	public Map<String, String> getOverrides() {
		return overrides;
	}

	public static void printUsage() {
		System.out.println("Usage: java -jar atlas-generator.jar --config <path to config file>");
	}
	
}

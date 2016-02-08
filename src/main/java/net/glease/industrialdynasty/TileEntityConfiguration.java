package net.glease.industrialdynasty;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

public class TileEntityConfiguration {
	private TileEntityConfiguration() {

	}

	public final static Map<String, Object[]> configs = Maps.newHashMap();

	public static void addConfig(String name, Object[] args) {
		if (configs.put(name, args) != null)
			throw new IllegalStateException(name + ":" + Arrays.toString(args));
	}

	public static Object[] getConfig(String name) {
		return configs.get(name);
	}

	public static void init() {
		addConfig("CoalGenerator", new Object[] {

		});
	}
}

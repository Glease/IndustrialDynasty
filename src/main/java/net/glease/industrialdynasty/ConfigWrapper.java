package net.glease.industrialdynasty;

import net.minecraftforge.common.config.Configuration;

public class ConfigWrapper {
	final Configuration config;
	public ConfigWrapper(Configuration config){
		this.config = config;
	}
	public static void init(){
		
	}
	/**
	 * 
	 * @return in seconds
	 */
	public int getMultiblockSaveInterval(){
		return 60;
	}
}

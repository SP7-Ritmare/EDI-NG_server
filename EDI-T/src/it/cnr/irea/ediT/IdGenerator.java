package it.cnr.irea.ediT;

import it.cnr.irea.ediT.exception.Settings;

import javax.inject.Singleton;

@Singleton
public class IdGenerator {
	private static IdGenerator instance = null;
	private static int currentId = -1;
	private IdGenerator() {
		instance = this;
		System.out.println("'" + Settings.get("generatedXMLs", "1000") + "'");
		currentId = Integer.parseInt(Settings.get("generatedXMLs", "1000"));
	}
	
	public static IdGenerator getInstance() {
		if ( instance == null ) {
			instance = new IdGenerator();
		}
		return instance;
	}
	
	public int getId() {
		currentId++;
		Settings.set("generatedXMLs", "" + currentId);
		return currentId;
	}
}

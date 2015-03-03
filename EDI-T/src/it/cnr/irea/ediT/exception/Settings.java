package it.cnr.irea.ediT.exception;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Settings {
	static ServletContext context = null;
	static Preferences preferences = Preferences.systemRoot();
	static Properties properties = new Properties();
	public static void load() {
		/*
		try {
			if ( context != null ) {
				properties.load(context.getResourceAsStream("/WEB-INF/edi.properties"));
			} else {
				properties.load(new FileInputStream("WEB-INF/edi.properties"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public static String get(String key) {
		return get(key, null);
	}	
	
	public static String get(String key, String defaultValue) {
		return preferences.get(key, defaultValue);
	}
	
	public static void set(String key, String value) {
		preferences.put(key, value);
	}
}

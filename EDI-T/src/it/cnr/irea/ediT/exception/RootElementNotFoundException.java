package it.cnr.irea.ediT.exception;

public class RootElementNotFoundException extends Exception {

	public RootElementNotFoundException(String rootPath) {
		super("root not found: " + rootPath);
	}
	
}

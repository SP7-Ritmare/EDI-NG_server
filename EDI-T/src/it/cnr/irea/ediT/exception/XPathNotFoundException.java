package it.cnr.irea.ediT.exception;

public class XPathNotFoundException extends Exception {
	public XPathNotFoundException(String path) {
		super(path);
	}
}

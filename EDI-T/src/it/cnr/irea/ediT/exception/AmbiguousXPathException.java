package it.cnr.irea.ediT.exception;

public class AmbiguousXPathException extends Exception {
	public AmbiguousXPathException(String path) {
		super(path);
	}
}

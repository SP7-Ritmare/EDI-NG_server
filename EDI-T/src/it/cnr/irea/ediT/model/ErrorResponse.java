package it.cnr.irea.ediT.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {
	public static ErrorResponse HOST_NOT_CONFIGURED = new ErrorResponse(1000, "Host name has not been configured");
	
	private int code;
	private String message;
	
	public ErrorResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}




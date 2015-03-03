package it.cnr.irea.ediT.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="response")
public class PostMetadataResponse {
	private int edimlId;
	private String ip;
	private String inputEDIML;
	private String generatedXml;
	private int responseCode;
	private String responseMessage;
	private List<String> messages;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getInputEDIML() {
		return inputEDIML;
	}
	public void setInputEDIML(String inputEDIML) {
		this.inputEDIML = inputEDIML;
	}
	public String getGeneratedXml() {
		return generatedXml;
	}
	public void setGeneratedXml(String generatedXml) {
		this.generatedXml = generatedXml;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public int getEdimlId() {
		return edimlId;
	}
	public void setEdimlId(int edimlId) {
		this.edimlId = edimlId;
	}
	
}

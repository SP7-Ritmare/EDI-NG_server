package it.cnr.irea.ediT.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MetadataListDTO {
	private String getItURI;
	private List<Metadata> metadata;
	
	public String getGetItURI() {
		return getItURI;
	}
	public void setGetItURI(String getItURI) {
		this.getItURI = getItURI;
	}
	public List<Metadata> getMetadata() {
		return metadata;
	}
	public void setMetadata(List<Metadata> metadata) {
		this.metadata = metadata;
	}
	
	
}

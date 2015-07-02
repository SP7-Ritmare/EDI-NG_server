package it.cnr.irea.ediT.model;

import it.cnr.irea.ediT.service.BaseService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@XmlRootElement
/*
 * 
 * @author: Fabio Pavesi
 * @version: 1.0
 * 
 */
public class Metadata {
	@Transient
	@Autowired
	public static BaseService service;
	
	// private String BASE_URL = service.getSetting("base_url", "https://sp7.irea.cnr.it/jboss/MDService/rest/ediml/");
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private URI uri;
	
	@Lob @Basic(fetch=FetchType.LAZY)
	private String input;
	@Lob @Basic(fetch=FetchType.LAZY)
	private String output;
	@Lob @Basic(fetch=FetchType.LAZY)
	private String template;
	
	private Date metadataCreated;
	private Date processStarted;
	private Date processEnded;
	@ManyToOne
	private StarterKit starterKit;
	
	public Metadata() {
		metadataCreated = new Date();
	}
	
	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public Date getMetadataCreated() {
		return metadataCreated;
	}

	public void setMetadataCreated(Date metadataCreated) {
		this.metadataCreated = metadataCreated;
	}

	public int getId() {
		try {
			this.uri = new URI(getBaseURL() + id);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return id;
	}
	public void setId(int id) {
		this.id = id;
		try {
			this.uri = new URI(getBaseURL() + id);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public Date getProcessStarted() {
		return processStarted;
	}
	public void setProcessStarted(Date processStarted) {
		this.processStarted = processStarted;
	}
	public Date getProcessEnded() {
		return processEnded;
	}
	public void setProcessEnded(Date processEnded) {
		this.processEnded = processEnded;
	}

	public static BaseService getService() {
		return service;
	}

	public static void setService(BaseService service) {
		Metadata.service = service;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@XmlElement(name="starter-kit")
	@JsonProperty("starter-kit")
	public StarterKit getStarterKit() {
		return starterKit;
	}

	public void setStarterKit(StarterKit starterKit) {
		this.starterKit = starterKit;
	}
	
	private String getBaseURL() {
		if ( service != null ) {
			return service.getSetting("base_url", "https://sp7.irea.cnr.it/jboss/MDService/") + "rest/ediml/";
		} else {
			return "no base url";
		}
	}
	
	
}

package it.cnr.irea.ediT.model;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

@XmlRootElement(name = "elements")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "generator", "ediVersion", "version", "timestamp", "baseDocument", "xsltChain", "numElements", "templateName", "fileId", "fileUri", "user", "queryString", "elements", "starterKitUri"} )
@JsonPropertyOrder({
	"generator", 
	"ediVersion",
	"starterKitUri", 
	"timestamp",
	"baseDocument",
	"xsltChain",
	"templateName", 
	"version", 
	"fileId", 
	"fileUri", 
	"user", 
	"queryString", 
	"numElements", 
	"elements" 
})
public class TemplateElementList {
	private List<TemplateElement> elements;
	private Date timestamp = new Date();
	private String queryString;
	private String templateName;
	private int fileId;
	private URI fileUri;
	private String user;
	private String version;
	private String starterKitUri;
	private String baseDocument;
	private List<XsltUrl> xsltChain;
	private String ediVersion;

	public TemplateElement findElement(String id) {
		for ( TemplateElement element : elements ) {
			if ( element.getId().equalsIgnoreCase(id) ) {
				return element;
			}
		}
		return null;
	}
	
	public TemplateItem findItem(String elementId, String itemId) {
		TemplateElement element = findElement(elementId);
		if ( element != null ) {
			for ( TemplateItem item : element.getItems() ) {
				if ( item.getId().equalsIgnoreCase(elementId + "_" + itemId) ) {
					return item;
				}
			}
		}
		return null;
	}
	
	public String getEdiVersion() {
		return ediVersion;
	}

	public void setEdiVersion(String ediVersion) {
		this.ediVersion = ediVersion;
	}

	public URI getFileUri() {
		return fileUri;
	}

	public void setFileUri(URI fileUri) {
		this.fileUri = fileUri;
	}

	@XmlElement(name = "version", defaultValue = "1.00")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
	

	public String getBaseDocument() {
		return baseDocument;
	}

	public void setBaseDocument(String baseDocument) {
		this.baseDocument = baseDocument;
	}

	@XmlElement(name = "template")
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@XmlElement(name = "fileId")
	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	@XmlElement(name = "user")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@XmlElement(name = "querystring")
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@XmlElement(name = "element")
	public List<TemplateElement> getElements() {
		return elements;
	}

	public void setElements(List<TemplateElement> elements) {
		this.elements = elements;
	}
	
	@XmlElement(name = "timestamp")
	public Date getTimestamp() {
		return timestamp;
	}
	
	@XmlElement(name = "num_elements")
	public int getNumElements() {
		if ( elements == null ) {
			return 0;
		} else {
			return elements.size();
		}
	}
	
	@XmlElement(name = "generator")
	public String getGenerator() {
		return "RITMARE Metadata Utilities Web Service";
	}

	public String getStarterKitUri() {
		return starterKitUri;
	}

	public void setStarterKitUri(String starterKitUri) {
		this.starterKitUri = starterKitUri;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<XsltUrl> getXsltChain() {
		return xsltChain;
	}

	public void setXsltChain(List<XsltUrl> xsltChain) {
		this.xsltChain = xsltChain;
	}

	
}

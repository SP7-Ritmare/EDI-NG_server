package it.cnr.irea.ediT.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

public class StarterKit {
	private String uri;
	private boolean allowed;
	private boolean canAuthorise = false;
	private String instituteUri;
	private String contactPersonUri;
	private String xmlMetadata;
	
	private List<WebServiceEndpoint> endpoints;
	
	public List<WebServiceEndpoint> getEndpoints() {
		return endpoints;
	}
	public void setEndpoints(List<WebServiceEndpoint> endpoints) {
		this.endpoints = endpoints;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allowed ? 1231 : 1237);
		result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
		result = prime * result + (canAuthorise ? 1231 : 1237);
		result = prime
				* result
				+ ((contactPersonUri == null) ? 0 : contactPersonUri.hashCode());
		result = prime * result
				+ ((instituteUri == null) ? 0 : instituteUri.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result
				+ ((xmlMetadata == null) ? 0 : xmlMetadata.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StarterKit other = (StarterKit) obj;
		if (allowed != other.allowed)
			return false;
		if (apiKey == null) {
			if (other.apiKey != null)
				return false;
		} else if (!apiKey.equals(other.apiKey))
			return false;
		if (canAuthorise != other.canAuthorise)
			return false;
		if (contactPersonUri == null) {
			if (other.contactPersonUri != null)
				return false;
		} else if (!contactPersonUri.equals(other.contactPersonUri))
			return false;
		if (instituteUri == null) {
			if (other.instituteUri != null)
				return false;
		} else if (!instituteUri.equals(other.instituteUri))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		if (xmlMetadata == null) {
			if (other.xmlMetadata != null)
				return false;
		} else if (!xmlMetadata.equals(other.xmlMetadata))
			return false;
		return true;
	}
	private String ip;
	private String apiKey;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public boolean isAllowed() {
		return allowed;
	}
	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}
	public String getInstituteUri() {
		return instituteUri;
	}
	public void setInstituteUri(String instituteUri) {
		this.instituteUri = instituteUri;
	}
	public String getContactPersonUri() {
		return contactPersonUri;
	}
	public void setContactPersonUri(String contactPersonUri) {
		this.contactPersonUri = contactPersonUri;
	}
	public String getXmlMetadata() {
		return xmlMetadata;
	}
	public void setXmlMetadata(String xmlMetadata) {
		this.xmlMetadata = xmlMetadata;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public boolean isCanAuthorise() {
		return canAuthorise;
	}
	public void setCanAuthorise(boolean canAuthorise) {
		this.canAuthorise = canAuthorise;
	}
	@Override
	public String toString() {
		return "StarterKit [uri=" + uri + ", allowed=" + allowed
				+ ", canAuthorise=" + canAuthorise + ", instituteUri="
				+ instituteUri + ", contactPersonUri=" + contactPersonUri
				+ ", xmlMetadata=" + xmlMetadata + ", ip=" + ip + ", apiKey="
				+ apiKey + "]";
	}
	
}

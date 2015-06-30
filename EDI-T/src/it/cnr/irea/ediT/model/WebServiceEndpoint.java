package it.cnr.irea.ediT.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@XmlRootElement(name = "endpoint")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries(value = {
		@NamedQuery(name = "listEndpoints", query = "SELECT e FROM WebServiceEndpoint e"),
		@NamedQuery(name = "deleteEndpointsForStarterKit", query = "DELETE FROM WebServiceEndpoint e WHERE e.starterKit = :sk")
})
public class WebServiceEndpoint {
	// resource urn
	@Id
	private String id;
	
	@ManyToOne
	private WebServiceType type;
	
	@ManyToOne
	@JsonIgnore
	private StarterKit starterKit;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isReachable ? 1231 : 1237);
		result = prime * result
				+ ((lastChecked == null) ? 0 : lastChecked.hashCode());
		result = prime * result
				+ ((starterKit == null) ? 0 : starterKit.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "WebServiceEndpoint [id=" + id + ", type=" + type
				+ ", starterKit=" + starterKit + ", url=" + url
				+ ", lastChecked=" + lastChecked + ", isReachable="
				+ isReachable + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebServiceEndpoint other = (WebServiceEndpoint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isReachable != other.isReachable)
			return false;
		if (lastChecked == null) {
			if (other.lastChecked != null)
				return false;
		} else if (!lastChecked.equals(other.lastChecked))
			return false;
		if (starterKit == null) {
			if (other.starterKit != null)
				return false;
		} else if (!starterKit.equals(other.starterKit))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	public StarterKit getStarterKit() {
		return starterKit;
	}
	public void setStarterKit(StarterKit starterKit) {
		this.starterKit = starterKit;
	}
	private String url;
	private Date lastChecked;
	private boolean isReachable;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public WebServiceType getType() {
		return type;
	}
	public void setType(WebServiceType type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getLastChecked() {
		return lastChecked;
	}
	public void setLastChecked(Date lastChecked) {
		this.lastChecked = lastChecked;
	}
	public boolean isReachable() {
		return isReachable;
	}
	public void setReachable(boolean isReachable) {
		this.isReachable = isReachable;
	}
	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in 
	 * the 200-399 range.
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
	 * the total timeout is effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
	 * given timeout, otherwise <code>false</code>.
	 */
	public static boolean ping(String url, int timeout) {
	    url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
	    if ( !url.startsWith("http://") ) {
	    	url = "http://" + url;
	    }
	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    } catch (IOException exception) {
	        return false;
	    }
	}
	public void checkReachable() {
		String testUrl = url + type.getQueryString();
		System.out.println("url to test: " + testUrl);
		isReachable = ping(testUrl, 100);
		lastChecked = new Date();
	}
}

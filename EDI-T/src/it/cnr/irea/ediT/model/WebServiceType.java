package it.cnr.irea.ediT.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="listWebServiceTypes", query="SELECT wst FROM WebServiceType wst")
})
public class WebServiceType {
	@EmbeddedId
	WebServiceTypePK id;

	private String queryString;
	
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((queryString == null) ? 0 : queryString.hashCode());
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
		WebServiceType other = (WebServiceType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (queryString == null) {
			if (other.queryString != null)
				return false;
		} else if (!queryString.equals(other.queryString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WebServiceType [id=" + id + ", queryString=" + queryString
				+ "]";
	}

	public WebServiceTypePK getId() {
		return id;
	}

	public void setId(WebServiceTypePK id) {
		this.id = id;
	}
	
	
}

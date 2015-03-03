package it.cnr.irea.ediT.model;

import java.io.Serializable;

public class WebServiceTypePK implements Serializable {
	private String name;
	private String version;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int hashCode() {
        return (int) name.hashCode() + version.hashCode();
    }
	
	public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof WebServiceTypePK)) return false;
        if (obj == null) return false;
        WebServiceTypePK pk = (WebServiceTypePK) obj;
        return pk.version == version && pk.name.equals(name);
    }
}

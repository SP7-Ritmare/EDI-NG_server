package it.cnr.irea.ediT.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "element")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TemplateElement {
	private String id;
	private String root;
	private String mandatory;
	private String label;
	private List<TemplateItem> items;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoot() {
		if ( root == null || root.equalsIgnoreCase("NA") ) {
			return null;
		}
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	public List<TemplateItem> getItems() {
		return items;
	}
	public void setItems(List<TemplateItem> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "TemplateElement [id=" + id + ", root=" + root + ", mandatory="
				+ mandatory + ", label=" + label + ", items=" + items + "]";
	}
	
	
}

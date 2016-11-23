package it.cnr.irea.ediT.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TemplateItem {
	
	/**
	 * matches template element's id + "_" + item's hasIndex
	 */
	private String id;
	/**
	 * parent element's id
	 */
	private String elementId;
	/**
	 * matches hasPath in element
	 */
	private String path;
	/**
	 * URI version of item's value
	 */
	private String codeValue;
	/**
	 * URN version of item's value
	 */
	private String urnValue;
	/**
	 * version to be used of item's value
	 */

	private String value;
	/**
	 * matches hasDatatype in template
	 */
	private String dataType;
	/**
	 * matches isFixed in template
	 */
	private String fixed;
	private String isLanguageNeutral;
	private String languageNeutral;
	/**
	 * the value version to be used is the URI
	 */
	private String useCode;
	/**
	 * the value version to be used is the URN
	 */
	private String useURN;
	private String outIndex;
	private String datasource;
	
	
	public String getUrnValue() {
		return urnValue;
	}
	public void setUrnValue(String urnValue) {
		this.urnValue = urnValue;
	}
	public String getUseCode() {
		return useCode;
	}
	public void setUseCode(String useCode) {
		this.useCode = useCode;
	}
	public String getUseURN() {
		return useURN;
	}
	public void setUseURN(String useURN) {
		this.useURN = useURN;
	}
	public String getOutIndex() {
		return outIndex;
	}
	public void setOutIndex(String outIndex) {
		this.outIndex = outIndex;
	}
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	@XmlElement(name = "datatype")
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getFixed() {
		return fixed;
	}
	public void setFixed(String fixed) {
		this.fixed = fixed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement(name = "element_id")
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public String getIsLanguageNeutral() {
		return isLanguageNeutral;
	}
	
	public void setIsLanguageNeutral(String isLanguageNeutral) {
		this.isLanguageNeutral = isLanguageNeutral;
	}
	
	public String getLanguageNeutral() {
		return languageNeutral;
	}
	public void setLanguageNeutral(String languageNeutral) {
		this.languageNeutral = languageNeutral;
	}
	public void dump() {
		System.out.println(this.toString());
	}
	@Override
	public String toString() {
		return "TemplateItem [id=" + id + ", elementId=" + elementId
				+ ", path=" + path + ", codeValue=" + codeValue + ", urnValue="
				+ urnValue + ", value=" + value + ", dataType=" + dataType
				+ ", fixed=" + fixed + ", isLanguageNeutral="
				+ isLanguageNeutral + ", languageNeutral=" + languageNeutral
				+ ", useCode=" + useCode + ", useURN=" + useURN + ", outIndex="
				+ outIndex + ", datasource=" + datasource + "]";
	}
	
}

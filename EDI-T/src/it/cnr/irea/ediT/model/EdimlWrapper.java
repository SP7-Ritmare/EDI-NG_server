package it.cnr.irea.ediT.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EdimlWrapper {
    int ediVersion = 2;
    String version = "1.00";
    String template;
    String templateDocument;
    String starterKit = "noKS";
    String starterKitUri;
    int fileId;
    String fileUri;
    String user;
    String baseDocument;
    String xsltChain;
    TemplateElementList element;

    public int getEdiVersion() {
        return ediVersion;
    }

    public void setEdiVersion(int ediVersion) {
        this.ediVersion = ediVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplateDocument() {
        return templateDocument;
    }

    public void setTemplateDocument(String templateDocument) {
        this.templateDocument = templateDocument;
    }

    public String getStarterKit() {
        return starterKit;
    }

    public void setStarterKit(String starterKit) {
        this.starterKit = starterKit;
    }

    public String getStarterKitUri() {
        return starterKitUri;
    }

    public void setStarterKitUri(String starterKitUri) {
        this.starterKitUri = starterKitUri;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBaseDocument() {
        return baseDocument;
    }

    public void setBaseDocument(String baseDocument) {
        this.baseDocument = baseDocument;
    }

    public String getXsltChain() {
        return xsltChain;
    }

    public void setXsltChain(String xsltChain) {
        this.xsltChain = xsltChain;
    }

    public TemplateElementList getElement() {
        return element;
    }

    public void setElement(TemplateElementList element) {
        this.element = element;
    }
}

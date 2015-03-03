package it.cnr.irea.ediT;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

public class UniversalNamespaceResolver implements NamespaceContext {
    // the delegate
    private Document sourceDocument = null;
    private DocumentFragment sourceFragment = null;

    /**
     * This constructor stores the source document to search the namespaces in
     * it.
     * 
     * @param document
     *            source document
     */
    public UniversalNamespaceResolver(Document document) {
        sourceDocument = document;
    }
    
    public UniversalNamespaceResolver(DocumentFragment document) {
        sourceFragment = document;
    }

    /**
     * The lookup for the namespace uris is delegated to the stored document.
     * 
     * @param prefix
     *            to search for
     * @return uri
     */
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
        	if ( sourceDocument != null ) {
        		return sourceDocument.lookupNamespaceURI(null);
        	} else {
        		return sourceFragment.lookupNamespaceURI(null);
        	}
        } else {
        	if ( sourceDocument != null ) {
        		return sourceDocument.lookupNamespaceURI(prefix);
        	} else {
        		return sourceFragment.lookupNamespaceURI(prefix);
        	}
        }
    }

    /**
     * This method is not needed in this context, but can be implemented in a
     * similar way.
     */
    public String getPrefix(String namespaceURI) {
    	if ( sourceDocument != null ) {
    		return sourceDocument.lookupPrefix(namespaceURI);
    	} else {
    		return sourceFragment.lookupPrefix(namespaceURI);
    	}
    }

    public Iterator getPrefixes(String namespaceURI) {
        // not implemented yet
        return null;
    }

}
package it.cnr.irea.ediT.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import it.cnr.irea.ediT.MetadataTemplateDocument;
import it.cnr.irea.ediT.model.Metadata;
import it.cnr.irea.ediT.model.Setting;
import it.cnr.irea.ediT.model.TemplateElementList;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

@Transactional
@Service
public class BaseService {
	@Autowired
	EntityManager em;

	public String getHostName() {
		return getSetting("base_url", null);		
	}
	
	@Transactional(readOnly = true)
	public String getSetting(String setting, String defaultValue) {
		Setting result = em.find(Setting.class, setting);
		return ( result != null ? result.getValue() : defaultValue );
	}

	public Metadata prepareMetadata() {
		Metadata md = new Metadata();
		em.persist(md);
		
		return md;
	}

	@Transactional(readOnly = false)
	public void saveMetadata(MetadataTemplateDocument document, String xml, TemplateElementList elementList) throws UnsupportedEncodingException {
	   Document xmlDoc = document.getOutput();
		Metadata metadata = null;
		   if ( elementList.getFileId() != null && !elementList.getFileId().trim().equalsIgnoreCase("") ) {
			   metadata = em.find(Metadata.class, Integer.parseInt(elementList.getFileId()));
		   } else {
			   metadata = new Metadata();
		   }
		   // metadata.setStarterKit(service.getSetting("starterKit", "noSK"));
		   metadata.setProcessStarted(new Date());
		   metadata.setInput(xml);
			
		   metadata.setOutput(new String(document.xmlUTF8String(xmlDoc), "utf-8"));
		   metadata.setProcessEnded(new Date());
		metadata = em.merge(metadata);
		metadata.getId();
		em.persist(metadata);
	}
	
	@Transactional(readOnly = true)
	public Metadata getMetadata(int id) {
		Metadata md = em.find(Metadata.class, id);
		return md;
	}
}

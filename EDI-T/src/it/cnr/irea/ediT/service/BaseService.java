package it.cnr.irea.ediT.service;

import it.cnr.irea.ediT.MetadataTemplateDocument;
import it.cnr.irea.ediT.model.Metadata;
import it.cnr.irea.ediT.model.MetadataListDTO;
import it.cnr.irea.ediT.model.ServiceResponse;
import it.cnr.irea.ediT.model.Setting;
import it.cnr.irea.ediT.model.TemplateElementList;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

@Transactional
@Service
public class BaseService {
	@Autowired
	EntityManager em;
	
	Logger log = Logger.getRootLogger();
	static int counter = 0;

	@Scheduled(fixedDelay = 1000)
	public void doSync() {
		if ( ++counter >= Integer.parseInt(getSetting("interval", "3600")) ) {
			counter = 0;
			log.info("syncing metadata");
			syncMetadata();
		}
	}
	

	public String getHostName() {
		return getSetting("starterKitURI", null);		
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
		   metadata.setSynchronised(false);
		metadata = em.merge(metadata);
		metadata.getId();
		em.persist(metadata);
	}
	
	@Transactional(readOnly = true)
	public Metadata getMetadata(int id) {
		Metadata md = em.find(Metadata.class, id);
		return md;
	}

	@Transactional
	public ServiceResponse syncMetadata() {
		final String uri = "http://localhost:8080/springrestexample/employees.xml";
	    MetadataListDTO dto = new MetadataListDTO();
	    dto.setGetItURI(getHostName());
	    dto.setMetadata(em.createNamedQuery("mdToSync").getResultList());
	    
	    if ( dto.getMetadata().size() > 0 ) {
		    RestTemplate restTemplate = new RestTemplate();
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
	
		    HttpEntity<MetadataListDTO> entity = new HttpEntity<MetadataListDTO>(dto, headers);
		    
		    // restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		    
	// 	    MetadataListDTO result = restTemplate.postForObject(getSetting("md_principal", "http://localhost:8081/registry/metadata"), dto, MetadataListDTO.class);
		    ServiceResponse result = restTemplate.postForObject(getSetting("md_principal", "http://localhost:8081/registry/metadata"), entity, ServiceResponse.class);
		    
		    if ( result.getCode() == 200 ) {
		    	for ( Metadata m : dto.getMetadata() ) {
		    		m.setSynchronised(true);
		    		em.merge(m);
		    	}
		    }
		    
		    return result;
	    } else {
	    	ServiceResponse result = new ServiceResponse();
	    	result.setCode(100);
	    	result.setMessage("Nothing to do");
	    	return result;
	    }
	}
}

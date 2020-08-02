package it.cnr.irea.ediT.service;

import it.cnr.irea.ediT.MetadataTemplateDocument;
import it.cnr.irea.ediT.model.*;
import lombok.extern.slf4j.Slf4j;
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

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

// import org.apache.log4j.Logger;

@Slf4j
@Transactional
@Service
public class BaseService {
	@Autowired
	EntityManager em;
	
	static int counter = 0;

	@Scheduled(fixedDelay = 1000)
	public void doSync() {
		if ( ++counter >= Integer.parseInt(getSetting("interval", "3600")) ) {
			counter = 0;
			// log.info("syncing metadata");
			syncMetadata();
		}
	}
	
	public Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();

	    return builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	public String getHostName() {
		return getSetting("starterKitURI", "noSK");		
	}
	
	@Transactional(readOnly = true)
	public String getSetting(String setting, String defaultValue) {
		Setting result = em.find(Setting.class, setting);
		if ( result == null ) {
			result = new Setting();
			result.setId(setting);
			result.setValue(defaultValue);
			em.persist(result);
		}
		return ( result != null ? result.getValue() : defaultValue );
	}

	public Metadata prepareMetadata() {
		Metadata md = new Metadata();
		em.persist(md);
		
		return md;
	}

	public boolean isTestServer(HttpServletRequest req) {
		String requestUrl = req.getHeader("X-Forwarded-Host");
		String testServerPattern = getSetting("testServerPattern", "edidemo.get-it.it");
		if ( requestUrl == null || requestUrl.contains(testServerPattern) ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public void saveMetadata(MetadataTemplateDocument document, String xml, TemplateElementList elementList, boolean isTest) throws UnsupportedEncodingException {
	   Document xmlDoc = document.getOutput();
		Metadata metadata = null;
		   if ( elementList.getFileId() != null && !elementList.getFileId().trim().equalsIgnoreCase("") ) {
			   metadata = em.find(Metadata.class, Integer.parseInt(elementList.getFileId()));
		   } else {
			   metadata = new Metadata();
		   }
		   // metadata.setStarterKit(service.getSetting("starterKit", "noSK"));
		   metadata.setStarterKit(elementList.getStarterKit());
		   metadata.setProcessStarted(new Date());
		   metadata.setInput(xml);
		   metadata.setTemplate(elementList.getTemplateDocument());
		   metadata.setOutput(new String(document.xmlUTF8String(xmlDoc), "utf-8"));
		   metadata.setProcessEnded(new Date());
		   metadata.setSynchronised(false);
		   metadata.setTest(isTest);
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
		    ServiceResponse result = null;
		    try {
			    result = restTemplate.postForObject(getSetting("md_principal", "http://localhost:8081/registry/metadata"), entity, ServiceResponse.class);
			    
			    if ( result.getCode() == 200 ) {
			    	for ( Metadata m : dto.getMetadata() ) {
			    		m.setSynchronised(true);
			    		em.merge(m);
			    	}
			    }
		    } catch(Exception e) {
		    	
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

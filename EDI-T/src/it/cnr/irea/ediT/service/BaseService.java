package it.cnr.irea.ediT.service;

import it.cnr.irea.ediT.model.Metadata;
import it.cnr.irea.ediT.model.Setting;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BaseService {
	@Autowired
	EntityManager em;

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

}

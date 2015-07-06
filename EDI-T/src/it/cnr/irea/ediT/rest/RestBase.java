package it.cnr.irea.ediT.rest;

import it.cnr.irea.ediT.IdGenerator;
import it.cnr.irea.ediT.MetadataTemplateDocument;
import it.cnr.irea.ediT.XsltService;
import it.cnr.irea.ediT.exception.HostNotConfiguredException;
import it.cnr.irea.ediT.exception.Settings;
import it.cnr.irea.ediT.model.ErrorResponse;
import it.cnr.irea.ediT.model.Metadata;
import it.cnr.irea.ediT.model.MetadataListDTO;
import it.cnr.irea.ediT.model.PostMetadataResponse;
import it.cnr.irea.ediT.model.ServiceResponse;
import it.cnr.irea.ediT.model.TemplateElement;
import it.cnr.irea.ediT.model.TemplateElementList;
import it.cnr.irea.ediT.model.TemplateItem;
import it.cnr.irea.ediT.model.XsltUrl;
import it.cnr.irea.ediT.service.BaseService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@RestController
public class RestBase extends CORSDecorator {
	static String personURI    = "http://somewhere/JohnSmith";
	static String fullName     = "John Smith";
	private static int id = 1;
	private XsltService xsltService = new XsltService();
	Logger log = Logger.getAnonymousLogger();
	private IdGenerator generator;
	@Autowired
	private BaseService service;
	@Autowired EntityManager em;
	
	public RestBase() {
		Settings.load();
		generator = IdGenerator.getInstance();
	}
	
	@PostConstruct
	public void postConstruct() {
		Metadata.service = service;
	}
	
	@ExceptionHandler(HostNotConfiguredException.class)
	@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
	public @ResponseBody ErrorResponse handleException(HostNotConfiguredException e) {
		log.severe("Host not configured");
	    return ErrorResponse.HOST_NOT_CONFIGURED;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "rest/ediml/requestId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Metadata getNewId(HttpServletRequest request, @RequestHeader("Host") String host) throws HostNotConfiguredException {
		if ( service.getHostName() == null ) {
			throw new HostNotConfiguredException();
		}
		log.info("requestId " + host);
		log.info("request: " + request.getRequestURL());
		Metadata md = service.prepareMetadata();
		log.info("" + md.getId());
		return md;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "rest/metadata", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	   public ResponseEntity<PostMetadataResponse> postMetadataNG(HttpServletRequest req, @RequestHeader(value = "X-Forwarded-For", required = false) String ip, @RequestBody String xml, @RequestHeader(value = "api_key", required = false) String apiKey) {
		   log.info("xml: " + xml);
		   PostMetadataResponse response = new PostMetadataResponse();
		   MetadataTemplateDocument document = null;
		   // String ip = null;
		   if ( ip == null ) {
			   ip = req.getRemoteAddr();
		   }
		   
		   log.info("ip: " + ip);
		   log.info("servlet path: " + req.getServletPath());
		   log.info("request url: " + req.getRequestURL());
		   response.setIp(ip);
		   response.setInputEDIML(xml);
		   
		    boolean isUnauthorised = false;
		    if ( isUnauthorised ) {
		    }
		    
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			try {
				dbFactory.setNamespaceAware(true);
				dbFactory.setValidating(false);
				dBuilder = dbFactory.newDocumentBuilder();
				Document input = dBuilder.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
				JAXBContext jc = JAXBContext.newInstance(TemplateElementList.class);
				//Create unmarshaller
				Unmarshaller um = jc.createUnmarshaller();
				//Unmarshal XML contents of the file myDoc.xml into your Java object instance.
				TemplateElementList elementList = (TemplateElementList) 
				um.unmarshal(new ByteArrayInputStream(xml.getBytes("utf-8")));
				
				for ( TemplateElement element : elementList.getElements() ) {
					for ( TemplateItem item : element.getItems() ) {
						if ( item.getDataType() == null || item.getDataType().trim().equalsIgnoreCase("") ) {
							log.info("Item " + item.getId() + " has no datatype");
						}
					}
				}

				byte[] baseDocument;
				
				if ( elementList.getBaseDocument() == null ) {
					baseDocument = null;
				} else {
					baseDocument = elementList.getBaseDocument().getBytes("utf-8");
				}
				document = new MetadataTemplateDocument(baseDocument, service);
			   document.addAll(elementList);
			//			   return Response.ok(xml).build();
			   Document xmlDoc = document.getOutput();
				
			   service.saveMetadata(document, xml, elementList);

			   document.saveTo("/tmp/last_md.xml");

			   try {
					service.syncMetadata();
					log.info("sync done");
				} catch(Exception e) {
					// do nothing
					log.severe(e.getMessage());
				}

			   response.setEdimlId(Integer.parseInt(elementList.getFileId()));
			   response.setResponseCode(200);
			   response.setMessages(document.getMessages());
			   
			   if ( elementList.getXsltChain() == null ) {
			   } else {
				   log.info("xsltChain is correctly defined in EDIML");
				   String xmlTemp = new String(document.xmlUTF8String(xmlDoc), "utf-8");
				   for ( XsltUrl xslt : elementList.getXsltChain() ) {
					   if ( xslt.getXslt() != null ) {
						   log.info("xmlTemp: ");
						   log.info(xmlTemp);

						   log.info("Transforming result with " + xslt.getXslt());
							byte[] result = xsltService.transform(xslt.getXslt(), xmlTemp , (HashMap<String, String>) null);
							xmlTemp = new String(result, "utf-8");
					   }
				   }
					response.setGeneratedXml(xmlTemp);
					
					System.out.println("Морски");
					return new ResponseEntity<PostMetadataResponse>(response, HttpStatus.OK);
			   }
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponseCode(500);
				response.setResponseMessage(e.getMessage());
				if ( document != null ) {
					response.setMessages(document.getMessages());
				}
			}
		   return new ResponseEntity<PostMetadataResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	   }

	   @RequestMapping(method = RequestMethod.OPTIONS, value = "rest/metadata", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
	   @ResponseBody
	   public ResponseEntity<PostMetadataResponse> optionsPostMetadata(String xml) {
		   return new ResponseEntity<PostMetadataResponse>(HttpStatus.OK);
	   }
	   
	   
		@RequestMapping(method = RequestMethod.GET, value = "/rest/ediml/{id}", produces = MediaType.APPLICATION_XML_VALUE)
		@ResponseBody
		public String getEDIMLXml(@PathVariable int id) {
			log.info("getEDIMLXml " + id);
			Metadata md = service.getMetadata(id);
			return md.getInput();
		}
		
		@RequestMapping(method = RequestMethod.GET, value = "/rest/ediml/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
		@ResponseBody
		public TemplateElementList getEDIML(@PathVariable int id) {
			log.info("getEDIML " + id);
			Metadata md = service.getMetadata(id);
			TemplateElementList elementList = null;
			String xml = md.getInput();
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				if ( xml != null ) {
					Document input = dBuilder.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
					JAXBContext jc = JAXBContext.newInstance(TemplateElementList.class);
					//Create unmarshaller
					Unmarshaller um = jc.createUnmarshaller();
					//Unmarshal XML contents of the file myDoc.xml into your Java object instance.
					elementList = (TemplateElementList)	um.unmarshal(new ByteArrayInputStream(xml.getBytes("utf-8")));
					for ( TemplateElement element : elementList.getElements() ) {
						for ( TemplateItem item : element.getItems() ) {
							if ( item.getDataType() == null || item.getDataType().trim().equalsIgnoreCase("") ) {
								log.info("Item " + item.getId() + " has no datatype");
							}
						}
					}
				} else {
					elementList = new TemplateElementList();
				}
				if ( elementList != null && (elementList.getFileId() == null || elementList.getFileId().trim().equalsIgnoreCase("")) ) {
					elementList.setFileId("" + md.getId());
					elementList.setFileUri(md.getUri());
					elementList.setStarterKitUri(service.getHostName());
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return elementList;
		}
		
		@RequestMapping(method = RequestMethod.GET, value = "rest/metadata/sync", produces = MediaType.APPLICATION_JSON_VALUE)
		public ServiceResponse syncMetadata() {
			return service.syncMetadata();
		}

}

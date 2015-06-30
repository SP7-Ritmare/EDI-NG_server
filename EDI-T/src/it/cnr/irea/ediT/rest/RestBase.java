package it.cnr.irea.ediT.rest;

import it.cnr.irea.ediT.IdGenerator;
import it.cnr.irea.ediT.XsltService;
import it.cnr.irea.ediT.exception.Settings;
import it.cnr.irea.ediT.model.Metadata;
import it.cnr.irea.ediT.service.BaseService;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
	
	public RestBase() {
		Settings.load();
		generator = IdGenerator.getInstance();
	}
	
	@PostConstruct
	public void postConstruct() {
		Metadata.service = service;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "rest/ediml/requestId", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Metadata getNewId(HttpServletRequest request, @RequestHeader("Host") String host) {
		log.info("requestId " + host);
		log.info("request: " + request.getRequestURL());
		Metadata md = service.prepareMetadata();
		log.info("" + md.getId());
		return md;
	}
	

/*	
	   @OPTIONS
	   @Path("rest/metadata")
	   @Consumes(value = {"application/xml", "text/xml"})
	   @Produces("application/xml")
	   public Response optionsPostMetadata(String xml) {
		   return build(Response.ok());
	   }
	   

	   @POST
	   @Path("rest/metadata")
	   @Consumes(value = {"application/xml", "text/xml"})
	   @Produces("application/json")
	   public Response postMetadataNG(@Context HttpServletRequest req, @HeaderParam("X-Forwarded-For") String ip, String xml, @HeaderParam("api_key") String apiKey) {
		   PostMetadataResponse response = new PostMetadataResponse();
		   MetadataTemplateDocument document = null;
		   
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
				document = new MetadataTemplateDocument(baseDocument);
			   document.addAll(elementList);
			//			   return Response.ok(xml).build();
			   Document xmlDoc = document.getOutput();
				
			   document.saveTo("/tmp/last_md.xml");
			 
			   response.setEdimlId(elementList.getFileId());
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
					return build(Response.ok(response));
			   }
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponseCode(500);
				response.setResponseMessage(e.getMessage());
				if ( document != null ) {
					response.setMessages(document.getMessages());
				}
			}
		   return build(Response.status(500).entity(response));
	   }
	   */
}

package it.cnr.irea.ediT.rest;

import it.cnr.irea.ediT.IdGenerator;
import it.cnr.irea.ediT.MetadataTemplateDocument;
import it.cnr.irea.ediT.XsltService;
import it.cnr.irea.ediT.exception.Settings;
import it.cnr.irea.ediT.model.PostMetadataResponse;
import it.cnr.irea.ediT.model.TemplateElement;
import it.cnr.irea.ediT.model.TemplateElementList;
import it.cnr.irea.ediT.model.TemplateItem;
import it.cnr.irea.ediT.model.XsltUrl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

@Path("/")
public class RestBase extends CORSDecorator {
	static String personURI    = "http://somewhere/JohnSmith";
	static String fullName     = "John Smith";
	private static int id = 1;
	private XsltService xsltService = new XsltService();
	Logger log = Logger.getAnonymousLogger();
	private IdGenerator generator;
	
	public RestBase() {
		Settings.load();
		generator = IdGenerator.getInstance();
	}
	
	@GET
	@Path("rest/ediml/requestId")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNewId() {
		return build(Response.ok("{ \"id\": " + (generator.getId()) + ", \"uri\": \"uri\" }"));
	}
	
	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_XML)
	public Response test() {
		OntModel onto = ModelFactory.createOntologyModel( 
                OntModelSpec.OWL_MEM, null );
		onto.read( "http://localhost:8081/test/art.n3", "N3" );
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
		reasoner = reasoner.bindSchema (onto);
		
		Model instances = ModelFactory.createDefaultModel();
		instances.read ("http://localhost:8081/test/art.n3", "N3");
		
		InfModel model = ModelFactory.createInfModel (reasoner, instances);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		model.write(out);
		return Response.ok(out.toByteArray()).build();
	}
	
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

	@GET
	@Path("sparql")
	@Produces(MediaType.APPLICATION_JSON)
	public Response sparql(@QueryParam("query") String queryString, @QueryParam("url") String url) {
		// String queryString = "SELECT * WHERE {?s ?p ?o}";
		/*
		queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX ns: <http://www.w3.org/2006/vcard/ns#> " +
          " SELECT DISTINCT ?c ?l " +
          " FROM <http://ritmare.it/rdfdata/project> " +
          " WHERE { " +
          "  ?c rdf:type foaf:Person . " +
          "  ?c ns:email ?l . " +
          "  ?c ns:org ?o . " +
          "  FILTER( REGEX( STR(?l), 'oggi', 'i') ) " +
          " } " +
          " ORDER BY ASC(?l)";
          */
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://10.0.0.20:8890/sparql", query);
        ResultSet results;
        results = queryExecution.execSelect();
        results = ResultSetFactory.copyResults(results) ;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(out, results);
		return Response.ok(out.toByteArray()).header("Access-Control-Allow-Origin", "*")
		        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		        .header("Access-Control-Allow-Credentials", "true")
		        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		        .header("Access-Control-Max-Age", "1209600").build();
	}
}

package it.cnr.irea.ediT.rest;

import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class CORSDecorator {
    private Logger log = Logger.getAnonymousLogger();

    // Match root-resources
	@OPTIONS
	@PermitAll
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response options() {
		log.info("Response options");
	    return Response.status(Response.Status.NO_CONTENT).header("Access-Control-Allow-Origin", "*")
		        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		        .header("Access-Control-Allow-Credentials", "true")
		        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		        .header("Access-Control-Max-Age", "1209600").build();
	}

	// Match sub-resources
	@OPTIONS
	@Path("{path:.*}")
	@PermitAll
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response optionsAll(@PathParam("path") String path) {
		log.info("Response optionsAll");
	    return Response.status(Response.Status.NO_CONTENT).header("Access-Control-Allow-Origin", "*")
		        .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		        .header("Access-Control-Allow-Credentials", "true")
		        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		        .header("Access-Control-Max-Age", "1209600").build();
	}
	
	public Response build(ResponseBuilder r) {
		return r.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}
}

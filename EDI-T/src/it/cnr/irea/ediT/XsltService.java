package it.cnr.irea.ediT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/*
 * 
 * Name: XsltService
 * Author: Fabio Pavesi
 * 
 * Description:
 * MetadataTemplates can have a tree of XsltTransformations connected, that get executed by postMetadata after composing the first instance of the metadatum.
 * XML gets passed over by each tree node to its children down to leaf nodes.
 * Leaf nodes return their results that will then bubble up by appending them to an ArrayList of byte[]
 * 
 */
public class XsltService {
	private Logger log = Logger.getAnonymousLogger();
	
	public void transform(InputStream xslt, InputStream xml, OutputStream outStream, HashMap<String, String> parameters) {
		TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
		StreamResult result = new StreamResult(outStream);
		try {
			StreamSource xsltSource = new StreamSource(xslt);
			
			Transformer transformer = tFactory.newTransformer(xsltSource);
			if ( parameters != null ) {
				for ( String parameter : parameters.keySet() ) {
					transformer.setParameter(parameter, parameters.get(parameter));
					log.info("setting XSLT parameter '" + parameter + "' to '" + parameters.get(parameter) + "'");
				}
			}
			transformer.transform(new StreamSource(xml), new StreamResult(outStream));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			System.err.println("contenuto dell'XSLT:");
			int c;
			try {
				xslt.reset();
				String temp = "";
				while ( (c = xslt.read()) >= 0 ) {
					temp += new Character((char) c);
				}
				System.err.println(temp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] transform(byte[] xslt, byte[] xml, HashMap<String, String> parameters) {
		// simply transform an xml by an xslt, with optional xslt parameter passing
		TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayInputStream xsltStrem = new ByteArrayInputStream(xslt);
		ByteArrayInputStream xmlStrem = new ByteArrayInputStream(xml);
		transform(xsltStrem, xmlStrem, outStream, parameters);
		
		return outStream.toByteArray();
	}
	
	/*
	 * @param xslt URL of XSLT file or XSLT text
	 * @param xml URL of XML file or XML text
	 * @param parameters the XSLT parameters
	 */
	public byte[] transform(String xslt, String xml, HashMap<String, String> parameters) {
		// simply transform an xml by an xslt, with optional xslt parameter passing
		TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		InputStream xsltStream = null;
		InputStream xmlStream = null;
		log.info("xslt: " + xslt);
		log.info("xml: " + xml);

		try {
			// if it qualifies as a URL open a URL stream
			URL xsltUrl = new URL(xslt);
			xsltStream = xsltUrl.openStream();
			log.info("xslt from " + xslt);
		} catch(MalformedURLException e) {
			// otherwise treat it as a byte array
			xsltStream = new ByteArrayInputStream(xslt.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			// if it qualifies as a URL open a URL stream
			URL xmlUrl = new URL(xml);
			xmlStream = xmlUrl.openStream();
		} catch(MalformedURLException e) {
			// otherwise treat it as a byte array
			log.warning("Malformed URL: " + xml);
			xmlStream = new ByteArrayInputStream(xml.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		transform(xsltStream, xmlStream, outStream, parameters);
		
		return outStream.toByteArray();

	}

}

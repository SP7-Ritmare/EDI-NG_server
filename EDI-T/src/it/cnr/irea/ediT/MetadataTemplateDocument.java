package it.cnr.irea.ediT;

import it.cnr.irea.ediT.exception.RootElementNotFoundException;
import it.cnr.irea.ediT.exception.Settings;
import it.cnr.irea.ediT.model.Setting;
import it.cnr.irea.ediT.model.TemplateElement;
import it.cnr.irea.ediT.model.TemplateElementList;
import it.cnr.irea.ediT.model.TemplateItem;
import it.cnr.irea.ediT.service.BaseService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.parboiled.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Controller
public class MetadataTemplateDocument {
	private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder dBuilder;
	private static XPathFactory xPathfactory = XPathFactory.newInstance();
	private List<String> messages = new ArrayList<String>();
	@Autowired
	BaseService service;
	String starterKit;
	
	/** The xpath. */
	private static XPath xpath = xPathfactory.newXPath();
	
	private HashMap<String, Integer> autoNumber = new HashMap<String, Integer>();
		
	Date today = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String todayString = sdf.format(today);
	private TemplateElementList elementList;
	// public static final String attributeRegex = "\\/(\\w*)\\[@*(\\w+){1}?=['\"](\\w+){1}?['\"]\\]";

	public static final String attributeRegex = "@*([^\\[ =]+)='([^']+)";
	public static final String nodeNameRegex = "([^\\[]*)";
	
	private static int exceptionCounter = 1;
		
	private Document output = null;

	public MetadataTemplateDocument() {
		super();
	}
	/*
	 * Creates a new Metadata Template Document
	 * @author Fabio Pavesi
	 * @param templateObject 	the template this instance is supposed to work with
	 * @param service 			the service to be used to persist results
	 */
	public MetadataTemplateDocument(/* MetadataTemplate templateObject, */ byte[] baseDocument, BaseService service) {
		this.service = service;
		// this.starterKit = service.getSetting("starterKit", "noSK");

		/*
		System.setProperty("jaxp.debug","1");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");	
		System.setProperty("javax.xml.xpath.XPathFactory:", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl");
		*/
		try {
			dbFactory.setNamespaceAware(true);
			dbFactory.setValidating(false);
			dBuilder = dbFactory.newDocumentBuilder();
			System.out.println("base document: \n" + new String(baseDocument, "utf-8"));
			output = dBuilder.parse(new ByteArrayInputStream(baseDocument));

			Node baseComment = output.createComment("XML generato da RITMARE - EDI - " + todayString);
			output.insertBefore(baseComment, output.getDocumentElement());
			// System.out.println("Base document: " + xmlString(output));
			
			/*
			NodeList elements = template.getElementsByTagName("element");
			for ( int j = 0; j < elements.getLength(); j++ ) {
				Node element = elements.item(j);
				NodeList items = ((Element) element).getElementsByTagName("item");
				Node item;
				String root = getTextValue((Element)element, "hasRoot");
				if ( !root.equalsIgnoreCase("NA") ) {
					String[] nodes = root.split("\\/");
					String newPath = "";
					for ( int i = 2; i < nodes.length; i++ ) {
						newPath += "/" + nodes[i];
					}
					// System.out.println("newPath: " + newPath);
					Element temp = createOrEditRootNode(output.getDocumentElement(), newPath, "");
				}
			}
			
			this.saveTo("/tmp/last_roots.xml");
			// System.out.println(xmlString(output));
			// System.exit(0);
			 * 
			 */
			/*
			for ( int j = 0; j < elements.getLength(); j++ ) {
				Node element = elements.item(j);
				String path = getTextValue((Element) element, "hasPath");
				if ( !path.contains("@") ) {
					addElement(output.getDocumentElement(), (Element)element);
				}
			}
			*/
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(xmlString(output));
		// saveTo(output, "data/out.xml");
	}

	public String generateURN(String userUri, String fileType) {
		return generateURN(Settings.get("organisation", "CNR"), Settings.get("organisation_unit", "IREA"), fileType);
	}
	
	public String generateURN(String organisationName, String organisationUnit, String fileType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		int progressivo = (int) Math.round(Math.random() * 100000.0);
		return ("urn:" + organisationName + ":" + organisationUnit + ":" + fileType + ":" + sdf.format(new Date()) + ":" + progressivo)
				.toLowerCase();	
	}


	/*
	 * 
	 * Returns an integer representing this element's instance number
	 * @author Fabio Pavesi
	 * @param	element		the element you need a number for
	 * 
	 */
	private int getAutonumberForElement(String element) {
		if ( autoNumber.containsKey(element) ) {
			Integer value = autoNumber.get(element);
			autoNumber.put(element, ++value);
			return value;
		} else {
			autoNumber.put(element, 1);
			return 1;
		}
	}
	
	public String xmlString(Node node) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			
			Writer stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			transformer.transform(source, streamResult);        
			String xmlString = stringWriter.toString();
			/*
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			*/
			return xmlString;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] xmlUTF8String(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		byte[] output = null;
		
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			output = writer.toByteArray()/*.replaceAll("\n|\r", "")*/;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	/*
	 * 
	 * Persists generated XML document to filename
	 * @author Fabio Pavesi
	 * 
	 */
	public String saveTo(String fileName) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		String output = null;
		Document doc = this.getOutput();
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileWriter writer = new FileWriter(fileName);
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			// output = writer.getBuffer().toString()/*.replaceAll("\n|\r", "")*/;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	public String getAttributeValue(Element ele, String tagName, String attributeName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			if (el.getFirstChild() != null) {
				textVal = el.getAttribute(attributeName);
			} else {
				textVal = null;
			}
		}

		return ("" + textVal).trim();
	}
	
	public String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			if (el.getFirstChild() != null) {
				textVal = el.getTextContent();
			} else {
				textVal = null;
			}
		}

		return (textVal == null ? "" : textVal).trim();
	}

	public Element getDirectChild(Element parent, String name) {
	    for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
	    	// System.out.println(child.getNodeName());
	        if(child instanceof Element && name.equals(child.getNodeName())) 
	        	return (Element) child;
	    }
	    return null;
	}
	
	public void addAll(TemplateElementList elementList) {
		this.elementList = elementList;
		this.starterKit = elementList.getStarterKit();
		System.out.println("Starter Kit: " + starterKit);
		
		for ( TemplateElement element : elementList.getElements() ) {
			try {
				addElement(element);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootElementNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathFactoryConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Find longest matching path.
	 *
	 * @param path the path
	 * @return the string
	 */
	public String findLongestMatchingPath(String path) {
		XPathFactory xPathfactory;
		XPath xpath = null;
		try {
			xPathfactory = XPathFactory.newInstance();
			xpath = xPathfactory.newXPath();
			UniversalNamespaceResolver resolver = new UniversalNamespaceResolver(output);
			xpath.setNamespaceContext(resolver);
			XPathExpression expr = xpath.compile(path);
			NodeList list = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
			if ( list.getLength() > 0 ) {
				return path;
			} else {
				int slashPosition = path.lastIndexOf("/");
				if ( slashPosition < 0 ) {
					return "";
				} else {
					path = path.substring(0, slashPosition);
					return findLongestMatchingPath(path);
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private void createRootPaths(String rootPath) {
		if ( !rootPath.equalsIgnoreCase("NA") ) {
			String[] nodes = rootPath.split("\\/");
			String newPath = "";
			for ( int i = 2; i < nodes.length; i++ ) {
				newPath += "/" + nodes[i];
			}
			// System.out.println("newPath: " + newPath);
			Element temp = createOrEditRootNode(output.getDocumentElement(), newPath, "");
		}
	}
	
	private boolean isItemAttribute(TemplateItem item) {
		if ( item.getPath() != null ) {
			return item.getPath().contains("/@");
		} else {
			return false;
		}
	}
	
	private boolean shouldItemBeSkipped(TemplateItem item) {
		if ( item.getValue() == null || item.getValue().trim().equalsIgnoreCase("") ) {
			return true;
		}
		
		String itemPath = item.getPath() != null ? item.getPath().trim() : null;
		
		if ( itemPath == null || itemPath.equalsIgnoreCase("") ) {
			System.err.println("itemPath nullo per item " + item.getId() + " in element " + item.getElementId());
			messages.add("NULL path for item " + item.getId() + " in element " + item.getElementId());
			return true;
		}	
		return false;
	}
	
	/**
	 * Adds the element.
	 *
	 * @param element the element
	 * @throws XPathExpressionException the x path expression exception
	 * @throws RootElementNotFoundException the root element not found exception
	 * @throws XPathFactoryConfigurationException the x path factory configuration exception
	 */
	public void addElement(TemplateElement element) throws XPathExpressionException, RootElementNotFoundException, XPathFactoryConfigurationException {
		Node appendPoint = null;
		Node last = null;
		
		if ( element == null || element.getItems() == null ) {
			return;
		}
		XPathExpression expr = null;
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
		UniversalNamespaceResolver resolver = new UniversalNamespaceResolver(output);
		xpath.setNamespaceContext(resolver);
		
		String rootPath = element.getRoot() != null ? element.getRoot().trim() : null;
		Node rootElement = null;
		if ( rootPath == null || rootPath.equalsIgnoreCase("") || rootPath.equalsIgnoreCase("NA") ) {
			System.err.println("root path '" + rootPath + "' not found: using / on " + element.toString());
			messages.add("Root path '" + rootPath + "' not found: using / on " + element.toString());
			rootElement = this.output.getDocumentElement();
		} else {
			createRootPaths(rootPath);
			
			expr = xpath.compile(rootPath);
			NodeList rootElements = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
			if ( rootElements.getLength() <= 0 ) {
				exceptionCounter++;
				saveTo("/tmp/temp_output." + (exceptionCounter) + ".xml");
				throw new RootElementNotFoundException("Exception #" + exceptionCounter + " - " + rootPath + " - length: " + rootElements.getLength());
			}
			rootElement = rootElements.item(0);
		}
		DocumentFragment fragment = null;
		fragment = output.createDocumentFragment();
		// System.out.println("\n\nELEMENT " + rootPath);
		for ( TemplateItem item : element.getItems() ) {
				appendPoint = (Node) fragment;
				
				boolean isAttribute = isItemAttribute(item);

				if ( shouldItemBeSkipped(item) ) {
					continue;
				}
				
				String itemPath = item.getPath() != null ? item.getPath().trim() : null;
				
				if ( rootPath == null || rootPath.equalsIgnoreCase("NA") ) {
					if ( itemPath.length() > 3 ) {
						itemPath = itemPath.substring(itemPath.indexOf("/", 2));
					}
				} else {
					itemPath = itemPath.replace(rootPath, "");
				}

				System.out.println("actual item path: " + itemPath);
				
				// Management of "isFixed = true" items
				if ( item.getFixed() != null && item.getFixed().equalsIgnoreCase("true") ) {
					if ( item.getDataType() != null ) {
						if ( item.getDataType().equalsIgnoreCase("ref") ) {
							boolean found = false;
							if ( item.getValue() != null ) {
								// First search items in the same element
								// messages.add("searching for item " + item.getId() + " referring to path " + item.getValue());
								String needle = item.getValue().trim(); 
								for ( TemplateItem baseItem : element.getItems() ) {
									String candidate = baseItem.getPath().trim();
									// messages.add("comparing to item " + baseItem.getId() + " with path " + baseItem.getPath());
									if ( needle != null & candidate != null && needle.equalsIgnoreCase(candidate) ) {
										found = true;
										if ( item.getIsLanguageNeutral() != null && item.getIsLanguageNeutral().trim().equalsIgnoreCase("true") ) {
											// System.out.println("setting language neutral value " + baseItem.getLanguageNeutral() + " for item " + item.getPath());
											item.setValue(baseItem.getLanguageNeutral());
										} else if ( item.getUseCode() != null && item.getUseCode().trim().equalsIgnoreCase("true") ) {
											item.setValue(baseItem.getCodeValue());
										} else if ( item.getUseURN() != null  && item.getUseURN().trim().equalsIgnoreCase("true") ) {
											item.setValue(baseItem.getUrnValue());
										} else {
											item.setValue(baseItem.getValue());
										}
									}
								}
								// Next, if unsuccessful, assume it's outside the scope of current element
								if ( !found ) {
									try {
										expr = xpath.compile(item.getValue());
										NodeList list = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
										if ( list.getLength() > 0 ) {
											item.setValue(list.item(0).getTextContent());
										} else {
											System.err.println(rootPath + " non trovato in " + xmlUTF8String(output));
										}
									} catch (XPathExpressionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (DOMException e) {
										e.printStackTrace();
									}
								}
							} else {
								System.err.println("Item " + item.getId() + " is a ref with no value");
								messages.add("Item " + item.getId() + " is a ref with no value");
							}
						} else if ( item.getDataType().equalsIgnoreCase("function") ){
							XPath localXPath = xPathfactory.newXPath();
							UniversalNamespaceResolver resolver2= new UniversalNamespaceResolver(output);
							localXPath.setNamespaceContext(resolver2);
							
							XPathExpression functionExpression = localXPath.compile(item.getValue());
							String result = (String) functionExpression.evaluate(fragment, XPathConstants.STRING);
							// System.out.println("XPath " + item.getValue() + " returns: '" + result + "'");
							item.setValue(result); 
						} else if ( item.getDataType().equalsIgnoreCase("date") ) {
							item.setValue(todayString);
						} else if ( item.getDataType().equalsIgnoreCase("URN") && item.getValue() != null && item.getValue().equalsIgnoreCase("auto") ) {
							item.setValue(generateURN(elementList.getUser(), elementList.getTemplateName()));
						} else if ( item.getDataType().equalsIgnoreCase("sensorID") ) {
							String disallowedCharacters = "[^A-Za-z0-9]";
							String baseSensorIdUrl = service.getSetting("baseSensorIdUrl", "http://sp7.irea.cnr.it/sensors/");
							String SENSOR_URI = baseSensorIdUrl + (starterKit != null ? starterKit : "noSK" ) + "/procedure/";
							String manufacturer = elementList.findItem("manuf_name", "3").getValue();
							String model = elementList.findItem("mod_number", "3").getValue();
							String serial = elementList.findItem("serial_num", "3").getValue();
							
							if ( manufacturer == null || manufacturer.trim().equalsIgnoreCase("") ) {
								manufacturer = "noManufacturerDeclared";
							} else {
								manufacturer = manufacturer.replaceAll(disallowedCharacters, "");
							}
							if ( model == null || model.trim().equalsIgnoreCase("") ) {
								model = "noModelDeclared";
							} else {
								model = model.replaceAll(disallowedCharacters, "");
							}
							if ( serial == null || serial.trim().equalsIgnoreCase("") ) {
								serial = "noSerialNumberDeclared";
							} else {
								serial = serial.replaceAll(disallowedCharacters, "");
							}
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
							int progressivo = (int) Math.round(Math.random() * 100000.0);
							String timeStamp = sdf.format(new Date());
							
							item.setValue(SENSOR_URI + manufacturer + "/" + model + "/" + serial +"/" + timeStamp + "_" + progressivo);
						} else if ( item.getDataType().equalsIgnoreCase("autonumber") ) {
							// autonumber prefixed by whatever is in the hasValue tag
							item.setValue("" + item.getValue() + getAutonumberForElement(element.getId().replaceAll("_XritX", "")));
						} 
						
					} else {
						messages.add("item " + item.getId() + " has no datatype");
					}
				}
				
				// This is when the item actually gets appended to XML
				if ( item.getIsLanguageNeutral() != null && item.getIsLanguageNeutral().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting language neutral value " + item.getLanguageNeutral() + " for item " + item.getId());
					item.setValue(item.getLanguageNeutral());
				} else if ( item.getUseCode() != null && item.getUseCode().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting URI value " + item.getCodeValue() + " for item " + item.getId() + " - " + item.getPath());
					item.setValue(item.getCodeValue());
				} else if ( item.getUseURN() != null  && item.getUseURN().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting URN value " + item.getUseURN() + " for item " + item.getId());
					item.setValue(item.getUrnValue());
				} else {
					// item.setValue(item.getValue());
				}


				String[] nodes = itemPath.split("\\/");
				last = appendPoint;
				Element temp = null;
				boolean found = false;
				String comment = "item: " + item.getId() + "\npath: " + item.getPath() + "\nvalue: " + item.getValue();
				for ( int j = 0; j < nodes.length; j++ ) {
					String node = nodes[j];
					boolean hasAttribute = node.contains("[");
					String tagName = node.replaceAll("\\[.*\\]", "");
					System.out.println("last: " + last.toString() + "\nj = " + j);
					
					if ( !nodes[j].trim().equalsIgnoreCase("") ) {
						// System.out.println("adding node: '" + nodes[j] + "'");
						if ( nodes[j].startsWith("@") ) {
							try {
								Attr attr = output.createAttribute(nodes[j].substring(1));
								attr.setNodeValue("" + item.getValue());
								try {
									if ( last instanceof DocumentFragment ) {
										System.err.println("last is a document fragment");
										System.out.println("this is the rebuilt path: '" + rootPath + "'");
										expr = xpath.compile(rootPath);
										NodeList list = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
										if ( list.getLength() > 0 ) {
											item.setValue(list.item(0).getTextContent());
											((Element)list.item(0)).setAttributeNode(attr);
										} else {
											System.err.println(StringUtils.join(nodes, "/", 0, j - 1) + " non trovato in " + xmlUTF8String(output));
										}

										DocumentFragment df = (DocumentFragment) last;
										System.err.println("local name: " + df.getLocalName());
										System.err.println("local name: " + df.getNodeName());
									} else {
										((Element) last).setAttributeNode(attr);
									}
								} catch (ClassCastException e) {
									System.err.println("root: " + rootPath);
									System.err.println("path: " + item.getPath());
									System.err.println("current node: " + nodes[j]);
									messages.add("root: " + rootPath + ", path: " + item.getPath() + ", current node: " + nodes[j] + " - " + e.getLocalizedMessage());
									e.printStackTrace();
									System.err.println(last.toString());
								}
							} catch ( DOMException e ) {
								e.printStackTrace();
								System.err.println("on element " + nodes[j].substring(1));
								messages.add("DOM Exception on " + nodes[j].substring(1) + " - " + e.getLocalizedMessage());
							}
							if ( item.getFixed() != null) {
								comment += "\nisFixed: " + item.getFixed();
//									last.setAttribute("ediFixed", item.getFixed());
							}
							if ( item.getDataType() != null ) {
								comment += "\nhasDatatype: " + item.getDataType();
//									last.setAttribute("ediDatatype", item.getDataType());
							}
//							last.appendChild(output.createComment(comment));
						} else {
							try {
								found = false;
								if ( last != null ) {
									NodeList children = last.getChildNodes();
									for ( int k = 0; k < children.getLength(); k++ ) {
										if ( children.item(k).getNodeName().equalsIgnoreCase(tagName) ) {
											temp = (Element) children.item(k);
											found = true;
										}
									}
								} else { 
									System.err.println("last is null on element " + element.getId() + " with root: " + rootPath + " - " + element.getRoot());
									System.err.println("item is: ");
									item.dump();
									System.err.println("current node is " + nodes[j]);
									messages.add("'last' is NULL on element " + element.getId() + " with root: " + rootPath + " - " + element.getRoot());
									messages.add("item is " + item.getId());
									return;
								}
								String attributeName = "";
								String attributeValue = "";
								if ( hasAttribute ) {
									// It's an attribute
									Pattern pattern = Pattern.compile(attributeRegex);
									Matcher matcher;

									tagName = tagName.replace("\"", "'");
									matcher = pattern.matcher(node);
									tagName = tagName.replaceAll("\\[.*\\]", "");
									attributeName = "";
									attributeValue="";
									if ( matcher.find() ) {
										attributeName = matcher.group(1);
										attributeValue = matcher.group(2);
										System.out.println("tag: " + tagName + " @" + attributeName + " = " + attributeValue);
									}
								}
								String prefix = "";
								String namespaceURI = "";
								
								if ( !found ) {
									try {
										if ( tagName.contains(":") ) {
											UniversalNamespaceResolver resolver1 = new UniversalNamespaceResolver(output);

											prefix = tagName.substring(0, tagName.indexOf(":"));
											// System.out.println("prefix: " + prefix);
											namespaceURI = resolver1.getNamespaceURI(prefix);
											try {
												temp = output.createElementNS(namespaceURI, tagName);
											} catch (DOMException e) {
												System.err.println(namespaceURI + " - " + tagName + " " + e.getLocalizedMessage());
												messages.add(namespaceURI + " - " + tagName + " " + e.getLocalizedMessage());
												throw e;
											}
										} else {
											try {
												temp = output.createElement(tagName);
											} catch (DOMException e) {
												item.dump();
												System.err.println(" - " + tagName + " " + e.getLocalizedMessage());
												messages.add(" - " + tagName + " " + e.getLocalizedMessage());
												throw e;
											}
										}
										last.appendChild(temp);
									} catch ( DOMException e ) {
										e.printStackTrace();
										System.err.println("on element " + item.toString() + " -> '" + nodes[j].trim() + "'");
										messages.add("DOM Exception " + e.getLocalizedMessage() + " on element " + item.toString() + " -> '" + nodes[j].trim() + "'");
										
									}
								}
								if ( hasAttribute ) {
									if ( attributeName.contains(":") ) {
										UniversalNamespaceResolver resolver1 = new UniversalNamespaceResolver(output);
										prefix = attributeName.substring(0, attributeName.indexOf(":"));
										namespaceURI = resolver1.getNamespaceURI(prefix);
										temp.setAttributeNS(namespaceURI, attributeName, attributeValue);
									} else {
										try {
											temp.setAttribute(attributeName, attributeValue);
										} catch(DOMException e) {
											e.printStackTrace();
											System.err.println("on " + attributeName + " - " + attributeValue);
										}
									}
								}

								last = temp;
							} catch (NullPointerException e) {
								e.printStackTrace();
								item.dump();
							}
						}
					}
				}
				if ( last != null ) {
					if ( !isAttribute && (last.getTextContent() == null || last.getTextContent().trim().equalsIgnoreCase("")) ) {
						last.setTextContent("" + item.getValue());
					}
					if ( item.getFixed() != null) {
						comment += "\nisFixed: " + item.getFixed();
//							last.setAttribute("ediFixed", item.getFixed());
					}
					if ( item.getDataType() != null ) {
						comment += "\nhasDatatype: " + item.getDataType();
//							last.setAttribute("ediDatatype", item.getDataType());
					}
					// last.appendChild(output.createComment(comment));
				}
				/*
				System.out.println("item: " + itemPath);
				System.out.println("append to: " + ( appendPoint == null ? "null" : appendPoint.toString()));
				*/

		}
		rootElement.appendChild(fragment);
		// System.out.println("XPath: " + xpath.getClass().getPackage().getName() + " - " + xpath.getClass().getCanonicalName());
		outputJaxpImplementationInfo();
	}
	
	/**
	 * Adds the element.
	 *
	 * @param element the element
	 * @throws XPathExpressionException the x path expression exception
	 * @throws RootElementNotFoundException the root element not found exception
	 * @throws XPathFactoryConfigurationException the x path factory configuration exception
	 */
	public void addElementOld(TemplateElement element) throws XPathExpressionException, RootElementNotFoundException, XPathFactoryConfigurationException {
		Node appendPoint = null;
		Node last = null;
		
		if ( element == null || element.getItems() == null ) {
			return;
		}
		
		NamespaceContext context = new NamespaceContextMap(
				"gsr", "http://www.isotc211.org/2005/gsr",
				"xlink", "http://www.w3.org/1999/xlink", 
				"gts", "http://www.isotc211.org/2005/gts", 
				"gmd", "http://www.isotc211.org/2005/gmd",
				"gco", "http://www.isotc211.org/2005/gco",
				"gml", "http://www.opengis.net/gml/3.2", 
				"xsi", "http://www.w3.org/2001/XMLSchema-instance", 
				"gss", "http://www.isotc211.org/2005/gss",
				"srv", "http://www.isotc211.org/2005/srv",
				"sml", "http://schemas.opengis.net/sensorML/1.0.1");
		XPathExpression expr = null;
//		xPathfactory = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
		UniversalNamespaceResolver resolver = new UniversalNamespaceResolver(output);
		xpath.setNamespaceContext(resolver);
		
		String rootPath = element.getRoot() != null ? element.getRoot().trim() : null;
		Node rootElement = null;
		if ( rootPath == null || rootPath.equalsIgnoreCase("") || rootPath.equalsIgnoreCase("NA") ) {
			System.err.println("root path '" + rootPath + "' not found: using / on " + element.toString());
			messages.add("Root path '" + rootPath + "' not found: using / on " + element.toString());
			rootElement = this.output.getDocumentElement();
		} else {
			// rootPath = rootPath.replaceAll("'", "\"");
			
			// $FP$ creazione posticipata delle root
			if ( !rootPath.equalsIgnoreCase("NA") ) {
				String[] nodes = rootPath.split("\\/");
				String newPath = "";
				for ( int i = 2; i < nodes.length; i++ ) {
					newPath += "/" + nodes[i];
				}
				// System.out.println("newPath: " + newPath);
				Element temp = createOrEditRootNode(output.getDocumentElement(), newPath, "");
			}
			// $FP$ end
			
			expr = xpath.compile(rootPath);
			NodeList rootElements = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
			if ( rootElements.getLength() <= 0 ) {
				exceptionCounter++;
				saveTo("/tmp/temp_output." + (exceptionCounter) + ".xml");
				throw new RootElementNotFoundException("Exception #" + exceptionCounter + " - " + rootPath + " - length: " + rootElements.getLength());
			}
			rootElement = rootElements.item(0);
		}
		DocumentFragment fragment = null;
		fragment = output.createDocumentFragment();
		// System.out.println("\n\nELEMENT " + rootPath);
		for ( TemplateItem item : element.getItems() ) {
				boolean isAttribute;
				
				if ( item.getPath() != null ) {
					isAttribute = item.getPath().contains("/@");
				} else {
					isAttribute = false;
				}
				
				// System.out.println("\n\nITEM " + item.getPath() + " -> '" + item.getValue() + "'");
				String itemPath = item.getPath() != null ? item.getPath().trim() : null;
				if ( item.getValue() == null || item.getValue().trim().equalsIgnoreCase("") ) {
					continue;
				}
				appendPoint = (Node) fragment;
				if ( itemPath == null || itemPath.trim().equalsIgnoreCase("") ) {
					System.err.println("itemPath nullo per item " + item.getId() + " in element " + item.getElementId());
					messages.add("NULL path for item " + item.getId() + " in element " + item.getElementId());
					continue;
				}
				if ( rootPath == null || rootPath.equalsIgnoreCase("NA") ) {
					if ( itemPath.length() > 3 ) {
						itemPath = itemPath.substring(itemPath.indexOf("/", 2));
					}
					// System.out.println("no root\nitemPath: " + itemPath + ", item.path: " + item.getPath() + ", appendPoint: " + appendPoint.toString());
				} else {
					itemPath = itemPath.replace(rootPath, "");
				}
				
				// Management of "isFixed = true" items
				if ( item.getFixed() != null && item.getFixed().equalsIgnoreCase("true") ) {
					if ( item.getDataType() != null ) {
						if ( item.getDataType().equalsIgnoreCase("ref") ) {
							boolean found = false;
							if ( item.getValue() != null ) {
								// First search items in the same element
								// messages.add("searching for item " + item.getId() + " referring to path " + item.getValue());
								String needle = item.getValue().trim(); 
								for ( TemplateItem baseItem : element.getItems() ) {
									String candidate = baseItem.getPath().trim();
									// messages.add("comparing to item " + baseItem.getId() + " with path " + baseItem.getPath());
									if ( needle != null & candidate != null && needle.equalsIgnoreCase(candidate) ) {
										found = true;
										if ( item.getIsLanguageNeutral() != null && item.getIsLanguageNeutral().trim().equalsIgnoreCase("true") ) {
											// System.out.println("setting language neutral value " + baseItem.getLanguageNeutral() + " for item " + item.getPath());
											item.setValue(baseItem.getLanguageNeutral());
										} else if ( item.getUseCode() != null && item.getUseCode().trim().equalsIgnoreCase("true") ) {
											item.setValue(baseItem.getCodeValue());
										} else if ( item.getUseURN() != null  && item.getUseURN().trim().equalsIgnoreCase("true") ) {
											item.setValue(baseItem.getUrnValue());
										} else {
											item.setValue(baseItem.getValue());
										}
									}
								}
								// Next, if unsuccessful, assume it's outside the scope of current element
								if ( !found ) {
									try {
										expr = xpath.compile(item.getValue());
										NodeList list = (NodeList) expr.evaluate(output, XPathConstants.NODESET);
										if ( list.getLength() > 0 ) {
											item.setValue(list.item(0).getTextContent());
										} else {
											System.err.println(rootPath + " non trovato in " + xmlUTF8String(output));
										}
									} catch (XPathExpressionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (DOMException e) {
										e.printStackTrace();
									}
								}
							} else {
								System.err.println("Item " + item.getId() + " is a ref with no value");
								messages.add("Item " + item.getId() + " is a ref with no value");
							}
						} else if ( item.getDataType().equalsIgnoreCase("function") ){
							XPath localXPath = xPathfactory.newXPath();
							NamespaceContext context2 = new NamespaceContextMap(
									"gsr", "http://www.isotc211.org/2005/gsr",
									"xlink", "http://www.w3.org/1999/xlink", 
									"gts", "http://www.isotc211.org/2005/gts", 
									"gmd", "http://www.isotc211.org/2005/gmd",
									"gco", "http://www.isotc211.org/2005/gco",
									"gml", "http://www.opengis.net/gml/3.2", 
									"xsi", "http://www.w3.org/2001/XMLSchema-instance", 
									"gss", "http://www.isotc211.org/2005/gss",
									"srv", "http://www.isotc211.org/2005/srv",
									"sml", "http://schemas.opengis.net/sensorML/1.0.1");
							UniversalNamespaceResolver resolver2= new UniversalNamespaceResolver(output);
							localXPath.setNamespaceContext(resolver2);
							
							XPathExpression functionExpression = localXPath.compile(item.getValue());
							String result = (String) functionExpression.evaluate(fragment, XPathConstants.STRING);
							// System.out.println("XPath " + item.getValue() + " returns: '" + result + "'");
							item.setValue(result); 
						} else if ( item.getDataType().equalsIgnoreCase("date") ) {
							item.setValue(todayString);
						} else if ( item.getDataType().equalsIgnoreCase("URN") && item.getValue() != null && item.getValue().equalsIgnoreCase("auto") ) {
							item.setValue(generateURN(elementList.getUser(), elementList.getTemplateName()));
						} else if ( item.getDataType().equalsIgnoreCase("sensorID") ) {
							String disallowedCharacters = "[^A-Za-z0-9]";
							String SENSOR_URI = "http://sp7.irea.cnr.it/sensors/" + (starterKit != null ? starterKit : "noSK" ) + "/procedure/";
							String manufacturer = elementList.findItem("manuf_name", "3").getValue();
							String model = elementList.findItem("mod_number", "3").getValue();
							String serial = elementList.findItem("serial_num", "3").getValue();
							
							if ( manufacturer == null || manufacturer.trim().equalsIgnoreCase("") ) {
								manufacturer = "noManufacturerDeclared";
							} else {
								manufacturer = manufacturer.replaceAll(disallowedCharacters, "");
							}
							if ( model == null || model.trim().equalsIgnoreCase("") ) {
								model = "noModelDeclared";
							} else {
								model = model.replaceAll(disallowedCharacters, "");
							}
							if ( serial == null || serial.trim().equalsIgnoreCase("") ) {
								serial = "noSerialNumberDeclared";
							} else {
								serial = serial.replaceAll(disallowedCharacters, "");
							}
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
							int progressivo = (int) Math.round(Math.random() * 100000.0);
							String timeStamp = sdf.format(new Date());
							
							item.setValue(SENSOR_URI + manufacturer + "/" + model + "/" + serial +"/" + timeStamp + "_" + progressivo);
						} else if ( item.getDataType().equalsIgnoreCase("autonumber") ) {
							// autonumber prefixed by whatever is in the hasValue tag
							item.setValue("" + item.getValue() + getAutonumberForElement(element.getId().replaceAll("_XritX", "")));
						} 
						
					} else {
						messages.add("item " + item.getId() + " has no datatype");
					}
				}
				
				// This is when the item actually gets appended to XML
				if ( item.getIsLanguageNeutral() != null && item.getIsLanguageNeutral().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting language neutral value " + item.getLanguageNeutral() + " for item " + item.getId());
					item.setValue(item.getLanguageNeutral());
				} else if ( item.getUseCode() != null && item.getUseCode().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting URI value " + item.getCodeValue() + " for item " + item.getId() + " - " + item.getPath());
					item.setValue(item.getCodeValue());
				} else if ( item.getUseURN() != null  && item.getUseURN().trim().equalsIgnoreCase("true") ) {
					System.out.println("setting URN value " + item.getUseURN() + " for item " + item.getId());
					item.setValue(item.getUrnValue());
				} else {
					// item.setValue(item.getValue());
				}


				String[] nodes = itemPath.split("\\/");
				last = appendPoint;
				Element temp = null;
				boolean found = false;
				String comment = "item: " + item.getId() + "\npath: " + item.getPath() + "\nvalue: " + item.getValue();
				for ( int j = 0; j < nodes.length; j++ ) {
					String node = nodes[j];
					boolean hasAttribute = node.contains("[");
					String tagName = node.replaceAll("\\[.*\\]", "");
					System.out.println("last: " + last.toString() + "\nj = " + j);
					
					if ( !nodes[j].trim().equalsIgnoreCase("") ) {
						// System.out.println("adding node: '" + nodes[j] + "'");
						if ( nodes[j].startsWith("@") ) {
							try {
								Attr attr = output.createAttribute(nodes[j].substring(1));
								attr.setNodeValue("" + item.getValue());
								try {
									if ( last instanceof DocumentFragment ) {
										System.err.println("last is a document fragment");
										DocumentFragment df = (DocumentFragment) last;
										System.err.println("local name: " + df.getLocalName());
										System.err.println("local name: " + df.getNodeName());
									} else {
										((Element) last).setAttributeNode(attr);
									}
								} catch (ClassCastException e) {
									System.err.println("root: " + rootPath);
									System.err.println("path: " + item.getPath());
									System.err.println("current node: " + nodes[j]);
									messages.add("root: " + rootPath + ", path: " + item.getPath() + ", current node: " + nodes[j] + " - " + e.getLocalizedMessage());
									e.printStackTrace();
									System.err.println(last.toString());
								}
							} catch ( DOMException e ) {
								e.printStackTrace();
								System.err.println("on element " + nodes[j].substring(1));
								messages.add("DOM Exception on " + nodes[j].substring(1) + " - " + e.getLocalizedMessage());
							}
							last.appendChild(output.createComment(comment));
							if ( item.getFixed() != null) {
								comment += "\nisFixed: " + item.getFixed();
//									last.setAttribute("ediFixed", item.getFixed());
							}
							if ( item.getDataType() != null ) {
								comment += "\nhasDatatype: " + item.getDataType();
//									last.setAttribute("ediDatatype", item.getDataType());
							}
						} else {
							try {
								found = false;
								if ( last != null ) {
									NodeList children = last.getChildNodes();
									for ( int k = 0; k < children.getLength(); k++ ) {
										if ( children.item(k).getNodeName().equalsIgnoreCase(tagName) ) {
											temp = (Element) children.item(k);
											found = true;
										}
									}
								} else { 
									System.err.println("last is null on element " + element.getId() + " with root: " + rootPath + " - " + element.getRoot());
									System.err.println("item is: ");
									item.dump();
									System.err.println("current node is " + nodes[j]);
									messages.add("'last' is NULL on element " + element.getId() + " with root: " + rootPath + " - " + element.getRoot());
									messages.add("item is " + item.getId());
									return;
								}
								String attributeName = "";
								String attributeValue = "";
								if ( hasAttribute ) {
									// It's an attribute
									Pattern pattern = Pattern.compile(attributeRegex);
									Matcher matcher;

									tagName = tagName.replace("\"", "'");
									matcher = pattern.matcher(node);
									tagName = tagName.replaceAll("\\[.*\\]", "");
									attributeName = "";
									attributeValue="";
									if ( matcher.find() ) {
										attributeName = matcher.group(1);
										attributeValue = matcher.group(2);
										System.out.println("tag: " + tagName + " @" + attributeName + " = " + attributeValue);
									}
								}
								String prefix = "";
								String namespaceURI = "";
								
								if ( !found ) {
									try {
										if ( tagName.contains(":") ) {
											UniversalNamespaceResolver resolver1 = new UniversalNamespaceResolver(output);

											prefix = tagName.substring(0, tagName.indexOf(":"));
											// System.out.println("prefix: " + prefix);
											namespaceURI = resolver1.getNamespaceURI(prefix);
											try {
												temp = output.createElementNS(namespaceURI, tagName);
											} catch (DOMException e) {
												System.err.println(namespaceURI + " - " + tagName + " " + e.getLocalizedMessage());
												messages.add(namespaceURI + " - " + tagName + " " + e.getLocalizedMessage());
												throw e;
											}
										} else {
											try {
												temp = output.createElement(tagName);
											} catch (DOMException e) {
												item.dump();
												System.err.println(" - " + tagName + " " + e.getLocalizedMessage());
												messages.add(" - " + tagName + " " + e.getLocalizedMessage());
												throw e;
											}
										}
										last.appendChild(temp);
									} catch ( DOMException e ) {
										e.printStackTrace();
										System.err.println("on element " + item.toString() + " -> '" + nodes[j].trim() + "'");
										messages.add("DOM Exception " + e.getLocalizedMessage() + " on element " + item.toString() + " -> '" + nodes[j].trim() + "'");
										
									}
								}
								if ( hasAttribute ) {
									if ( attributeName.contains(":") ) {
										UniversalNamespaceResolver resolver1 = new UniversalNamespaceResolver(output);
										prefix = attributeName.substring(0, attributeName.indexOf(":"));
										namespaceURI = resolver1.getNamespaceURI(prefix);
										temp.setAttributeNS(namespaceURI, attributeName, attributeValue);
									} else {
										try {
											temp.setAttribute(attributeName, attributeValue);
										} catch(DOMException e) {
											e.printStackTrace();
											System.err.println("on " + attributeName + " - " + attributeValue);
										}
									}
								}

								last = temp;
							} catch (NullPointerException e) {
								e.printStackTrace();
								item.dump();
							}
						}
					}
				}
				if ( last != null ) {
					if ( !isAttribute && (last.getTextContent() == null || last.getTextContent().trim().equalsIgnoreCase("")) ) {
						last.setTextContent("" + item.getValue());
					}
					if ( item.getFixed() != null) {
						comment += "\nisFixed: " + item.getFixed();
//							last.setAttribute("ediFixed", item.getFixed());
					}
					if ( item.getDataType() != null ) {
						comment += "\nhasDatatype: " + item.getDataType();
//							last.setAttribute("ediDatatype", item.getDataType());
					}
					// last.appendChild(output.createComment(comment));
				}
				/*
				System.out.println("item: " + itemPath);
				System.out.println("append to: " + ( appendPoint == null ? "null" : appendPoint.toString()));
				*/

		}
		rootElement.appendChild(fragment);
		// System.out.println("XPath: " + xpath.getClass().getPackage().getName() + " - " + xpath.getClass().getCanonicalName());
		outputJaxpImplementationInfo();
	}
	
	
	/**
	 * Creates the or edit root node.
	 *
	 * @param element the element
	 * @param path the path
	 * @param value the value
	 * @return the element
	 */
	public Element createOrEditRootNode(Element element, String path, String value) {
		Element temp = null;
		boolean found = false;
		boolean nodeCreated = false;
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		if ( path.length() <= 0 ) {
			return null;
		}
		String[] nodes = path.substring(1).split("\\/");
		Pattern pattern = Pattern.compile(attributeRegex);
		Matcher matcher;
		String node = "dummy";;
		String attribute = null;
		String val = null;

		System.out.println("createOrEditRootNode: " + path);
		
		XPathExpression expr;
		if ( nodes.length > 0 && !nodes[0].trim().equalsIgnoreCase("")) {
			// System.out.println("nodes[0] = " + nodes[0]);
			node = nodes[0];
			for ( int i = 0; element != null && element.getChildNodes() != null && i < element.getChildNodes().getLength(); i++ ) {
				if ( node.contains("@") ) {
					// It's an attribute
					node = node.replace("\"", "'");
					matcher = pattern.matcher(node);
					if ( matcher.find() ) {
						node = node.replaceAll("\\[.*\\]", "");
						attribute = matcher.group(1);
						val = matcher.group(2);
						System.out.println("createOrEditRootNode: @" + attribute + "='" + val + "'");
						if ( element.getChildNodes().item(i).getNodeName().equalsIgnoreCase(node) ) {
							if ( element.getChildNodes().item(i).getAttributes().getLength() > 0 ) {
								for ( int j = 0; j < element.getChildNodes().item(i).getAttributes().getLength(); j++ ) {
									Node foundAttribute = element.getChildNodes().item(i).getAttributes().getNamedItem(attribute);
									if ( foundAttribute != null && foundAttribute.getNodeName().equalsIgnoreCase(attribute) && foundAttribute.getNodeValue().equalsIgnoreCase(val) ) {
										System.out.println("createOrEditRootNode: @" + attribute + "='" + val + "' already in place");
										found = true;
										temp = (Element) element.getChildNodes().item(i);
										break;
									}
								}
							}
							// TODO: verificare 'sto break
							break; 
						}
					}
				} else {
					if ( element.getChildNodes().item(i).getNodeName().equalsIgnoreCase(nodes[0]) ) {
						found = true;
						temp = (Element) element.getChildNodes().item(i);
						break;
					}
				}
			}
			if ( !found ) {
				node = node.replace("\n", "");
				if ( node.contains("[") ) {
					// It's an attribute
					node = node.replace("\"", "'");
					matcher = pattern.matcher(node);
					node = node.replaceAll("\\[.*\\]", "");
					attribute = "";
					val="";
					if ( matcher.find() ) {
						attribute = matcher.group(1);
						val = matcher.group(2);
					}
				}
				System.out.println("creating node " + node + ", " + attribute + "=" + val);
				String prefix = "";
				String namespaceURI = "";
				if ( node.contains(":") ) {
					UniversalNamespaceResolver resolver1 = new UniversalNamespaceResolver(output);

					prefix = node.substring(0, node.indexOf(":"));
					// System.out.println("prefix: " + prefix);
					namespaceURI = resolver1.getNamespaceURI(prefix);
					try {
						temp = output.createElementNS(namespaceURI, node);
					} catch (DOMException e) {
						System.err.println(namespaceURI + " - " + node + " " + e.getLocalizedMessage());
						throw e;
					}
				} else {
					try {
						temp = output.createElement(node);
					} catch (DOMException e) {
						System.err.println(" - " + node + " " + e.getLocalizedMessage());
						throw e;
					}
				}
				if ( attribute != null && val != null ) {
					temp.setAttribute(attribute, val);
				}
				element.appendChild(temp);
			}
			if ( nodes.length > 1 ) {
				String newPath = "";
				for ( int i = 1; i < nodes.length; i++ ) {
					newPath += "/" + nodes[i];
				}
				createOrEditRootNode(temp, newPath, value);
			} else {
				// temp.setTextContent(value);
			}
		} else {
			return null;
		}
		// System.err.println();
		// System.err.println(xmlString(temp));
		return temp;
	}

	/**
	 * Gets the output XML document.
	 *
	 * @return the output XML document
	 */
	public Document getOutput() {
		return output;
	}

	public void setOutput(Document output) {
		this.output = output;
	}

	public static void outputJaxpImplementationInfo() {
		/*
	    Logger logger = Logger.getLogger(MetadataTemplateDocument.class);
		logger.debug(getImplementationInfo("DocumentBuilderFactory", DocumentBuilderFactory.newInstance().getClass()));
	    logger.debug(getImplementationInfo("XPathFactory", XPathFactory.newInstance().getClass()));
	    logger.debug(getImplementationInfo("TransformerFactory", TransformerFactory.newInstance().getClass()));
	    logger.debug(getImplementationInfo("SAXParserFactory", SAXParserFactory.newInstance().getClass()));
	    */
	}

	/**
	 * Get the JAXB implementation information for a particular class
	 * @param componentName
	 * @param componentClass
	 * @return
	 */
	private static String getImplementationInfo(String componentName, Class componentClass) {
	    CodeSource source = componentClass.getProtectionDomain().getCodeSource();
	    return MessageFormat.format(
	            "{0} implementation: {1} loaded from: {2}",
	            componentName,
	            componentClass.getName(),
	            source == null ? "Java Runtime" : source.getLocation());
	}
	
	public List<String> getMessages() {
		return messages;
	}
}

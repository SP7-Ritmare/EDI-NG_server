package it.cnr.irea.ediT;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathElement {
	public static final String attributeRegex = "@*([^\\[ =]+)='([^']+)";
	public static final String nodeNameRegex = "([^\\[]*)";

	private String name;
	private boolean isAttribute;
	private String value;
	
	public PathElement(String name) {
		this.name = name;
		this.isAttribute = false;
		this.value = null;
	}
	
	public PathElement(String name, String value) {
		this.name = name;
		this.value = value;
		this.isAttribute = true;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isAttribute() {
		return isAttribute;
	}
	public void setAttribute(boolean isAttribute) {
		this.isAttribute = isAttribute;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "PathElement [name=" + name + ", isAttribute=" + isAttribute + ", value=" + value + "]";
	}

	public static ArrayList<PathElement> parsePath(String path) {
		Pattern pattern = Pattern.compile(attributeRegex);
		Matcher matcher;
		String attribute;
		String value;
		
		ArrayList<PathElement> res = new ArrayList<>();
		String[] nodes = path.split("\\/");
		for ( String n : nodes ) {
			if ( !n.equalsIgnoreCase("") ) {
				if ( n.contains("@") ) {
					matcher = pattern.matcher(n);
					if ( matcher.find() ) {
						attribute = matcher.group(1);
						value = matcher.group(2);
						String node = n.replaceAll("\\[.*\\]", "");
						res.add(new PathElement(node));
						res.add(new PathElement(attribute, value));
	/*					System.out.println("node " + node);
						System.out.println("attribute " + attribute + " = " + value);
	*/				}
				} else {
					res.add(new PathElement(n));
/*
					System.out.println("'" + n + "'");
*/
				}
			}
		}
		return res;
	}
	public static String toString(ArrayList<PathElement> pathElements) {
		String temp = "";
		for ( int i = 0; i < pathElements.size(); i++ ) {
			PathElement p = pathElements.get(i);
			if ( !p.isAttribute ) {
				temp += p.getName();
				if ( i < pathElements.size() - 1 ) {
					// peek whether next item is an attribute
					if ( !pathElements.get(i + 1).isAttribute ) {
						temp += "/";
					}
				}
			} else {
				if ( i != 0 ) {
					// can't start with an attribute, now, can we?
					temp += "[@" + p.getName() + "='" + p.getValue() + "']/";
				}
			}
		}
		return temp;
	}
	
	public static boolean isNextAnAttribute(ArrayList<PathElement> pathElements, int i) {
		if ( i < pathElements.size() - 1 ) {
			if ( pathElements.get(i + 1).isAttribute ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLastNode(ArrayList<PathElement> pathElements, int i) {
		if ( i == pathElements.size() - 1 ) {
			return true;
		}
		if ( i == pathElements.size() - 2 && pathElements.get(i + 1).isAttribute ) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		// String test = "/sml:PhysicalSystem/sml:characteristics[@name='generalProperties']/sml:CharacteristicList/sml:characteristic[@name='platform_physical_properties']/swe:DataRecord[@definition='platform_physical_properties']/swe:field[@name='Dimensions_and_General_Facts']/swe:DataRecord";
		String test = "sml:CharacteristicList/sml:characteristic[@name='platform_physical_properties']/swe:DataRecord[@definition='platform_physical_properties']/swe:field/swe:DataRecord";
		ArrayList<PathElement> paths = parsePath(test);
		for ( PathElement p  : paths ) {
			System.out.println(p);
		}
		paths.remove(0);
		paths.remove(0);
		System.out.println();
		System.out.println(PathElement.toString(paths));
	}

}

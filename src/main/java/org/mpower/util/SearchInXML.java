package org.mpower.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class SearchInXML {
	
	public SearchInXML() {
	}
	
	public String searchValueFromXML(String nodePath, Document xmlDocument) {
		String nodeValue = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			nodeValue = xPath.compile(nodePath).evaluate(xmlDocument);
		}
		catch (Exception e) {
			System.out.println("exception:" + e.getMessage());
		}
		System.out.println("Finding nodePath: " + nodePath + " ,nodeValue: " + nodeValue);
		return nodeValue;
		
	}
	
}

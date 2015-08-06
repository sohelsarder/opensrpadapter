package org.mpower.form;

import org.w3c.dom.Document;

public class XMLData {
	private static Document xmlDocument = null;

	public static Document getXmlDocument() {
		return xmlDocument;
	}

	public static void setXmlDocument(Document xmlDocument) {
		XMLData.xmlDocument = xmlDocument;
	}

}

package org.mpower.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SubForm {

	public String name;
	public String bind_type;
	public String default_bind_path;
	public List<SubFormField> fields = new ArrayList<SubFormField>();
	public List<HashMap<String, String>> instances = new ArrayList<HashMap<String, String>>();
	public void buildSubFormFields() {

		for (SubFormField field : fields) {
			// condition
			if (field.source == null) {
				field.source = this.bind_type + "." + field.name;
			}
			if (field.bind == null) {

			}

		}

	}

	public void buildSubFormInstanceFields(String subFormDefaultBindPath) {

		try {
			Document xmlDocument = XMLData.getXmlDocument();
			NodeList nodeList = xmlDocument
					.getElementsByTagName(subFormDefaultBindPath);
			for (int i = 0; i < nodeList.getLength(); i++) {
				HashMap<String, String> hm = new HashMap<>();
				NodeList childNodeList = nodeList.item(i).getChildNodes();
				for (int j = 0; j < childNodeList.getLength(); j++) {
					if (childNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						hm.put(childNodeList.item(j).getNodeName(),
								childNodeList.item(j).getTextContent());
/*						System.out.println("Name : "
								+ childNodeList.item(j).getNodeName());
						System.out.println("Values : "
								+ childNodeList.item(j).getTextContent());*/
					}

				}
				instances.add(hm);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}

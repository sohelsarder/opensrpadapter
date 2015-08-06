package org.mpower.form;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.mpower.http.HTTPAgent;

public class Form {
	public String bind_type;
	public String default_bind_path;
	public List<Field> fields = new ArrayList<Field>();
	public List<SubForm> sub_forms = new ArrayList<SubForm>();

	public void buildFields() {

		for (Field field : fields) {
			// condition
			if (field.source == null)
				field.source = this.bind_type + "." + field.name;

			if (field.bind != null) {
				String nodeValue = searchInXML("/data/"
						+ nodeNameConverter(extractLastNode(field.bind)));
				field.value = nodeValue;
			} else {
				String entityID = checkExistingClients(getBRNList("/data/woman/FWWOMBID"));
				if (entityID != null) {
					field.value = entityID;
					SubmissionBuilder.entityID = entityID;
				} else {
					String nodeValue = searchInXML("/data/meta/instanceID");
					field.value = nodeValue;
					SubmissionBuilder.entityID = nodeValue;
				}
			}

		}

	}

	public void buildSubForm() {
		for (SubForm subForm : sub_forms) {
			subForm.buildSubFormFields();
			subForm.buildSubFormInstanceFields(extractLastNode(subForm.default_bind_path));
		}
	}

	private static String nodeNameConverter(String openSRPNodeName) {
		String jivitaNodeName = "";
		if ("start".equalsIgnoreCase(openSRPNodeName))
			jivitaNodeName = "i_start_time";
		else if ("end".equalsIgnoreCase(openSRPNodeName))
			jivitaNodeName = "i_end_time";
		else {
			jivitaNodeName = openSRPNodeName;
		}
		return jivitaNodeName;
	}

	private static String checkExistingClients(List<String> BRN) {
		String existingEntityID = null;
		HTTPAgent httpAgent = new HTTPAgent();
		System.out
				.println("brnList: "
						+ httpAgent
								.fetch("http://192.168.21.195:9979/entity-id?brn-id=65321111111111112,65321111111111112")
								.payload());
		return existingEntityID;
	}

	private static String extractLastNode(String bindNode) {
		
		int lastIndexOfSlash = bindNode.lastIndexOf("/");
		String retVal = bindNode.substring(lastIndexOfSlash + 1,
				bindNode.length());
		return retVal;
	}

	private String searchInXML(String nodePath) {

		String nodeValue = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			nodeValue = xPath.compile(nodePath).evaluate(
					XMLData.getXmlDocument());
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Finding nodePath: " + nodePath + " ,nodeValue: "
				+ nodeValue);
		return nodeValue;

	}

	private List<String> getBRNList(String nodePath) {
		
		List<String> brnList = new ArrayList<>();
		String nodeValue = "";

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			nodeValue = xPath.compile(nodePath).evaluate(
					XMLData.getXmlDocument());
		} catch (Exception e) {
			// TODO: handle exception
		}
		brnList.add(nodeValue);
		return brnList;

	}

}

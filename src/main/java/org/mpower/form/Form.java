package org.mpower.form;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.mpower.http.HTTPAgent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Form {
	public String bind_type;
	public String default_bind_path;
	private static final String SUBMISSION_BRNID = "http://192.168.21.218:9979/entity-id?brn-id=";
	public List<Field> fields = new ArrayList<Field>();
	public List<SubForm> sub_forms = new ArrayList<SubForm>();

	public void buildFields() {

		for (Field field : fields) {
			// condition
			System.out.println(field.name + " --)");
			if (field.source == null)
				field.source = this.bind_type + "." + field.name;

			if (field.bind != null) {
				String nodeValue = searchInXML(field.read);
				field.value = nodeValue;
			} else {
				//String entityID = checkExistingClients(getBRNList("/data/woman/FWWOMBID"));
				/*List<String> list = buildFormInstanceFields("/data/census/FDWOMBID");
				String entityID = null;
				if (!list.isEmpty()) {
				entityID = checkExistingClients(list);	
				}*/
				String Brn = searchInXML("/data/woman/FDWOMBID");
				List<String> list = new ArrayList<String>();
				list.add(Brn);
				String entityID = checkExistingClients(list);
				System.out.println("..service enityID: " + entityID);
				if(entityID != null)
				{
					if (entityID.equalsIgnoreCase("null")) {
						String nodeValue = searchInXML("/data/meta/instanceID");
						System.out.println("meta/instanceID- " + nodeValue);
						field.value = nodeValue;
						SubmissionBuilder.entityID = nodeValue;
						System.out.println("service enityID:" + SubmissionBuilder.entityID);
						
					} 
					else {
						field.value = entityID;
						SubmissionBuilder.entityID = entityID;					
					}
				}	
				else
				{
					entityID = UUID.randomUUID().toString();
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

	private static String checkExistingClients(List<String> BRN) {
		String existingEntityID = null;
		StringBuilder listString = new StringBuilder();
		for (String s : BRN)
		     listString.append(s+",");
		if(listString.length()>0){
			listString.deleteCharAt(listString.length()-1);
			HTTPAgent httpAgent = new HTTPAgent();
			existingEntityID = httpAgent.fetch(SUBMISSION_BRNID + listString).payload();
			System.out.println("brnList: " + existingEntityID);
		}
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
			nodeValue = xPath.compile(nodePath).evaluate(XMLData.getXmlDocument());
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
			nodeValue = xPath.compile(nodePath).evaluate(XMLData.getXmlDocument());
		} catch (Exception e) {
			// TODO: handle exception
		}
		brnList.add(nodeValue);
		return brnList;

	}
	
	private List<String> buildFormInstanceFields(String subFormDefaultBindPath) {

		try {
			List<String> brnList = new ArrayList<>();
			Document xmlDocument = XMLData.getXmlDocument();
			NodeList nodeList = xmlDocument
					.getElementsByTagName(subFormDefaultBindPath);
			for (int i = 0; i < nodeList.getLength(); i++) {
				NodeList childNodeList = nodeList.item(i).getChildNodes();
				for (int j = 0; j < childNodeList.getLength(); j++) {
					if (childNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
		                if (childNodeList.item(i).getNodeName().equalsIgnoreCase("FWWOMBID")) {                    	
		                    brnList.add(childNodeList.item(j).getTextContent());
		                }					
					}

				}
			}
			return brnList;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

}

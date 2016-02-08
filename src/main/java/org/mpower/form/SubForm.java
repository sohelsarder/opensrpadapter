package org.mpower.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.test.util.XpathExpectationsHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scala.annotation.meta.getter;
import org.mpower.form.SubFormField;
import com.google.gson.annotations.Expose;

public class SubForm {

	@Expose public String name;
	@Expose public String bind_type;
	@Expose public String default_bind_path;
	@Expose public List<SubFormField> fields = new ArrayList<SubFormField>();
	@Expose public List<HashMap<String, String>> instances = new ArrayList<HashMap<String, String>>();
	
	public void buildSubFormFields() {

		for (SubFormField field : fields) {
			System.out.println(field.name + " -subFormField-- ");			
			field.source = this.bind_type + "." + field.name;
		}
	}
	
	private String searchInXML(String nodePath) {

		String nodeValue = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			nodeValue = xPath.compile(nodePath).evaluate(XMLData.getXmlDocument());
		} catch (Exception e) {
			// TODO: handle exception
		}
		//System.out.println("Finding nodePath: " + nodePath + " ,nodeValue: " + nodeValue);
		return nodeValue;

	}
	
	public void buildSubFormInstanceFields() {
		String subFormDefaultBindPath = SubmissionBuilder.variableMapperForForm.get(this.name+"_default_bind_path");
		boolean addCommonFields = false;
		try {
			Document xmlDocument = XMLData.getXmlDocument();
			NodeList nodeList = xmlDocument.getElementsByTagName(subFormDefaultBindPath);
			
			System.out.println("buildSubFormInstanceFields - " + nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				HashMap<String, String> hm = new HashMap<>();
				
				NodeList childNodeList = nodeList.item(i).getChildNodes();
				System.out.println(nodeList.item(i).getNodeName() + " -has number of children- " + childNodeList.getLength());
				for (int j = 0; j < childNodeList.getLength(); j++) {
					
					Node currentNode = childNodeList.item(j);
					
					System.out.println(currentNode.getChildNodes().getLength() + " -?- " + currentNode.getNodeName());
					
					if(currentNode.getNodeType() == Node.ELEMENT_NODE && SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath+"/"+ currentNode.getNodeName()) != null){
						if(SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath+"/"+ currentNode.getNodeName()).equalsIgnoreCase("groupTag")){
							NodeList groupTagChildNodeList = currentNode.getChildNodes();
							for (int k = 0; k < groupTagChildNodeList.getLength(); k++) {
								Node innerNode = groupTagChildNodeList.item(k);
								String path = subFormDefaultBindPath+"/"+ currentNode.getNodeName()+"/"+innerNode.getNodeName();
								
								if(innerNode.getNodeType() == Node.ELEMENT_NODE && SubmissionBuilder.variableMapperForForm.get(path) != null){
									System.out.println(path + " --*****-- " + SubmissionBuilder.variableMapperForForm.get(path));
									hm.put(SubmissionBuilder.variableMapperForForm.get(path), innerNode.getTextContent().trim());
								}
							}
						}							
						else
							hm.put(SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath+"/"+ currentNode.getNodeName()), currentNode.getTextContent());
					}
					addCommonFields = true;
				}
				if(addCommonFields){
					for(String key : SubmissionBuilder.variableMapperForForm.keySet()){
						if(key.charAt(0) == '_'){
							hm.put(key.substring(1), searchInXML(SubmissionBuilder.variableMapperForForm.get(key)));
						}
					}
					addCommonFields = false;
					System.out.println("Common fields in instance are added.");
				}				
				if(hm.size() > 0)
					instances.add(hm);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}

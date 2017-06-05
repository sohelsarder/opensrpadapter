package org.mpower.form;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SubForm {

    @Expose
    public String name;
    @Expose
    public String bind_type;
    @Expose
    public String default_bind_path;
    @Expose
    public List<SubFormField> fields = new ArrayList<SubFormField>();
    @Expose
    public List<HashMap<String, String>> instances = new ArrayList<HashMap<String, String>>();

    public void buildSubFormFields() {

        for (SubFormField field : fields) {
            //System.out.println(field.name + " -subFormField-- ");
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
        String subFormDefaultBindPath = SubmissionBuilder.variableMapperForForm.get(this.name + "_default_bind_path");
        System.out.println("building forminstance for subform- " + this.name + " with deafault bind path- " + subFormDefaultBindPath);
        boolean addCommonFields = false;
        try {
            Document xmlDocument = XMLData.getXmlDocument();
            NodeList nodeList = xmlDocument.getElementsByTagName(subFormDefaultBindPath);

           // System.out.println("buildSubFormInstanceFields - " + new Gson().toJson(nodeList));
            for (int i = 0; i < nodeList.getLength(); i++) {
                HashMap<String, String> hm = new HashMap<>();

                NodeList childNodeList = nodeList.item(i).getChildNodes();
                System.out.println(nodeList.item(i).getNodeName() + " -has number of children- " + childNodeList.getLength());
                for (int j = 0; j < childNodeList.getLength(); j++) {

                    Node currentNode = childNodeList.item(j);
                    System.out.println("Here we are:");
                    System.out.println(currentNode.getChildNodes().getLength() + " -?- " + currentNode.getNodeName());

                    if (currentNode.getNodeType() == Node.ELEMENT_NODE && SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath + "/" + currentNode.getNodeName()) != null) {
                        if (SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath + "/" + currentNode.getNodeName()).equalsIgnoreCase("groupTag")) {
                            NodeList groupTagChildNodeList = currentNode.getChildNodes();
                            for (int k = 0; k < groupTagChildNodeList.getLength(); k++) {
                                Node innerNode = groupTagChildNodeList.item(k);
                                String path = subFormDefaultBindPath + "/" + currentNode.getNodeName() + "/" + innerNode.getNodeName();

                                if (innerNode.getNodeType() == Node.ELEMENT_NODE && SubmissionBuilder.variableMapperForForm.get(path) != null) {
                                    System.out.println(path + " --*****-- " + SubmissionBuilder.variableMapperForForm.get(path));
                                    hm.put(SubmissionBuilder.variableMapperForForm.get(path), innerNode.getTextContent().trim());
                                }
                            }
                        } else
                            hm.put(SubmissionBuilder.variableMapperForForm.get(subFormDefaultBindPath + "/" + currentNode.getNodeName()), currentNode.getTextContent());
                    }
                    addCommonFields = true;
                }
                if (addCommonFields) {
                    for (String key : SubmissionBuilder.variableMapperForForm.keySet()) {
                        if (key.charAt(0) == '_') {
                            hm.put(key.substring(1), searchInXML(SubmissionBuilder.variableMapperForForm.get(key)));
                        }
                    }
                    addCommonFields = false;
                    //System.out.println("Common fields in instance are added.");
                }
                if (hm.size() > 0) {
                    if(hm.containsKey("FWBNFDOB")) {
                        String dateTimeToConvert = hm.get("FWBNFDOB");
                        SimpleDateFormat inputDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                        SimpleDateFormat outputDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String adaptedDateStr = "";
                        try {
                            Date inputDateTime = inputDateTimeFormat.parse(dateTimeToConvert);
                            System.out.println(inputDateTime.toString());
                            adaptedDateStr = outputDateTimeFormat.format(inputDateTime);
                            System.out.println(adaptedDateStr);
                            hm.put("FWBNFDOB", adaptedDateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean noRelationalId = !hm.containsKey("relationalid") || hm.get("relationalid").isEmpty();
                    if (noRelationalId) {
                        if (SubmissionBuilder.variableMapperForForm.get("entityID").startsWith("/")) {
                            String entityID = searchInXML(SubmissionBuilder.variableMapperForForm.get("entityID"));
                            hm.put("relationalid", entityID);

                        } else {
                            String entityID = SubmissionBuilder.variableMapperForForm.get("entityID");
                            hm.put("relationalid", entityID);
                        }

                    }

                    hm.put("user_type","FD");
                    System.out.println(new Gson().toJson(hm));
                    instances.add(hm);
                }

            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}

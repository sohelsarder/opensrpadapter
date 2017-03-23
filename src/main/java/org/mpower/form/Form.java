package org.mpower.form;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import org.mpower.http.HTTPAgent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Form {
    @Expose
    public String bind_type;
    @Expose
    public String default_bind_path;
    @Expose
    private static final String SUBMISSION_BRNID = "http://localhost:9979/entity-id?brn-id=";
    @Expose
    public List<Field> fields = new ArrayList<Field>();
    @Expose
    public List<SubForm> sub_forms = new ArrayList<SubForm>();

    public void buildFields() {

        for (Field field : fields) {
            // condition
            //System.out.println(field.name + " --)");
            if (field.bind != null) {
                field.source = field.source == null ? this.bind_type + "." + field.name : field.source;
                field.value = searchInXML(SubmissionBuilder.variableMapperForForm.get(field.name));
                boolean convertibleDateTimeField = field.name.equalsIgnoreCase("start") || field.name.equalsIgnoreCase("end");
                if (convertibleDateTimeField) {
                    String convertibleDateTimeStr = field.value;
                    System.out.println(convertibleDateTimeStr);
                    String adaptedDateStr = this.dateTimeConverter(convertibleDateTimeStr);
                    field.value = adaptedDateStr;
                    System.out.println(new Gson().toJson(field));
                }
                //System.out.println( "field.bind - " + field.bind + ", field.name - " + field.name + ", field.value - " + field.value + " ---- " + SubmissionBuilder.variableMapperForForm.get(field.name));
            } else {

                if (SubmissionBuilder.variableMapperForForm.get("entityID").startsWith("/")) {
                    SubmissionBuilder.entityID = searchInXML(SubmissionBuilder.variableMapperForForm.get("entityID"));
                    field.source = field.source == null ? this.bind_type + "." + field.name : field.source;
                    field.value = SubmissionBuilder.entityID;
                    SubmissionBuilder.variableMapperForForm.remove("entityID");
                    SubmissionBuilder.variableMapperForForm.put("entityID", SubmissionBuilder.entityID);
                } else {
                    SubmissionBuilder.entityID = SubmissionBuilder.variableMapperForForm.get("entityID");
                    field.source = field.source == null ? this.bind_type + "." + field.name : field.source;
                    field.value = SubmissionBuilder.entityID;
                }

            }

        }

    }

    public void buildSubForm() {
        //System.out.println("now subform will be built");
        for (SubForm subForm : sub_forms) {
            //System.out.println("for the subform- " + subForm.name);
            subForm.buildSubFormFields();
            //subForm.buildSubFormInstanceFields(extractLastNode(subForm.default_bind_path));
            subForm.buildSubFormInstanceFields();
        }
    }

    private static String checkExistingClients(List<String> BRN) {
        String existingEntityID = null;
        StringBuilder listString = new StringBuilder();
        for (String s : BRN)
            listString.append(s + ",");
        if (listString.length() > 0) {
            listString.deleteCharAt(listString.length() - 1);
            HTTPAgent httpAgent = new HTTPAgent();
            existingEntityID = httpAgent.fetch(SUBMISSION_BRNID + listString).payload();
            //System.out.println("brnList: " + existingEntityID);
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
        //System.out.println("Finding nodePath: " + nodePath + " ,nodeValue: " + nodeValue);
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

    public String dateTimeConverter(String dateTimeToConvert) {
        SimpleDateFormat inputDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        SimpleDateFormat outputDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String adaptedDateStr = "";
        try {
            Date inputDateTime = inputDateTimeFormat.parse(dateTimeToConvert);
            adaptedDateStr = outputDateTimeFormat.format(inputDateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return adaptedDateStr;
    }

}

package org.mpower.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.mockito.internal.creation.util.SearchingClassLoader;
import org.mpower.http.HTTPAgent;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class SubmissionBuilder {
	private static final String FORMS_DIR = "/home/cyrus/project/opensrpadapter/Forms/";
	private static final String OPENSRP_BASE_URL = "http://192.168.21.218:9979/";
	private static final String SUBMISSION_URL = "http://192.168.21.218:9979/form-submissions/";
	private static final String LOCATION_URL = "http://192.168.21.218:9979/user-location?location-name=";
	private static final String OPENSRP_USER = "sohel";
	private static final String OPENSRP_PWD = "Sohel@123";
	private static FormSubmission formSubmission;
	private static FormInstance formInstance;
	private static HTTPAgent httpagent;
	protected static String entityID = "";

	public static void buildFormSubmission() {	
		writeFormSubmission();
	}

	private static String getFormInstance() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			// read from file, convert it to FormSubmission class
			formInstance = mapper.readValue(new File(FORMS_DIR + "read.json"),
					FormInstance.class);
			
			System.out.println("**&&");
			for (Field field : formInstance.form.fields) {
				System.out.println(field.name + " - " + field.bind + " - " + field.read);
			}
			System.out.println("**&&");
			
			formInstance.buildFormInstance();
			mapper.writeValue(new File(FORMS_DIR + "write.json"), formInstance);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Gson().toJson(formInstance);

	}

	private static void writeFormSubmission() {
		String jsonPayload = mapToFormSubmissionDTO();
		httpagent = new HTTPAgent();
		System.out.println("Submission Status: " + httpagent.post(SUBMISSION_URL, jsonPayload).isFailure());
	}
	
	private static String getAnmID (String locationName){
		HTTPAgent httpAgent = new HTTPAgent();
		System.out.println("Anm location name:" + locationName );
		return httpAgent.fetch(LOCATION_URL + locationName).payload();
	}

	private static String mapToFormSubmissionDTO() {
		List<org.ei.drishti.dto.form.FormSubmissionDTO> formSubmissions = new ArrayList<org.ei.drishti.dto.form.FormSubmissionDTO>();
		String instanceID = UUID.randomUUID().toString();
		String fromFormInstance = getFormInstance();	
		formSubmissions.add(new org.ei.drishti.dto.form.FormSubmissionDTO(
				getAnmID(searchInXML("/data/woman/MauzaparaName")).replaceAll("^\"|\"$", ""), instanceID, entityID,
				"new_household_registration", fromFormInstance,
				"1435819226470", "7"));

		return new Gson().toJson(formSubmissions);
	}
	
	private static String searchInXML(String nodePath) {

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
}

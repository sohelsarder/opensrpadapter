package org.mpower.form;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.mpower.http.HTTPAgent;
import org.mpower.properties.AdapterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
@ComponentScan
@Component
@EnableAutoConfiguration
public class SubmissionBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(SubmissionBuilder.class);
	
	@Autowired
	private AdapterProperties adapterProperties;

	
	public SubmissionBuilder() {
	}
	
	private static final String FORMS_DIR = "forms/";
	
	private static FormInstance formInstance;
	
	private static HTTPAgent httpagent;
	
	protected static HashMap<String, String> variableMapperForForm = new HashMap<String, String>();
	
	public List<String> buildFormSubmission(String formName, String entityId) {
		if (formName.equalsIgnoreCase("pvf_form")) {
			formName = "birthnotificationpregnancystatusfollowup";
		}
		variableMapper(formName);
		if (!variableMapperForForm.containsKey("entityID")) {
			variableMapperForForm.put("entityID", entityId);
		}
		List<String> status = writeFormSubmission(formName, entityId);
		variableMapperForForm.clear();
		return status;
	}
	
	public static void variableMapper(String formName) {
		InputStream input = SubmissionBuilder.class.getClassLoader()
		        .getResourceAsStream(FORMS_DIR + formName + "_mapper.csv");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = br.readLine()) != null) {
				String[] strings = line.split(",");
				if (strings.length == 2)
					variableMapperForForm.put(strings[0].trim(), strings[1].trim());
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.warn("exception:" + e.getMessage());
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("exception:" + e.getMessage());
		}
	}
	
	private static String getFormInstance(String formName) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			formInstance = mapper.readValue(
			    SubmissionBuilder.class.getClassLoader().getResourceAsStream(FORMS_DIR + formName + "_read.json"),
			    FormInstance.class);
			logger.debug("number of subforms of current " + formName + " is " + formInstance.form.sub_forms.size());
			formInstance.buildFormInstance();
		}
		catch (JsonGenerationException e) {
			logger.warn("exception:" + e.getMessage());
		}
		catch (JsonMappingException e) {
			logger.warn("exception:" + e.getMessage());
		}
		catch (IOException e) {
			logger.warn("exception:" + e.getMessage());
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(formInstance);
	}
	
	private List<String> writeFormSubmission(String formName, String entityId) {
		List<String> responseAr = new ArrayList<String>();
		String jsonPayload = mapToFormSubmissionDTO(formName, entityId);		
		httpagent = new HTTPAgent();
		logger.info("sending reqeust to opensrp entityId: " + entityId + ",SUBMISSION_URL:" + adapterProperties.getSUBMISSION_URL());
		String response = httpagent.post(adapterProperties.getSUBMISSION_URL(), jsonPayload).isFailure() == false ? "S"
		        : "F";
		responseAr.add(jsonPayload);
		responseAr.add(response);
		logger.info("response received from opensrp entityId: " + entityId + ",response:" + responseAr.get(1));
		return responseAr;
	}
	
	private static String mapToFormSubmissionDTO(String formName, String entityId) {
		List<org.ei.drishti.dto.form.FormSubmissionDTO> formSubmissions = new ArrayList<org.ei.drishti.dto.form.FormSubmissionDTO>();
		String instanceID = UUID.randomUUID().toString();
		String formInstanceString = getFormInstance(formName);
		formSubmissions.add(new org.ei.drishti.dto.form.FormSubmissionDTO(
		        searchInXML("/data/fwaName").replaceAll("^\"|\"$", ""), instanceID, entityId, formName, formInstanceString,
		        System.currentTimeMillis() + "", formInstance.form_data_definition_version));
		
		return new Gson().toJson(formSubmissions);
	}
	
	private static String searchInXML(String nodePath) {
		
		String nodeValue = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			nodeValue = xPath.compile(nodePath).evaluate(XMLData.getXmlDocument());
		}
		catch (Exception e) {
			logger.warn("exception:" + e.getMessage());
		}
		return nodeValue;
		
	}
}

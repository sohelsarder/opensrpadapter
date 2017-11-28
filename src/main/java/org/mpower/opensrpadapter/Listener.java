package org.mpower.opensrpadapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mpower.entity.RequestLog;
import org.mpower.entity.RequestQueue;
import org.mpower.form.SubmissionBuilder;
import org.mpower.form.XMLData;
import org.mpower.http.HTTPAgent;
import org.mpower.util.SearchInXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "org.mpower.form")
public class Listener {
	
	@Autowired
	private AdapterProperties adapterProperties;
	
	@Autowired
	private SubmissionBuilder submissionBuilder;
	
	public Listener() {
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Listener.class);
	
	@Scheduled(fixedDelay = 6000)
	@Transactional
	public void scheduleFixedDelayTask() {
		logger.debug("OpenSRP transfer started.......");
		SearchInXML searchIndXML = new SearchInXML();
		List<RequestQueue> rquestQueues = new DataBaseOperation<RequestQueue>().getAll();
		ByteArrayInputStream fileIS = null;
		DocumentBuilder builder = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			logger.warn("exception:" + e.getMessage());
			
		}
		for (RequestQueue requestQueue : rquestQueues) {
			fileIS = new ByteArrayInputStream(requestQueue.getData_xml());
			try {
				XMLData.setXmlDocument(builder.parse(fileIS));
			}
			catch (SAXException e) {
				logger.warn("exception:" + e.getMessage());
			}
			catch (IOException e) {
				logger.warn("exception:" + e.getMessage());
			}
			
			RequestLog requestLog = new RequestLog();
			if (!"new_household_registration".equals(requestQueue.getFromName())) {
				requestQueue.setEntity_id(searchIndXML.searchValueFromXML("/data/entityID", XMLData.getXmlDocument()));
				requestQueue.setRelational_id(searchIndXML.searchValueFromXML("/data/woman/id", XMLData.getXmlDocument()));
			}
			requestLog.setReqeust_id(requestQueue.getReqeust_id());
			requestLog.setEntity_id(requestQueue.getEntity_id());
			requestLog.setFromName(requestQueue.getFromName());
			requestLog.setData_xml(requestQueue.getData_xml());
			requestLog.setReqeust_time(requestQueue.getReqeust_time());
			requestLog.setResponse_time(new Date());
			requestLog.setResponse_time(new Date());
			requestLog.setRelational_id(requestQueue.getRelational_id());
			List<String> status = submissionBuilder.buildFormSubmission(requestQueue.getFromName(),
			    requestQueue.getEntity_id());
			requestLog.setFormsubmission(status.get(0));
			requestLog.setStatus(status.get(1));
			HTTPAgent httpagent = new HTTPAgent();
			httpagent.fetch(adapterProperties.getNotificationURL() + "?request_id=" + requestQueue.getReqeust_id()
			        + "&entity_id=" + requestQueue.getEntity_id() + "&relational_id=" + requestQueue.getRelational_id()
			        + "&status=" + status.get(1));
			if (new DataBaseOperation<RequestLog>().save(requestLog) > 0) {
				if (new DataBaseOperation<RequestQueue>().delete(requestQueue) < 0) {
					logger.error("could not delete from queue requestId:" + requestQueue.getId());
				}
				
			}
		}
	}
	
}

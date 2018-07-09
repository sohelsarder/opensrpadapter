package org.mpower.opensrpadapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mpower.db.DataBaseOperation;
import org.mpower.entity.RequestLog;
import org.mpower.entity.RequestQueue;
import org.mpower.form.SubmissionBuilder;
import org.mpower.form.XMLData;
import org.mpower.http.HTTPAgent;
import org.mpower.properties.AdapterProperties;
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
@ComponentScan(basePackageClasses = {SubmissionBuilder.class,AdapterProperties.class})
public class Listener {
	
	@Autowired
	private AdapterProperties adapterProperties;
	
	@Autowired
	private SubmissionBuilder submissionBuilder;

	private static final int listenerDelay=6000;
	
	public Listener() {
		
	}
	
	private static final Logger logger = LoggerFactory.getLogger(Listener.class);
	
	@Scheduled(fixedDelay = listenerDelay)
	@Transactional
	public void scheduleFixedDelayTask() {		
		SearchInXML searchIndXML = new SearchInXML();		
		ByteArrayInputStream fileIS = null;
		DocumentBuilder builder = null;
		//boolean isFailure = false;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			logger.warn("failed to create newDocumentBuilder error:" + e.getMessage());
			
		}
		
		List<RequestQueue> rquestQueues = new DataBaseOperation<RequestQueue>().getAll();
		logger.debug("total number of request found in request queue: " + rquestQueues.size());
		for (RequestQueue requestQueue : rquestQueues) {
			Date startProcess = new Date();
			fileIS = new ByteArrayInputStream(requestQueue.getData_xml());
			try {
				XMLData.setXmlDocument(builder.parse(fileIS));
			}
			catch (SAXException e) {
				logger.warn("request processing error requestId: " + requestQueue.getId() + " ,error: " + e.getMessage());
			}
			catch (IOException e) {
				logger.warn("request processing error requestId: " + requestQueue.getId() + " ,error: " + e.getMessage());
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
			requestLog.setRelational_id(requestQueue.getRelational_id());
			
			Date finishProcess = new Date();
			Date startSubmit = new Date();
			List<String> status = submissionBuilder.buildFormSubmission(requestQueue.getFromName(),
			    requestQueue.getEntity_id());
			Date finishSubmit = new Date();
			requestLog.setFormsubmission(status.get(0));
			requestLog.setStatus(status.get(1));
			requestLog.setIsNotified(0);
			
			logger.info("requestID : " + requestQueue.getReqeust_id()
					+" start_process : " + startProcess
					+" finish_process : " + finishProcess
					+" start_submit : " + startSubmit
					+" finish_submit : " + finishSubmit);
			//split from here 
			/*HTTPAgent httpagent = new HTTPAgent();
			isFailure = httpagent.fetch(adapterProperties.getNotificationURL() + "?request_id=" + requestQueue.getReqeust_id()
			        + "&entity_id=" + requestQueue.getEntity_id() + "&relational_id=" + requestQueue.getRelational_id()
			        + "&status=" + status.get(1)).isFailure();
			if(isFailure!=false) {
				logger.error("could not notify requestId:" + requestQueue.getId());
				
			}*/
			//remove only this portion
			
			
			if (new DataBaseOperation<RequestLog>().save(requestLog) > 0) {
				if (new DataBaseOperation<RequestQueue>().delete(requestQueue) < 0) {
					logger.error("could not delete from queue requestId:" + requestQueue.getId());
				}
				
			}
		}
		logger.info("reqeust processing completed total request processed: " + rquestQueues.size());
	}
	
}

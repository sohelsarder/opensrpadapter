package org.mpower.opensrpadapter;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.mpower.entity.RequestQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ServiceController {
	
	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
	
	@RequestMapping("/sendDataXML")
	@Produces("application/json")
	public @ResponseBody String sendDataXML(@RequestParam(value = "message", defaultValue = "hello") String message) {
		
		logger.debug("Receive data successfully !!" + message);
		return "{success : true}";
	}
	
	@RequestMapping(value = "/upload", headers = "content-type=multipart/*", method = RequestMethod.POST)
	@Consumes("multipart/form-data")
	@Produces("application/json")
	public @ResponseBody String dataXMLReceiver(@RequestParam("name") String name,
	                                            @RequestParam("request_id") String requestId,
	                                            @RequestParam("file") MultipartFile file) {
		String status = "F";
		byte[] bytes = null;
		RequestQueue requestQueue = new RequestQueue();
		if (!file.isEmpty()) {
			try {
				logger.debug("Received a request, RequestTime:" + System.currentTimeMillis());
				bytes = file.getBytes();
				requestQueue.setReqeust_id(requestId);
				requestQueue.setData_xml(bytes);
				requestQueue.setReqeust_time(new Date());
				requestQueue.setFromName(name);
				if ("new_household_registration".equals(name)) {
					requestQueue.setEntity_id(UUID.randomUUID().toString());
				}
				if (saveRequestInQueue(requestQueue) > 0) {
					status = "P";
				}
			}
			catch (Exception e) {
				logger.warn("exception:" + e.getMessage());
			}
		} else {
			logger.warn("File not found");
		}
		
		logger.debug("Sending response: " + name + ",ResponseTime:" + System.currentTimeMillis());
		return "{request_id:" + requestQueue.getReqeust_id() + ",entity_id:" + requestQueue.getEntity_id() + "}";
	}
	
	private int saveRequestInQueue(RequestQueue requestQueue) {
		return new DataBaseOperation<RequestQueue>().save(requestQueue);
	}
	
	@RequestMapping(method = GET, value = "/test")
	@ResponseBody
	public void pp() {
		for (int i = 0; i < 200; i++) {
			RequestQueue requestQueue = new RequestQueue();
			requestQueue.setEntity_id("entity_id" + i);
			new DataBaseOperation<RequestQueue>().save(requestQueue);
			
		}
		
	}
}

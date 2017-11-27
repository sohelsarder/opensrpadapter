package org.mpower.opensrpadapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:adapter.properties")
public class AdapterProperties {
	
	public AdapterProperties() {
		// TODO Auto-generated constructor stub
	}
	
	@Value("${NOTIFICATION_URL}")
	private String notificationURL;
	
	@Value("${SUBMISSION_URL}")
	private String SUBMISSION_URL;
	
	public String getNotificationURL() {
		return notificationURL;
	}
	
	public void setNotificationURL(String notificationURL) {
		this.notificationURL = notificationURL;
	}
	
	public String getSUBMISSION_URL() {
		return SUBMISSION_URL;
	}
	
	public void setSUBMISSION_URL(String sUBMISSION_URL) {
		SUBMISSION_URL = sUBMISSION_URL;
	}
	
	@Override
	public String toString() {
		return "AdapterProperties [notificationURL=" + notificationURL + ", SUBMISSION_URL=" + SUBMISSION_URL + "]";
	}
	
}

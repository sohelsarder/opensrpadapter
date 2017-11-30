package org.mpower.properties;

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
	

	@Value("${OPENSRP_USER}")
	private String user;
	
	@Value("${OPENSRP_PASSWORD}")
	private String password;
	

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AdapterProperties [notificationURL=" + notificationURL + ", SUBMISSION_URL=" + SUBMISSION_URL
				+ ", user=" + user + ", password=" + password +  "]";
	}

	
	
}

package opensrpadapter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(locations = "classpath:adapter.properties", prefix = "adapter")
public class  AdapterProperties {

	private static String baseurl;
	
	public AdapterProperties(){}

	public String getBaseurl() {
		return baseurl;
	}

	public void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}


}

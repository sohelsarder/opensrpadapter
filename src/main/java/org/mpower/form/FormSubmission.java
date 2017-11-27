package org.mpower.form;

import com.google.gson.annotations.Expose;

public class FormSubmission {
	
	@Expose
	public String anmId; //get from opensrp service
	
	@Expose
	public String instanceId; //need to create new for each submission 
	
	@Expose
	public String formName; //static value
	
	@Expose
	public String entityId; //get from opensrp service for existing client and create new for non existing client
	
	@Expose
	public String clientVersion; //static value
	
	@Expose
	public String formDataDefinitionVersion; //static value
	
	@Expose
	public FormInstance formInstance; //generate from data collection xml of external app
	
	public FormSubmission(String anmId, String instanceId, String entityId, String formName, FormInstance formInstance,
	    String clientVersion, String formDataDefinitionVersion) {
		super();
		this.anmId = anmId;
		this.instanceId = instanceId;
		this.entityId = entityId;
		this.formName = formName;
		this.formInstance = formInstance;
		this.clientVersion = clientVersion;
		this.formDataDefinitionVersion = formDataDefinitionVersion;
		
	}
	
}

package org.mpower.form;

public class FormSubmission {
	public String anmId;	//get from opensrp service
	public String instanceId;	//need to create new for each submission 
	public String formName;	//static value
	public String entityId;	//get from opensrp service for existing client and create new for non existing client
	public String clientVersion;	//static value
	public String formDataDefinitionVersion;	//static value
	public String formInstance;	//generate from data collection xml of external app
	public FormSubmission(String anmId, String instanceId, String formName,
			String entityId, String clientVersion,
			String formDataDefinitionVersion, String formInstance) {
		super();
		this.anmId = anmId;
		this.instanceId = instanceId;
		this.formName = formName;
		this.entityId = entityId;
		this.clientVersion = clientVersion;
		this.formDataDefinitionVersion = formDataDefinitionVersion;
		this.formInstance = formInstance;
	}
	

}

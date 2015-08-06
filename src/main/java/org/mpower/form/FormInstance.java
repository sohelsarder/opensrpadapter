package org.mpower.form;

public class FormInstance {
	
	public String form_data_definition_version;
	public Form form;
	
	public void buildFormInstance() {
		form.buildFields();
		form.buildSubForm();
	}

	public FormInstance() {
		super();
	}
	
	
}

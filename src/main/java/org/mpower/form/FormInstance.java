package org.mpower.form;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class FormInstance {
	
	@Expose public String form_data_definition_version;
	@Expose public Form form;
	
	public void buildFormInstance() {
		System.out.println("Received form instance from mJivita+ :   " + form.toString());
		form.buildFields();
		form.buildSubForm();		
	}

	public FormInstance() {
		super();
	}
		
}

package org.mpower.form;

import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class FormInstance {
	
	@Expose public String form_data_definition_version;
	@Expose public Form form;
	
	public void buildFormInstance(HashMap<String, String> mapper) {
		form.buildFields(mapper);
		form.buildSubForm(mapper);
	}

	public FormInstance() {
		super();
	}
	
	
}

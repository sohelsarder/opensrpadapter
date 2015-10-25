package org.mpower.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;

@JsonInclude(Include.NON_NULL)
public class SubFormField {
	
	@Expose public String name;
	public String shouldLoadValue;
	@Expose public String source;
	@Expose public String bind;
	//public String value;
	
	public SubFormField() {
	
	}
		
}

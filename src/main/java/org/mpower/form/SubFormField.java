package org.mpower.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SubFormField {
	
	public String name;
	public String shouldLoadValue;
	public String source;
	public String bind;
	
	public SubFormField() {
	
	}
		
}

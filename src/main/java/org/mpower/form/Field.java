package org.mpower.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Field {
	
	public String name;
	public boolean shouldLoadValue;
	public String bind;
	public String source;
	public String value;
	public String read;
	
	public Field() {
	
	}

}

package org.mpower.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "request_log")
public class RequestLog {
	
	public RequestLog() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_log_id_seq")
	@SequenceGenerator(name = "request_log_id_seq", sequenceName = "request_log_id_seq", allocationSize = 1)
	private int id;
	
	private String reqeust_id;
	
	private String formName;
	
	private Date reqeust_time;
	
	private Date response_time;
	
	private byte[] data_xml;
	
	private String status;
	
	private String entity_id;
	
	private String relational_id;
	
	@Column(columnDefinition = "text")
	private String formsubmission;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getReqeust_id() {
		return reqeust_id;
	}
	
	public void setReqeust_id(String reqeust_id) {
		this.reqeust_id = reqeust_id;
	}
	
	public String getFromName() {
		return formName;
	}
	
	public void setFromName(String fromName) {
		this.formName = fromName;
	}
	
	public Date getReqeust_time() {
		return reqeust_time;
	}
	
	public void setReqeust_time(Date reqeust_time) {
		this.reqeust_time = reqeust_time;
	}
	
	public Date getResponse_time() {
		return response_time;
	}
	
	public void setResponse_time(Date response_time) {
		this.response_time = response_time;
	}
	
	public byte[] getData_xml() {
		return data_xml;
	}
	
	public void setData_xml(byte[] data_xml) {
		this.data_xml = data_xml;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getEntity_id() {
		return entity_id;
	}
	
	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}
	
	public String getRelational_id() {
		return relational_id;
	}
	
	public void setRelational_id(String relational_id) {
		this.relational_id = relational_id;
	}
	
	public String getFormsubmission() {
		return formsubmission;
	}
	
	public void setFormsubmission(String formsubmission) {
		this.formsubmission = formsubmission;
	}
	
}

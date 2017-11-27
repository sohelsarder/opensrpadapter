/**
 * 
 */
package org.mpower.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author proshanto
 *
 */
@Entity
@Table(name="form")
public class Form {
	
	public Form() {
		// TODO Auto-generated constructor stub
	}
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "form_id_seq")
	@SequenceGenerator(name = "form_id_seq", sequenceName = "form_id_seq", allocationSize = 1)
	private int id;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

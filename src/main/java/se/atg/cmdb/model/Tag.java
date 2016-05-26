package se.atg.cmdb.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Tag {

	public String name;
	public boolean editable = true;

	@JsonCreator
	public Tag(String name) {
		this.name = name;
	}
}

package com.example.accessingdatajpa;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class Person {

	@Id
	private Long id;
	private String first_name;
	private String last_name;

	protected Person() {}

	public Person(String firstName, String lastName) {
		this.first_name = firstName;
		this.last_name = lastName;
	}

	@Override
	public String toString() {
		return String.format(
				"Customer[id=%d, firstName='%s', lastName='%s']",
				id, first_name, last_name);
	}

	public Long getId() {
		return id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}
}

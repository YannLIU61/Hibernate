package com.yann.hql.helloworld;

import java.util.HashSet;
import java.util.Set;

public class Departement {
	private Integer dept_id;
	private String dept_Name;
	private Set<Employee> employees = new HashSet<Employee>();
	public Integer getDept_id() {
		return dept_id;
	}
	public void setDept_id(Integer dept_id) {
		this.dept_id = dept_id;
	}
	public String getDept_Name() {
		return dept_Name;
	}
	public void setDept_Name(String dept_Name) {
		this.dept_Name = dept_Name;
	}
	public Set<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	

}

package com.yann.hql.helloworld;

public class Employee {

	private Integer em_id;
	private String em_name;
	private int salary;
	private Departement departement;

	public Integer getEm_id() {
		return em_id;
	}

	public void setEm_id(Integer em_id) {
		this.em_id = em_id;
	}

	public String getEm_name() {
		return em_name;
	}

	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public Departement getDepartement() {
		return departement;
	}

	public void setDepartement(Departement departement) {
		this.departement = departement;
	}

	public Employee() {
	}

	public Employee(String em_name, int salary, Departement departement) {
		this.em_name = em_name;
		this.salary = salary;
		this.departement = departement;
	}


}

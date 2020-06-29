package com.yan.helloworld;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "jpa_maanager")
public class Manager {
private Integer id;
private String name;
private Department dept;

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

@Column(name = "MGR_NAME")
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

//没有外键的一端,不维护关联关系. 否则会多余update语句
@OneToOne(mappedBy = "manager")
public Department getDept() {
	return dept;
}

public void setDept(Department dept) {
	this.dept = dept;
}
}

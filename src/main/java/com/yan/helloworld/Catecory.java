package com.yan.helloworld;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

//允许缓存
@Cacheable
@Table(name = "jpa_catecory")
@Entity
public class Catecory {

	private Integer id;
	private String catecoryName;
	private Set<Item> items = new HashSet<>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "CATECORY_NAME")
	public String getCatecoryName() {
		return catecoryName;
	}

	public void setCatecoryName(String catecoryName) {
		this.catecoryName = catecoryName;
	}

	@ManyToMany
	//@JoinTable中间表
	@JoinTable(name = "ITEM_CAT",joinColumns = { @JoinColumn(name = "CAT_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
			@JoinColumn(name = "ITEM_ID", referencedColumnName = "ID") })
	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}

}

package com.yan.helloworld;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "jpa_item")
public class Item {

	private Integer id;
	private String itemName;
	private Set<Catecory> catecoties = new HashSet<>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ITEM_NAME")
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@ManyToMany(mappedBy = "items")
	public Set<Catecory> getCatecoties() {
		return catecoties;
	}

	public void setCatecoties(Set<Catecory> catecoties) {
		this.catecoties = catecoties;
	}

}

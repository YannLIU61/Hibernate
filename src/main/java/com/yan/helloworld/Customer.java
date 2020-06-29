package com.yan.helloworld;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Table(name = "jpa_customer")
@Entity
public class Customer {
	private Integer id;
	private String lastName;
	private String email;
	private int age;
	private Date birth;
	private Date createdDate;
	private Set<Order> orders=new HashSet<>();

	//映射一对多
	//设置及联删除, 默认情况删除一的一端时先将多的一端置空
	//mappedBy 放弃维护主键, 使用mappedBy时不能再使用@JoinColumn
	@OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "customer")
	//@JoinColumn(name = "CUSTOMER_ID")
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	// 定义date类型精确度
	@Temporal(TemporalType.DATE)
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "last_Name")
	public String getLastName() {
		return lastName;
	}

	// 默认 @Basic 名字一致
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// 比飞数据库表映射的字段 @Transient 忽略
	@Transient
	public String getInfo() {
		return "Email: " + email;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", lastName=" + lastName + ", email=" + email + ", age=" + age + ", birth="
				+ birth + ", createdDate=" + createdDate + "]";
	}

}

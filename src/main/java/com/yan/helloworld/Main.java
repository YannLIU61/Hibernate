package com.yan.helloworld;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		// 1. 创建EntitymanagerFactory
		String persistenceUnitName = "JPA";
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		
		// 2.创建Entitymanager
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		// 3.开启事务
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		// 4.进行持久化操作
		Customer customer = new Customer();
		customer.setAge(24);
		customer.setEmail("yann.liu@outlook.fr");
		customer.setLastName("LIU");
		customer.setBirth(new Date());
		customer.setCreatedDate(new Date());
		
		entityManager.persist(customer);
		
		// 5.提交事务
		entityTransaction.commit();
		
		//关闭EntityManager
		entityManager.close();
		
		//关闭EntityManagerFactory
		entityManagerFactory.close();

	}

}

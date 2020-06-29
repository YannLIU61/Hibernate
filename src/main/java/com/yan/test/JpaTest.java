package com.yan.test;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.yan.helloworld.Catecory;
import com.yan.helloworld.Customer;
import com.yan.helloworld.Department;
import com.yan.helloworld.Item;
import com.yan.helloworld.Manager;
import com.yan.helloworld.Order;

public class JpaTest {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction entityTransaction;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("JPA");
		entityManager = entityManagerFactory.createEntityManager();
		entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
	}

	@After
	public void destroy() {
		entityTransaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}

	// 类似于Hibernate中Session的load方法
	@Test
	public void testGetReference() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		//此时只返回一个代理
		System.out.println("----------");
		//发送select
		System.out.println("Result: " + customer);
	}

	// 类似于Hibernate中Session的get
	@Test
	public void testFind() {
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println("Result: " + customer);
	}
	
	//类似于Hibernate 的save 使对象从临时状态转换为持久状态
	//和Hibernate的不同: 若有id 会抛异常
	@Test
	public void testPersistence() {
		Customer customer = new Customer();
		customer.setAge(25);
		customer.setBirth(new Date());
		customer.setCreatedDate(new Date());
		customer.setEmail("aa@outlook.com");
		customer.setLastName("XIANG");
		
		entityManager.persist(customer);
	}
	
	
	/*
	 * 1. 临时对象
	 * 相当于Hibernate saveorUpdate
	 * 复制临时对象的属性到新的对象中,生成id, 将新对象进行insert
	 * 2. 游离对象(有ID) 进行更新 或者插入
	 */
	@Test
	public void testMerge() {
		Customer customer = new Customer();
		customer.setAge(25);
		customer.setBirth(new Date());
		customer.setCreatedDate(new Date());
		customer.setEmail("bb@outlook.com");
		customer.setLastName("BB");
		//新的对象进行insert  
		Customer customer2= entityManager.merge(customer);
		//null
		System.out.println(customer.getId());
		//id
		System.out.println(customer2.getId());
	}
	//类似于Hibernate中Session的delete 从数据库中移除
	//只能删除数据库中的持久化对象而Hibernate中持久化和临时都能删
	@Test
	public void testRemove() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		entityManager.remove(customer);
	}
	/*
	//先保存一的一端后保存多的一端, 这样不会多出多的update语句
	@Test
	public void testManyToOne() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		Order order = new Order();
		order.setOrderName("J2EE");
		order.setCustomer(customer);
		
		entityManager.persist(order);
	}
	
	//可以使用 @ManyToOne(fetch = FetchType.LAZY)设置懒加载
	@Test
	public void testManyToOneFind() {
		Order order = entityManager.find(Order.class, 3);
		System.out.println(order.getCustomer().getLastName());
	}
	
	//不等直接删除一的一端(多的那端有外键约束)
	@Test 
	public void testManyToOneRemove() {
		Customer customer = entityManager.find(Customer.class, 1);
		entityManager.remove(customer);
	} */
	
	
	//单向一对多执行保存时一定会多出update的语句
	@Test
	public void testOneToMany() {
		Customer customer = new Customer();
		customer.setAge(25);
		customer.setBirth(new Date());
		customer.setCreatedDate(new Date());
		customer.setEmail("aa@outlook.com");
		customer.setLastName("aa");
		
		Order order1 = new Order();
		order1.setOrderName("javaee");
		
		Order order2 = new Order();
		order2.setOrderName("php");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);		
	}
	
	//默认使用了懒加载
	@Test
	public void testOneToManyFind() {
		Customer customer = entityManager.find(Customer.class, 4);
		System.out.println(customer.getOrders().size());
	}
	
	//@OneToMany(cascade = CascadeType.REMOVE)设置及联删除
	@Test
	public void testOneToManyRemove() {
		Customer customer = entityManager.find(Customer.class, 4);
		entityManager.remove(customer);
	}
	
	//双向一对多
	//建议使用多的一方维护关联关系, 有效的减少sql语句, 提升效率
	@Test
	public void testOneToManyBoth() {
		Customer customer = new Customer();
		customer.setAge(25);
		customer.setBirth(new Date());
		customer.setCreatedDate(new Date());
		customer.setEmail("bb@outlook.com");
		customer.setLastName("bb");
		
		Order order1 = new Order();
		order1.setOrderName("c++");
		
		Order order2 = new Order();
		order2.setOrderName("c");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);		
	}
	
	@Test
	public void testOneToOne() {
		Department department = new Department();	
		department.setDeptName("DEV");
		
		Manager manager = new Manager();
		manager.setName("aa");
		
		manager.setDept(department);
		department.setManager(manager);
		
		//建议先保存没有外键的一端, 否则会多出update语句
		entityManager.persist(manager);
		entityManager.persist(department);
	}
	
	@Test
	public void testManyToMany() {
		Item item1= new Item();
		Item item2 = new Item();
		Item item3 = new Item();
		
		item1.setItemName("pen");
		item2.setItemName("book");
		item3.setItemName("robot");
		
		Catecory catecory1 = new Catecory();
		Catecory catecory2 = new Catecory();
		
		catecory1.setCatecoryName("school");
		catecory2.setCatecoryName("toy");
		
		catecory1.getItems().add(item1);
		catecory1.getItems().add(item2);
		catecory2.getItems().add(item3);
		
		item1.getCatecoties().add(catecory1);
		item2.getCatecoties().add(catecory1);
		item3.getCatecoties().add(catecory2);
		item3.getCatecoties().add(catecory1);
		
		
		entityManager.persist(item1);
		entityManager.persist(item2);
		entityManager.persist(item3);
		
		entityManager.persist(catecory1);
		entityManager.persist(catecory2);
		
	}
}

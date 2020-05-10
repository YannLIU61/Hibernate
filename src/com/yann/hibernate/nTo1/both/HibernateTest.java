package com.yann.hibernate.nTo1.both;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@Before
	public void init() {
		Configuration configuration = new Configuration().configure();
		configuration.addClass(Customer.class);
		configuration.addClass(Order.class);
		StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.build());
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}

	/*
	 * 四种状态: Transient临时: OID 为空, 不在session中, 数据库中没有记录, Persist持久: OID不为null,
	 * session 中,和数据库中记录对应, 在同一个session实例中,数据库表中每条记录只有唯一的持久化对象, Detached游离:
	 * OID不为null, 不在session缓存中 Removed删除
	 */

	@After
	public void destroy() {
		/*
		 * 在Transaction的commit()中: 先flush数据表中的记录和session缓存中的对象的状态保持一致再commit flush()
		 * 方法可能会发送sql语句但不会提交事务 在为提交事务之前也有可能记性flush()操作,得到数据表最新数据 1) 执行HQL或QBC查询,
		 * 会先进行flush() 2) 若记录的ID是自增, 则在调用save()方法后立即调发送insert()
		 */
		transaction.commit();
		session.close();
		sessionFactory.close();
	}

	@Test
	public void testManyToOneSave() {
		Customer c = new Customer();
		c.setCustomerName("LIU");
		
		Order o1 = new Order();
		o1.setOrderName("Order-1");
		
		Order o2 = new Order();
		o2.setOrderName("Order-2");
		
		//关联
		o1.setCustomer(c);
		o2.setCustomer(c);
		
		c.getOrders().add(o1);
		c.getOrders().add(o2);
		
		//推荐:先插1的一端后插多的一端	
		//如果反过来也可以, 但要多出update语句
		//可以在1的一端set 节点 指定inverse=true, 使1的一端放弃维护关联关系
		//建议:set inverse=true; 先插1的一端
		session.save(c);
		session.save(o1);
		session.save(o2);
	}
	@Test
	public void testOneToMany() {
		//1.对多的一端的集合使用延迟加载
		Customer c = session.get(Customer.class, 1);
		System.out.println(c.getCustomerName());
		//2.返回的多的一端的集合是hibernate内置的集合类型, 具有延迟加载和存放代理对象的功能
		//3.可能会抛出懒加载异常(session关闭)
		System.out.println(c.getOrders().getClass());
		//4. 在需要使用集合元素时进行初始化
	}
	
	@Test
	public void testUpdate() {
		Order order = session.get(Order.class, 1);
		order.getCustomer().setCustomerName("Xiang");
	}
	
	@Test
	public void testDelete() {
//		Customer customer = session.get(Customer.class, 1);
		//不可以删除, 有关联关系
		//session.delete(customer);

	}

}

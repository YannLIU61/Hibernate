package com.yann.hibernate.nTon;

import java.util.Iterator;
import java.util.Set;

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
		configuration.addClass(Category.class);
		configuration.addClass(Item.class);
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
	public void testn2nSave() {
		Category c1 = new Category();
		Category c2 = new Category();
		
		c1.setName("Man");
		c2.setName("Sport");
		
		Item i1= new Item();
		Item i2 = new Item();
		
		i1.setName("Football");
		i2.setName("Shave");
		
		c1.getItems().add(i1);
		c1.getItems().add(i2);
		
		c2.getItems().add(i1);
		
		session.save(c1);
		session.save(c2);
		session.save(i1);
		session.save(i2);
		
	}

	@Test
	public void testGet() {
//		Lazy load
		Category c = session.get(Category.class, 1);
		System.out.println(c.getName());
		
		Set<Item> items = c.getItems();
		for(Item item : items) {
			System.out.println(item.getName());
		}
	}

}

package com.yann.hibernate.joined.subclass;

import java.util.List;

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
		configuration.addClass(Person.class);
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

	/*
	 * 插入需要多张表
	 * 优点:1.不需要辨别者列 2.子类独有字段可以添加非空约束 3. 表分开没有冗余字段
	 *
	 */
	@Test
	public void testsubClass() {
		Person p = new Person();
		p.setAge(24);
		p.setName("LIU");
		
		Student s= new Student();
		s.setAge(25);
		s.setName("XIANG");
		s.setSchool("UTC");
		
		session.save(p);
		session.save(s);
		
	}
	
	/*
	 * 查询父类需要做链接查询
	 * 子类需要内部链接查询
	 */
	@Test
	public void testGet() {
		List<Person> persons = session.createQuery("FROM Person").list();
		for(Person p : persons) {
			System.out.println(p.getName());
		}
		
		List<Student> students = session.createQuery("FROM Student").list();
		for(Student s : students) {
			System.out.println(s.getSchool());
		}
	}
	

}

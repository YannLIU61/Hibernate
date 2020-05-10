package com.yann.hibernate.one2one.foreign;

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
		configuration.addClass(Department.class);
		configuration.addClass(Manager.class);
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
	public void testOne2One() {
		Department dpt = new Department();
		dpt.setDeptName("Dev-2");
		
		Manager m= new Manager();
		m.setMgrName("LIU");
		
		m.setDept(dpt);
		dpt.setMgr(m);
		//建议先保存没有外键列的那个对象
		session.save(m);
		session.save(dpt);
		
	}
	@Test
	public void testGet() {
		Department dpt = new Department();
		//懒加载
		dpt= session.get(Department.class, 1);
		System.out.println(dpt.getDeptName());
		//查询Manager 对象的链接条件应该是 dept.manager_id = mgr.manager_id
		System.out.println(dpt.getMgr().getMgrName());
	}
	@Test
	public void testGet2() {
		//查询没有外键的实体对象时, 使用左外链接一并查询出关联的对象并进行初始化
		Manager m = session.get(Manager.class, 1);
		System.out.println(m.getMgrName());
		System.out.println(m.getDept().getDeptName());
	}

}

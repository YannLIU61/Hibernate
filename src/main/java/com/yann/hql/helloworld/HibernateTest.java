package com.yann.hql.helloworld;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Query;
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
		configuration.addClass(Departement.class);
		configuration.addClass(Employee.class);
		StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		//1.内置缓存:自带的,不可卸载的
		//2.外置缓存: 二级缓存(需要在配置文件中启用:EHCache) 很少被修改, 不重要的
		sessionFactory = configuration.buildSessionFactory(serviceRegistryBuilder.build());
		//一级缓存: 重要的, 经常修改的
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
	public void test() {

		Departement dep = new Departement();
		dep.setDept_Name("DD");
		Employee em = new Employee();
		em.setEm_name("BB");
		em.setSalary(30000);

		em.setDepartement(dep);
		session.save(dep);
		session.save(em);
	}

	@Test
	public void testHQL() {
		/*
		 * but instead of operating on tables and columns, HQL works with persistent
		 * objects and their properties. FROM Employee-> 不是表的名字, 是object
		 */
		String hql = "FROM Employee e WHERE e.salary > 30000";
		Query query = session.createQuery(hql);
		List results = query.list();
		System.out.println(results.toString());
	}

	/*
	 * Pagination using query 分页查询
	 */
	@Test
	public void paginationtest() {
		String hql = "FROM Employee";
		Query query = session.createQuery(hql);
		query.setFirstResult(1);
		query.setMaxResults(2);
		List results = query.list();
		System.out.println(results.size());
	}

	@Test
	public void testFiledQuery() {
		String hql = "SELECT new Employee(e.em_name, e.salary, e.departement) FROM Employee e WHERE e.departement = :dept";
		Query query = session.createQuery(hql);

		Departement dept = new Departement();
		dept.setDept_id(1);
		List<Employee> lst = query.setEntity("dept", dept).list();
		for (Employee e : lst) {
			System.out.println(e.getEm_name() + e.getSalary() + e.getDepartement().getDept_id());
		}
	}

	@Test
	public void testGroupBy() {
		String hql = "SELECT min(e.salary), max(e.salary) FROM Employee e GROUP BY e.departement HAVING min(e.salary)>40000";
	}

	/**
	 * 
	 * 迫切左外连接: 特点是：如果左表有不满足条件的，也返回左表不满足条件 1. LEFT JOIN FETCH 关键字表示迫切左外连接检索策略. 2.
	 * list() 方法返回的集合中存放实体对象的引用, 每个 Department 对象关联的 Employee 集合都被初始化, 存放所有关联的
	 * Employee 的实体对象. 3. 查询结果中可能会包含重复元素, 可以通过一个 HashSet 来过滤重复元素
	 * 
	 * 去重： 方法一:使用 distinct String hql = "SELECT DISTINCT d FROM Department d LEFT
	 * JOIN FETCH d.emps "; Query query = session.createQuery(hql);
	 *
	 * List<Department> depts = query.list(); System.out.println(depts.size());
	 * 
	 * 方法二 String hql = "FROM Department d LEFT JOIN FETCH d.emps "; Query query =
	 * session.createQuery(hql);
	 *
	 * List<Department> depts = query.list();
	 *
	 * depts = new ArrayList<>(new LinkedHashSet(depts));
	 * System.out.println(depts.size());
	 * 
	 * for(Department dept:depts){ System.out.println(dept.getName() + "--" +
	 * dept.getEmps().size() ); }
	 * 
	 * 
	 */
	@Test
	public void testLeftJoinFetch() {
//		String hql  = "SELECT DISTINCT d FROM  Department d LEFT JOIN FETCH d.emps ";
//		Query query = session.createQuery(hql);
//		
//		List<Department> depts = query.list();
//		System.out.println(depts.size());
//		

		String hql = "FROM  Department d LEFT JOIN FETCH d.emps ";
		Query query = session.createQuery(hql);

		List<Departement> depts = query.list();
		System.out.println(depts.size());

		depts = new ArrayList<>(new LinkedHashSet(depts));
		System.out.println(depts.size());

		for (Departement dept : depts) {
			System.out.println(dept.getDept_Name() + "--" + dept.getEmployees().size());
		}
	}

	/**
	 * 左外连接: 1. LEFT JOIN 关键字表示左外连接查询. 2. list() 方法返回的集合中存放的是对象数组类型 3. 根据配置文件来决定
	 * Employee 集合的检索策略. 4. 如果希望 list() 方法返回的集合中仅包含 Department 对象, 可以在HQL 查询语句中使用
	 * SELECT 关键字
	 * 
	 * 这样的语句查询的结果有重复： String hql = "FROM Department d LEFT JOIN d.emps"; Query query
	 * = session.createQuery(hql);
	 * 
	 * List<Object[]> results = query.list(); System.out.println(results.size());
	 * 
	 * 去重： 仅能使用 distinct 的方法去除重复
	 * 
	 * String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.emps"; Query
	 * query = session.createQuery(hql);
	 * 
	 * List<Department> depts = query.list(); System.out.println(depts.size());
	 * 
	 * for(Department dept:depts){ System.out.println(dept.getName() +
	 * dept.getEmps().size()); }
	 * 
	 */
	@Test
	public void testLeftJoin() {
		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.emps";
		Query query = session.createQuery(hql);

		List<Departement> depts = query.list();
		System.out.println(depts.size());

		for (Departement dept : depts) {
			System.out.println(dept.getDept_Name() + dept.getEmployees().size());
		}

	}
	/**
	 * 迫切内连接: 特点是：不返回左表不满足条件
	 *		INNER JOIN FETCH 关键字表示迫切内连接, 也可以省略 INNER 关键字
	 *		list() 方法返回的集合中存放 Department 对象的引用, 每个 Department 
	 *				对象的 Employee 集合都被初始化, 存放所有关联的 Employee 对象
	 * 
	 * 内连接:
	 *		INNER JOIN 关键字表示内连接, 也可以省略 INNER 关键字
	 *		list() 方法的集合中存放的每个元素对应查询结果的一条记录, 每个元素都是对象数组类型
	 *		如果希望 list() 方法的返回的集合仅包含 Department  对象, 可以在 HQL 查询语句中使用 SELECT 关键字
	 *
	 * 
	 * 
	 */
	@Test
	public void testInnerJoinFetch(){
		//String hql  = "SELECT DISTINCT d FROM  Department d LEFT JOIN FETCH d.emps ";
		String hql  = "FROM  Department d INNER JOIN FETCH  d.emps ";
		Query query = session.createQuery(hql);
		
		
		List<Departement> depts = query.list();
		depts = new ArrayList<>(new LinkedHashSet(depts));
		System.out.println(depts.size());
		
		for(Departement dept:depts){
			System.out.println(dept.getDept_Name() + "--" + dept.getEmployees().size() );
		}
	}


}

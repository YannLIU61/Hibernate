package com.yann.hibernate.helloworld;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
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
		configuration.addClass(News.class);
		configuration.addClass(Worker.class);
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
	public void doWork() {
		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println(connection);
			}

		});
	}

//	Session 缓存可以减少访问数据库的频率(一级)
	@Test
	public void testFlush() {
		/*
		 * get() load() 区别: 1). get() 立即加载对象 ;load() 若不使用该对象则不会立即执行查询操作, 而返回一个代理对象 get
		 * 是立即检索, load 是延迟检索 2). 若数据表中没有记录: load 抛出异常, get 返回null
		 */
		News news = session.get(News.class, 2);
		news.setAuthor("XIANG");
		/*
		 * update 更新一个持久化对象Persist时不需要自己调用update方法: 在提交事务trasaction.commit时会自动执行session
		 * 的flush方法 更新游离对象时需要手动调用update(游离对象Detached:数据表里有, 有OID, session断开后的对象;
		 * 临时对象Transient无id) 1: session.update()无论要更新的游离对象和数据表记录是否一致, 都会发送update语句
		 * 如何使其不盲目发出update: a): 在.hbm.xml中 的class 节点设置
		 * select-before-update=true(默认是false)--不推荐 2. 若数据表中没有此数据:异常 3.
		 * 在session缓存中不能有相同id 的对象
		 */
		// session.update(news);
	}

	@Test
	public void testSaveOrUpdate() {
		// Transient object
		News news = new News();
		news.setDate(new Date());
		news.setAuthor("Test");
		news.setTitle("Test");
		/*
		 * 如果不设置id 则为Transient object 调用Save 变成Persist对象 如果设置为一个数据表中已有的id, 则被当作Detached
		 * Object 进行 Update 如果设置一个新id, 表中没有, 报错. 使用saveOrUpdate时如果设置id 则认为表里有数据
		 */
//		news.setId(1);
		session.saveOrUpdate(news);
	}

	/*
	 * delete 在提交事务时执行, 只要表里有id就删
	 */
	@Test
	public void testDelete() {
		News news = new News();
		news.setId(1);
		session.delete(news);
	}

	/*
	 * 刷新缓存
	 */
	@Test
	public void testRefresh() {
		News news = session.get(News.class, 2);
		System.out.println(news);

//		设置hibern事务隔离级别,才有效
		session.refresh(news);
		System.out.println(news);
	}

	/*
	 * 清理缓存
	 */
	@Test
	public void testClear() {
		News news = session.get(News.class, 2);
		session.clear();
//		清理缓存后将重新发送select
//		News news2 = session.get(News.class, 2);
		System.out.println(news);
	}

	/*
	 * 计划执行insert, flush缓存的时候 save(): 将临时对象转化为持久化对象 和persist 区别: 如果对象set一个新id, setId
	 * 那么执行persist 会报错, save不会报错但也不会更改id(自动分配情况下). save之前setId无效,持久化对象id不能该 save()
	 * 立刻分配id, 返回值为id; persist() 在进行flush()的时候在插入, 无返回值
	 */
	@Test
	public void testSave() {
		News news = new News();
		news.setAuthor("Test");
		news.setTitle("SQL");
		news.setDate(new Date());

		System.out.println(news);
//		Save 持久化对象: 持久化对象id不能重复,代理主键时setId无效
		session.save(news);
		// 持久化对象id不能该
//		news.setId(101);
		System.out.println(news);
	}

	@Test
	public void testBlob() throws IOException, SQLException {
//		News news = new News();
//		news.setAuthor("LIU");
//		news.setTitle("Hibernate");
//		news.setContent("Content");
//		news.setDate(new Date());
//		
//		InputStream stream = new FileInputStream("yann.jpeg");
//		Blob image = Hibernate.getLobCreator(session).createBlob(stream, stream.available());
//		news.setImage(image);
//		session.save(news);
		/*
		 * read
		 */
		News news = session.get(News.class, 1);
		Blob image = news.getImage();
		InputStream ins = image.getBinaryStream();
		System.out.println(ins.available());
	}

	@Test
	public void testComponent() {
		Worker worker = new Worker();
		Pay pay = new Pay();
		
		pay.setMonthlyPay(30000);
		pay.setYearPay(400000);
		pay.setVocationWithPay(30000);
		
		worker.setName("LIU");
		worker.setPay(pay);

		session.save(worker);

	}

}

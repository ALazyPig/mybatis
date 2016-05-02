package com.zhaojun.maybatis.test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.zhaojun.mybatis.model.Student;
import com.zhaojun.mybatis.model.User;
import com.zhaojun.mybatis.studentDao.StudentDao;

public class mybatisTest {
	private static SqlSessionFactory sqlSessionFactory;
	private static Reader reader;
	static{
		try {
			reader = Resources.getResourceAsReader("Configuration.xml");
			 sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static SqlSessionFactory getSession(){
		return sqlSessionFactory;
	}
	@Test
	public void test1() {
		SqlSession session = sqlSessionFactory.openSession();
		Student student = (Student)session.selectOne("com.zhaojun.mybatis.queryStudent","1");
		session.commit();
		session.close();
		System.out.println(student);
	}
	@Test
	public void test2() {
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		Student student = studentDao.queryStudent("3");
		session.commit();
		session.close();
		System.out.println(student);
	}
	@Test
	public void test3() {
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		List<Student> listStudent= studentDao.selectStudent();
		session.commit();
		session.close();
		System.out.println(listStudent);
	}
	//增加数据时必须提交事务
	@Test
	public void test4() {
		Student student= new Student();
		student.setAge(10);
		student.setId("11");
		student.setName("zhao");
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		studentDao.addStudent(student);
		session.commit();
		session.close();
	}
	
	@Test
	public void test5() {
		Student student= new Student();
		student.setAge(10);
		student.setId("1");
		student.setName("zhao");
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		studentDao.updateStudent(student);
		session.commit();
		session.close();
	}
	
	@Test
	public void test6() {
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		studentDao.deleteStudent("10");
		session.commit();
		session.close();
	}
	
	@Test
	public void test7() {
		SqlSession session = sqlSessionFactory.openSession();
		StudentDao studentDao = session.getMapper(StudentDao.class);
		List<User> list = studentDao.getStudentUser("1");
		System.out.println(list);
		session.commit();
		session.close();
	}
}

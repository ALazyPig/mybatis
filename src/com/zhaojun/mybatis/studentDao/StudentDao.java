package com.zhaojun.mybatis.studentDao;

import java.util.List;

import com.zhaojun.mybatis.model.Student;
import com.zhaojun.mybatis.model.User;

public interface StudentDao {
	public Student queryStudent(String id);
	public List<Student> selectStudent();  
	public void addStudent(Student student);
	public void updateStudent(Student student);
	public void deleteStudent(String id);
	public List<User> getStudentUser(String id);
}

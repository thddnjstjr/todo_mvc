package com.tenco.model;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TodoDAOImpl implements TodoDAO{

	private DataSource dataSource;
	
	public TodoDAOImpl() {
		
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			dataSource = (DataSource)ctx.lookup("java:comp/env/jdbc/MyDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addTodo(TodoDTO dto, int principalId) {
		
		String sql = "insert into todos(user_id, title, description, due_date,completed) values(?,?,?,?,?)";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				
				pstmt.setInt(1, principalId);
				pstmt.setString(2, dto.getTitle());
				pstmt.setString(3, dto.getDescription());
				pstmt.setString(4, dto.getDueDate());
				pstmt.setBoolean(5, dto.getCompleted());
				pstmt.executeUpdate();
				
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public TodoDTO getTodoById(int id) {
		TodoDTO todo = new TodoDTO();
		String sql = "select * from todos where id = ?";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setInt(1, id);
				ResultSet rs = pstmt.executeQuery();
				conn.commit();
				if(rs.next()) {
					todo.setCompleted(rs.getBoolean("completed"));
					todo.setDescription(rs.getString("description"));
					todo.setDueDate(rs.getString("due_date"));
					todo.setTitle(rs.getString("title"));
					todo.setUserId(rs.getInt("user_id"));
					todo.setId(rs.getInt("id"));
				} else {
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return todo;
	}

	@Override
	public List<TodoDTO> getTodosByUserId(int userId) {
		List<TodoDTO> todos = new ArrayList<>();
		String sql = "select * from todos where user_id = ?";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setInt(1, userId);
				ResultSet rs = pstmt.executeQuery();
				conn.commit();
				while(rs.next()) {
					TodoDTO todo = new TodoDTO();
					todo.setCompleted(rs.getBoolean("completed"));
					System.out.println(todo.getCompleted());
					todo.setDescription(rs.getString("description"));
					todo.setDueDate(rs.getString("due_date"));
					todo.setTitle(rs.getString("title"));
					todo.setUserId(rs.getInt("user_id"));
					todo.setId(rs.getInt("id"));
					todos.add(todo);
				} 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todos;
	}

	@Override
	public List<TodoDTO> getAllTodos() {
		List<TodoDTO> todos = new ArrayList<>();
		String sql = "select * from todos";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				ResultSet rs = pstmt.executeQuery();
				conn.commit();
				while(rs.next()) {
					TodoDTO todo = new TodoDTO();
					todo.setCompleted(rs.getBoolean("completed"));
					todo.setDescription(rs.getString("description"));
					todo.setDueDate(rs.getString("due_date"));
					todo.setTitle(rs.getString("title"));
					todo.setUserId(rs.getInt("user_id"));
					todo.setId(rs.getInt("id"));
					todos.add(todo);
				} 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return todos;
	}

	@Override
	public void updateTodo(TodoDTO dto, int principalId) {
		String sql = "update todos set title = ? , description = ? , due_date = ? , completed = ? where user_id = ? and id = ?";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setString(1, dto.getTitle());
				pstmt.setString(2, dto.getDescription());
				pstmt.setString(3, dto.getDueDate());
				pstmt.setBoolean(4, dto.getCompleted());
				pstmt.setInt(5, principalId);
				pstmt.setInt(6, dto.getId());
				System.out.println(dto.getTitle());
				System.out.println(principalId);
				System.out.println(dto.getId());
				int rowCount = pstmt.executeUpdate();
				conn.commit();
				System.out.println("수정 성공 : " + rowCount);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteTodo(int id, int principalId) {
		String sql = "delete from todos where id = ? and user_id = ?";
		try (Connection conn = dataSource.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)){
				pstmt.setInt(1, id);
				pstmt.setInt(2, principalId);
				pstmt.executeUpdate();
				System.out.println("삭제 성공!");
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

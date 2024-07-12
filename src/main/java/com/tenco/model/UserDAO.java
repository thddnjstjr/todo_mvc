package com.tenco.model;

import java.util.List;

public interface UserDAO {
	
	int addUser(UserDTO userDTO);
	UserDTO	getUserById(int id);
	UserDTO getUserByUsername(String username);
	List<UserDTO> getAllUsers();
	int updateUser(UserDTO user,int principalId); // ,,, WHERE id = 3
	int deleteUser(int id);
	
}

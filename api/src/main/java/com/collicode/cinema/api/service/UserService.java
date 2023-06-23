package com.collicode.cinema.api.service;

import com.collicode.cinema.api.dto.LoginDTO;
import com.collicode.cinema.api.dto.UserDTO;
import com.collicode.cinema.api.model.Login;
import com.collicode.cinema.api.model.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface UserService {
	
	List<UserDTO> findAllUsers();
	
	User save(User user);
	User findOne(Long id);
	User findByUsername(String username);
	User delete(User user);
	User getCurrentUser();
	String validateUser(User user);
//	String validateUserUpdate(User user);
//	String updateUser(User u);
	LoginDTO generateToken(Login login);
	String isValidLogout();
	String deactivateUser(Long id);
	
	public void setCurrentUser(User user);

}

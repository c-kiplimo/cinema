package com.collicode.cinema.api.controller;


import com.collicode.cinema.api.dto.UserDTO;
import com.collicode.cinema.api.model.Role;
import com.collicode.cinema.api.model.User;
import com.collicode.cinema.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin("*")
@RestController
@RequestMapping(value = "api/user")
public class UserController {
	
	@Autowired
	UserService userService;

	
	@RequestMapping(value = "/registration", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createUser(@RequestBody User user) {
		String responseToClient;
		if (userService.validateUser(user).equals("invalid")) {
			//userDTO.setUserInvalidInput("yes");
			responseToClient = "invalidInput";
		} else if (userService.findByUsername(user.getUsername()) != null
				|| userService.validateUser(user).equals("not unique")) {
			responseToClient = "emailOrUsernameAlreadyExist";
		} else {
			user.setRole(Role.USER);
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			userService.save(user);
			responseToClient = "success";
		}
		return new ResponseEntity<String>(responseToClient, HttpStatus.OK);

	}
	
	
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> usersDTO = userService.findAllUsers();
		return new ResponseEntity<List<UserDTO>>(usersDTO, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/getCurrentUser", method = RequestMethod.GET)
	public ResponseEntity<User> getCurrentUser() {
		User user = userService.getCurrentUser();
		// UserDTO userDTO = UserMapper.INSTANCE.entityToDTO(user);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		if (userService.findOne(id) == null) {
			return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
		}	
		User user = userService.findOne(id);
	
		return new ResponseEntity<UserDTO>(new UserDTO(user), HttpStatus.OK);
	}
	@RequestMapping(value = "/deactivateUser/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
		if (userService.findOne(id) == null) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		String response = userService.deactivateUser(id);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
}

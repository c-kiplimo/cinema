package com.collicode.cinema.api.controller;

import com.collicode.cinema.api.dto.LoginDTO;
import com.collicode.cinema.api.model.Login;
import com.collicode.cinema.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api")
public class LoginController {
	
	
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/welcomeTest", method = RequestMethod.GET)
	public String welcome() {
		return "test!!!";
	}
	
	@RequestMapping(value = "/welcomeTestIgnore", method = RequestMethod.GET)
	public String welcomeIgnore() {
		return "testIgnore!!!";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginDTO> generateToken(@RequestBody Login login) throws Exception{
		LoginDTO loginDTO = userService.generateToken(login);
		return new ResponseEntity<LoginDTO>(loginDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<String> Logout() throws Exception{
		String responseToClient = userService.isValidLogout();
		
		return new ResponseEntity<String>(responseToClient, HttpStatus.OK);
	}
}
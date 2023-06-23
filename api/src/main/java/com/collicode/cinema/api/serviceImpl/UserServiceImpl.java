package com.collicode.cinema.api.serviceImpl;


import com.collicode.cinema.api.dto.JWTLogin;
import com.collicode.cinema.api.dto.LoginDTO;
import com.collicode.cinema.api.dto.UserDTO;
import com.collicode.cinema.api.model.Login;
import com.collicode.cinema.api.model.Role;
import com.collicode.cinema.api.model.User;
import com.collicode.cinema.api.repository.UserRepository;
import com.collicode.cinema.api.security.JwtUtil;
import com.collicode.cinema.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Override
	public User findOne(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public List<UserDTO> findAllUsers() {
		UserDTO userDTO;
		List<User> users = userRepository.findAll();
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User u : users) {
			if(u.getRole().equals(Role.USER)) {
				userDTO = new UserDTO(u);
				usersDTO.add(userDTO);
			}
			
		}
		return usersDTO;
	}
	
	

	@Override
	public User save(User user) {

		return userRepository.save(user);
	}

	@Override
	public User delete(User user) {
		if (user == null)
			throw new IllegalArgumentException("Attempt to delete non-existing course.");

		userRepository.delete(user);
		return user;
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User currentUser = userRepository.findByUsername(currentPrincipalName);
		return currentUser;
	}
	
	@Override
	public void setCurrentUser(User user) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user.getId(), null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//Long id = Long.parseLong(auth.getName());
		
	}


	@Override
	public String validateUser(User user) {
		if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().matches("^(.+)@(.+)$") 
				|| user.getPassword() == null || user.getPassword().trim().isEmpty() || user.getNameSurname() == null || user.getNameSurname().trim().isEmpty()){
			return "invalid";
		}
		if (!isEmailUnique(user.getEmail())) {
			return "not unique";
		}
		return "valid";
	}

	private boolean isEmailUnique(String emal) {
		List<User> allUsers = userRepository.findAll();
		for (User u : allUsers) {
			if (u.getEmail().equals(emal))
				return false;
		}
		return true;
	}

	
	@Override
	public String deactivateUser(Long id) {
		try {
			User user = userRepository.findById(id).get(); //mora get() jos
			user.setDeleted(true);
			userRepository.save(user);
			return "success";
		} catch (Exception e) {
			return "fail";
		}
		
	}
	
	
	@Override
	public LoginDTO generateToken(Login login) {
		LoginDTO loginDTO = new LoginDTO();
		User user = new User();
		
		try {
//			authenticationManager.authenticate(
//					new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
			User userFromDB = findByUsername(login.getUsername());
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			if(!(userFromDB.getPassword().equals(login.getPassword()))) {
				if(encoder.matches(login.getPassword(), userFromDB.getPassword()) == false) {
					throw new Exception();
				}
				else {
					JWTLogin jwtDetails = new JWTLogin();
					jwtDetails.setRole(userFromDB.getRole().toString());
					jwtDetails.setUsername(userFromDB.getUsername());
					String token = jwtUtil.generateToken(jwtDetails);
					loginDTO = new LoginDTO(token, userFromDB.getNameSurname(), "no");
				}
			}
			else {
				JWTLogin jwtDetails = new JWTLogin();
				jwtDetails.setRole(userFromDB.getRole().toString());
				jwtDetails.setUsername(userFromDB.getUsername());
				String token = jwtUtil.generateToken(jwtDetails);
				loginDTO = new LoginDTO(token, userFromDB.getNameSurname(), "no");
			}	
		} catch (Exception e) {
			loginDTO = new LoginDTO();
			loginDTO.setMessageInvalidUsernameOrPassword("yes");
			//loginDTO.setUserNameSurname(user.getNameSurname());
			return loginDTO;
		}
		if(user.isDeleted()) {
			loginDTO = new LoginDTO();
			loginDTO.setMessageInvalidUsernameOrPassword("deactivatedUser");
			//loginDTO.setUserNameSurname(user.getNameSurname());
			return loginDTO;
		}
		return loginDTO;
	}
	
	@Override
	public String isValidLogout() {
		String responseToClient;
		if (getCurrentUser() != null) {
			SecurityContextHolder.clearContext();
			//getCurrentUser();
			responseToClient = "valid";
		} else {
			responseToClient = "invalid";
		}
		return responseToClient;
	}
	
	
}

package com.tasker.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tasker.domain.users.User;
import com.tasker.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	public User loadUserByUsername(String username) throws UsernameNotFoundException {		
		User user = userRepository.findByUserName(username);		
		if(user == null) throw new UsernameNotFoundException("username");
		
		User copy = new User.Builder()
				.userName(user.getUsername())
				.password(user.getPassword())
				.roles( user.getRoles())
				.id( user.getId())
				.build();
		
		return copy;
	}
	
	public User findByUserName ( String username ) {
		return userRepository.findByUserName(username);
	}
	
	public User saveUser( User user ) {
		return userRepository.saveUser(user);
	}
	public List<User> getUsers() {
		return userRepository.getUsers();
	}

	public User findOne(String uid) {
		System.out.printf("findOne by: %s \n", uid);
		return userRepository.findOne( uid );
	}
	public User deleteOne( String id ){
		return userRepository.deleteOne(id);
	}
	public User updateOne(String uid, String username, String password, String role ){
		System.out.printf("updateOne by: %s, %s, %s \n", username,password,role);
		return userRepository.updateOne( uid, username, password, role);
	}
	public boolean findDuplicateUser( String username, String id ) {
		return userRepository.findDuplicateUser( username, id );
	}
}

package com.tasker.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tasker.domain.users.User;
import com.tasker.domain.users.Role;

@Repository
public class UserRepository {
	
	public Map<String, User> users;
	public User user1, user2;
	
	public UserRepository() {
		
		users = new HashMap< String, User >();
		List<Role> roles = Arrays.asList( new Role[] { new Role("ROLE_ADMIN") } );
		
		user1 = new User.Builder()
				.userName("Bilbo")
				.id( "0")
				.password("Baggins")
				.roles( roles )
				.build();
		users.put( user1.getId(), user1 );
		
	}
	
	public User saveUser ( User user ) {
		
		user.setId( UUID.randomUUID().toString() );
		users.put(user.getId(), user);
		return user;
	}
	
	
	public User findOne( String id ) {
		
		for( User user : users.values() ) {
			if( user.getId().equals( id ) ) {
				return user;
			}
		}
		return null;
	}
	
	public User deleteOne( String id ) {
		for( User user : users.values() ) {
			if( user.getId().equals( id ) ) {
				users.remove(id);
				return user;
			}
		}
		return null;
	}
	public User updateOne( String id, String username, String password, String role ) {
		List<Role> roles = Arrays.asList( new Role(role) );

		for( User user : users.values() ) {
			if( user.getId().equals( id ) ) {
				user.setUsername(username);
				user.setPassword(password);
				user.setRoles(roles);
			}
		}
		return null;
	}
	
	public User findByUserName(String username) {
		
		for( User user : users.values() ) {
			if( user.getUsername().equals( username ) ) {
				return user;
			}
		}		
		return null;
	}

	public List<User> getUsers() {
		List<User> result = new ArrayList<User>();
		for( User user : users.values() ) {
			result.add(user);
		}
		return result;
	}
	
	public boolean findDuplicateUser( String username, String id ) {
		
		for( User user : users.values() ) {
			if( !user.getId().equals( id ) && user.getUsername().equals(username) ) {
				return true;
			}
		}
		return false;
	}

}

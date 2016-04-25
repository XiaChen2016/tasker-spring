package com.tasker.controllers;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tasker.services.TaskService;
import com.tasker.services.UserService;
import com.tasker.domain.tasks.Task;
import com.tasker.domain.users.Role;
import com.tasker.domain.users.User;

@Controller
public class TaskerController {
	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;
	

	@RequestMapping(value = "/", method = RequestMethod.GET )
	public String getLoginPage() {
		return "resources/index.html";
	}
	
	@RequestMapping(value = "/api/user", method = RequestMethod.GET )
	@ResponseBody
	public User login(Principal p, HttpServletResponse response ) throws IOException {
		User user = userService.loadUserByUsername (p.getName() );
		if ( user.equals(null) )
			response.sendError(400,"Invalid username or password!");
		return user;
	}

	// Create a user
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/tasker/user", method = RequestMethod.POST )
	@ResponseBody
	public User createUser(   @RequestParam(required=true) String username,
								@RequestParam(required=true) String password,
								@RequestParam(required=true) String role,
								HttpServletResponse response ) throws IOException {
		if( userService.findByUserName( username ) == null ) {
				List<Role> roles = Arrays.asList( new Role[] { new Role(role) } );
		
				User user = new User.Builder()
						.userName(username)
						.password(password)
						.roles( roles )
						.build();
				return userService.saveUser(user);
			}
			else 
				response.sendError(400,"User already exist!");
				return null; 
	}

	// Get list of users
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/tasker/users", method = RequestMethod.GET )
	@ResponseBody
	public List<User> getAllUsers( Principal p ) {
		List<User> users = userService.getUsers();
		return users;
	}

	// Edit a user
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/tasker/user/{uid}", method = RequestMethod.PUT)
	@ResponseBody
	public User updateUserInfo( @RequestParam(required=true) String newUsername,
								@RequestParam(required=true) String password,
								@RequestParam(required=true) String role,
								@RequestParam(required=true) String tid,
								@PathVariable String uid, HttpServletResponse response) throws IOException {
		
		// Check if the role is provided with right word, and if user name or password is missing.
		if( (!role.equals("ROLE_ADMIN") &&  !role.equals("ROLE_USER") )  || newUsername.length()<1|| password.length()<1 )
			response.sendError(400,"Invalid information!");
		
		// Check if user doesn't exist.
		else if (userService.findOne(uid).equals(null))
			response.sendError(400,"User doesn't exist!");
		
		// Check if someone is trying to edit Bilbo's information.
		else if ( tid.equals("0"))
			response.sendError(403,"Can't edit Admin Bilbo's information!");
		
		// Check if there already exist one username same as the new one.
		else if ( userService.findDuplicateUser( newUsername, tid ) )
			response.sendError(400,"Find dupliate username!");
		else
			return userService.updateOne(uid, newUsername, password, role);
		return null;
	}

	//Delete a user
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/tasker/user/{uid}", method = RequestMethod.DELETE)
	@ResponseBody
	public User deleteUser( @PathVariable String uid, HttpServletResponse response ) throws IOException {
		
		// Check if someone is trying to delete Bilbo
		if( uid.equals("0"))
			response.sendError(403,"You can't delete this user!");
		
		// Check if the user exists.
		else if (userService.findOne(uid).equals(null))
			response.sendError(400,"User doesn't exist!");
		else
			return userService.deleteOne(uid);
		return null;
	}
	

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@RequestMapping(value = "/user", method = RequestMethod.GET )
	public String returnUserPage( @AuthenticationPrincipal User user ) {
		if( user.getRoles().get(0).getName().equals("ROLE_ADMIN"))
			return "resources/admin.html";
		else
			return "resources/home.html";
	}
	
	// User get list of tasks
	@RequestMapping(value = "/tasker/users/{uid}/tasks", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_USER') and #user.id == #uid")
	@ResponseBody
	public List<Task<String>> getTasks( @PathVariable String uid, 
										@RequestParam(required=false, defaultValue="no") String incomplete,
										@RequestParam(required=false, defaultValue="no") String overdue,
										@AuthenticationPrincipal User user ) throws ParseException {
		if(  incomplete.equals("yes") && overdue.equals("no") ){
			return taskService.getIncompletedTasks(uid);
		}
		else if ( incomplete.equals("no") && overdue.equals("yes")) {
			return taskService.getOverDueTasks(uid);
		}
		else
			return taskService.getTaskByOwner(uid);
	}
	
	// User create a task
	@RequestMapping(value = "/tasker/users/{uid}/tasks", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_USER') and #user.id == #uid")
	@ResponseBody
	public Task<String> createTask( @PathVariable String uid,
									@RequestParam(required=true) String description,
									@RequestParam(required=true) String dueDate,
									@RequestParam(required=true) String color,
									@AuthenticationPrincipal User user ) {
		String[] temp = dueDate.split("/");
		String newDate = temp[2]+"-"+temp[0]+ "-"+ temp[1];
		@SuppressWarnings("unchecked")
		Task<String> task = new Task.Builder()
				.description(description)
				.due(newDate)
				.color(color)
				.build();
		task.setOwnerId( user.getId() );		
		return taskService.saveTask( uid, task );
	}

	// User edit a task
	@RequestMapping(value = "/tasker/users/{uid}/tasks/{tid}", method = RequestMethod.PUT)
	@PreAuthorize("hasAnyRole('ROLE_USER') and #user.id == #uid")
	@ResponseBody
	public Task<String> updateTask( @PathVariable String uid,
			 						@PathVariable String tid,
									@RequestParam(required=true) String description,
									@RequestParam(required=true) String dueDate,
									@RequestParam(required=true) String color,
									@RequestParam(required=true) String completed,
									@AuthenticationPrincipal User user,
									HttpServletResponse response) {
		boolean tempCompleted;
		if( completed.equals("true") )
			 tempCompleted = true;
		else tempCompleted = false;
		String[] temp = dueDate.split("/");
		String newDate = temp[2]+"-"+temp[0]+ "-"+ temp[1];
		@SuppressWarnings("unchecked")
		Task<String> task = new Task.Builder()
				.description(description)
				.due(newDate)
				.color(color)
				.id(tid)
				.ownerId(uid)
				.completed(tempCompleted)
				.build();
		return taskService.update( task );
	}
	
	// User delete a task
	@RequestMapping(value = "/tasker/users/{uid}/tasks/{tid}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyRole('ROLE_USER') and #user.id == #uid")
	@ResponseBody
	public String deleteTask( @PathVariable String uid,
									@PathVariable String tid,
									@AuthenticationPrincipal User user,
									HttpServletResponse response) {

		return taskService.deleteTask( tid );
	}
}


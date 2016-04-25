package com.tasker.services;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tasker.domain.tasks.Task;
import com.tasker.repositories.TaskRepository;

@Service
public class TaskService {
	@Autowired
	private TaskRepository<String> taskRepository;
	
	public List<Task<String>> getTaskByOwner( String uid ) {
		return taskRepository.getTasksByOwner(uid);
	}
	
	public Task<String> saveTask(String uid, Task<String> task) {
		task.setOwnerId( uid );
		return taskRepository.saveTask(task);
	}
	
	public Task<String> update( Task<String> task ) {
		return taskRepository.update(task);
	}
	
	public String deleteTask( String tid ){
		return taskRepository.deleteTask(tid);
	}
	public List<Task<String>> getIncompletedTasks ( String uid ){
		return taskRepository.getIncompletedTasks( uid );
	}
	public List<Task<String>> getOverDueTasks ( String uid ) throws ParseException{
		return taskRepository.getOverDueTasks( uid );
	}
}

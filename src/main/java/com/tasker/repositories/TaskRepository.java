package com.tasker.repositories;

import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.tasker.domain.tasks.Task;

@Repository
public class TaskRepository<E> {
	public Map<String, Task<E>> tasks;
	
	public TaskRepository() {
		tasks = new HashMap<String, Task<E>>();
	}
	
	@SuppressWarnings("deprecation")
	public boolean overDue( String date ) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date temp = sdf.parse( date );
		temp.setHours(23);
		temp.setMinutes(59);
		temp.setSeconds(59);
		Date today = new Date();
		
		if( today.compareTo( temp ) > 0 ){
			return true;
		}
		return false;
	}
	
	public Task<E> saveTask( Task<E> task) {
		task.setId( UUID.randomUUID().toString() );
		tasks.put(task.getId(), task);
		return task;
	}
	
	public List<Task<E>> getTasksByOwner( String uid ) {
		List<Task<E>> result = new ArrayList<Task<E>>();
		for( Task<E> task : tasks.values() ) {
			if( task.getOwnerId() != null &&
					task.getOwnerId().equals( uid ) ) {
				result.add( task );
			}
		}
		return result;
	}
	public List<Task<E>> getIncompletedTasks ( String uid ) {
		List<Task<E>> result = new ArrayList<Task<E>>();
		for( Task<E> task : tasks.values() ) {
			if( task.getOwnerId() != null &&
					task.getOwnerId().equals( uid )
					&& !task.isCompleted() ) {
				result.add( task );
			}
		}
		return result;
	}

	public List<Task<E>> getOverDueTasks ( String uid ) throws ParseException {
		List<Task<E>> result = new ArrayList<Task<E>>();
		for( Task<E> task : tasks.values() ) {
			if( task.getOwnerId() != null &&
					task.getOwnerId().equals( uid )
					&& overDue(task.getDue()) ) {
				result.add( task );
			}
		}
		return result;
	}
	public String deleteTask( String tid ) {
		tasks.remove( tid );
		return tid;
	}
	
	public Task<E> update( Task<E> t ) {

		for( Task<E> task : tasks.values() ) {
			if( task.getId().equals( t.getId() ) ) {
				System.out.println("EQUAL!!");
				task.setDescription( t.getDescription() );
				task.setColor( t.getColor() );
				task.setDue( t.getDue() );
				task.setCompleted( t.isCompleted() );
			}
		}
		return t;
	}
}
package com.tasker.domain.tasks;

public class Task<E> {

	private String description;
	private String due;
	private String color;
	private boolean completed;
	private String id;
	private String ownerId;
	
	public String getDescription() {
		return description;
	}
	public void setDescription( String description) {
		this.description = description;
	}
	
	public String getDue() {
		return due;
	}
	public void setDue( String due ) {
		this.due = due;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor( String color ) {
		this.color = color;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted( Boolean completed ) {
		this.completed = completed;
	}
	
	public String getId() {
		return id;
	}
	public void setId( String id ) {
		this.id = id;
	}
	
	public String getOwnerId() {
		return this.ownerId;
	}
	public void setOwnerId( String ownerId ) {
		this.ownerId = ownerId;
	}
	
	public Task( Builder builder ) {
		
		this.description = builder.description;
		this.due = builder.due;
		this.color = builder.color;
		this.completed = builder.completed;
		this.id = builder.id;
		this.ownerId = builder.ownerId;

	}
	
	public static class Builder {
		
		private String description;
		private String due;
		private String color;
		private boolean completed;
		private String id;
		private String ownerId;
		
		public Builder description( String description ) {
			this.description = description;
			return this;
		}
		public Builder due( String due ) {
			this.due = due;
			return this;
		}
		public Builder color( String color ) {
			this.color = color;
			return this;
		}
		public Builder completed( boolean completed ) {
			this.completed = completed;
			return this;
		}
		public Builder id( String id ) {
			this.id = id;
			return this;
		}
		public Builder ownerId( String ownerId ){
			this.ownerId = ownerId;
			return this;
		}
		@SuppressWarnings("rawtypes")
		public Task build() {
			return new Task(this);
		}
	}
}

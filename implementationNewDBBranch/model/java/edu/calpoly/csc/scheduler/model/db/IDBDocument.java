package edu.calpoly.csc.scheduler.model.db;

public interface IDBDocument extends IDBObject {
	public String getName();
	public void setName(String name);
	
	public boolean isTrashed();
	public void setIsTrashed(boolean isTrashed);
}
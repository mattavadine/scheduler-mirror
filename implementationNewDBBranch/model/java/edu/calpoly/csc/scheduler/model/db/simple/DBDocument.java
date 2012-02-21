package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class DBDocument extends DBObject implements IDBDocument {
	String name;
	boolean isTrashed;
	Integer originalID; // null if this is an original
	int startHalfHour;
	int endHalfHour;
	
	public DBDocument(Integer id, String name, Integer originalID, int startHalfHour, int endHalfHour) {
		super(id);
		this.name = name;
		this.originalID = originalID;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
	}
	
	public DBDocument(DBDocument that) {
		this(that.id, that.name, that.originalID, that.startHalfHour, that.endHalfHour);
	}
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String name) { this.name = name; }

	@Override
	public boolean isTrashed() { return isTrashed; }
	@Override
	public void setIsTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }

	@Override
	public int getStartHalfHour() { return startHalfHour; }
	@Override
	public void setStartHalfHour(int halfHour) { startHalfHour = halfHour; }

	@Override
	public int getEndHalfHour() { return endHalfHour; }
	@Override
	public void setEndHalfHour(int halfHour) { endHalfHour = halfHour; }
}

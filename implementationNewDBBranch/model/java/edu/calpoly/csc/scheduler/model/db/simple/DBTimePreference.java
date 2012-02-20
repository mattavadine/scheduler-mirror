package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;

public class DBTimePreference extends DBObject implements IDBTimePreference {
	int instructorID;
	int timeID;
	int preference;
	
	public DBTimePreference(Integer id, int instructorID, int timeID, int preference) {
		super(id);
		this.instructorID = instructorID;
		this.timeID = timeID;
		this.preference = preference;
	}
	public DBTimePreference(DBTimePreference that) {
		this(that.id, that.instructorID, that.timeID, that.preference);
	}
	
	@Override
	public int getPreference() { return preference; }
	@Override
	public void setPreference(int preference) { this.preference = preference; }
}
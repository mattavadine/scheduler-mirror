package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class DBSchedule extends DBObject implements IDBSchedule {
	Integer documentID;
	
	public DBSchedule(Integer id, Integer documentID) {
		super(id);
		this.documentID = documentID;
	}
	
	public DBSchedule(DBSchedule that) {
		this(that.id, that.documentID);
	}

}

package scheduler.model.db.simple;

import scheduler.model.db.IDBDocument;

public class DBDocument extends DBObject implements IDBDocument {
	private static final long serialVersionUID = 1337L;
	
	String name;
	boolean isTrashed;
	Integer originalID; // null if this is an original
	int startHalfHour;
	int endHalfHour;
	Integer staffInstructorID; // null if there is none
	Integer tbaLocationID; // null if there is none
	Integer chooseForMeInstructorID; // null if there is none
	Integer chooseForMeLocationID; //null if there is none
	
	public DBDocument(Integer id, String name, Integer originalID, int startHalfHour, int endHalfHour, Integer staffInstructorID, 
			Integer tbaLocationID, Integer chooseForMeInstructorID, Integer chooseForMeLocationID, boolean isTrashed) {
		super(id);
		this.name = name;
		this.originalID = originalID;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		this.staffInstructorID = staffInstructorID;
		this.tbaLocationID = tbaLocationID;
		this.chooseForMeInstructorID = chooseForMeInstructorID;
		this.chooseForMeLocationID = chooseForMeLocationID;
		this.isTrashed = isTrashed;
	}
	
	public DBDocument(DBDocument that) {
		this(that.id, that.name, that.originalID, that.startHalfHour, that.endHalfHour, that.staffInstructorID, that.tbaLocationID, 
				that.chooseForMeInstructorID, that.chooseForMeLocationID, that.isTrashed);
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

	public void sanityCheck() {
		assert(name != null);
		assert(staffInstructorID != null);
		assert(tbaLocationID != null);
		assert(chooseForMeInstructorID != null);
		assert(chooseForMeLocationID != null);
	}
}

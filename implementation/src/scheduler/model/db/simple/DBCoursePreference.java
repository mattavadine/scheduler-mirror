package scheduler.model.db.simple;

import scheduler.model.db.IDBCoursePreference;

public class DBCoursePreference extends DBObject implements IDBCoursePreference {
	private static final long serialVersionUID = 1337L;
	
	Integer instructorID;
	Integer courseID;
	Integer preference;
	
	public DBCoursePreference(Integer id, Integer instructorID, Integer courseID, int preference) {
		super(id);
		this.instructorID = instructorID;
		this.courseID = courseID;
		this.preference = preference;
	}
	public DBCoursePreference(DBCoursePreference that) {
		this(that.id, that.instructorID, that.courseID, that.preference);
	}

	@Override
	public int getPreference() { return preference; }
	@Override
	public void setPreference(int preference) { this.preference = preference; }

	public void sanityCheck() {
		assert(instructorID != null);
		assert(courseID != null);
		assert(preference != null);
	}
}

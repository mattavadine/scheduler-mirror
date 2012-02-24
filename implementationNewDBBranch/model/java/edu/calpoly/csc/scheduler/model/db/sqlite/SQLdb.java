package edu.calpoly.csc.scheduler.model.db.sqlite;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCourseAssociation;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBObject;
import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDBUsedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.simple.DBUsedEquipment;

public class SQLdb implements IDatabase {
	
	Connection conn = null;
	
	public static void main(String[] args) throws Exception {
		SQLdb db = new SQLdb();
		db.openConnection();
	}
	

	public void openConnection() throws SQLException, Exception
	{
		Class.forName("org.sqlite.JDBC");
		conn =
			DriverManager.getConnection("jdbc:sqlite:database.db");
		System.out.println("Connected to database");
	}
	
	public void closeConnection() throws SQLException
	{
		if(conn != null)
		{
			conn.close();
			System.out.println("Database connection closed");
		}
		else
		{
			System.out.println("Connection is null, never opened");
		}
	}
	
	public Connection getConnection()
	{
		return conn;
	}


	@Override
	public IDBUser findUserByUsername(String username) throws DatabaseException {
		IDBUser user = null;
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("select * from userdata where userid = ?");
			stmnt.setString(1, username);
			
			ResultSet rs = stmnt.executeQuery();
			if (rs.next())
				user = new SQLUser(rs.getInt("id"), rs.getString("username"), rs.getBoolean("isAdmin"));
			else
				throw new NotFoundException();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		return user;
	}

	@Override
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new SQLUser(null, username, isAdmin);
	}


	@Override
	public void insertUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("insert into userdata (username, isAdmin) values (?, ?)");
			stmnt.setString(1, user.getUsername());
			stmnt.setBoolean(2, user.isAdmin());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public void updateUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("update userdata set username = ?, isAdmin = ?," +
					" where id = ?");
			stmnt.setString(1, user.getUsername());
			stmnt.setBoolean(2, user.isAdmin());
			stmnt.setInt(3, user.getID());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public void deleteUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("delete from userdata where id = ?");
			stmnt.setInt(1, user.getID());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public Collection<IDBDocument> findAllDocuments() throws DatabaseException {
		ArrayList<IDBDocument> docs = new ArrayList<IDBDocument>();
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("select * from document inner join workingcopy using (id)");
			
			ResultSet rs = stmnt.executeQuery();
			while (rs.next()) {
				docs.add(new SQLDocument(rs.getInt("id"), rs.getString("name"), rs.getInt("originalDocID"),
						rs.getInt("startHalfHour"), rs.getInt("endHalfHour")));
			}
			if (docs.size() == 0)
			{
				throw new NotFoundException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		
		return docs;
	}


	@Override
	public IDBDocument findDocumentByID(int id) throws DatabaseException {
		SQLDocument doc = null;
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("select * from document inner join workingcopy using (id)" +
					" where id = ?");
			stmnt.setInt(1, id);
			
			ResultSet rs = stmnt.executeQuery();
			if (rs.next()) {
				doc = new SQLDocument(rs.getInt("id"), rs.getString("name"), rs.getInt("originalDocID"),
						rs.getInt("startHalfHour"), rs.getInt("endHalfHour"));
			}
			else {
				throw new NotFoundException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		
		return doc;
	}


	@Override
	public void insertDocument(IDBDocument document) throws DatabaseException {
		PreparedStatement stmnt = null;
		SQLDocument doc = (SQLDocument) document;
		
		try {
			stmnt = conn.prepareStatement("insert into document (name, isTrash, startHalfHour, endHalfHour) values (?, ?, ?, ?)");
			stmnt.setString(1, doc.getName());
			stmnt.setBoolean(2, doc.isTrashed());
			stmnt.setInt(3, doc.getStartHalfHour());
			stmnt.setInt(4, doc.getEndHalfHour());
			
			stmnt.executeUpdate();
			
			if (doc.getOriginalId() != null) {
				stmnt = conn.prepareStatement("insert into workingcopy (id, originalDocID) values (?, ?)");
				stmnt.setInt(1, doc.getID());
				stmnt.setInt(2, doc.getOriginalId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public IDBDocument assembleDocument(String name, int startHalfHour,
			int endHalfHour) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateDocument(IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteDocument(IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument findDocumentForSchedule(IDBSchedule schedule)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isOriginalDocument(IDBDocument doc) throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean documentIsWorkingCopy(IDBDocument document)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBDocument getOriginalForWorkingCopyDocument(IDBDocument rawDocument)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(
			IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disassociateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBSchedule> findAllSchedulesForDocument(
			IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule findScheduleByID(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule assembleSchedule() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertSchedule(IDBDocument containingDocument,
			IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateSchedule(IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteSchedule(IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBScheduleItem> findScheduleItemsBySchedule(
			IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(
			IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBScheduleItem findScheduleItemByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced,
			boolean isConflicted) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBLocation getScheduleItemLocation(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getScheduleItemCourse(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor getScheduleItemInstructor(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setScheduleItemCourse(IDBScheduleItem underlying,
			IDBCourse findCourseByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemLocation(IDBScheduleItem underlying,
			IDBLocation findLocationByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemInstructor(IDBScheduleItem underlying,
			IDBInstructor findInstructorByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBLocation> findLocationsForDocument(IDBDocument document)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation findLocationByID(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation assembleLocation(String room, String type,
			String maxOccupancy, boolean isSchedulable)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertLocation(IDBDocument containingDocument,
			IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateLocation(IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteLocation(IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBCourse> findCoursesForDocument(IDBDocument document)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse findCourseByID(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse assembleCourse(String name, String catalogNumber,
			String department, String wtu, String scu, String numSections,
			String type, String maxEnrollment, String numHalfHoursPerWeek,
			boolean isSchedulable) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertCourse(IDBDocument underlyingDocument, IDBCourse course)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCourse(IDBCourse course) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCourse(IDBCourse course) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument findDocumentForCourse(IDBCourse underlyingCourse)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBCourseAssociation> getAssociationsForLecture(
			IDBCourse lectureCourse) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLecture(IDBCourseAssociation association)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLab(IDBCourseAssociation association)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateLectureAndLab(IDBCourse lecture, IDBCourse lab)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(
			IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor findInstructorByID(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor assembleInstructor(String firstName, String lastName,
			String username, String maxWTU, boolean isSchedulable)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertInstructor(IDBDocument containingDocument,
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateInstructor(IDBInstructor instructor)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteInstructor(IDBInstructor instructor)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference findTimePreferenceByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference findTimePreferenceForInstructorAndTime(
			IDBInstructor instructor, IDBTime time) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference assembleTimePreference(int preference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertTimePreference(IDBInstructor ins, IDBTime time,
			IDBTimePreference timePreference) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(
			IDBInstructor instructor, IDBCourse course)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference assembleCoursePreference(int preference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBEquipmentType findEquipmentTypeByDescription(
			String equipmentTypeDescription) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBEquipmentType> findAllEquipmentTypes()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(
			IDBCourse course) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public DBUsedEquipment assembleUsedEquipment() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertUsedEquipment(IDBCourse course,
			IDBEquipmentType equipmentType, IDBUsedEquipment equip)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(
			IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBProvidedEquipment assembleProvidedEquipment()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertProvidedEquipment(IDBLocation location,
			IDBEquipmentType equipmentType, IDBProvidedEquipment equip)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(
			IDBCourse underlying) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(
			IDBOfferedDayPattern offered) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertOfferedDayPattern(IDBCourse underlying,
			IDBDayPattern dayPattern, IDBOfferedDayPattern pattern)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isInserted(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBObject findDocumentForInstructor(
			IDBInstructor underlyingInstructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBInstructor getDocumentStaffInstructor(
			IDBDocument underlyingDocument) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation getDocumentTBALocation(IDBDocument underlyingDocument)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setDocumentStaffInstructor(IDBDocument underlyingDocument,
			IDBInstructor underlyingInstructor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setDocumentTBALocation(IDBDocument underlyingDocument,
			IDBLocation underlyingLocation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) {
		// TODO Auto-generated method stub
		
	}
	
}

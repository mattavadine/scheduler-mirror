package scheduler.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;
import scheduler.model.db.IDBCoursePreference;
import scheduler.model.db.IDBDayPattern;
import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBEquipmentType;
import scheduler.model.db.IDBInstructor;
import scheduler.model.db.IDBLocation;
import scheduler.model.db.IDBObject;
import scheduler.model.db.IDBOfferedDayPattern;
import scheduler.model.db.IDBScheduleItem;
import scheduler.model.db.IDBTime;
import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.IDBUser;
import scheduler.model.db.IDatabase;
import scheduler.model.db.IDatabase.NotFoundException;

public class Model {
	private static String maxWTU = "1000000";
	
	abstract static class Cache<DecoratedT extends ModelObject, UnderlyingT extends IDBObject> {
		HashMap<Integer, DecoratedT> cache = new HashMap<Integer, DecoratedT>();

		private DecoratedT decorateAndPut(UnderlyingT underlying) {
			DecoratedT result = decorate(underlying);
			cache.put(underlying.getID(), result);
			return result;
		}
		
		public DecoratedT decorateAndPutIfNotPresent(UnderlyingT underlying) {
			DecoratedT result = cache.get(underlying.getID());
			if (result == null)
				result = decorateAndPut(underlying);
			return result;
		}

		public DecoratedT findByID(int id) throws DatabaseException {
			DecoratedT result = cache.get(id);
			if (result == null)
				result = decorateAndPut(loadFromDatabase(id));
			return result;
		}
		
		protected abstract DecoratedT decorate(UnderlyingT underlying);
		
		protected abstract UnderlyingT loadFromDatabase(int id) throws NotFoundException, DatabaseException;
		
		void insert(DecoratedT obj) throws DatabaseException {
			insertIntoDatabase(obj);
			cache.put(obj.getID(), obj);
		}
		
		protected abstract void insertIntoDatabase(DecoratedT obj) throws DatabaseException;

		public void delete(DecoratedT obj) throws DatabaseException {
			cache.remove(obj.getID());
			removeFromDatabase(obj);
		}
		
		protected abstract void removeFromDatabase(DecoratedT obj) throws DatabaseException;

		public void update(DecoratedT obj) throws DatabaseException {
			assert(!obj.isTransient());
			assert(cache.containsKey(obj.getID()));
			assert(cache.get(obj.getID()) == obj);
			updateInDatabase(obj);
		}
		
		protected abstract void updateInDatabase(DecoratedT obj) throws DatabaseException;

		boolean inCache(UnderlyingT obj) { return cache.containsKey(obj.getID()); }
	}

	final IDatabase database;
	
	public Model() {
		this.database = new scheduler.model.db.simple.Database();
	}
	
	public Model(IDatabase database) {
		this.database = database;
	}

	
	
	
	// USERS
	
	Cache<User, IDBUser> userCache = new Cache<User, IDBUser>() {
		protected User decorate(IDBUser underlying) {
			return new User(Model.this, underlying);
		}
		protected IDBUser loadFromDatabase(int id) throws DatabaseException {
			throw new UnsupportedOperationException();
		}
		protected void insertIntoDatabase(User obj) throws DatabaseException {
			database.insertUser(obj.underlyingUser);
		}
		protected void removeFromDatabase(User obj) throws DatabaseException {
			database.deleteUser(obj.underlyingUser);
		}
		protected void updateInDatabase(User obj) throws DatabaseException {
			database.updateUser(obj.underlyingUser);
		}
	};

	public User findUserByUsername(String username) throws NotFoundException, DatabaseException {
		IDBUser underlyingUser = database.findUserByUsername(username);
		return userCache.decorateAndPutIfNotPresent(underlyingUser);
	}

	public User createTransientUser(String username, boolean b) {
		return new User(Model.this, database.assembleUser(username, b));
	}
	
	
	
	// DOCUMENTS

	Cache<Document, IDBDocument> documentCache = new Cache<Document, IDBDocument>() {
		protected Document decorate(IDBDocument underlying) {
			return new Document(Model.this, underlying);
		}
		protected IDBDocument loadFromDatabase(int id) throws DatabaseException {
			return database.findDocumentByID(id);
		}
		protected void insertIntoDatabase(Document obj) throws DatabaseException {
			database.insertDocument(obj.underlyingDocument);
		}
		protected void removeFromDatabase(Document obj) throws DatabaseException {
			database.deleteDocument(obj.underlyingDocument);
		}
		protected void updateInDatabase(Document obj) throws DatabaseException {
			database.updateDocument(obj.underlyingDocument);
		}
	};

	public Document createAndInsertDocumentWithSpecialInstructorsAndLocations(String name, int startHalfHour, int endHalfHour) throws DatabaseException {
		Document document = createTransientDocument(name, startHalfHour, endHalfHour)
				.insert();

		document.setStaffInstructor(createTransientInstructor("STAFF", "STAFF", "STAFF", maxWTU, true)
				.setDocument(document)
				.insert());
		
		document.setTBALocation(createTransientLocation("TBA", "Lecture", maxWTU, true)
				.setDocument(document)
				.insert());

		document.setChooseForMeInstructor(createTransientInstructor("ChooseForMe", "ChooseForMe", "ChooseForMe", maxWTU, true)
				.setDocument(document)
				.insert());
		
		document.setChooseForMeLocation(createTransientLocation("ChooseForMe", "ChooseForMe", maxWTU, true)
				.setDocument(document)
				.insert());
		
		document.update();
		
		return document;
	}
	
	public Document createTransientDocument(String name, int startHalfHour, int endHalfHour) throws DatabaseException {
		return new Document(this, database.assembleDocument(name, startHalfHour, endHalfHour));
	}
	
	public Document findDocumentByID(int documentID) throws DatabaseException {
		return documentCache.findByID(documentID);
	}

	public Collection<Document> findAllDocuments() throws DatabaseException {
		Collection<Document> result = new LinkedList<Document>();
		for (IDBDocument underlying : database.findAllDocuments())
			result.add(documentCache.decorateAndPutIfNotPresent(underlying));
		return result;
	}

	void disassociateWorkingCopyFromOriginal(Document workingCopyDocument, Document original) throws DatabaseException{
		database.disassociateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, original.underlyingDocument);
	}

	Document getWorkingCopyForOriginalDocumentOrNull(Document originalDocument) throws DatabaseException {
		IDBDocument underlying = database.getWorkingCopyForOriginalDocumentOrNull(originalDocument.underlyingDocument);
		if (underlying == null)
			return null;
		return new Document(this, underlying);
	}
	
	public void copyDocument(Document existingDocument, Document newDocument) throws DatabaseException {
//		IDBDocument newUnderlyingDocument = database.assembleDocument(newDocument, existingDocument.getStartHalfHour(), existingDocument.getEndHalfHour());
//		database.insertDocument(newUnderlyingDocument);
//		Document newDocument = new Document(this, underlying);
		
		assert(newDocument.getCourses().isEmpty());		
		assert(newDocument.getLocations(true).isEmpty());		
		assert(newDocument.getInstructors(true).isEmpty());
		assert(newDocument.getStaffInstructor() == null);
		assert(newDocument.getTBALocation() == null);
		assert(newDocument.getChooseForMeInstructor() == null);
		assert(newDocument.getChooseForMeLocation() == null);
		IDBDocument newUnderlyingDocument = newDocument.underlyingDocument;
		
		// Locations
		Map<Integer, IDBLocation> newDocumentLocationsByExistingDocumentLocationIDs = new HashMap<Integer, IDBLocation>();
		for (IDBLocation existingDocumentLocation : database.findLocationsForDocument(existingDocument.underlyingDocument)) {
			IDBLocation newDocumentLocation = database.assembleLocation(existingDocumentLocation.getRoom(), existingDocumentLocation.getType(), existingDocumentLocation.getMaxOccupancy(), existingDocumentLocation.isSchedulable());
			database.insertLocation(newUnderlyingDocument, newDocumentLocation);
			newDocumentLocationsByExistingDocumentLocationIDs.put(existingDocumentLocation.getID(), newDocumentLocation);

			for (IDBEquipmentType providedEquipment : database.findProvidedEquipmentByEquipmentForLocation(existingDocumentLocation).keySet()) {
				database.insertProvidedEquipment(newDocumentLocation, providedEquipment, database.assembleProvidedEquipment());
			}

			database.updateLocation(newDocumentLocation);
		}

		// Courses
		Map<Integer, IDBCourse> newDocumentCoursesByExistingDocumentCourseIDs = new HashMap<Integer, IDBCourse>();
		for (IDBCourse existingDocumentCourse : database.findCoursesForDocument(existingDocument.underlyingDocument)) {
			IDBCourse newDocumentCourse = database.assembleCourse(existingDocumentCourse.getName(), existingDocumentCourse.getCalatogNumber(), existingDocumentCourse.getDepartment(), existingDocumentCourse.getWTU(), existingDocumentCourse.getSCU(), existingDocumentCourse.getNumSections(), existingDocumentCourse.getType(), existingDocumentCourse.getMaxEnrollment(), existingDocumentCourse.getNumHalfHoursPerWeek(), existingDocumentCourse.isSchedulable());
			database.insertCourse(newUnderlyingDocument, newDocumentCourse);
			newDocumentCoursesByExistingDocumentCourseIDs.put(existingDocumentCourse.getID(), newDocumentCourse);
			
			Collection<IDBOfferedDayPattern> existingOfferedDayPatterns = database.findOfferedDayPatternsForCourse(existingDocumentCourse);
			System.out.println("old course " + existingDocumentCourse.getName() + " has " + existingOfferedDayPatterns.size() + " day patterns");
			
			for (IDBOfferedDayPattern existingOfferedDayPattern : existingOfferedDayPatterns) {
				IDBDayPattern dayPattern = database.getDayPatternForOfferedDayPattern(existingOfferedDayPattern);
				System.out.println("new course " + existingDocumentCourse.getName() + " has day pattern " + dayPattern.getDays().size());
				database.insertOfferedDayPattern(newDocumentCourse, dayPattern, database.assembleOfferedDayPattern());
			}

			for (IDBEquipmentType usedEquipment : database.findUsedEquipmentByEquipmentForCourse(existingDocumentCourse).keySet()) {
				database.insertUsedEquipment(newDocumentCourse, usedEquipment, database.assembleUsedEquipment());
			}
			
			database.updateCourse(newDocumentCourse);
		}
		
		// Course Associations
		for (IDBCourse existingDocumentCourse : database.findCoursesForDocument(existingDocument.underlyingDocument)) {
			IDBCourseAssociation assoc = database.getAssociationForLabOrNull(existingDocumentCourse);
//			System.out.println("course " + existingDocumentCourse.getCalatogNumber() + " " + existingDocumentCourse.getType() + ": " + assoc);
			if (assoc == null)
				continue;
//			System.out.println("copying assoc!");
			IDBCourse existingDocumentLecture = database.getAssociationLecture(assoc);
			
			IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCourse.getID());
			IDBCourse newDocumentLecture = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentLecture.getID());
			database.associateLectureAndLab(newDocumentLecture, newDocumentCourse, assoc.isTethered());
			database.updateCourse(newDocumentCourse);
			database.updateCourse(newDocumentLecture);
		}

		// Instructors
		Map<Integer, IDBInstructor> newDocumentInstructorsByExistingDocumentInstructorIDs = new HashMap<Integer, IDBInstructor>();
		for (IDBInstructor existingDocumentInstructor : database.findInstructorsForDocument(existingDocument.underlyingDocument)) {
			IDBInstructor newDocumentInstructor = database.assembleInstructor(existingDocumentInstructor.getFirstName(), existingDocumentInstructor.getLastName(), existingDocumentInstructor.getUsername(), existingDocumentInstructor.getMaxWTU(), existingDocumentInstructor.isSchedulable());
			database.insertInstructor(newUnderlyingDocument, newDocumentInstructor);
			newDocumentInstructorsByExistingDocumentInstructorIDs.put(existingDocumentInstructor.getID(), newDocumentInstructor);
			
			for (Entry<IDBCourse, IDBCoursePreference> existingDocumentEntry : database.findCoursePreferencesByCourseForInstructor(existingDocumentInstructor).entrySet()) {
				IDBCourse existingDocumentCoursePreferenceCourse = existingDocumentEntry.getKey();
				IDBCoursePreference existingDocumentCoursePreference = existingDocumentEntry.getValue();
				System.out.println("exist: " + existingDocumentCoursePreferenceCourse.getName() + " pref " + existingDocumentCoursePreference.getPreference());
				IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCoursePreferenceCourse.getID());
				IDBCoursePreference newDocumentCoursePreference = database.assembleCoursePreference(existingDocumentCoursePreference.getPreference());
				database.insertCoursePreference(newDocumentInstructor, newDocumentCourse, newDocumentCoursePreference);
			}
			
			for (Entry<IDBTime, IDBTimePreference> existingDocumentEntry : database.findTimePreferencesByTimeForInstructor(existingDocumentInstructor).entrySet()) {
				IDBTime time = existingDocumentEntry.getKey();
				IDBTimePreference existingDocumentTimePreference = existingDocumentEntry.getValue();
				IDBTimePreference newDocumentTimePreference = database.assembleTimePreference(existingDocumentTimePreference.getPreference());
				database.insertTimePreference(newDocumentInstructor, time, newDocumentTimePreference);
			}
			
			database.updateInstructor(newDocumentInstructor);
		}
		
		// Schedule Items
		Map<Integer, IDBScheduleItem> newDocumentScheduleItemsByExistingDocumentScheduleItemIDs = new HashMap<Integer, IDBScheduleItem>();
		for (IDBScheduleItem existingDocumentScheduleItem : database.findAllScheduleItemsForDocument(existingDocument.underlyingDocument)) {
			IDBCourse existingDocumentCourse = database.getScheduleItemCourse(existingDocumentScheduleItem);
			IDBLocation existingDocumentLocation = database.getScheduleItemLocation(existingDocumentScheduleItem);
			IDBInstructor existingDocumentInstructor = database.getScheduleItemInstructor(existingDocumentScheduleItem);

			IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCourse.getID());
			IDBLocation newDocumentLocation = newDocumentLocationsByExistingDocumentLocationIDs.get(existingDocumentLocation.getID());
			IDBInstructor newDocumentInstructor = newDocumentInstructorsByExistingDocumentInstructorIDs.get(existingDocumentInstructor.getID());
			
			IDBScheduleItem newDocumentScheduleItem = database.assembleScheduleItem(
					existingDocumentScheduleItem.getSection(),
					existingDocumentScheduleItem.getDays(),
					existingDocumentScheduleItem.getStartHalfHour(),
					existingDocumentScheduleItem.getEndHalfHour(),
					existingDocumentScheduleItem.isPlaced(),
					existingDocumentScheduleItem.isConflicted());
			
			database.insertScheduleItem(
					newDocument.underlyingDocument,
					newDocumentCourse,
					newDocumentInstructor,
					newDocumentLocation,
					newDocumentScheduleItem);
			database.updateDocument(newDocument.underlyingDocument);
			database.updateCourse(newDocumentCourse);
			database.updateInstructor(newDocumentInstructor);
			database.updateLocation(newDocumentLocation);
			
			newDocumentScheduleItemsByExistingDocumentScheduleItemIDs.put(existingDocumentScheduleItem.getID(), newDocumentScheduleItem);
		}
		
		// Schedule Item Associations
		for (IDBScheduleItem existingDocumentLabScheduleItem : database.findAllScheduleItemsForDocument(existingDocument.underlyingDocument)) {
			IDBScheduleItem existingDocumentLectureScheduleItem = database.getScheduleItemLectureOrNull(existingDocumentLabScheduleItem);
//			System.out.println("course " + existingDocumentScheduleItem.getCalatogNumber() + " " + existingDocumentScheduleItem.getType() + ": " + assoc);
			if (existingDocumentLectureScheduleItem == null)
				continue;
			
			IDBScheduleItem newDocumentLabScheduleItem = newDocumentScheduleItemsByExistingDocumentScheduleItemIDs.get(existingDocumentLabScheduleItem.getID());
			IDBScheduleItem newDocumentLectureScheduleItem = newDocumentScheduleItemsByExistingDocumentScheduleItemIDs.get(existingDocumentLectureScheduleItem.getID());
			database.associateScheduleItemLab(newDocumentLectureScheduleItem, newDocumentLabScheduleItem);
			database.updateScheduleItem(newDocumentLabScheduleItem);
			database.updateScheduleItem(newDocumentLectureScheduleItem);
		}


		assert(newDocumentInstructorsByExistingDocumentInstructorIDs.containsKey(existingDocument.getStaffInstructor().getID()));
		database.setDocumentStaffInstructorOrNull(newUnderlyingDocument, newDocumentInstructorsByExistingDocumentInstructorIDs.get(existingDocument.getStaffInstructor().getID()));

		assert(newDocumentLocationsByExistingDocumentLocationIDs.containsKey(existingDocument.getTBALocation().getID()));
		database.setDocumentTBALocationOrNull(newUnderlyingDocument, newDocumentLocationsByExistingDocumentLocationIDs.get(existingDocument.getTBALocation().getID()));
		
		assert(newDocumentInstructorsByExistingDocumentInstructorIDs.containsKey(existingDocument.getChooseForMeInstructor().getID()));
		database.setDocumentChooseForMeInstructorOrNull(newUnderlyingDocument, newDocumentInstructorsByExistingDocumentInstructorIDs.get(existingDocument.getChooseForMeInstructor().getID()));

		assert(newDocumentLocationsByExistingDocumentLocationIDs.containsKey(existingDocument.getChooseForMeLocation().getID()));	
		database.setDocumentChooseForMeLocationOrNull(newUnderlyingDocument, newDocumentLocationsByExistingDocumentLocationIDs.get(existingDocument.getChooseForMeLocation().getID()));
		
		database.updateDocument(newUnderlyingDocument);
		
		newDocument.invalidateLoaded();
	}

	void associateWorkingCopyWithOriginal(Document workingCopyDocument, Document newOriginal) throws DatabaseException {
		database.associateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, newOriginal.underlyingDocument);
	}

	Document getOriginalForWorkingCopyDocument(Document workingCopyDocument) throws DatabaseException {
		IDBDocument underlying = database.getOriginalForWorkingCopyDocumentOrNull(workingCopyDocument.underlyingDocument);
		assert(underlying != null);
		return documentCache.decorateAndPutIfNotPresent(underlying);
	}

	boolean isOriginalDocument(Document doc) throws DatabaseException {
		return database.isOriginalDocument(doc.underlyingDocument);
	}

	
	
	
//	// SCHEDULES
//
//	Cache<Schedule, IDBSchedule> scheduleCache = new Cache<Schedule, IDBSchedule>() {
//		protected Schedule decorate(IDBSchedule underlying) {
//			return new Schedule(Model.this, underlying);
//		}
//		protected IDBSchedule loadFromDatabase(int id) throws DatabaseException {
//			return database.findScheduleByID(id);
//		}
//		protected void insertIntoDatabase(Schedule obj) throws DatabaseException {
//			database.insertSchedule(obj.getDocument().underlyingDocument, obj.underlyingSchedule);
//		}
//		protected void removeFromDatabase(Schedule obj) throws DatabaseException {
//			database.deleteSchedule(obj.underlyingSchedule);
//		}
//		@Override
//		protected void updateInDatabase(Schedule obj) throws DatabaseException {
//			database.updateSchedule(obj.underlyingSchedule);
//		}
//	};
//	
//	public Collection<Schedule> findSchedulesForDocument(Document doc) throws DatabaseException {
//		Collection<Schedule> result = new LinkedList<Schedule>();
//		for (IDBSchedule underlying : database.findAllSchedulesForDocument(doc.underlyingDocument))
//			result.add(scheduleCache.decorateAndPutIfNotPresent(underlying));
//		return result;
//	}
//
//	public Schedule findScheduleByID(int scheduleID) throws DatabaseException {
//		return scheduleCache.findByID(scheduleID);
//	}
//	
//	public Schedule createTransientSchedule() throws DatabaseException {
//		return new Schedule(this, database.assembleSchedule());
//	}
	
	
	
	
	// INSTRUCTORS

	Cache<Instructor, IDBInstructor> instructorCache = new Cache<Instructor, IDBInstructor>() {
		protected Instructor decorate(IDBInstructor underlying) {
			return new Instructor(Model.this, underlying);
		}
		protected IDBInstructor loadFromDatabase(int id) throws DatabaseException {
			return database.findInstructorByID(id);
		}
		protected void insertIntoDatabase(Instructor obj) throws DatabaseException {
			database.insertInstructor(obj.getDocument().underlyingDocument, obj.underlyingInstructor);
		}
		protected void removeFromDatabase(Instructor obj) throws DatabaseException {
			database.deleteInstructor(obj.underlyingInstructor);
		}
		protected void updateInDatabase(Instructor obj) throws DatabaseException {
			database.updateInstructor(obj.underlyingInstructor);
		}
	};
	
	public Collection<Instructor> findInstructorsForDocument(Document doc, boolean excludeSpecialCaseInstructors) throws DatabaseException {
		Collection<Instructor> result = new LinkedList<Instructor>();

		for (IDBInstructor underlying : database.findInstructorsForDocument(doc.underlyingDocument)) {
			
			if (excludeSpecialCaseInstructors) {
				if (underlying.getID().equals(doc.getStaffInstructor().getID()))
					continue;
				if (underlying.getID().equals(doc.getChooseForMeInstructor().getID()))
					continue;
			}
			
			result.add(instructorCache.decorateAndPutIfNotPresent(underlying));
		}
		return result;
	}

	public Instructor findInstructorByID(int instructorID) throws DatabaseException {
		return instructorCache.findByID(instructorID);
	}
	
	public Instructor createTransientInstructor(String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) throws DatabaseException {
		IDBInstructor underlyingInstructor = database.assembleInstructor(firstName, lastName, username, maxWTU, isSchedulable);
		return new Instructor(this, underlyingInstructor);
	}


	

	// COURSES

	Cache<Course, IDBCourse> courseCache = new Cache<Course, IDBCourse>() {
		protected Course decorate(IDBCourse underlying) {
			return new Course(Model.this, underlying);
		}
		protected IDBCourse loadFromDatabase(int id) throws DatabaseException {
			return database.findCourseByID(id);
		}
		protected void insertIntoDatabase(Course obj) throws DatabaseException {
			database.insertCourse(obj.getDocument().underlyingDocument, obj.underlyingCourse);
		}
		protected void removeFromDatabase(Course obj) throws DatabaseException{
			database.deleteCourse(obj.underlyingCourse);
		}
		protected void updateInDatabase(Course obj) throws DatabaseException {
			database.updateCourse(obj.underlyingCourse);
		}
	};
	
	public Course createTransientCourse(String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek, boolean isSchedulable) throws DatabaseException {
		return new Course(this, database.assembleCourse(name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable));
	}
	
	public Collection<Course> findCoursesForDocument(Document doc) throws DatabaseException {
		Collection<Course> result = new LinkedList<Course>();
		for (IDBCourse underlying : database.findCoursesForDocument(doc.underlyingDocument))
			result.add(courseCache.decorateAndPutIfNotPresent(underlying));
		return result;
	}

	public Course findCourseByID(int courseID) throws DatabaseException {
		return courseCache.findByID(courseID);
	}
	
	
	

	// LOCATIONS
	
	Cache<Location, IDBLocation> locationCache = new Cache<Location, IDBLocation>() {
		protected Location decorate(IDBLocation underlying) {
			return new Location(Model.this, underlying);
		}
		protected IDBLocation loadFromDatabase(int id) throws DatabaseException {
			return database.findLocationByID(id);
		}
		protected void insertIntoDatabase(Location obj) throws DatabaseException {
			database.insertLocation(obj.getDocument().underlyingDocument, obj.underlyingLocation);
		}
		protected void removeFromDatabase(Location obj) throws DatabaseException {
			database.deleteLocation(obj.underlyingLocation);
		}
		protected void updateInDatabase(Location obj) throws DatabaseException {
			database.updateLocation(obj.underlyingLocation);
		}
	};
	
	
	
	public Location createTransientLocation(String room, String type, String maxOccupancy, boolean isSchedulable) throws DatabaseException {
		return new Location(this, database.assembleLocation(room, type, maxOccupancy, isSchedulable));
	}

	public Collection<Location> findLocationsForDocument(Document doc, boolean excludeSpecialCaseLocations) throws DatabaseException {
//		assert(doc.getTBALocation() != null);
//		assert(doc.getTBALocation().getID() != null);
		Collection<Location> result = new LinkedList<Location>();
		for (IDBLocation underlying : database.findLocationsForDocument(doc.underlyingDocument)) {
			assert underlying != null : "under null";
			assert underlying.getID() != null : " under id null";
			assert doc != null : "doc null";
			
			if (excludeSpecialCaseLocations) {
				if (doc.getTBALocation() != null) {
					if (underlying.getID().equals(doc.getTBALocation().getID()))
						continue;
					if (underlying.getID().equals(doc.getChooseForMeLocation().getID()))
						continue;
				}
			}
			
			result.add(locationCache.decorateAndPutIfNotPresent(underlying));
		}
		return result;
	}

	public Location findLocationByID(int locationID) throws DatabaseException {
		return locationCache.findByID(locationID);
	}
	
	

	// SCHEDULE ITEMS

	Cache<ScheduleItem, IDBScheduleItem> itemCache = new Cache<ScheduleItem, IDBScheduleItem>() {
		protected ScheduleItem decorate(IDBScheduleItem underlying) {
			return new ScheduleItem(Model.this, underlying);
		}
		protected IDBScheduleItem loadFromDatabase(int id) throws DatabaseException {
			return database.findScheduleItemByID(id);
		}
		protected void insertIntoDatabase(ScheduleItem obj) throws DatabaseException {
			database.insertScheduleItem(obj.getDocument().underlyingDocument, obj.getCourse().underlyingCourse, obj.getInstructor().underlyingInstructor, obj.getLocation().underlyingLocation, obj.underlying);
		}
		protected void removeFromDatabase(ScheduleItem obj) throws DatabaseException {
			database.deleteScheduleItem(obj.underlying);
		}
		protected void updateInDatabase(ScheduleItem obj) throws DatabaseException {
			database.updateScheduleItem(obj.underlying);
		}
	};
	
	public Collection<ScheduleItem> findAllScheduleItemsForDocument(Document document) throws DatabaseException {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (IDBScheduleItem underlying : database.findAllScheduleItemsForDocument(document.underlyingDocument))
			result.add(itemCache.decorateAndPutIfNotPresent(underlying));
		return result;
	}

	public ScheduleItem createTransientScheduleItem(int section,
			Set<Day> days, int startHalfHour, int endHalfHour,
			boolean isPlaced, boolean isConflicted) throws DatabaseException {
		IDBScheduleItem underlying = database.assembleScheduleItem(section, days, startHalfHour, endHalfHour, isPlaced, isConflicted);
		return new ScheduleItem(this, underlying);
	}

	public ScheduleItem findScheduleItemByID(int id) throws DatabaseException {
		return itemCache.findByID(id);
	}

	public boolean isEmpty() throws DatabaseException {
		return database.isEmpty();
	}
	
	public void writeState(ObjectOutputStream oos) throws IOException {
		database.writeState(oos);
	}

	public void readState(ObjectInputStream ois) throws IOException {
		database.readState(ois);
	}

	// For testing purposes only.
	// BE CAREFUL WITH THIS METHOD! MAKE SURE THERE ARE NO MODEL OBJECTS INSTANTIATED ANYWHERE!
	public void clearCache() {
		documentCache.cache.clear();
		userCache.cache.clear();
		itemCache.cache.clear();
		locationCache.cache.clear();
		instructorCache.cache.clear();
		courseCache.cache.clear();
	}

	public Collection<ScheduleItem> getLabScheduleItemsForScheduleItem(ScheduleItem item) throws DatabaseException {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (IDBScheduleItem lab : database.findAllLabScheduleItemsForScheduleItem(item.underlying))
			result.add(itemCache.decorateAndPutIfNotPresent(lab));
		return result;
	}

	public ScheduleItem getScheduleItemLectureOrNull(ScheduleItem scheduleItem) throws DatabaseException {
		IDBScheduleItem lecture = database.getScheduleItemLectureOrNull(scheduleItem.underlying);
		if (lecture == null)
			return null;
		return itemCache.decorateAndPutIfNotPresent(lecture);
	}

	public void insertEquipmentType(String string) throws DatabaseException {
		database.insertEquipmentType(string);
	}

	public Collection<String> getEquipmentTypes() throws DatabaseException {
		Collection<String> equipmentTypeStrings = new TreeSet<String>();
		for (IDBEquipmentType equipmentType : database.findAllEquipmentTypes())
			equipmentTypeStrings.add(equipmentType.getDescription());
		return equipmentTypeStrings;
	}

	public Document findDocumentByNameOrNull(String scheduleName) throws DatabaseException {
		IDBDocument doc = database.findDocumentByName(scheduleName);
		if (doc == null)
			return null;
		return documentCache.decorateAndPutIfNotPresent(doc);
	}
	
	public void closeModel()
	{
		this.database.closeDatabase();
	}
}

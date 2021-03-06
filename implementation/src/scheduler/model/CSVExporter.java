package scheduler.model;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csvreader.CsvWriter;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

/**
 * The Class CSVExporter. Exports a model to a CSV formatted string.
 * 
 * Current Status: partially working due to other uninitialized data and
 * inability to test w/ teacher populated schedules. TODO Once fully working,
 * will likely need to be resynced with CSV importer
 * 
 * @author Evan Ovadia
 * @author Jordan Hand
 */
public class CSVExporter {
	/** The locations. */
	private ArrayList<String[]> locations = new ArrayList<String[]>();
	private Map<Integer, Integer> locationRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors. */
	private ArrayList<String[]> instructors = new ArrayList<String[]>();
	private Map<Integer, Integer> instructorRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors time prefs. */
	private ArrayList<String[][]> instructorsTimePrefs = new ArrayList<String[][]>();
	private Map<Integer, Integer> instructorsTimePrefsRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors course prefs. */
	private ArrayList<String[][]> instructorsCoursePrefs = new ArrayList<String[][]>();
	private Map<Integer, Integer> instructorsCoursePrefsRowIndexByID = new HashMap<Integer, Integer>();

	/** The courses. */
	private ArrayList<String[]> courses = new ArrayList<String[]>();
	private Map<Integer, Integer> courseRowIndexByID = new HashMap<Integer, Integer>();

	/** The schedule items. */
	private ArrayList<String[]> scheduleItems = new ArrayList<String[]>();
	private Map<Integer, Integer> scheduleItemsRowIndexByID = new HashMap<Integer, Integer>();

	private static String join(Collection<String> strings, String glue) {
		String result = "";
		for (String str : strings) {
			if (!result.equals(""))
				result += glue;
			result += str;
		}
		return result;
	}

	/**
	 * Instantiates a new cSV exporter.
	 */
	public CSVExporter() {
	}

	/**
	 * Compile location. Turns location data into a string and adds it to the
	 * global locations ArrayList
	 * 
	 * @param location
	 *            A location
	 * @return A string with the location's index
	 */
	private String compileLocation(Location location) throws DatabaseException {

		int index = this.locations.indexOf(location);
		if (index < 0) {
			index = this.locations.size();
			this.locations.add(new String[] { "location#" + index,
					location.getRoom(), location.getMaxOccupancy(),
					location.getType(),
					(new Boolean (location.isSchedulable())).toString()
		//			join(location.getProvidedEquipment(), " & ") /* Commented out due to not currently implemented */
					}
			);
			this.locationRowIndexByID.put(location.getID(), index);

		}

		return "location#" + index;
	}

	/**
	 * Compile instructor. Turns instructor data into a string and adds it to
	 * the global instructors ArrayList
	 * 
	 * @param instructor
	 *            the instructor
	 * @return A string with the instructor index
	 */
	private String compileInstructor(Instructor instructor)
			throws DatabaseException {

		int index = this.instructors.indexOf(instructor);
		if (index < 0) {
			index = this.instructors.size();

			this.instructors.add(new String[] { "instructor#" + index,
					instructor.getLastName(),	instructor.getFirstName(), 
					instructor.getUsername(), instructor.getMaxWTU(), (new Boolean (instructor.isSchedulable())).toString(),
					compileCoursePrefs(instructor.getCoursePreferences()),
					compileTimePrefs(instructor.getTimePreferences()) });
			this.instructorRowIndexByID.put(instructor.getID(), index);

		}
		return "instructor#" + index;
	}

	private static String halfHourToString(int halfHourInDay) {
		int hour = halfHourInDay / 2;
		int halfHourInHour = halfHourInDay % 2;
		boolean am = hour < 12;
		hour %= 12;
		return hour + ":" + (halfHourInHour == 0 ? "00" : "30")
				+ (am ? "am" : "pm");
	}

	/**
	 * Compile time prefs. Turns Time Preference data into a string and adds it
	 * to the global instructorTimePrefs ArrayList
	 * 
	 * @param hashMap
	 *            A hashmap<Day rows, Hashmap<Time Columns, TimePreference>>
	 *            mapping the days and times with a teacher's preference for
	 *            that combination.
	 * @return A string of time prefs. ie Time,SUN,MON,TUE,WED,THU,FRI,SAT
	 *         00:00,5,5,5,5,5,5,5
	 */
	private String compileTimePrefs(int[][] hashMap) {
		final int startHalfHour = 14;
		final int endHalfHour = 44;
		final int numTimesInDay = endHalfHour - startHalfHour;
		String[][] strings = new String[1 + numTimesInDay][1 + Day.values().length];

		strings[0][0] = "Time";

		for (int halfHour = 0; halfHour < numTimesInDay; halfHour++)
			strings[halfHour + 1][0] = halfHourToString(halfHour
					+ startHalfHour);

		for (int col = 0; col < Day.values().length; col++)
			strings[0][col + 1] = Day.values()[col].toString();

		for (int halfHourNum = 0; halfHourNum < numTimesInDay; halfHourNum++) {
			for (int dayNum = 0; dayNum < Day.values().length; dayNum++) {
				Day day = Day.values()[dayNum];
				int row = halfHourNum + 1;
				int col = dayNum + 1;
				strings[row][col] = Integer
						.toString(hashMap[day.ordinal()][halfHourNum
								+ startHalfHour]);
			}
		}

		int newIndex = this.instructorsTimePrefs.size();
		this.instructorsTimePrefs.add(strings);
		return "timePrefs#" + newIndex;
	}

	/**
	 * Compile course prefs. Turns course preference data into a string and adds
	 * it to the global instructorsCoursePrefs ArrayList
	 * 
	 * @param coursePreferences
	 *            A hashmap of course ID's to preferences
	 * @return A string representing the course preference index
	 */
	private String compileCoursePrefs(
			HashMap<Integer, Integer> coursePreferences) {
		String[][] strings = new String[coursePreferences.size()][2];
		int row = 0;
		for (Entry<Integer, Integer> pref : coursePreferences.entrySet()) {
			strings[row][0] = "course#" + courseRowIndexByID.get(pref.getKey());
			strings[row][1] = Integer.toString(pref.getValue()); // Preference
																	// for the
																	// course
			row++;
		}

		int newIndex = this.instructorsCoursePrefs.size();

		this.instructorsCoursePrefs.add(strings);

		return "coursePrefs#" + newIndex;
	}

	private ScheduleItem findScheduleItemByID(int id,
			Collection<ScheduleItem> items) {
		for (ScheduleItem item : items)
			if (item.getID() == id)
				return item;
		assert (false);
		return null;
	}

	private Course findCourseByID(int id, Collection<Course> items) {
		for (Course item : items)
			if (item.getID() == id)
				return item;
		assert (false);
		return null;
	}

	/**
	 * Compile schedule item. Turns Schedule Item data into a string and adds it
	 * to the global Schedule.Items ArrayList
	 * 
	 * @param conflictingSchedule
	 *            .Item True is part of a conflicting schedule item, false if
	 *            not
	 * @param item
	 *            A Schedule.Item
	 * @return the string of the Schedule.Item Number
	 * @throws NotFoundException
	 */
	private String compileScheduleItem(ScheduleItem item,
			Collection<ScheduleItem> others) throws DatabaseException {

		int index = this.scheduleItems.indexOf(item);

		if (index < 0) {

			index = scheduleItems.size();
			this.scheduleItems.add(new String[] {
					"item#" + index,
					"instructor#"
							+ this.instructorRowIndexByID.get(item.getInstructor()
									.getID()),

					"course#"
							+ this.courseRowIndexByID.get(item.getCourse().getID()),

					"location#"
							+ this.locationRowIndexByID.get(item.getLocation()
									.getID()),

					Integer.toString(item.getSection()),
					Boolean.toString(item.isPlaced()),
					Boolean.toString(item.isConflicted()),
					compileDayPattern(item.getDays()),
					compileTimeRange(item.getStartHalfHour(),
							item.getEndHalfHour()), });
		}

		return "item#" + index;
	}

	private String compileDayPattern(Set<Day> days) {
		String result = "";
		for (Day day : days)
			result += day.abbreviation;
		return result;
	}

	/**
	 * Compile time range. Converts a TimeRange into a string
	 * 
	 * @param timeRange
	 *            the time range
	 * @return the string of time ranges
	 */
	private static String compileTimeRange(int startHalfHourNum,
			int endHalfHourNum) {
		return halfHourToString(startHalfHourNum) + " to "
				+ halfHourToString(endHalfHourNum);
	}

	/**
	 * Compile course. Turns Course data into a string and adds it to the global
	 * courses ArrayList
	 * 
	 * @param course
	 *            A course
	 * @return A string representing the course index
	 * @throws DatabaseException
	 */
	private String compileCourse(Course course, Collection<Course> others)
			throws DatabaseException {

		int index = courses.indexOf(course);
		if (index < 0) {
		
			String dayPatterns = "";
			for (Set<Day> pattern : course.getDayPatterns()) {
				if (!dayPatterns.equals(""))
					dayPatterns += " ";
				dayPatterns += compileDayPattern(pattern);
			}

			String association;
		
			association = "Course# -1";
		
			index = this.courses.size();
			this.courses.add(new String[] { "course#" + index,

			course.getType().toString(), course.getName(),
					course.getCatalogNumber(), course.getDepartment(),
					course.getWTU(), course.getSCU(), course.getNumSections(),
					course.getNumHalfHoursPerWeek(), dayPatterns,
					course.getMaxEnrollment(), (new Boolean(course.isSchedulable()).toString()), 
					(new Boolean(course.isTetheredToLecture())).toString() ,association });
			courseRowIndexByID.put(course.getID(), index);

		}

		return "course#" + index;
	}

	/**
	 * Compile associated Courses. Finds associated courses and modifies the proper value in courses if there is an associated course  
	 */
	private void compileAssociatedCourse(Course course,
			Collection<Course> others) throws DatabaseException {

		// Goes through each course again, finds ones that have associated
		// values
		// Finds main course and associated course in courses

	
		if (course.getLecture() != null) {
			int lecIndex = 0;
			
			String dayPatterns = "";
			for (Set<Day> pattern : course.getDayPatterns()) {
				if (!dayPatterns.equals(""))
					dayPatterns += " ";
				dayPatterns += compileDayPattern(pattern);
			}
			
			//Find main course in courses
			for (String[] c : this.courses) {
				if(c[1] == course.getType() &&
						c[2].equals(course.getName()) &&
						c[3].equals(course.getCatalogNumber()) &&
						c[4].equals(course.getDepartment()) &&
						c[5].equals(course.getWTU()) &&
						c[6].equals(course.getSCU()) &&
						c[7].equals(course.getNumSections()) &&
						c[8].equals(course.getNumHalfHoursPerWeek()) &&
						c[9].equals(dayPatterns) &&
						c[10].equals(course.getMaxEnrollment())
						){
					lecIndex = this.courses.indexOf(c);
					break;
				}
			}
			Course associatedCourse = findCourseByID(course.getLecture()
					.getID(), others);
			
			dayPatterns = "";
			for (Set<Day> pattern : associatedCourse.getDayPatterns()) {
				if (!dayPatterns.equals(""))
					dayPatterns += " ";
				dayPatterns += compileDayPattern(pattern);
			}
			
			for (String[] c : this.courses) {
				if(c[1] == associatedCourse.getType() &&
						c[2].equals(associatedCourse.getName()) &&
						c[3].equals(associatedCourse.getCatalogNumber()) &&
						c[4].equals(associatedCourse.getDepartment()) &&
						c[5].equals(associatedCourse.getWTU()) &&
						c[6].equals(associatedCourse.getSCU()) &&
						c[7].equals(associatedCourse.getNumSections()) &&
						c[8].equals(associatedCourse.getNumHalfHoursPerWeek()) &&
						c[9].equals(dayPatterns) &&
						c[10].equals(associatedCourse.getMaxEnrollment())
						){
					this.courses.get(lecIndex)[11] = c[0];
					break;
				}
			}
		
		
		}

	}

	/**
	 * Export. Turns a Model into a CSV String
	 * 
	 * @param model
	 *            A model
	 * @return The CSV String
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NotFoundException
	 */
	public String export(Model model, Document document) throws IOException,
			DatabaseException {
		/* Gather model information into the global string ArrayLists */
		for (Location location : model.findLocationsForDocument(document, true))
			compileLocation(location);

		Collection<Course> coursesInDocument = model
				.findCoursesForDocument(document);
		for (Course course : coursesInDocument) {
			compileCourse(course, coursesInDocument);
		}

		for (Course course : coursesInDocument) {
			compileAssociatedCourse(course, coursesInDocument);
		}

		for (Instructor instructor : model.findInstructorsForDocument(document, true))
			compileInstructor(instructor);

		Collection<ScheduleItem> items = model.findAllScheduleItemsForDocument(document);

		for (ScheduleItem item : items)
			compileScheduleItem(item, items);

		/*
		 * Start writing model data to a charArray that'll eventually be turned
		 * into a string
		 */
		Writer stringWriter = new CharArrayWriter();
		CsvWriter writer = new CsvWriter(stringWriter, ',');

		for (String topComment : CSVStructure.TOP_COMMENTS)
			writer.writeComment(topComment);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_MARKER);
		writer.write(document.getName());
		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.COURSES_MARKER);
		for (int i = 0; i < this.courses.size(); i++) {
			writer.writeRecord(this.courses.get(i));
		}
		writer.writeComment(CSVStructure.COURSES_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.LOCATIONS_MARKER);
		for (int i = 0; i < this.locations.size(); i++) {
			writer.writeRecord(this.locations.get(i));
		}
		writer.writeComment(CSVStructure.LOCATIONS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);

		for (int i = 0; i < this.instructorsCoursePrefs.size(); i++) {
			writer.write("coursePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_MARKER);

			String[][] prefs = this.instructorsCoursePrefs.get(i);
			for (String[] rec : prefs)
				writer.writeRecord(rec);

			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_MARKER);
		for (int i = 0; i < this.instructorsTimePrefs.size(); i++) {
			writer.write("timePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_MARKER);
			String[][] prefs = this.instructorsTimePrefs.get(i);
			for (String[] rec : prefs)
				writer.writeRecord(rec);
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_MARKER);
		for (int i = 0; i < this.instructors.size(); i++) {
			writer.writeRecord(this.instructors.get(i));
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_MARKER);
		for (int i = 0; i < this.scheduleItems.size(); i++)
			writer.writeRecord(this.scheduleItems.get(i));
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_END_MARKER);

		writer.flush();
		writer.close();
		stringWriter.flush();
		stringWriter.close();

		return stringWriter.toString();
	}

}

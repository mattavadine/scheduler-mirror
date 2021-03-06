package scheduler.model;

import java.util.HashMap;

import scheduler.model.Day;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Model;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

public abstract class InstructorsPreferencesTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testInsertAndFindInstructorWTimePrefs() throws DatabaseException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
			
			int[][] timePrefs = ModelTestUtility.createSampleTimePreferences(doc);
			
			instructorID = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(timePrefs)
					.setDocument(doc).insert()
					.getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assertEquals(found.getFirstName(), "Evan");
		assertEquals(found.getLastName(), "Ovadia");
		assertEquals(found.getUsername(), "eovadia");
		assertEquals(found.getMaxWTU(), "20");
		
		model.closeModel();
	}


	public void testInsertAndFindInstructorWCoursePrefs() throws DatabaseException {
		Model model = createBlankModel();
		
		int instructorID;
		int courseID1;
		int courseID2;
		
		{
			Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);

			courseID1 = model.createTransientCourse("Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
			courseID2 = model.createTransientCourse("Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
			
			HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
			coursePrefs.put(courseID1, 2);
			coursePrefs.put(courseID2, 3);
			
			instructorID = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setCoursePreferences(coursePrefs)
					.setDocument(doc).insert()
					.getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assertTrue(found.getCoursePreferences().get(courseID1).equals(2));
		assertTrue(found.getCoursePreferences().get(courseID2).equals(3));
		
		model.closeModel();
	}

//	public void testUtilityInstructorEquals() throws DatabaseException {
//		Model model = createBlankModel();
//		Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
//		
//		int courseID1 = model.createTransientCourse("Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
//		int courseID2 = model.createTransientCourse("Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
//		
//		HashMap<Integer, Integer> coursePrefs1 = new HashMap<Integer, Integer>();
//		coursePrefs1.put(courseID1, 2);
//		coursePrefs1.put(courseID2, 3);
//		
//		int[][] timePrefs1 = ModelTestUtility.createSampleTimePreferences(doc);
//		
//		Instructor ins1 = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
//				.setTimePreferences(timePrefs1)
//				.setCoursePreferences(coursePrefs1)
//				.setDocument(doc).insert();
//		
//		HashMap<Integer, Integer> coursePrefs2 = new HashMap<Integer, Integer>();
//		coursePrefs2.put(courseID1, 2);
//		coursePrefs2.put(courseID2, 3);
//		
//		int[][] timePrefs2 = ModelTestUtility.createSampleTimePreferences(doc);
//
//		Instructor ins2 = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
//				.setTimePreferences(timePrefs2)
//				.setCoursePreferences(coursePrefs2)
//				.setDocument(doc).insert();
//		
//		
//		assertTrue(ModelTestUtility.instructorsContentsEqual(ins1, ins2));
//		
//		ins1.setTimePreferences(Day.FRIDAY, 10, 4);
//
//		assertFalse(ModelTestUtility.instructorsContentsEqual(ins1, ins2));
//	}
	
}

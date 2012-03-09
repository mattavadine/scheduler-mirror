package edu.calpoly.csc.scheduler.model;


import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.calpoly.csc.scheduler.model.db.DatabaseTestSimpleDB;
import edu.calpoly.csc.scheduler.model.tempalgorithm.TempAlgorithmTestSimpleDB;

public class SampleTestSuite extends TestCase {

	/**
	 * Instantiates a new test suite.
	 *
	 * @param name the name
	 */
	public SampleTestSuite(String name) {
		super(name);
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Example testsuite (Junit3)");

		suite.addTestSuite(UsersTestSimpleDB.class);
		suite.addTestSuite(DatabaseTestSimpleDB.class);
		suite.addTestSuite(DocumentsTestSimpleDB.class);
		suite.addTestSuite(InstructorsTestSimpleDB.class);
		suite.addTestSuite(InstructorsPreferencesTestSimpleDB.class);
		suite.addTestSuite(LocationsTestSimpleDB.class);
		suite.addTestSuite(CoursesTestSimpleDB.class);
		suite.addTestSuite(TempAlgorithmTestSimpleDB.class);
		suite.addTestSuite(ScheduleItemsTestSimpleDB.class);
		suite.addTestSuite(CSVTest.class);
		
		return suite;
	}
}
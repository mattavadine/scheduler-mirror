package scheduler.view.web.shared;

//import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;
import junit.framework.TestCase;

/**
 * This test class tests the admin view of the instructor settings
 * including adding and deleting istructors, as well as setting the
 * preferences for certain instructors
 * 
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorsViewTest extends TestCase {
	private FirefoxDriver driver;
	private SchedulerBot bot;
	
	/**
	 * sets up the test class and initializes the Firefox driver
	 */
	public void setUp()
	{
		this.driver = new FirefoxDriver();
		driver.get("http://scheduler.csc.calpoly.edu/dev");
		this.bot = new SchedulerBot(driver);
		
		try {
			this.bot.waitForElementPresent(By.id("s_loginBtn"));
		} catch (InterruptedException e) {
			// print the exception and wait 1 second instead and hope that it works
			e.printStackTrace();
			this.waitMillis(1000);
		}
		
		WebElement loginBtn = driver.findElement(By.id("s_loginBtn")); 
		WebElement unameField = driver.findElement(By.id("s_unameBox"));
		
		unameField.sendKeys("admin");
		loginBtn.click();
		
		
		this.waitForSmartGWTElement("s_createBtn");
		// klick the first item in the schedule document list
		WebElement first_doc = this.getElementBySmartGWTID("sc_document_0");
		if(first_doc == null)
		{
			this.addNewDocument();
		}
		
		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		this.waitForSmartGWTElement("sc_document_0");
		first_doc.click();
		
		// change broswer tab here
		String newWindowHandle = popupWaiter.waitForPopup();
		driver.switchTo().window(newWindowHandle);
		
		// click on the instructors tab
//		this.waitForSmartGWTElement("s_instructorsTab");
//		WebElement tab = this.getElementBySmartGWTID("s_instructorsTab");
		// just temporary until the next dev deploy
		this.waitForSmartGWTElement("isc_Tab_1");
		WebElement tab = this.getElementBySmartGWTID("isc_Tab_1");
		
		assertEquals("Instructors", tab.getText());
		tab.click();
		
		System.out.println("set up");
	}
	
	/**
	 * closes the Firefox driver
	 */
	public void tearDown() {
		driver.close();
		System.out.println("teared down\n");
	}
	
	/**
	 * tests if instructors can be added
	 */
	public void testAddInstructor()
	{
		// click the Button to add an instructor

		this.waitForSmartGWTElement("addInstructorBtn");

//		driver.findElement(By.id("addInstructorBtn")).click();
		
		// TODO: insert data
		try {
			bot.enterIntoInstructorsResourceTableNewRow(0, true, "World", "Hello", "foo", "30");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.waitMillis(2000);
		
		// TODO: set focus somewhere else
		
		// TODO: check if there is a new instructor with this data
	}
	
//	/**
//	 * tests if instructors can be duplicated
//	 */
//	public void testDuplicateInstructor()
//	{
//		// TODO: select the first instructor
//		
//		// click the button to duplicate an instructor
//		driver.findElement(By.id("duplicateBtn")).click();
//		
//		// TODO: check if there are two of them
//	}
//	
//	/**
//	 * tests if instructors can be removed
//	 */
//	public void testRemoveInstructor()
//	{
//		// TODO: select the first instructor
//		
//		// click the button to remove an instructor
//		driver.findElement(By.id("removeBtn")).click();
//		
//		// TODO: check if the instructor is still in the system
//	}
//	
//	/**
//	 * tests if the time and course preferences
//	 * can be set.
//	 */
//	public void testSetPreferences()
//	{
//		// click on the preferences button of the first instructor
//		WebElement prefsButton = this.getElementBySmartGWTID("instrPrefsButton_0");
//		assertEquals("Instructors", prefsButton.getText());
//		prefsButton.click();
//		
//		WebElement timeTable = this.getElementBySmartGWTID("timePrefsTable");
//		List<WebElement> rows = timeTable.findElements(By.tagName("tr"));;
//		WebElement cell;
//		WebElement select;
//		
//		// set Monday    7:00 am to "Not Preferred"
//		cell = rows.get(1).findElements(By.tagName("td")).get(1);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(1).click();
////		assertEquals("Not Preferred", );
//		
//		// set Tuesday   8:00 am to "Acceptable"
//		cell = rows.get(2).findElements(By.tagName("td")).get(2);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(2).click();
////		assertEquals("Acceptable", );
//		
//		// set Wednesday 9:00 am to "Preferred"
//		cell = rows.get(3).findElements(By.tagName("td")).get(3);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(3).click();
////		assertEquals("Preferred", );
//		
//		
//		
//		WebElement courseTable = this.getElementBySmartGWTID("coursePrefsTable");
//		rows = courseTable.findElements(By.tagName("tr"));;
//		
//		// set the secont course to "Not Preferred"
//		cell = rows.get(2).findElements(By.tagName("td")).get(1);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(1).click();
////		assertEquals("Not Preferred", );
//		
//		// set the third  course to "Acceptable"
//		cell = rows.get(3).findElements(By.tagName("td")).get(1);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(2).click();
////		assertEquals("Acceptable", );
//		
//		// set the fourth course to "Preferred"
//		cell = rows.get(4).findElements(By.tagName("td")).get(1);
//		select = cell.findElement(By.tagName("select"));
//		select.click();
//		select.findElements(By.tagName("option")).get(3).click();
////		assertEquals("Preferred", );
//		
//		
//		// TODO: check if all settings are correct
//	}
	
	/**
	 * Waits the given amount, it is used when elements
	 * need more time to be loaded
	 * @param millis
	 */
	public void waitMillis(long millis)
	{
		long t0, t1;
		t0 = System.currentTimeMillis();
		do{
			t1 = System.currentTimeMillis();
		}while(t1-t0 < millis);
	}
	
	/**
	 * finds a smartgwt element by its id
	 * @param smartGWTID
	 * @return the found element
	 */
	private WebElement getElementBySmartGWTID(String smartGWTID) {
		try
		{
			return this.driver.findElement(By.xpath("//div[@eventproxy='" + smartGWTID + "']"));
		}
		catch(NoSuchElementException e)
		{
			return null;
		}
	}
	
	/**
	 * adds a new document if ther is no document for testing
	 */
	private void addNewDocument()
	{
		System.out.println("new document");
		WebElement createBtn = this.getElementBySmartGWTID("s_createBtn");
		createBtn.click();
		
		this.waitMillis(1000);
		
		WebElement docName = driver.findElement(By.id("s_createBox"));
		docName.sendKeys("first document");

		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	/**
	 * waits for the given smartGWT element
	 * @param id
	 */
	private void waitForSmartGWTElement(String id)
	{
		try {
			this.bot.waitForElementPresent(By.xpath("//div[@eventproxy='" + id + "']"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
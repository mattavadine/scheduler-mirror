package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class CMAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/CM";
		
		this.driver = drv;
		super.setUp(protoURL, drv);
	}
	
	public void tearDown() {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	private void deleteDocumentFromHomeTab(final String documentName) throws InterruptedException {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		List<WebElement> existingDocumentsNames = driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]"));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		Integer existingDocumentIndex = null;
		for (int i = 0; existingDocumentIndex == null && i < existingDocumentsNames.size(); i++) {
			System.out.println("Existing document index " + i + " has name " + existingDocumentsNames.get(i).getText().trim() + " comparing to " + documentName);
			if (existingDocumentsNames.get(i).getText().trim().equalsIgnoreCase(documentName))
				existingDocumentIndex = i;
		}
		System.out.println("Found document " + documentName + " at: " + existingDocumentIndex);
		
		if (existingDocumentIndex != null) {
			By rowXPath = By.xpath("//table[@class='listTable']/tbody/tr[@role='listitem'][" + (existingDocumentIndex + 1) + "]");
			
			WebElement existingDocumentClickableCell = driver.findElement(rowXPath).findElement(By.xpath("td[2]/div"));
			WebUtility.mouseDownAndUpAt(driver, existingDocumentClickableCell, 5, 5);
			
			assert("true".equals(driver.findElement(rowXPath).getAttribute("aria-selected"))); // Sanity check, sometimes it was selecting the wrong one, given my xpath...
			
			WebUtility.mouseDownAndUpAt(driver, By.xpath("//div[@eventproxy='s_deleteBtn']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		WebUtility.mouseDownAndUpAt(driver, By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		WebUtility.waitForElementPresent(driver, By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForCM() throws InterruptedException {
		login("tyhollan");
		
		final String documentName = "CM Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "CM", "102", "Introduction to Construction Management", "1", "3", "3", "MW,TuTh", "3", "97", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "CM", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "CM", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 3, true, "CM", "202", "Digital Photography", "1", "3", "3", "MW", "2", "50", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 4, true, "CM", "202", "Digital Photography", "3", "3", "3", null, "3", "20", "LAB", null, "CM 202");
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 5, true, "CM", "203", "Digital File Preparation and Workflow", "1", "2", "3", "TuTh", "1", "45", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 6, true, "CM", "203", "Digital File Preparation and Workflow", "3", "3", "3", null, "3", "15", "LAB", null, "CM 203 (tethered)");
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 7, true, "CM", "211", "Substrates, Inks and Toners", "1", "3", "3", "TuTh", "3", "43", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 8, true, "CM", "211", "Substrates, Inks and Toners", "3", "3", "3", null, "3", "14", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 9, true, "CM", "212", "Substrates, Inks and Toners: Theory", "1", "3", "3", "TuTh", "3", "6", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 10, true, "CM", "320", "Managing Quality in Graphic Communication", "1", "3", "3", "TuTh", "3", "46", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 11, true, "CM", "320", "Managing Quality in Graphic Communication", "3", "3", "3", null, "3", "16", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 12, true, "CM", "324", "Binding, Finishing and Distribution Process", "1", "3", "3", "WF", "3", "44", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 13, true, "CM", "324", "Binding, Finishing and Distribution Process", "3", "3", "3", null, "3", "14", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 14, true, "CM", "325", "Binding, Finishing and Distribution Process: Theory", "1", "3", "3", "WF", "3", "66", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 15, true, "CM", "328", "Sheetfed Printing Technology", "1", "3", "3", "TuTh", "3", "84", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 16, true, "CM", "328", "Sheetfed Printing Technology", "4", "3", "3", null, "3", "12", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 17, true, "CM", "331", "Color Management and Quality Analysis", "1", "3", "3", "TuTh", "3", "36", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 18, true, "CM", "331", "Color Management and Quality Analysis", "2", "3", "3", null, "2", "12", "ACT", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 19, false, "CM", "337", "Consumer Packaging", "1", "3", "3", "MW", "2", "48", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 20, false, "CM", "337", "Consumer Packaging", "2", "3", "3", null, "3", "20", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 21, true, "CM", "377", "Web and Print Publishing", "1", "3", "3", "MW", "3", "48", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 22, true, "CM", "377", "Web and Print Publishing", "3", "3", "3", null, "3", "20", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 23, true, "CM", "400", "Special Problems", "8", "3", "3", null, null, "10", "IND", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 24, true, "CM", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "1", "3", "3", "MW", "2", "54", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 25, true, "CM", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "3", "3", "3", null, "2", "12", "ACT", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 26, true, "CM", "403", "Estimating for Print and Digital Media", "1", "3", "3", "TuTh", "3", "56", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 27, true, "CM", "403", "Estimating for Print and Digital Media", "3", "3", "3", null, "3", "20", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 28, true, "CM", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "1", "3", "3", "TuTh", "3", "50", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 29, true, "CM", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 30, true, "CM", "421", "Production Management for Print and Digital Media", "1", "3", "3", "TuTh", "3", "56", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 31, true, "CM", "421", "Production Management for Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 32, false, "CM", "422", "Human Resource Management Issues for Print and Digital Media", "1", "3", "3", "TuTh", "3", "40", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 33, false, "CM", "422", "Human Resource Management Issues for Print and Digital Media", "2", "3", "3", null, "3", "20", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 34, true, "CM", "429", "Digital Media", "1", "3", "3", "M", "2", "46", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 35, true, "CM", "429", "Digital Media", "2", "3", "3", null, "3", "12", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 36, true, "CM", "440", "Magazine and Newspaper Design Technology", "1", "3", "3", "TuTh", "3", "48", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 37, true, "CM", "440", "Magazine and Newspaper Design Technology", "2", "3", "3", null, "3", "12", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 38, true, "CM", "460", "Research Methods in Graphic Communication", "1", "3", "3", "M", "1", "24", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 39, true, "CM", "460", "Research Methods in Graphic Communication", "1", "3", "3", null, "3", "20", "LAB", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 40, true, "CM", "461", "Senior Project", "13", "3", "3", null, null, "10", "IND", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 41, true, "CM", "461", "Senior Project", "1", "3", "3", null, null, "15", "IND", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 42, true, "CM", "472", "Applied Graphic Communication Practices", "1", "3", "3", "W", "2", "75", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 43, false, "CM", "473", "Applied Graphic Communication Practices", "1", "3", "3", "M", "2", "20", "LEC", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 44, true, "CM", "485", "Cooperative Education Experience", "3", "3", "3", null, null, "10", "IND", null, null);
//		SchedulerBot.enterIntoCoursesResourceTableNewRow(driver, 45, true, "CM", "495", "Cooperative Education Experience", "2", "3", "3", null, null, "5", "IND", null, null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Holland", "Tyler", "tyholla", "20");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "14-255", "Smart Room", "9001", null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}

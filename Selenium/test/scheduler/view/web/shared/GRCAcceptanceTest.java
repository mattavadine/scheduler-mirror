package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;
import scheduler.view.web.shared.Selenium.WebUtility.TPrefsChunk;

public abstract class GRCAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/GRC";
		
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
			
			WebUtility.mouseDownAndUpAt(driver, By.xpath("//div/table/tbody/tr/td[text()='Delete Selected Documents']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		WebUtility.mouseDownAndUpAt(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"), 5, 5);

		WebUtility.waitForElementPresent(driver, By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("admin");
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForGRC() throws InterruptedException {
		login("eovadia");
		
		final String documentName = "GRC Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "GRC", "101", "Graphics", "1", "3", "3", "MW,TR", "3", "97", "LEC", "Smart Room", null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "GRC", "202", "Digital Photography", "1", "3", "3", "MW", "2", "50", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "GRC", "202", "Digital Photography", "3", "3", "3", null, "3", "20", "LAB", null, "GRC 202");
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "GRC", "203", "Digital File Preparation and Workflow", "1", "2", "3", "TR", "1", "45", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "GRC", "203", "Digital File Preparation and Workflow", "3", "3", "3", null, "3", "15", "LAB", null, "GRC 203 (tethered)");
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "GRC", "211", "Substrates, Inks and Toners", "1", "3", "3", "TR", "3", "43", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "GRC", "211", "Substrates, Inks and Toners", "3", "3", "3", null, "3", "14", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 9, true, "GRC", "212", "Substrates, Inks and Toners: Reory", "1", "3", "3", "TR", "3", "6", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 10, true, "GRC", "320", "Managing Quality in Graphic Communication", "1", "3", "3", "TR", "3", "46", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "GRC", "320", "Managing Quality in Graphic Communication", "3", "3", "3", null, "3", "16", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "GRC", "324", "Binding, Finishing and Distribution Process", "1", "3", "3", "WF", "3", "44", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "GRC", "324", "Binding, Finishing and Distribution Process", "3", "3", "3", null, "3", "14", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "GRC", "325", "Binding, Finishing and Distribution Process: Theory", "1", "3", "3", "WF", "3", "66", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "GRC", "328", "Sheetfed Printing Technology", "1", "3", "3", "TR", "3", "84", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "GRC", "328", "Sheetfed Printing Technology", "4", "3", "3", null, "3", "12", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "GRC", "331", "Color Management and Quality Analysis", "1", "3", "3", "TR", "3", "36", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "GRC", "331", "Color Management and Quality Analysis", "2", "3", "3", null, "2", "12", "ACT", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, false, "GRC", "337", "Consumer Packaging", "1", "3", "3", "MW", "2", "48", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, false, "GRC", "337", "Consumer Packaging", "2", "3", "3", null, "3", "20", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "GRC", "377", "Web and Print Publishing", "1", "3", "3", "MW", "3", "48", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "GRC", "377", "Web and Print Publishing", "3", "3", "3", null, "3", "20", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "GRC", "400", "Special Problems", "8", "3", "3", null, null, "10", "IND", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "GRC", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "1", "3", "3", "MW", "2", "54", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "GRC", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "3", "3", "3", null, "2", "12", "ACT", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "GRC", "403", "Estimating for Print and Digital Media", "1", "3", "3", "TR", "3", "56", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "GRC", "403", "Estimating for Print and Digital Media", "3", "3", "3", null, "3", "20", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "GRC", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "1", "3", "3", "TR", "3", "50", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "GRC", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "GRC", "421", "Production Management for Print and Digital Media", "1", "3", "3", "TR", "3", "56", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "GRC", "421", "Production Management for Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, false, "GRC", "422", "Human Resource Management Issues for Print and Digital Media", "1", "3", "3", "TR", "3", "40", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, false, "GRC", "422", "Human Resource Management Issues for Print and Digital Media", "2", "3", "3", null, "3", "20", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "GRC", "429", "Digital Media", "1", "3", "3", "M", "2", "46", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 35, true, "GRC", "429", "Digital Media", "2", "3", "3", null, "3", "12", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 36, true, "GRC", "440", "Magazine and Newspaper Design Technology", "1", "3", "3", "TR", "3", "48", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 37, true, "GRC", "440", "Magazine and Newspaper Design Technology", "2", "3", "3", null, "3", "12", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 38, true, "GRC", "460", "Research Methods in Graphic Communication", "1", "3", "3", "M", "1", "24", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 39, true, "GRC", "460", "Research Methods in Graphic Communication", "1", "3", "3", null, "3", "20", "LAB", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 40, true, "GRC", "461", "Senior Project", "13", "3", "3", null, null, "10", "IND", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 41, true, "GRC", "461", "Senior Project", "1", "3", "3", null, null, "15", "IND", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 42, true, "GRC", "472", "Applied Graphic Communication Practices", "1", "3", "3", "W", "2", "75", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 43, false, "GRC", "473", "Applied Graphic Communication Practices", "1", "3", "3", "M", "2", "20", "LEC", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 44, true, "GRC", "485", "Cooperative Education Experience", "3", "3", "3", null, null, "10", "IND", null, null);
////		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 45, true, "GRC", "495", "Cooperative Education Experience", "2", "3", "3", null, null, "5", "IND", null, null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		Thread.sleep(500);
		
		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Ovadia", "Evan", "eovadia", "20", new TPrefsChunk("MWF", 3, 6, "Acceptable"));
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 1, true, "Derp", "Herp", "hderp", "20");
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 2, true, "Something", "Other", "osomethi", "16");
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 3, true, "World", "Hello", "hworld", "20");
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 4, true, "Craft", "Star", "scraft", "20");
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 5, true, "Boo", "Lol", "lboo", "16");
//		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 6, true, "Grokka", "Wogga", "wgrokka", "16");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
//		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "14-255", "Smart Room", "9001", null);
//		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 1, true, "14-230", null, "200", null);
//		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 2, true, "14-240", null, "300", null);
//		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 3, true, "14-260", null, "250", null);
//		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 4, true, "14-232B", null, "220", null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}

// TODO: see if documentName appears anywhere on screen?

// saving:

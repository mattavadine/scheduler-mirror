package scheduler.view.web.shared;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.xml.LaunchSuite.ExistingSuite;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

public abstract class MUAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/MU";
	private SchedulerBot bot;	
	
	public void setUp(WebDriver drv) {
		this.driver = drv;
		super.setUp(protoURL, drv);
		bot = new SchedulerBot(driver);
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
			if (existingDocumentsNames.get(i).getText().trim().equalsIgnoreCase(documentName))
				existingDocumentIndex = i;
		}
		
		if (existingDocumentIndex != null) {
			By rowXPath = By.xpath("//table[@class='listTable']/tbody/tr[@role='listitem'][" + (existingDocumentIndex + 1) + "]");
			
			WebElement existingDocumentClickableCell = driver.findElement(rowXPath).findElement(By.xpath("td[2]/div"));
			bot.mouseDownAndUpAt(existingDocumentClickableCell, 5, 5);
			
			assert("true".equals(driver.findElement(rowXPath).getAttribute("aria-selected"))); // Sanity check, sometimes it was selecting the wrong one, given my xpath...
			
			bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_deleteBtn']"), 5, 5);
			
			Thread.sleep(2000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		bot.waitForElementPresent(By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForMU() throws InterruptedException {
		login("jrawson");
		
		final String documentName = "MU Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		bot.enterIntoCoursesResourceTableNewRow(0, true, "MU", "101", "Introduction to Music Theory", "1", "3", "3", "MW,TuTh", "3", "97", "LEC", "Smart Room", null);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		//bot.enterIntoInstructorsResourceTableNewRow(0, true, "Ovadia", "Evan", "eovadia", "20");

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		bot.enterIntoLocationsResourceTableNewRow(0, true, "14-255", "Smart Room", "9001", null);
	}
}

// TODO: see if documentName appears anywhere on screen?

// saving:
//driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
//driver.findElement(By.xpath("//td[@class='menuTitleField']/nobr[text()='Save']")).click();
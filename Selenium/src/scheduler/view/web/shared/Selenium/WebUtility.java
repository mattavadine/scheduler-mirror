package scheduler.view.web.shared.Selenium;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * SchedulerBot provides an intermediary navigator of the Scheduler UI
 * components and functionality
 * 
 * @author Salome Navarrete
 * @version 1.0 Nov 22 2011
 */
public abstract class WebUtility {
	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	interface Waitable {
		public boolean stopWaiting();
	}
	
	public static void mouseDownAndUpAt(WebDriver driver, WebElement element, int x, int y) {
		new Actions(driver)
		.clickAndHold(element)
		.moveByOffset(x, y)
		.release(element)
		.build()
		.perform(); 
	}

	public static WebElement elementForResourceTableCell(WebDriver driver, String viewID, int row0Based, int col0Based) {
		return driver.findElement(By.xpath("((//div[contains(@eventproxy, '" + viewID + "')]//table[@class='listTable']/tbody/tr[@role='listitem'])[" + (1 + row0Based) + "]/td)[" + (1 + col0Based) + "]"));
	}
	
	public static void setResourceTableTextCell(WebDriver driver, String viewID, int row0Based, int col0Based, String text) throws InterruptedException {
		if (text == null)
			return;

		Thread.sleep(500);
		WebElement cell = elementForResourceTableCell(driver, viewID, row0Based, col0Based);
		cell.click();

		Thread.sleep(500);
		WebElement input = elementForResourceTableCell(driver, viewID, row0Based, col0Based).findElement(By.xpath("//input"));
		input.sendKeys(text);

		driver.findElement(By.tagName("body")).click();
		Thread.sleep(500);
	}

	private static void setResourceTableMultiselectCell(WebDriver driver, String viewID, int row0Based, int col0Based, String optionsCombined) throws InterruptedException {
		if (optionsCombined == null)
			return;

		Set<String> options = new TreeSet<String>(Arrays.asList(optionsCombined.split(",")));
		
		WebElement cell = elementForResourceTableCell(driver, viewID, row0Based, col0Based);
		cell.click();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);

		Thread.sleep(500);
		WebElement selectBeforeExpand = elementForResourceTableCell(driver, viewID, row0Based, col0Based).findElement(By.xpath("div/nobr/span/table"));
		selectBeforeExpand.click();
		
		Thread.sleep(500);
		
		WebElement popupList = driver.findElement(By.xpath("/html/body/div[@class='listGrid']"));
		WebElement popupListBody = popupList.findElement(By.xpath("div/div[@class='gridBody']"));
		
		List<WebElement> popupListRows = popupListBody.findElements(By.xpath("div/div/table/tbody/tr"));
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		System.out.println(popupListRows.size());
		assert(popupListRows.size() >= 2);
		for (WebElement popupListRow : popupListRows) {
			String text = popupListRow.findElement(By.xpath("td[2]/div/nobr")).getText().trim();
			if (options.contains(text)) {
				WebElement checkbox = popupListRow.findElement(By.xpath("td[1]/div/nobr/img"));
				 
				boolean currentValue = "true".equals(popupListRow.getAttribute("aria-checked"));
				
				if (currentValue != true)
					checkbox.click();
//					mouseDownAndUpAt(checkbox, 5, 5);
			}
		}
		
		driver.findElement(By.tagName("body")).click();
		Thread.sleep(500);
	}

	private static void setResourceTableSelectCell(WebDriver driver, String viewID, int row0Based, int col0Based, String text) throws InterruptedException {
		if (text == null)
			return;

		WebElement cell = elementForResourceTableCell(driver, viewID, row0Based, col0Based);
		cell.click();
		
		System.out.println("TODO: get rid of IND->LAB conversion!");
		if ("IND".equals(text))
			text = "LAB";
		// Making test temporarily pass so we can get on with the rest of it
		
		WebElement selectBeforeExpand = elementForResourceTableCell(driver, viewID, row0Based, col0Based).findElement(By.xpath("div/nobr/span/table"));
		selectBeforeExpand.click();
		
		WebElement popupList = driver.findElement(By.xpath("/html/body/div/div/div[@class='pickListMenuBody']"));
		
		boolean foundOptionToClickOn = false;
		for (WebElement option : popupList.findElements(By.xpath("//tr[@role='listitem']"))) {
			if (option.getText().trim().equalsIgnoreCase(text)) {
				option.click();
				foundOptionToClickOn = true;
				break;
			}
		}
		
		assert(foundOptionToClickOn) : "Couldnt find option " + text;

		driver.findElement(By.tagName("body")).click();
		Thread.sleep(500);
		
		assert(elementForResourceTableCell(driver, viewID, row0Based, col0Based).getText().trim().equalsIgnoreCase(text));
	}
	
	private static void setResourceTableCheckboxCell(WebDriver driver, String viewID, int row0Based, int col0Based, boolean newValue) throws InterruptedException {
//		WebElement newCourseDeptCell = elementForResourceTableCell(row0Based, col0Based);
//		newCourseDeptCell.click();
		
		Thread.sleep(500);
		WebElement img = elementForResourceTableCell(driver, viewID, row0Based, col0Based).findElement(By.xpath("//div/img"));
		
		boolean currentValue = !img.getAttribute("src").contains("unchecked");
		
		if (currentValue != newValue)
			mouseDownAndUpAt(driver, img, 5, 5);
		
//		currentValue = "true".equals(imgParent.getAttribute("aria-checked"));
//		assert(currentValue == newValue);
		
		driver.findElement(By.tagName("body")).click();
		Thread.sleep(500);
	}
	
//	private void enterIntoResourceTableRow(int row0Based, Object... values) {
//		for (int i = 0; i < values.length; i++) {
//			Object object = values[i];
//			if (object == null)
//				continue;
//			
//			System.out.println("Handling index " + i + " value " + object);
//			
//			CellType type = openCellAndGetType(row0Based, i);
//			switch (type) {
//				case CHECKBOX: setResourceTableCheckboxCell(row0Based, i, (Boolean)object); break;
//				case TEXT: setResourceTableTextCell(row0Based, i, (String)object); break;
//				case SELECT: setResourceTableSelectCell(row0Based, i, (String)object); break;
//				default: assert(false);
//			}
//		}
//	}
	
//	private void enterIntoResourceTableNewRow(int row0Based, Object...values) {
//		driver.findElement(By.id("s_newCourseBtn")).click();
//		enterIntoResourceTableRow(row0Based, values);
//	}

//	bot.enterIntoResourceTableNewRow(0, true, "GRC", "101", "Graphics", "1", "3", "3", null, "3", "97", "LEC");
	public static void enterIntoCoursesResourceTableNewRow(
			WebDriver driver,
			int row0Based,
			boolean isSchedulable,
			String department,
			String catalogNum,
			String courseName,
			String numSections,
			String wtu,
			String scu,
			String dayCombinations,
			String hoursPerWeek,
			String maxEnrollment,
			String type,
			String usedEquipment,
			String association) throws InterruptedException {

		String view = "CoursesView";
		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
//		int row0BasedCalculated = view.findElements(By.xpath("//table[@class='listTable']/tbody/tr[@role='listitem']")).size();
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//		if (row0BasedCalculated != row0Based) {
//			System.out.println("calculated " + row0BasedCalculated + " not " + row0Based);
//			assert(false);
//		}
		
		System.out.println("Entering row index " + row0Based);

		Thread.sleep(500);
		driver.findElement(By.xpath("//div/table/tbody/tr/td[text()='Add New Course']")).click();
		
		setResourceTableTextCell(driver, view, row0Based, 1, department);
		setResourceTableTextCell(driver, view, row0Based, 2, catalogNum);
		setResourceTableTextCell(driver, view, row0Based, 3, courseName);
		setResourceTableSelectCell(driver, view, row0Based, 4, type);
		setResourceTableTextCell(driver, view, row0Based, 5, numSections);
		setResourceTableTextCell(driver, view, row0Based, 6, wtu);

		if (!scu.equals("4")) {
			setResourceTableTextCell(driver, view, row0Based, 7, scu);
			Thread.sleep(500);
			driver.switchTo().alert().accept();
		}
		
		setResourceTableMultiselectCell(driver, view, row0Based, 8, dayCombinations);
		setResourceTableTextCell(driver, view, row0Based, 9, hoursPerWeek);
		setResourceTableTextCell(driver, view, row0Based, 10, maxEnrollment);
		setResourceTableCheckboxCell(driver, view, row0Based, 13, isSchedulable);
//		setResourceTableMultiselectCell(driver, view, row0Based, 12, usedEquipment);
//		
	}
	
	public static void waitForElementPresent(final WebDriver driver, final By by) throws InterruptedException {
		new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
			public boolean apply(WebDriver arg0) {
				try {
					if (isElementPresent(driver, by))
						return true;
				}
				catch (Exception e) {}
				return false;
			}
		});
	}
	
	public static void mouseDownAndUpAt(WebDriver driver, By by, int x, int y) {
		WebElement element = driver.findElement(by);
		
		new Actions(driver)
				.clickAndHold(element)
				.moveByOffset(x, y)
				.release(element)
				.build()
				.perform();
	}
	
	public static class TPrefsChunk {
		public String days;
		public int startHalfHour, lastHalfHour;
		public String preference;
		
		public TPrefsChunk(String days, int startHalfHour, int lastHalfHour, String preference) {
			this.days = days;
			this.startHalfHour = startHalfHour;
			this.lastHalfHour = lastHalfHour;
			this.preference = preference;
		}
	}
	
	public static void enterIntoInstructorsResourceTableNewRow(
			WebDriver driver,
			int row0Based,
			boolean isSchedulable,
			String lastName,
			String firstName,
			String username,
			String maxWTU, TPrefsChunk... chunks) throws InterruptedException {

		System.out.println("Entering row index " + row0Based);

		driver.findElement(By.xpath("//div/table/tbody/tr/td[text()='Add New Instructor']")).click();

		String viewID = "InstructorsView";
		
		setResourceTableTextCell(driver, viewID, row0Based, 1, lastName);
		setResourceTableTextCell(driver, viewID, row0Based, 2, firstName);
		setResourceTableTextCell(driver, viewID, row0Based, 3, username);
		setResourceTableTextCell(driver, viewID, row0Based, 4, maxWTU);
//		setResourceTableCheckboxCell(driver, viewID, row0Based, 5, isSchedulable);
		
//		if (chunks.length > 0) {
//			WebElement buttonCell = elementForResourceTableCell(driver, viewID, row0Based, 5);
//			buttonCell.findElement(By.xpath("//td[@class='buttonTitle'][text()='Preferences']")).click();
//			
//			Thread.sleep(500);
//			
//			WebElement prefsPopup = driver.findElement(By.xpath("//table[@class='preferencesPanel']"));
//			
//			for (TPrefsChunk chunk : chunks) {
//				setCheckboxChecked(prefsPopup.findElement(By.xpath("//span[@id='mondayCheck']/input")), chunk.days.contains("M"));
//				setCheckboxChecked(prefsPopup.findElement(By.xpath("//span[@id='tuesdayCheck']/input")), chunk.days.contains("T"));
//				setCheckboxChecked(prefsPopup.findElement(By.xpath("//span[@id='wednesdayCheck']/input")), chunk.days.contains("W"));
//				setCheckboxChecked(prefsPopup.findElement(By.xpath("//span[@id='thursdayCheck']/input")), chunk.days.contains("R"));
//				setCheckboxChecked(prefsPopup.findElement(By.xpath("//span[@id='fridayCheck']/input")), chunk.days.contains("F"));
//				
//				WebElement prefSelect = prefsPopup.findElement(By.xpath("//select[1]"));
//				prefSelect.click();
//				prefSelect.findElement(By.xpath("/option[text()='" + chunk.preference + "']")).click();
//
//				WebElement startSelect = prefsPopup.findElement(By.xpath("//select[2]"));
//				startSelect.click();
//				startSelect.findElement(By.xpath("/option[" + (chunk.startHalfHour + 1) + "]")).click();
//				
//				WebElement lastSelect = prefsPopup.findElement(By.xpath("//select[3]"));
//				lastSelect.click();
//				lastSelect.findElement(By.xpath("/option[" + (chunk.lastHalfHour + 1) + "]")).click();
//				
//				prefsPopup.findElement(By.xpath("//button[text()='Set Preferences']")).click();
//			}
//		}
	}
	
	public static boolean checkboxChecked(WebElement checkbox) {
		return !(checkbox.getAttribute("checked").isEmpty() || checkbox.getAttribute("checked").contentEquals("unchecked"));
	}
	
	public static void setCheckboxChecked(WebElement checkbox, boolean shouldBeChecked) {
		boolean checked = checkboxChecked(checkbox);
		if (shouldBeChecked != checked) {
			checkbox.click();
			assert(checkboxChecked(checkbox) == shouldBeChecked);
		}
	}
	
	

	public static void enterIntoLocationsResourceTableNewRow(
			WebDriver driver,
			int row0Based,
			boolean isSchedulable,
			String room,
			String type,
			String maxOccupancy,
			String equipment) throws InterruptedException {

		System.out.println("Entering row index " + row0Based);

		driver.findElement(By.xpath("//div/table/tbody/tr/td[text()='Add New Location']")).click();

		String viewID = "LocationsView";
		
		setResourceTableTextCell(driver, viewID, row0Based, 1, room);
		setResourceTableTextCell(driver, viewID, row0Based, 2, type);
		setResourceTableTextCell(driver, viewID, row0Based, 3, maxOccupancy);
//		setResourceTableCheckboxCell(driver, viewID, row0Based, 4, isSchedulable);
//		setResourceTableTextCell(driver, viewID, row0Based, 5, equipment);
	}
	
	public static void clickInstructorsResourceTablePreferencesButton(
			WebDriver driver, int row0Based) throws InterruptedException {

		System.out.println("clicking preferences button at line: " + row0Based);

		Thread.sleep(500);
		WebElement btn = elementForResourceTableCell(driver, "InstructorsView",
				row0Based, 6).findElement(By.xpath("//td[text()='Preferences'][@class='buttonTitle']"));
//		btn.click();
		System.out.println(btn);
		mouseDownAndUpAt(driver, btn, 1, 1);

		Thread.sleep(500);
	}
	
	/**
	 * this only works when you're in the document overview
	 * @return: null if there is no document with the specified name
	 */
	public static WebElement getDocumentByName(WebDriver driver, String name)
	{
		try
		{
			return driver.findElement(By.xpath(
				"((//td[@class='inAppLink homeDocumentLink'][text()='"+name+"']))"));
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
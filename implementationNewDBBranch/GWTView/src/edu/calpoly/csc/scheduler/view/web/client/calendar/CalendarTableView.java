package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.gargoylesoftware.htmlunit.javascript.host.Window;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This class encapsulates both the view and model for the schedule table that allows 
 * drag and drop editing. It also contains the logic to layout all of the schedule
 * items on the table. Not the best design, but it was relatively easy and quick to implement
 * 
 * @author Matt Schirle
 */
public class CalendarTableView extends SimplePanel {
	
	/**
	 * Models one row in the table
	 */
	private static class CalendarRowModel extends Vector<ScheduleItemGWT> {
		
		@Override
		public ScheduleItemGWT get(int index) {
			if (index >= size())
				return null;
			return super.get(index);
		}
	}
	
	/**
	 * Models one day in the table. Each day has a ScheduleEditTableRowModel for each available time.
	 */
	private static class CalendarDayModel extends Vector<CalendarRowModel> {
		
		public CalendarDayModel() {
			mWidth = 1;
			mOffset = 0;
			
			for(int rowNdx = 0; rowNdx < END_TIMES.length; rowNdx++)
				add(new CalendarRowModel());
		}
		
		@Override 
		public void clear() {
			mWidth = 1;
			mOffset = 0;
			
			// Don't delete the rows, just clear each one
			for (int i = 0; i < size(); i++)
				get(i).clear();
		}
		
		/**
		 * @return The number of columns on this day 
		 */
		public int getWidth() { return mWidth; }
		
		/**
		 * @return The column this day starts on
		 */
		public int getOffset() { return mOffset; }
		
		public void setWidth(int width) { mWidth = width; }
		public void setOffset(int offset) { mOffset = offset; }
		
		private int mWidth;
		private int mOffset;
	}
	
	private static class CalendarModel {
		
		public CalendarModel() {
			mDays = new CalendarDayModel[7];
			for (DayGWT day : DayGWT.values())
				mDays[day.ordinal()] = new CalendarDayModel();
		}
		
		public void clear() {
			for (DayGWT day : DayGWT.values())
				get(day).clear();
		}

		public CalendarDayModel get(DayGWT day) {
			return mDays[day.ordinal()];
		}
		
		public DayGWT getDay(int colNum) {
			for (DayGWT day : DayGWT.values()) {
				CalendarDayModel dayModel = get(day);
				if (colNum >= dayModel.getOffset() && 
						colNum < dayModel.getOffset() + dayModel.getWidth())
					return day;
			}
			
			System.out.println("Day model not found!");
			return null;
		}
		
		public CalendarDayModel get(int colNum) {
			return get(getDay(colNum));
		}
		
		private final CalendarDayModel mDays[];
	}
	
	private List<ScheduleItemGWT> mScheduleItems;
	private List<ScheduleItemGWT> mFilteredScheduleItems;
	private CalendarModel mModel;
	private String mInnerHTML;
	private int mLeftOffset;
	
	private final DragAndDropController mDragController;
	private final ScheduleEditWidget mScheduleController;
	
	public static final String END_TIMES[] = { "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM",
		"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
		"12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
		"3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM",
		"6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM",
		"9:00 PM", "9:30 PM" };
	public static final String START_TIMES[] = { "7:10 AM", "7:40 AM", "8:10 AM", "8:40 AM",
		"9:10 AM", "9:40 AM", "10:10 AM", "10:40 AM", "11:10 AM", "11:40 AM",
		"12:10 PM", "12:40 PM", "1:10 PM", "1:40 PM", "2:10 PM", "2:40 PM",
		"3:10 PM", "3:40 PM", "4:10 PM", "4:40 PM", "5:10 PM", "5:40 PM",
		"6:10 PM", "6:40 PM", "7:10 PM", "7:40 PM", "8:10 PM", "8:40 PM",
		"9:10 PM", "9:40 PM" };
	
	public static final String DAYS[] = { "Monday", "Tuesday", "Wednesday", 
		"Thursday", "Friday" };
	
	public CalendarTableView(ScheduleEditWidget scheduleController, DragAndDropController dragController) {
		mModel = new CalendarModel();
		mScheduleController = scheduleController;
		mDragController = dragController;
		
		defineTableCallbacks();
	}
	
	/**
	 * Takes the table model and draws a raw HTML table
	 */
	public void drawTable() {
		clear();
		
		mModel = buildTableModel();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("<style type=\"text/css\">"+
			"* {-webkit-user-select:none;-moz-user-select:none;}" +
			"#CalendarTableContainer {position:absolute;top:116px;left:"+mLeftOffset+"px;right:0px;bottom:33px;overflow:auto;background-color:#FFFFFF;}"+
			"#CalendarTable {border-spacing:0px;cellspacing:0px;border:none;}"+
			"#CalendarTable tr {height:20px;}"+
			"#CalendarTable td {overflow:hidden;padding:4px;border-top:1px solid #d1dfdf;}"+
			"#CalendarTable td.item {background-color:#DFF0CF;text-align:center;border:1px solid #FFFFFF;cursor:move;}"+
			"#CalendarTable td.dayHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;border-bottom:1px solid #000000;font-weight:bold;text-align:center;z-index:2;}"+
			"#CalendarTable td.timeHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;white-space:nowrap;text-align:right;}" +
			"#CalendarTable td#topCorner {border-bottom:1px solid #000000;background-color:#edf2f2;}"+
			"#CalendarTable td.daySpacer {border-right:1px solid #000000;padding:0px;margin:0px;width:0px;}"+
			"#"+DragAndDropController.DRAGGED_ID+" {display:none;position:fixed;margin-left:-30px;margin-top:10px;width:70px;padding:3px;background-color:#DFF0CF;z-index:999;border:1px solid #FFFFFF;cursor:arrow;}"+
			".ScheduleAvailableCoursesList {position:absolute;top:116px;left:0px;bottom:33px;width:200px;border-right:1px solid #000000;background-color:#FFFFFF;}"+
			"</style>");
		builder.append("<div id=\"CalendarTableContainer\" onscroll=\"tableContainerScroll()\">");
		builder.append("<table id=\"CalendarTable\"><tr id=\"headerRow\"><td id=\"topCorner\" class=\"dayHeader\"></td>");
		
		// Add day headers
		for (DayGWT day : DayGWT.values()) {
			int colspan = mModel.get(day).getWidth() + 1; // +1 for day spacer cells (see loop below)
			
			builder.append("<td colspan="+colspan+" class=\"dayHeader\" id='h"+day.name+"'>"+day.name+"</td>");
		}
		builder.append("</tr>");
		
		// Fill in table
		for (int rowNum = 0; rowNum < END_TIMES.length; rowNum++) {
			builder.append("<tr id=\"y"+rowNum+"\" " +
				"onmouseover=\"tableMouseOver("+rowNum+")\" " +
				"onmouseout=\"tableMouseOut("+rowNum+")\" " +
				"><td class=\"timeHeader\" id=\"h"+rowNum+"\">"+END_TIMES[rowNum]+"</td>");
			
			for (DayGWT day : DayGWT.values()) {
				final CalendarDayModel dayModel = mModel.get(day);
				
				for (int colNum = 0; colNum < dayModel.getWidth(); colNum++) {
					final ScheduleItemGWT item = dayModel.get(rowNum).get(colNum);
					final int tableRow = rowNum;
					final int tableCol = colNum + dayModel.getOffset();
					
					if (item == null) {
						builder.append("<td id=\"x"+tableCol+"y"+tableRow+"\"" +
								"onmouseup=\"tableMouseUp("+tableRow+","+tableCol+")\" " +
								"></td>");
					}
					else if (rowNum == getStartRow(item)) {
						final int rowspan = getEndRow(item) - getStartRow(item) + 1;
						
						builder.append("<td " +
								"rowspan="+rowspan+" " +
								"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
								"ondblclick=\"tableDoubleClick("+tableRow+","+tableCol+")\" " +
								"onmousedown=\"tableMouseDown("+tableRow+","+tableCol+")\" " +
								"onmouseup=\"tableMouseUp("+tableRow+","+tableCol+")\" " +
								"onselectstart=\"return false\" " +
								">"+mScheduleController.getCourseString(item.getCourseID())+"</td>");
					}
				}
				
				builder.append("<td class=\"daySpacer\"></td>");
			}
			
			builder.append("</tr>");
		}
		builder.append("</table>");
		builder.append("</div>");

		// Add div that drags with cursor
		builder.append("<div id="+DragAndDropController.DRAGGED_ID+">ENGL 101</div>");
		
		mInnerHTML = builder.toString();
		setHTML(mInnerHTML);
	}
	
	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}
	
	/**
	 * Lays out a table model from the list of filtered scheduled items
	 */
	private CalendarModel buildTableModel() {
		// TODO link schedule item between its' days in model so you can 
		// highlight them item on all of it's days when it's selected on the UI
		
		CalendarModel model = new CalendarModel();
		
		for (ScheduleItemGWT item : mFilteredScheduleItems) {
			for (DayGWT day : item.getDays()) {
				CalendarDayModel dayModel = model.get(day);
				
				// Find the leftmost column that is open on all rows that this item needs to occupy
				int colNdx = 0;
				for (; colNdx < dayModel.getWidth(); colNdx++) {
					boolean occupied = false;
					
					for (int rowNdx = getStartRow(item); rowNdx <= getEndRow(item); rowNdx++) {
						CalendarRowModel row = dayModel.get(rowNdx);
						
						if (row.get(colNdx) != null) {
							occupied = true;
							break;
						}
					}
					
					if (!occupied)
						break;
				}
				
				// Check it we need to increase the width of this day
				if (colNdx >= dayModel.getWidth()) {
					dayModel.setWidth(colNdx + 1);
				}
				
				int startRow = getStartRow(item);
				int endRow = getEndRow(item);
				if (startRow > endRow)
					throw new RuntimeException("lol ur dum");
				
				// Add the item to the column we picked on all of the item's rows
				for (int rowNdx = startRow; rowNdx <= endRow; rowNdx++) {
					CalendarRowModel row = dayModel.get(rowNdx);
					
					if (colNdx >= row.size()) {
						row.setSize(colNdx + 1);
					}
					row.set(colNdx, item);
				}
			}
		}
		
		// Calculate all of the day offsets (the column each day starts on)
		for (DayGWT day : Arrays.asList(DayGWT.values()).subList(1, DayGWT.values().length - 1)) {
			DayGWT prevDay = DayGWT.values()[day.ordinal() - 1];
			int prevOffset = model.get(prevDay).getOffset();
			int prevWidth = model.get(prevDay).getWidth();
			model.get(day).setOffset(prevOffset + prevWidth);
		}
		
		return model;
	}
	
	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row, int col) {
		final CalendarDayModel day = mModel.get(col);
		final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());

		mScheduleController.editItem(false, item, new ArrayList<Integer>(0), row);
	}
	
	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row, int col) {
		final CalendarDayModel day = mModel.get(col);
		final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());
		
		// Set the text of the div that moves with the cursor
		Element dragDiv = DOM.getElementById(DragAndDropController.DRAGGED_ID);
		DOM.setInnerText(dragDiv, mScheduleController.getCourseString(item.getCourseID()));
		
		mDragController.onMouseDown(item, row, col);
		return false;
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseUp(int row, int col) {
		if (mDragController.isDragging()) {
			Element tr = DOM.getElementById("y"+row);
			DOM.setStyleAttribute(tr, "backgroundColor", "#FFFFFF");

			Element timeHeader = DOM.getElementById("h"+row);
			DOM.setStyleAttribute(timeHeader, "backgroundColor", "#edf2f2");
		}
		
		mDragController.onDrop(row, mModel.getDay(col));
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOver(int row) {
		if (mDragController.isDragging()) {
			Element tr = DOM.getElementById("y"+row);
			DOM.setStyleAttribute(tr, "backgroundColor", "#d1dfdf");

			Element timeHeader = DOM.getElementById("h"+row);
			DOM.setStyleAttribute(timeHeader, "backgroundColor", "#d1dfdf");
		}
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOut(int row) {
		Element cell = DOM.getElementById("y"+row);
		DOM.setStyleAttribute(cell, "backgroundColor", "#FFFFFF");

		Element timeHeader = DOM.getElementById("h"+row);
		DOM.setStyleAttribute(timeHeader, "backgroundColor", "#edf2f2");
	}
	
	/**
	 * Called when the table's container scrolls
	 */
	public void containerScroll() {
		final Element container = DOM.getElementById("CalendarTableContainer");
		final int top = DOM.getElementPropertyInt(container, "scrollTop");
		final int left = DOM.getElementPropertyInt(container, "scrollLeft");

		final Element corner = DOM.getElementById("topCorner");
		DOM.setStyleAttribute(corner, "top", top+"px");
		
		for (DayGWT day : DayGWT.values()) {
			final Element header = DOM.getElementById("h"+day.name);
			DOM.setStyleAttribute(header, "top", top+"px");
		}
		
		for (int time = 0; time < END_TIMES.length; time++) {
			final Element header = DOM.getElementById("h"+time);
			DOM.setStyleAttribute(header, "left", left+"px");
		}
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void defineTableCallbacks() /*-{
		var scheduleTable = this;
		$wnd.tableDoubleClick = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::doubleClick(II)(row, col);
		}
		$wnd.tableMouseDown = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::mouseDown(II)(row, col);
		}
		$wnd.tableMouseUp = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::mouseUp(II)(row, col);
		}
		$wnd.tableMouseOver = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::mouseOver(I)(row);
		}
		$wnd.tableMouseOut = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::mouseOut(I)(row);
		}
		$wnd.tableContainerScroll = function() {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarTableView::containerScroll()();
		}
    }-*/;
	
	public void setLeftOffset(int pixels) {
		mLeftOffset = pixels + 1;
		Element tableContainer = DOM.getElementById("CalendarTableContainer");
		DOM.setStyleAttribute(tableContainer, "left", mLeftOffset+"px");
	}
	
	private void applyFilters() {
		// TODO implement filtering
		mFilteredScheduleItems = mScheduleItems;
	}
	
	public static int getStartRow(ScheduleItemGWT item) {
		return item.getStartHalfHour() - 14;
	}
	
	public static int getEndRow(ScheduleItemGWT item) {
		return item.getEndHalfHour() - 14;
	}
	
	public List<ScheduleItemGWT> getScheduleItems() {
		return mScheduleItems;
	}
	
	public void setScheduleItems(List<ScheduleItemGWT> items) {
		mScheduleItems = items;
		applyFilters();
	}
	
	public void addScheduleItem(ScheduleItemGWT item) {
		
	}
	
	public void setDayFilter(List<Integer> days) {
		
	}
	
	public void setTimeFilter(List<Integer> times) {
		
	}
	
	public void setStringFilter(String str) {
		
	}
	
	public void setRoomFilter(List<String> rooms) {
		
	}
	
	public void setInstructorFilter(List<String> instructors) {
		
	}
	
	public void setCourseFilter(List<String> courses) {
		
	}
}

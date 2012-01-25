package edu.calpoly.csc.scheduler.view.web.client.views;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ReadingCell;
import edu.calpoly.csc.scheduler.view.web.client.views.AssociationsCell.GetCoursesCallback;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class AssociationsColumn implements OsmTable.IEditingColumn<CourseGWT> {
	final GetCoursesCallback getCourses;
	
	public AssociationsColumn(GetCoursesCallback getCourses) {
		this.getCourses = getCourses;
	}
	
	@Override
	public Cell createCell(IRowForColumn<CourseGWT> row) {
		return new AssociationsCell(getCourses, row);
	}

	@Override
	public void updateFromObject(IRowForColumn<CourseGWT> row, ReadingCell rawCell) {
		AssociationsCell cell = (AssociationsCell)rawCell;
		int lectureID = row.getObject().getLectureID();
		
		if (lectureID < 0) {
			cell.setSelectedCourse(null);
			return;
		}
		
		for (CourseGWT course : getCourses.getCourses()) {
			if (course.getID() == lectureID) {
				cell.setSelectedCourse(course);
				return;
			}
		}
		
		System.out.println("ERROR Trying to assign lecture id " + lectureID);

		cell.setSelectedCourse(null);
	}

	@Override
	public void commitToObject(IRowForColumn<CourseGWT> row, EditingCell rawCell) {
		AssociationsCell cell = (AssociationsCell)rawCell;
		row.getObject().setLectureID(cell.getSelectedCourseID());
		System.out.println("Setting lectureID to " + cell.getSelectedCourseID());
	}
}
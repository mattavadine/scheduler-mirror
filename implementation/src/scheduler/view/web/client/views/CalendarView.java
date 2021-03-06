package scheduler.view.web.client.views;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.calendar.ScheduleEditWidget;

import com.google.gwt.user.client.ui.VerticalPanel;

public class CalendarView extends VerticalPanel {
	public CalendarView(CachedOpenWorkingCopyDocument workingCopyDocument) {
		this.setWidth("100%");
		this.setHeight("100%");
		ScheduleEditWidget scheduleEditWidget = new ScheduleEditWidget(workingCopyDocument);
		this.add(scheduleEditWidget.getWidget());
	}

	public boolean canClose() {
		// If you want to keep the user from navigating away, return false here
		
		return true;
	}

	public void close() {
		this.clear();
	}
}

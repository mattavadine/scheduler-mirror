package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.views.View;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;

/**
 * This class represents the view for the instructors which shows
 * the documents which are available for the instructors and where
 * they are able to set their preferences.
 * @author Jake Juszak, Carsten Pfeffer <pfeffer.carsten@gmail.com>
 */
public class InstructorsHomeView extends VerticalPanel implements View
{
	protected CachedService service;
	protected ArrayList<String> scheduleNames;
	protected HashMap<Integer, DocumentGWT> allAvailableOriginalDocumentsByID;
	protected FlexTable schedList = new FlexTable();
	protected InstructorGWT instructor;
	
	protected InstructorPreferencesView iipv = null;
	protected Window prefsWindow = null;
	
	/**
	 * constructor sets up the view and loads the data
	 * @param service
	 */
	public InstructorsHomeView(final CachedService service)
	{	
		this.service = service;
		this.scheduleNames = new ArrayList<String>();
		this.setStyleName("centerness");
		
		// ------------------------------------
		
		this.setWidth("50%");
		this.schedList.setStyleName("otherCenterness");
		this.schedList.setWidth("100%");
		HTML schedule = new HTML("Scheduler<br/>\n\n\n");
		
		HTML mydocs = new HTML("My Scheduling Documents:<br/>");
		mydocs.setStyleName("centerness");
		
		schedule.addStyleName("editTableHeading");
		
		DOM.setElementAttribute(this.schedList.getElement(), "id", "schedList");
		this.setStyleName("centerness");
		this.add(schedule);
		this.add(mydocs);
		Label spacer = new Label();
		spacer.setHeight("10px");
		this.add(spacer);
		this.add(schedList);

		// ------------------------------------
		
		this.service.forceSynchronizeOriginalDocuments(new AsyncCallback<Void>(){
					 @Override
					 public void onFailure(Throwable caught)
					 {
						 com.google.gwt.user.client.Window.alert("There was an error getting the schedules: " + caught.getMessage());
					 }
					
					 @Override
					 public void onSuccess(Void v)
					 {
						 Collection<OriginalDocumentGWT> result = service.originalDocuments.getAll();
						 
						 allAvailableOriginalDocumentsByID = new HashMap<Integer, DocumentGWT>();
					
						 for (final OriginalDocumentGWT doc : result)
						 {
								allAvailableOriginalDocumentsByID.put(doc.getID(), doc);
								 
								if (!doc.isTrashed())
								{
									addNewDocument(doc);
									scheduleNames.add(doc.getName());
								}
						 }
						 if(scheduleNames.size() == 0)
						 {
							schedList.setWidget(schedList.getRowCount(),
									0, new HTML("There are currently no documents for you"));
						 }
					 }
				  });
	}
	
	/**
	 * adds a new document to the list in the view
	 * @param doc
	 */
	public void addNewDocument(final OriginalDocumentGWT doc)
	{
		// set the instructor

		int row = this.schedList.getRowCount();
		this.schedList.setWidget(row, 0, new HTML(doc.getName()));
		Button prefs = new Button("Preferences");
		this.schedList.getCellFormatter().addStyleName(row, 1, "rightness");
		prefs.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				service.openWorkingCopyForOriginalDocument(doc.getID(), false, new AsyncCallback<CachedOpenWorkingCopyDocument>() {
					public void onFailure(Throwable caught) {
						com.google.gwt.user.client.Window.alert("Failed to get instructors!");
					}
					public void onSuccess(CachedOpenWorkingCopyDocument result) {
						for (InstructorGWT i : result.getInstructors(true)) {
							if (i.getUsername().equals(service.username)) {
								
								sanityCheckInstructor(result, i);
								setInstructor(i);
								
								for(CourseGWT course : result.getCourses())
								{
									System.out.println("after loading ---------------" + course.getCourseName() + ": " +
											instructor.getCoursePreferences().get(course.getID()));
								}
								
								preferencesButtonClicked(result);
								break;
							}
						}
					}
				});
				
			}
			
		});
		schedList.setWidget(row, 1, prefs);
	}
	
	/**
	 * This opens the preferences window when a preferences button is clicked
	 * @param doc
	 */
	public void preferencesButtonClicked(CachedOpenWorkingCopyDocument doc) {
		
		// the window is automatically shown when the synchronization is done
		if(this.iipv == null)
		{
			this.prefsWindow = new Window();
			this.prefsWindow.setAutoSize(true);
			
			this.prefsWindow.setCanDragReposition(true);
			this.prefsWindow.setCanDragResize(false);
			
			this.iipv = new InstructorPreferencesView(doc, instructor);
			
			int height = com.google.gwt.user.client.Window.getClientHeight() - 30;
			
			ScrollPanel scroller = new ScrollPanel();
			scroller.setWidget(iipv);
			scroller.setSize("100%",(height-28)+"px");
			
			this.prefsWindow.addItem(scroller);
			this.iipv.afterPush(prefsWindow);
			
			this.prefsWindow.setAutoSize(false);
			this.prefsWindow.setSize("730px", height+"px");

			
			this.prefsWindow.addCloseClickHandler(new CloseClickHandler(){
				@Override
				public void onCloseClick(CloseClickEvent event) {
					savePreferences();
					prefsWindow.hide();
				}
			});
			
			this.iipv.addCloseHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					savePreferences();
				}
			});
		}
		else
		{
			this.iipv.synchronize(doc, instructor);
		}
		
		this.prefsWindow.setTitle("Instructor Preferences - <i>"
				+ doc.getDocument().getName() + "</i>");
	}
	
	/**
	 * saves the currently modified document to autosave the instructor's prefs
	 */
	private void savePreferences()
	{
//		this.iipv.save();
		this.iipv.getDocument().copyIntoAssociatedOriginalDocument(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				com.google.gwt.user.client.Window.alert("Successfully saved!");
				for(CourseGWT course : InstructorsHomeView.this.iipv.getDocument().getCourses())
				{
					System.out.println("after saving ---------------" + course.getCourseName() + ": " +
							instructor.getCoursePreferences().get(course.getID()));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				com.google.gwt.user.client.Window.alert("Failed to save!\n" + caught.getMessage());
			}
		});
	}
	
	/**
	 * sets the instructor who shows the document
	 * @param instructor
	 */
	public void setInstructor(InstructorGWT instructor1)
	{
		this.instructor = instructor1;
	}
	
	/**
	 * Checks if the instructor data is valid
	 * @param result
	 * @param instructor
	 */
	private void sanityCheckInstructor(CachedOpenWorkingCopyDocument result, InstructorGWT instructor) {
		for (CourseGWT course : result.getCourses())
			assert(instructor.getCoursePreferences().containsKey(course.getID()));
	}

	@Override
	/**
	 * returns whether this widget can be closed 
	 * @return always true
	 */
	public boolean canClose() {
		// If you want to keep the user from navigating away, return false here
		return true;
	}

	@Override
	/**
	 * closes this view
	 */
	public void close() {
		
	}

	@Override
	/**
	 * returns this class as a Widget object
	 */
	public Widget viewAsWidget() { return this; }
}

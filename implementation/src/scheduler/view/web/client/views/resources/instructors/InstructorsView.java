package scheduler.view.web.client.views.resources.instructors;

import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.views.resources.ValidatorUtil;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This view contains the resource table for the instructors
 */
public class InstructorsView extends VLayout {
	CachedOpenWorkingCopyDocument openDocument;
	
	protected InstructorPreferencesView iipv = null;
	protected Window prefsWindow = null;
	private ListGrid grid;

	/**
	 * the constructor calls the method to load the instructor list
	 * @param openDocument
	 */
	public InstructorsView(CachedOpenWorkingCopyDocument openDocument) {
		this.openDocument = openDocument;
		
//		setID("s_instructorviewTab");

		this.setWidth100();
		this.setHeight100();

		onPopulate();
	}
	
	/**
	 * loads the instructors into the listctrl
	 */
	private void onPopulate() {
		//com.google.gwt.user.client.Window.alert("onpopulate!");
		
		grid = new ListGrid() {
//			protected int rowCount = 0;

			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if(record != null)
				{
					if (getFieldName(colNum).equals("id")) {
						return "cursor: pointer; background: #D8D8D8;";
					} 
					else if (!ValidatorUtil.isValidInstructorType(getFieldName(colNum), record, getDataAsRecordList())) {
						// Invalid data, set background to red
						return "background: #FF9999;";
					} 
					else {
						// Valid data, do nothing
						return super.getCellCSSText(record, rowNum, colNum);
					}
				}
				return super.getCellCSSText(record, rowNum, colNum);
			}

			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("instructorPrefs")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("Preferences");
//					this.rowCount++;
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							final int instructorID = record.getAttributeAsInt("id");
							
							InstructorGWT instructor = openDocument.getInstructorByID(instructorID);
							preferencesButtonClicked(instructor);
						}
					});
					return button;
				} else {
					return null;
				}
			}
		};
		
		grid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		grid.setWidth100();
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		// grid.setCellHeight(22);
		grid.setDataSource(new InstructorsDataSource(openDocument));
		grid.setAutoSaveEdits(true);
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete")) {
					deleteSelected();
				}
			}
		});

//		ListGridField selectorField = new ListGridField("selectorField", "&nbsp;");
//		selectorField.setCanEdit(false);
//		selectorField.setCellFormatter(new CellFormatter() {
//			public String format(Object value, ListGridRecord record,
//					int rowNum, int colNum) {
//				return "\u22EE";
//			}
//		});
//		selectorField.setWidth(20);
//		selectorField.setAlign(Alignment.CENTER);

		IntegerRangeValidator nonnegativeInt = new IntegerRangeValidator();
		nonnegativeInt.setMin(0);

		ListGridField schedulableField = new ListGridField("isSchedulable",
				"Schedulable");
		schedulableField.setDefaultValue(true);
		schedulableField.setAlign(Alignment.CENTER);

		ListGridField lastNameField = new ListGridField("lastName", "Last Name");
		lastNameField.setAlign(Alignment.CENTER);
		lastNameField.setDefaultValue("");
//		lastNameField.setValidators(new CustomValidator() {
//			@Override
//			protected boolean condition(Object value) {
//				if (value == null) {
//					setErrorMessage("Username must be present!");
//					return false;
//				}
//				return true;
//			}
//		});

		ListGridField firstNameField = new ListGridField("firstName",
				"First Name");
		firstNameField.setAlign(Alignment.CENTER);
		firstNameField.setDefaultValue("");
//		firstNameField.setValidators(new CustomValidator() {
//			@Override
//			protected boolean condition(Object value) {
//				if (value == null) {
//					setErrorMessage("Username must be present!");
//					return false;
//				}
//				return true;
//			}
//		});

		ListGridField usernameField = new ListGridField("username", "Username");
		usernameField.setAlign(Alignment.CENTER);
//		usernameField.setValidators(new CustomValidator() {
//			protected boolean condition(Object value) {
//				if (value == null) {
//					setErrorMessage("Username must be present!");
//					return false;
//				}
//
//				assert (value instanceof String);
//				String username = (String) value;
//				if (username.trim().length() == 0) {
//					setErrorMessage("Username must be present!");
//					return false;
//				}
//
//				for (Record record : grid.getDataAsRecordList().getRange(0,
//						grid.getDataAsRecordList().getLength())) {
//					if (username.equals(record.getAttribute("username"))) {
//						setErrorMessage("Username \"" + username
//								+ "\" already exists!");
//						return false;
//					}
//				}
//				return true;
//			}
//		});

		ListGridField maxWTUField = new ListGridField("maxWTU", "Max WTU");
		maxWTUField.setAlign(Alignment.CENTER);
		maxWTUField.setDefaultValue(0);

		ListGridField instructorPrefsField = new ListGridField(
				"instructorPrefs", "Preferences");
		instructorPrefsField.setAlign(Alignment.CENTER);
		instructorPrefsField.setCanEdit(false);

		grid.setFields(lastNameField,
				firstNameField, usernameField, maxWTUField,
				instructorPrefsField, schedulableField);

		this.addMember(grid);
		// this.setHorizontalAlignment(ALIGN_DEFAULT);

		layoutBottomButtonBar(grid);
	}

	/**
	 * This opens the preferences window when a preferences button is clicked
	 * @param instructor
	 */
	public void preferencesButtonClicked(InstructorGWT instructor) {
		// the window is automatically shown when the synchronization is done
		if(this.iipv == null)
		{
			this.prefsWindow = new Window();
			this.prefsWindow.setAutoSize(true);
			
			this.prefsWindow.setCanDragReposition(true);
			this.prefsWindow.setCanDragResize(false);
			
			this.iipv = new InstructorPreferencesView(openDocument, instructor);
			
			int height = com.google.gwt.user.client.Window.getClientHeight() - 30;
			
			ScrollPanel scroller = new ScrollPanel();
			scroller.setWidget(iipv);
			scroller.setSize("100%",(height-28)+"px");
			
			this.prefsWindow.addItem(scroller);
			this.iipv.afterPush(prefsWindow);

			this.prefsWindow.setAutoSize(false);
			this.prefsWindow.setSize("730px", height+"px");
		}
		else
		{
			this.iipv.synchronize(openDocument, instructor);
		}
		
		this.prefsWindow.setTitle("Instructor Preferences - <i>"
				+ instructor.getUsername() + "</i> ("
				+ instructor.getFirstName() + " " + instructor.getLastName()
				+ ")");
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.addStyleName("floatingResourcesButtonBar");

		IButton addBtn = new IButton("Add New Instructor", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Record defaultValues = new Record();
				grid.startEditingNew(defaultValues);
			}
		});
		addBtn.setAutoWidth();
		addBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
//		addBtn.setID("s_newInstructorBtn");
		bottomButtonFlowPanel.addMember(addBtn);

		IButton duplicateBtn = new IButton("Duplicate Selected Instructors",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						grid.endEditing();
						
						for (ListGridRecord rec : grid.getSelectedRecords()) {
							int id = rec.getAttributeAsInt("id");
							InstructorGWT instructor = new InstructorGWT(openDocument.getInstructorByID(id));
							instructor.setID(null);
							openDocument.addInstructor(instructor);
						}
						
						grid.invalidateCache();
						grid.fetchData();
					}
				});
		duplicateBtn.setAutoWidth();
		duplicateBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
//		duplicateBtn.setID("s_dupeInstructorBtn");
		bottomButtonFlowPanel.addMember(duplicateBtn);

		IButton removeBtn = new IButton("Remove Selected Instructors",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						deleteSelected();
					}
				});
		// DOM.setElementAttribute(removeBtn.getElement(), "id", "removeBtn");


		removeBtn.setAutoWidth();
		removeBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
//		removeBtn.setID("s_removeInstructorBtn");

		bottomButtonFlowPanel.addMember(removeBtn);

		this.addMember(bottomButtonFlowPanel);
	}
	
	/**
	 * deletes the selected instructors
	 */
	void deleteSelected() {
		Set<Integer> referencedInstructorIDs = new TreeSet<Integer>();
		for (ScheduleItemGWT item : openDocument.getScheduleItems())
			referencedInstructorIDs.add(item.getInstructorID());
		
		Set<Integer> instructorsToDeleteIDs = new TreeSet<Integer>();
		for (ListGridRecord rec : grid.getSelectedRecords())
			instructorsToDeleteIDs.add(rec.getAttributeAsInt("id"));
		
		Set<Integer> referencedInstructorsToDeleteIDs = new TreeSet<Integer>(instructorsToDeleteIDs);
		referencedInstructorsToDeleteIDs.retainAll(referencedInstructorIDs);
		
		if (!referencedInstructorsToDeleteIDs.isEmpty()) {
			String usernamesCombined = "";
			for (int referencedInstructorToDeleteID : referencedInstructorsToDeleteIDs) {
				if (!usernamesCombined.equals(""))
					usernamesCombined += ", ";
				usernamesCombined += openDocument.getInstructorByID(referencedInstructorToDeleteID).getUsername();
			}
			
			String messageString = referencedInstructorsToDeleteIDs.size() == 1 ? "Instructor " : "Instructors ";
			messageString += usernamesCombined;
			messageString += referencedInstructorsToDeleteIDs.size() == 1 ? " is " : " are ";
			messageString += "scheduled already. Please unschedule, then try again.";
			com.google.gwt.user.client.Window.alert(messageString);
		}
		else {
			if (com.google.gwt.user.client.Window.confirm("Are you sure you want to remove this instructor?")) {
					grid.removeSelectedData();
			}
		}
	}
	
	/**
	 * returns whether this widget can be closed 
	 * @return always true
	 */
	public boolean canClose() {
		// If you want to keep the user from navigating away, return false here
		
		return true;
	}

	/**
	 * closes this view
	 */
	public void close() {
		this.clear();
	}
}

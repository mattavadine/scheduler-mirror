package scheduler.view.web.client.views.resources.locations;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class LocationsView extends VerticalPanel {
	public LocationsView(final GreetingServiceAsync service, final DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.setWidth("100%");
		this.setHeight("100%");
		
//		this.add(new HTML("<h2>Locations</h2>"));
		
		final ListGrid grid = new ListGrid();
		grid.setWidth("100%");
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		//grid.setCellHeight(22);
		grid.setDataSource(new LocationsDataSource(service, document, unsavedDocumentStrategy));
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);

		ListGridField scheduleableField = new ListGridField("isSchedulable", "Schedulable");
		ListGridField usernameField = new ListGridField("room", "Room");
		ListGridField firstNameField = new ListGridField("type", "Type");
		ListGridField lastNameField = new ListGridField("maxOccupancy", "Max Occupancy");
		ListGridField equipmentField = new ListGridField("equipment", "Equipment");

		grid.setFields(idField, scheduleableField, usernameField, firstNameField, lastNameField, equipmentField);
		
		this.add(grid);
		
		this.add(new Button("Add New Location", new ClickHandler() {
			public void onClick(ClickEvent event) {
            grid.startEditingNew();
			}
		}));

		this.add(new Button("Remove Selected Locations", new ClickHandler() {
			public void onClick(ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {  
                grid.removeData(rec);  
            }
			}
		}));
	}
}
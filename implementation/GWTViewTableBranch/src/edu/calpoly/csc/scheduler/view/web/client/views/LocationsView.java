package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.AddRemoveTable;
import edu.calpoly.csc.scheduler.view.web.client.table.CheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.IntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LocationsView extends ScrollPanel {
	private GreetingServiceAsync service;
	private final String scheduleName;
	int nextLocationID = 1;

	public LocationsView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Locations</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		final OsmTable<LocationGWT> table = new AddRemoveTable<LocationGWT>(new Factory<LocationGWT>() {
			public LocationGWT create() {
				return new LocationGWT(nextLocationID++, "", "", "LEC", 20, false);
			}
			public LocationGWT createHistoryFor(LocationGWT location) {
				return new LocationGWT(-location.getID(), location);
			}
		});

		table.addColumn(new StringColumn<LocationGWT>("Building", "6em",
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getBuilding(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setBuilding(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER));

		table.addColumn(new StringColumn<LocationGWT>("Room", "6em",
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getRoom(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setRoom(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER));
		
		table.addColumn(new SelectColumn<LocationGWT>("Type", "6em",
				new String[] { "LEC", "LAB" },
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getType(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setType(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER));

		table.addColumn(new IntColumn<LocationGWT>("Occupancy", "4em",
				new StaticGetter<LocationGWT, Integer>() {
					public Integer getValueForObject(LocationGWT object) { return object.getMaxOccupancy(); }
				}, new StaticSetter<LocationGWT, Integer>() {
					public void setValueInObject(LocationGWT object, Integer newValue) { object.setMaxOccupancy(newValue); }
				}));
		
		table.addColumn(new CheckboxColumn<LocationGWT>("ADA", "4em",
				new StaticGetter<LocationGWT, Boolean>() {
					public Boolean getValueForObject(LocationGWT object) { return object.isADACompliant(); }
				},
				new StaticSetter<LocationGWT, Boolean>() {
					public void setValueInObject(LocationGWT object, Boolean newValue) { object.setADACompliant(newValue); }
				}));
		
		vp.add(table);
		
		service.getLocations(new AsyncCallback<ArrayList<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<LocationGWT> result){
				assert(result != null);
				popup.hide();
				for (LocationGWT location : result)
					nextLocationID = Math.max(nextLocationID, location.getID() + 1);
				table.addRows(result);
			}
		});
		
	}
}

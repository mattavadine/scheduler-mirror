package edu.calpoly.csc.scheduler.view.web.client.views.resources.courses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ObjectChangedObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn.DeleteObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingMultiselectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.views.resources.courses.AssociationsCell.GetCoursesCallback;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;

public class CoursesTable extends SimplePanel {
	private static final String NAME_HEADER = "Course Name";
	private static final String NAME_WIDTH = null;
	
	private static final String CATALOG_NUM_HEADER = "Catalog Number";
	private static final String CATALOG_NUM_WIDTH = null;
	
	private static final String DEPARTMENT_HEADER = "Department";
	private static final String DEPARTMENT_WIDTH = null;
	
	private static final String WTU_HEADER = "WTU";
	private static final String WTU_WIDTH = "4em";
	
	private static final String HOURS_PER_WEEK_HEADER = "Hours Per Week";
	private static final String HOURS_PER_WEEK_WIDTH = "4em";
	
	private static final String SCU_HEADER = "SCU";
	private static final String SCU_WIDTH = "4em";
	
	private static final String NUM_SECTIONS_HEADER = "# of Sections";
	private static final String NUM_SECTIONS_WIDTH = "4em";
	
	private static final String TYPE_HEADER = "Course Type";
	private static final String TYPE_WIDTH = "4em";
	
	private static final String MAX_ENROLLMENT_HEADER = "Max Enrollment";
	private static final String MAX_ENROLLMENT_WIDTH = "4em";
	
	private static final String ASSOCIATIONS_HEADER = "Associations";
	private static final String ASSOCIATIONS_WIDTH = null;
	
	private static final String DAY_COMBINATIONS_HEADER = "Day Combinations";
	private static final String DAY_COMBINATIONS_WIDTH = null;
	
	public interface Strategy {
		void getAllCourses(AsyncCallback<List<CourseGWT>> callback);
		CourseGWT createCourse();
		void onCourseEdited(CourseGWT course);
		void onCourseDeleted(CourseGWT course);
	}
	
	final OsmTable<CourseGWT> table;
	final Strategy strategy;
	final ArrayList<CourseGWT> tableCourses = new ArrayList<CourseGWT>();
	
	public CoursesTable(Strategy strategy_) {
		this.strategy = strategy_;
		
		table = new OsmTable<CourseGWT>(
				new IFactory<CourseGWT>() {
					public CourseGWT create() {
						CourseGWT newCourse = strategy.createCourse();
						tableCourses.add(newCourse);
						return newCourse;
					}
				});
		
		table.setObjectChangedObserver(new ObjectChangedObserver<CourseGWT>() {
			public void objectChanged(final CourseGWT object) {
				strategy.onCourseEdited(object);
			}
		});

		table.addDeleteColumn(new DeleteObserver<CourseGWT>() {
			@Override
			public void afterDelete(CourseGWT object) {
				tableCourses.remove(object);
				strategy.onCourseDeleted(object);
			}
		});
		
		addFieldColumns();

		this.add(table);
	}

	@Override
	public void onLoad() {
		strategy.getAllCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<CourseGWT> courses){
				assert(tableCourses.isEmpty());
				for (CourseGWT course : courses)
					tableCourses.add(new CourseGWT(course));
				
				table.addRows(tableCourses);
			}
		});
	}
	
	void addFieldColumns() {
		table.addColumn(
				DEPARTMENT_HEADER,
				DEPARTMENT_WIDTH,
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getDept(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getDept(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setDept(newValue); }
						},
						null));
		
		table.addColumn(
				CATALOG_NUM_HEADER,
				CATALOG_NUM_WIDTH,
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setCatalogNum(newValue); }
						}, 
						new IStaticValidator<CourseGWT, String>() {
							@Override
							public IStaticValidator.ValidateResult validate(
									CourseGWT object, String newValue) {
								if (newValue.trim().equals(""))
									return new InputWarning(CATALOG_NUM_HEADER + " must be present.");
								return new InputValid();
							}
						}));

		table.addColumn(
				NAME_HEADER,
				NAME_WIDTH,
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getCourseName(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getCourseName(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setCourseName(newValue); }
						},
						null));
		
		table.addColumn(WTU_HEADER, WTU_WIDTH, true, null, new EditingStringColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getWtu(); }
				}, new IStaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setWtu(newValue); }
				}, 
				new IStaticValidator<CourseGWT, String>() {
					@Override
					public IStaticValidator.ValidateResult validate(
							CourseGWT object, String newValue) {
						int n;
						try { n = Integer.parseInt(newValue); }
						catch (NumberFormatException e) {
							return new InputWarning(WTU_HEADER + " must be an integer.");
						}
						
						if (n < 0)
							return new InputWarning(WTU_HEADER + " must not be negative.");
						
						return new InputValid();
					}
				}));
		
		table.addColumn(SCU_HEADER, SCU_WIDTH, true, null, new EditingStringColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getScu(); }
				}, new IStaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setScu(newValue); }
				}, 
				new IStaticValidator<CourseGWT, String>() {
					@Override
					public IStaticValidator.ValidateResult validate(
							CourseGWT object, String newValue) {
						int n;
						try { n = Integer.parseInt(newValue); }
						catch (NumberFormatException e) {
							return new InputWarning(SCU_HEADER + " must be an integer.");
						}
						
						if (n < 0)
							return new InputWarning(SCU_HEADER + " must not be negative.");
						
						return new InputValid();
					}
				}));
		
		table.addColumn(NUM_SECTIONS_HEADER, NUM_SECTIONS_WIDTH, true, null, new EditingStringColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getRawNumSections(); }
				}, new IStaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setNumSections(newValue); }
				}, 
				new IStaticValidator<CourseGWT, String>() {
					@Override
					public IStaticValidator.ValidateResult validate(
							CourseGWT object, String newValue) {
						int n;
						try { n = Integer.parseInt(newValue); }
						catch (NumberFormatException e) {
							return new InputWarning(NUM_SECTIONS_HEADER + " must be an integer.");
						}
						
						if (n < 1)
							return new InputWarning(NUM_SECTIONS_HEADER + " must be at least 1.");
						
						return new InputValid();
					}
				}));
		
		table.addColumn(
				TYPE_HEADER,
				TYPE_WIDTH,
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getType(); }
				}),
				new EditingSelectColumn<CourseGWT>(
						new String[] { "LEC", "LAB" },
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getType(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) {
								object.setType(newValue);
							}
						}));
		
		table.addColumn(MAX_ENROLLMENT_HEADER, MAX_ENROLLMENT_WIDTH, true, null, new EditingStringColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getMaxEnroll(); }
				}, new IStaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setMaxEnroll(newValue); }
				}, 
				new IStaticValidator<CourseGWT, String>() {
					@Override
					public IStaticValidator.ValidateResult validate(
							CourseGWT object, String newValue) {
						int n;
						try { n = Integer.parseInt(newValue); }
						catch (NumberFormatException e) {
							return new InputWarning(MAX_ENROLLMENT_HEADER + " must be an integer.");
						}
						
						if (n < 1)
							return new InputWarning(MAX_ENROLLMENT_HEADER + " must be at least 1.");
						
						return new InputValid();
					}
				}));
		
		table.addColumn(HOURS_PER_WEEK_HEADER, HOURS_PER_WEEK_WIDTH, true, null, new EditingStringColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getHalfHoursPerWeek(); }
				}, new IStaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setHalfHoursPerWeek(newValue); }
				}, 
				new IStaticValidator<CourseGWT, String>() {
					@Override
					public edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.ValidateResult validate(
							CourseGWT object, String newValue) {
						double d;
						try { d = Double.parseDouble(newValue); }
						catch (NumberFormatException e) {
							return new InputWarning(HOURS_PER_WEEK_HEADER + " must be a decimal number.");
						}
						
						// Must be a multiple of .5
						if (distanceFromNearestMultiple(d, .5) >= .001) // account for floating point imprecision
							return new InputWarning(HOURS_PER_WEEK_HEADER + " must be a multiple of .5.");
						
						int numHalfHours = (int)Math.round(d * 2);
						if (numHalfHours < 1)
							return new InputWarning(HOURS_PER_WEEK_HEADER + " must be at least .5.");
						
						return new InputValid();
					}
				}));
		
		table.addColumn(DAY_COMBINATIONS_HEADER, DAY_COMBINATIONS_WIDTH, true, null, new EditingMultiselectColumn<CourseGWT>(
				new String[] {
						"M", "Tu", "W", "Th", "F",
						"MW", "MF", "WF", "TuTh",
						"MWF",
						"TuWThF", "MWThF", "MTuThF", "MTuWTh"
				},
				new IStaticGetter<CourseGWT, Set<String>>() {
					public Set<String> getValueForObject(CourseGWT object) {
						Set<String> result = new HashSet<String>();
						for (DayCombinationGWT combo : object.getDays())
							result.add(combo.toString());
						return result;
					}
				},
				new IStaticSetter<CourseGWT, Set<String>>() {
					public void setValueInObject(CourseGWT object, Set<String> newCombos) {
						Set<DayCombinationGWT> set = new HashSet<DayCombinationGWT>();
						for (String newCombo : newCombos)
							set.add(DayCombinationGWT.fromString(newCombo));
						object.setDays(set);
					}
				}));
		
		table.addColumn(
				ASSOCIATIONS_HEADER,
				ASSOCIATIONS_WIDTH,
				true,
				null,
				new AssociationsColumn(new GetCoursesCallback() {
					public ArrayList<CourseGWT> getCourses() {
						return tableCourses;
					}
				}));
	}
	
	// If we wanted to know how far off 1.4 was from the nearest multiple of .5, we'd use this function
	private static double distanceFromNearestMultiple(double value, double multipleOf) {
		// How this function works:
		//     Math.abs((1.4 / .5 - Math.round(1.4 / .5)) * .5)
		//     Math.abs((2.8 - Math.round(2.8)) * .5)
		//     Math.abs((2.8 - 3.0) * .5)
		//     Math.abs(.2 * .5)
		//     Math.abs(.1)
		//     .1
		return Math.abs((value / multipleOf - Math.round(value / multipleOf)) * multipleOf);
	}
}
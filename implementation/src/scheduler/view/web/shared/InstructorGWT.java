package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class InstructorGWT implements Serializable, Identified {
	private static final long serialVersionUID = -4982539363690274674L;
	
	private Integer id;
	private String username;
	private String firstName, lastName;
	private String maxwtu;

	int[][] tPrefs;

	HashMap<Integer, Integer> coursePrefs;

	private boolean isSchedulable;

	public InstructorGWT(Integer id, String username, String firstName,
			String lastName, String maxwtu,
			int[][] tPrefs,
			HashMap<Integer, Integer> hashMap, boolean isSchedulable) {
		super();
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.maxwtu = maxwtu;
		this.tPrefs = tPrefs;
		this.coursePrefs = hashMap;
		this.isSchedulable = isSchedulable;
	}
	
	public InstructorGWT() {
		super();
	}

	public InstructorGWT(InstructorGWT that) {
		this(that.id, that.username, that.firstName, that.lastName,
				that.maxwtu, null, null, that.isSchedulable);
		
		int newTimePrefs[][] = new int[DayGWT.values().length][48];
		for (DayGWT day : DayGWT.values())
			for (int halfHour = 0; halfHour < 48; halfHour++)
				newTimePrefs[day.ordinal()][halfHour] = that.tPrefs[day.ordinal()][halfHour];
		tPrefs = newTimePrefs;
		
		HashMap<Integer, Integer> newCoursePrefs = new LinkedHashMap<Integer, Integer>(that.coursePrefs);
		for (Integer course : that.coursePrefs.keySet())
			newCoursePrefs.put(course, that.coursePrefs.get(course));
		coursePrefs = newCoursePrefs;
		
		verify();
	}

	public int[][] gettPrefs() {
		return tPrefs;
	}

	public void settPrefs(int[][] tPrefs) {
		this.tPrefs = tPrefs;
	}

	public void verify() {
		assert(username != null);
		assert(firstName != null);
		assert(lastName != null);
		assert(coursePrefs != null);
	}

	public HashMap<Integer, Integer> getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(HashMap<Integer, Integer> coursePrefs){
		this.coursePrefs = coursePrefs;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getName() {
		return firstName;
	}

	public void setName(String name) {
		this.firstName = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMaxWtu() {
		try { return Integer.parseInt(maxwtu); }
		catch (NumberFormatException e) { return 0; }
	}
	
	public String getRawMaxWtu() {
		return maxwtu;
	}

	public void setMaxWtu(String wtu) {
		this.maxwtu = wtu;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getID() {
		return id;
	}

	public void setID(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InstructorGWT))
			return false;
		InstructorGWT that = (InstructorGWT)obj;
		return this.id == that.id;
//		if (!(this.id == that.id &&
//				this.userID.equals(that.userID) &&
//				this.firstName.equals(that.firstName) &&
//				this.lastName.equals(that.lastName) &&
//				this.roomNumber.equals(that.roomNumber) &&
//				this.building.equals(that.building) &&
//				this.disabilities == that.disabilities &&
//				this.maxwtu == that.maxwtu &&
//				this.curwtu == that.curwtu &&
//				this.fairness == that.fairness &&
//				this.generosity == that.generosity))
//			return false;
//		// TODO: do we need to compare prefs and items taught?
//		return true;
	}

	public boolean isSchedulable() { return this.isSchedulable; }
	public void setIsSchedulable(boolean isSchedulable) { this.isSchedulable = isSchedulable; }

	public boolean attributesEqual(InstructorGWT that) {
		return username.equals(that.username) &&
				firstName.equals(that.firstName) &&
				lastName.equals(that.lastName) &&
				maxwtu.equals(that.maxwtu) &&
				isSchedulable == that.isSchedulable &&
				Arrays.deepEquals(tPrefs, that.tPrefs) &&
				coursePrefsEqual(coursePrefs, that.coursePrefs);
	}
	
	private static boolean coursePrefsEqual(HashMap<Integer, Integer> a, HashMap<Integer, Integer> b) {
		if (!a.keySet().equals(b.keySet()))
			return false;
		for (Integer courseID : a.keySet())
			if (!a.get(courseID).equals(b.get(courseID)))
				return false;
		return true;
	}
}

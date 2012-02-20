package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class InstructorGWT implements Serializable, Identified {
	private static final long serialVersionUID = -4982539363690274674L;
	
	private int id;
	private String username;
	private String firstName, lastName;
	private String maxwtu;

	HashMap<DayGWT, HashMap<Integer, Integer>> tPrefs;

	HashMap<Integer, Integer> coursePrefs;

	private boolean isSchedulable;

	public InstructorGWT(int id, String username, String firstName,
			String lastName, String maxwtu,
			HashMap<DayGWT, HashMap<Integer, Integer>> tPrefs,
			HashMap<Integer, Integer> hashMap) {
		super();
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.maxwtu = maxwtu;
		this.tPrefs = tPrefs;
		this.coursePrefs = hashMap;
	}
	
	public InstructorGWT() {
		super();
	}

	public InstructorGWT(InstructorGWT that) {
		this(that.id, that.username, that.firstName, that.lastName,
				that.maxwtu, null, null);
		
		HashMap<DayGWT, HashMap<Integer, Integer>> newTPrefs = new HashMap<DayGWT, HashMap<Integer,Integer>>(); 
		for (DayGWT day : that.tPrefs.keySet()) {
			Map<Integer, Integer> thatDayPrefs = that.tPrefs.get(day);
			HashMap<Integer, Integer> newDayPrefs = new HashMap<Integer, Integer>();
			
			for (Integer time : thatDayPrefs.keySet()) {
				Integer sourcePref = thatDayPrefs.get(time);
				newDayPrefs.put(time, new Integer(sourcePref));
			}
			
			newTPrefs.put(day, newDayPrefs);
		}
		tPrefs = newTPrefs;
			
		HashMap<Integer, Integer> newCoursePrefs = new LinkedHashMap<Integer, Integer>(that.coursePrefs);
		for (Integer course : that.coursePrefs.keySet())
			newCoursePrefs.put(course, that.coursePrefs.get(course));
		coursePrefs = newCoursePrefs;
		
		verify();
	}

	public HashMap<DayGWT, HashMap<Integer, Integer>> gettPrefs() {
		return tPrefs;
	}

	public void settPrefs(HashMap<DayGWT, HashMap<Integer, Integer>> tPrefs) {
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

	public void setID(int id) {
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
}
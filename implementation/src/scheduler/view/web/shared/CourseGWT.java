package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;


public class CourseGWT implements Serializable, Identified {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private Integer id;
	private boolean isSchedulable;
	private String courseName;
	private String catalogNum;
	private String dept;
	private String wtu, scu, numSections;
	private String type; //will be an object
	private String maxEnroll;
	private String halfHoursPerWeek;
	private Set<WeekGWT> dayPatterns;
	private Integer lectureID;
	private Boolean tetheredToLecture;
	private Set<String> usedEquipment;
	
	public CourseGWT() { }
	
	public CourseGWT(boolean isSchedulable, String courseName, String catalogNum, String dept,
			String wtu, String scu, String numSections, String type,
			String maxEnroll, Integer lectureID, String halfHoursPerWeek, Set<WeekGWT> dayCombinations,
			Integer id, Boolean tetheredToLecture, Set<String> equipment) {
		super();
		this.isSchedulable = isSchedulable;
		this.courseName = courseName;
		this.catalogNum = catalogNum;
		this.dept = dept;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnroll = maxEnroll;
		this.lectureID = lectureID;
		this.halfHoursPerWeek = halfHoursPerWeek;
		this.dayPatterns = dayCombinations;
		this.id = id;
		this.tetheredToLecture = tetheredToLecture;
		this.usedEquipment = equipment;
	}



	public CourseGWT(CourseGWT that) {
		this(that.isSchedulable, that.courseName, that.catalogNum, that.dept, that.wtu, that.scu,
				that.numSections, that.type, that.maxEnroll, that.lectureID, that.halfHoursPerWeek,
				that.dayPatterns, that.id, that.tetheredToLecture, that.usedEquipment);
	}

	public void verify() {
		assert(courseName != null);
		assert(dept != null);
		assert(type != null);
		assert(dayPatterns != null);
	}
	
	public void setDayPatterns(Set<WeekGWT> dayPatterns)
	{
		this.dayPatterns = dayPatterns;
	}
	
	public Set<WeekGWT> getDayPatterns()
	{
		return dayPatterns;
	}
   

	public String getHalfHoursPerWeek()
	{
	   return halfHoursPerWeek;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCatalogNum() {
		return catalogNum;
	}

	public void setCatalogNum(String catalogNum) {
		this.catalogNum = catalogNum;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getWtu() {
		return wtu;
	}

	public void setWtu(String wtu) {
		this.wtu = wtu;
	}

	public String getScu() {
		return scu;
	}

	public void setScu(String scu) {
		this.scu = scu;
	}

	public String getRawNumSections() {
		return numSections;
	}

	public Integer getNumSections() {
		try { return Integer.parseInt(numSections); }
		catch (NumberFormatException e) { return 0; }
	}

	public void setNumSections(String numSections) {
		this.numSections = numSections;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaxEnroll() {
		return maxEnroll;
	}

	public void setMaxEnroll(String maxEnroll) {
		this.maxEnroll = maxEnroll;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setHalfHoursPerWeek(String halfHoursPerWeek) {
		this.halfHoursPerWeek = halfHoursPerWeek;
	}
	

	public Integer getID() {
		return id;
	}

	public void setID(Integer id) {
		this.id = id;
	}

	public String toString() {
		return this.courseName + " " + this.catalogNum + " " + this.type;
	}
	
	@Override
	public int hashCode() {
		assert(false); // don't use this object in a hash! use its ID instead please.
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CourseGWT))
			return false;
		CourseGWT that = (CourseGWT)obj;
		return id == that.id;
//		assert(this.id.equals(that.id));
//		assert(this.scheduleID.equals(that.scheduleID));
//		return this.catalogNum.equals(that.catalogNum) &&
//				this.courseName.equals(that.courseName) &&
//				this.days.equals(that.days) &&
//				this.dept.equals(that.dept) &&
//				this.labCatalogNum.equals(that.labCatalogNum) &&
//				this.labDept.equals(that.labDept) &&
//				this.labId.equals(that.labId) &&
//				this.labName.equals(that.labName) &&
//				this.halfHoursPerWeek.equals(that.halfHoursPerWeek) &&
//				this.maxEnroll.equals(that.maxEnroll) &&
//				this.numSections.equals(that.numSections) &&
//				this.scu.equals(that.scu) &&
//				this.type.equals(that.type) &&
//				this.wtu.equals(that.wtu);
	}

	public boolean isSchedulable() {
		return isSchedulable;
	}

	public void setSchedulable(boolean isSchedulable) {
		this.isSchedulable = isSchedulable;
	}

	public Set<String> getUsedEquipment() {
		return usedEquipment;
	}

	public void setUsedEquipment(Set<String> usedEquipment) {
		this.usedEquipment = usedEquipment;
	}

	public int getLectureID() {
		return lectureID;
	}
	
	public void setLectureID(int lectureID) {
		this.lectureID = lectureID;
	}
	
	public Boolean getTetheredToLecture() {
		return tetheredToLecture;
	}

	public void setTetheredToLecture(Boolean tetheredToLecture) {
		this.tetheredToLecture = tetheredToLecture;
	}

	public void setLectureID(Integer lectureID) {
		this.lectureID = lectureID;
	}
	
	public boolean isValid() {
		try {
			Integer.parseInt(wtu);
			Integer.parseInt(scu);
			Integer.parseInt(numSections);
			Integer.parseInt(maxEnroll);
			Integer.parseInt(halfHoursPerWeek);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean attributesEqual(CourseGWT that) {
		return catalogNum.equals(that.catalogNum) &&
				courseName.equals(that.courseName) &&
				dayPatterns.equals(that.dayPatterns) &&
				dept.equals(that.dept) &&
				halfHoursPerWeek.equals(that.halfHoursPerWeek) &&
				lectureID == that.lectureID &&
				maxEnroll.equals(that.maxEnroll) &&
				numSections.equals(that.numSections) &&
				scu.equals(that.scu) &&
				tetheredToLecture == that.tetheredToLecture &&
				type.equals(that.type) &&
				usedEquipment.equals(that.usedEquipment);
	}
}

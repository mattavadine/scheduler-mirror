package scheduler.generate;

import java.io.PrintStream;
import java.io.Serializable;

import scheduler.db.*;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;

/**
 * Represents a single, scheduled item in the overall schedule. Contains 
 * information regarding:
 * <ul>
 *    <li>The instructor teaching</li>
 *    <li>The course he/she will teach</li>
 *    <li>Where the course will be taught</li>
 *    <li>When (time and day(s)) it'll be taught</li>
 *    <li>If applicable, an additional SI for the "lab" component</li>
 * </ul>
 *
 * Each ScheduleItem has a "value", which represents how valuable it is to the
 * instructor it represents. A higher value means equates to a higher 
 * desirability. A desire of 0 is considered impossible. In fact, there's a 
 * special constant used to represent an "impossible" SI.
 *
 * @author Eric Liebowitz
 * @version 12nov10
 */
public class ScheduleItem implements Serializable, Cloneable, 
                                     Comparable<ScheduleItem>
{
   public static final int serialVersionUID = 42;

   /** 
    * Represents the value a ScheduleItem would have it it contains values
    * (course, time, days) which the instructor it represents cannot do (has a
    * preference of 0 for)
    */
   public static final int IMPOSSIBLE = 0;

   /**
    * The instructor for this scheduled item
    */
   public Instructor i;
   /**
    * The course for this scheduled item
    */
   public Course c;
   /**
    * The location for this scheduled item
    */
   public Location l;
   /**
    * Course section (will someday be removed)
    */
   public int section;
   /**
    * The days in the week this item is scheduled
    */
   public Week days;
   /**
    * The time at which this scheduled item starts. Do not access this field directly. 
    * Use getters/setters, as this is no longer used for SI computations. 
    */
   public Time start;
   /**
    * The time at which this scheduled item ends. Do not access this field directly. 
    * Use getters/setters, as this is no longer used for SI computations. 
    */
   public Time end;
   /**
    * Time range representing when this ScheduleItem starts/ends. This is what 
    * is used in place of "start" and "end". However, when I added this new 
    * field, loads of other classes already depended on the public "start" and
    * "end" fields. So, I've left them there. But be kind: use this instead
    * for all your time range needs. Or, be even more kind and use the 
    * setters/getters. (In fact, you have to be, as it's private). 
    */
   private TimeRange tr;
   /**
    * Whether this SI is locked into the schedule or not
    */
   public boolean locked; 
   /**
    * The lab that goes with the class this SI represents, if any
    */
   public ScheduleItem lab;
   /**
    * How "valuable" this course/time combination is to the Instructor.
    */
   public int value;

   /**
    * Builds an empty Schedule Item. None of its fields will be initialized.
    */
   public ScheduleItem () { }

   /**
    * Creates a scheduled course, complete with instructor, location
    * section, days taught, and start/end time.
    *
    * @param i Instructor teaching the course
    * @param c Course being taught
    * @param l Location course will be taught
    * @param days Days of the week this course will be taught
    * @param s Time the course starts
    * @param e Time the course ends
    */
   public ScheduleItem (Instructor i,
                        Course c,
                        Location l, 
                        int section, 
                        Week days,
                        Time s,
                        Time e)
   {
      this.i = i;
      this.c = c;
      this.l = l;
      this.section = section;
      this.days = days;
      this.start = s;
      this.end = e;
      this.tr = new TimeRange(s, e);
      this.locked = false;
      this.lab = null;
   }

   /**
    * Sets the start and end time and days of this SI to the fields present in 
    * a given DaysAndTime object.
    *
    * @param dat DaysAndTime object where the start, end, and days fields will
    *            be found.
    */
   public void setDaysAndTime (DaysAndTime dat)
   {
      this.start = dat.getS();
      this.end   = dat.getE();
      this.tr = new TimeRange(this.start, this.end);
      this.days  = dat.getWeek ();
   }

   /** 
    * Returns the start time for this ScheduleItem. Can be null if you haven't
    * set the time yet.
    *
    * @return the start time for this ScheduleItem. Can be null.
    */
   public Time getStart ()
   {
      if (this.tr == null)
      {
         return null;
      }
      return this.tr.getS();
   }

   /** 
    * Returns the end time for this ScheduleItem. Can be null if you haven't 
    * set the time yet.
    *
    * @return the end time for this ScheduleItem. Can be null
    */
   public Time getEnd () 
   {
      return this.tr.getE();
   }

   /** 
    * Gets the TimeRange for this ScheduleItem. Can be null, if you haven't set
    * the times yet.
    *
    * @return the TimeRange for this SI. Can be null.
    */
   public TimeRange getTimeRange ()
   {
      return this.tr;
   }

   /** 
    * Gets the SI-lab components for this ScheduleItem. Can be null if you 
    * haven't set it yet, or if this SI isn't even supposed to have one
    *
    * @return the SI-lab component for this SI. Can be null.
    */
   public ScheduleItem getLab ()
   {
      return this.lab;
   }

   /** 
    * Sets the lab for this SI. 
    *
    * @param lab The SI you wish to apply to this SI's lab field.
    */
   public void setLab (ScheduleItem lab)
   {
      this.lab = lab;
   }

   /** 
    * Sets the instructor for this SI.
    *
    * @param i The Instructor you wish to apply to this SI.
    */
   public void setInstructor (Instructor i)
   {
      this.i = i;
   }

   /**
    * Gets the Week object which represents the days this SI will be taught. Can
    * be null if you haven't set it yet. 
    *
    * @return The Week object which represents the days this SI will be taught. 
    *         Can be null.
    */
   public Week getDays ()
   {
      return this.days;
   }

   /** 
    * Gets the instructor associated with this SI. Can be null if you haven't
    * set it yet. 
    *
    * @return the Instructor associated w/ this SI. Can be null.
    */
   public Instructor getInstructor ()
   {
      return this.i;
   }

   /** 
    * Gets the course associated w/ this SI. Can be null if you haven't set it
    * yet.
    *
    * @return the course associated w/ this SI. Can be null.
    */
   public Course getCourse ()
   {
      return this.c;
   }

   /** 
    * Sets the course for this SI, along with the "section" field (which'll be
    * whatever the "getSection" method returns for "c".
    *
    * @param c The course you wish to apply to this SI.
    */
   public void setCourse (Course c)
   {
      this.c = c;
      this.section = c.getSection();
   }

   /**
    * Used to determine whether this SI overlaps with another one. 
    *
    * @param the SI we'll check for overlapping-ness.
    *
    * @return (this.tr.overlaps(si.getTimeRange()) && this.days.overlaps(si.getDays()))
    */
   public boolean overlaps(ScheduleItem si)
   {
      return (this.tr.overlaps(si.getTimeRange()) &&
              this.days.overlaps(si.getDays()));
   }

   /**
    * Compares this to another ScheduleItem according to the double returned
    * by their "getValue" method. The return of this method is simply the return
    * of "Double.compare(double, double)".
    *
    * @param si The other ScheduleItem whose value is to be compare with this
    *           one's.
    *
    * @return Double.compare(this.getValue(), si.getValue())
    */
   public int compareTo (ScheduleItem si)
   {
      return Double.compare(this.getValue(), si.getValue());
   }

   /**
    * Returns how "valuable" this ScheduleItem is, where "value" is determined
    * by how closely the fields of this object adhere to the most desirable
    * preferences of the instructor it represents. This method will take the
    * following fields into account, so long as each of them is valid (i.e. not
    * null. If they <i>are</i> null, they just won't be considered):
    * <ul>
    *    <li>Start and end time</li>
    *    <li>Days to be taught</li>
    *    <li>Course to teach</li>
    * </ul>
    *
    * The fields applied to the "lab" field are also considered, but only if
    * the lab is actually defined.
    *
    * @return the sum of all the instructors preferences for this object's 
    *         fields. Thus, a higher number means a higher value.
    */
   public double getValue ()
   {
      double lab = 0, course = 0, time = 0;

      if (this.hasLab())
      {
         lab = this.lab.getValue();
      }
      /*
       * You can call "getValue" on an SI regarldess of whether all its fields
       * are ready. If they aren't, they just won't be considered.
       */
      if (this.i != null)
      {
         if (this.c != null)
         {
            course = this.i.getPreference(this.c);
         }
         if (this.days != null && this.tr != null)
         {
            time = i.getAvgPrefForTimeRange(this.days, 
                                            this.tr.getS(), 
                                            this.tr.getE());
         }
      }

      /*
       * If any one part of the value was 0, its impossible for the instructor
       * should teach this SI, so our value should reflect that with the 
       * impossible 0 value.
       */
      if ((this.hasLab() && (int) lab == 0) || /* Lab matters if we have one */
         ((int)course == 0)                 ||
         ((int)time  == 0))
      {
         return IMPOSSIBLE;
      }

      return lab + course + time;
   }

   /**
    * Compares to ScheduleItem's. 
    *
    * @param s The object to compare with "this"
    *
    * @return true if all the fields in "s" are equal to those in "this". False
    *         otherwise. Note that the "locked" value is not incorporated in
    *         this check.
    */
   public boolean equals (ScheduleItem s)
   {
      return (this.i.equals(s.i)          &&
              this.c.equals(s.c)          &&
              this.l.equals(s.l)          &&
              this.section == s.section   &&
              this.days.equals(s.days)    &&
              this.start.equals(s.start)  &&
              this.end.equals(s.end)      &&
              this.lab.equals(s.lab));
   }

   public boolean hasLab () { return this.lab != null; }

   

   /**
    * Displays all this object's fields in a visually easy-to-read form.
    */
   public String toString ()
   {
      return (this.c + " - Section: " + this.section + " - " + 
              this.c.getCourseType() + "\n" + 
              "Instructor:\t" + this.i + "\n" + 
              "In:\t\t" + this.l + "\n" + 
              "On:\t\t" + this.days + "\n" + 
              "Starts:\t\t" + this.start + "\n" + 
              "Ends:\t\t" + this.end + "\n" + 
              "Locked:\t\t" + this.locked + "\n" +
              "Value :\t\t" + this.getValue() + "\n");
   }

   public ScheduleItem clone()
   {
      ScheduleItem si = null;
      try
      {
         si = (ScheduleItem)super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         e.printStackTrace();
      }

      return si;
   }

   /**
    * Dumps the data of this object to text as easy-to-parse Perl code. In 
    * particular, the following pieces of data are dumped into a hash ref to
    * be eval'd by Perl:
    * <ul>
    *    <li>id => this.c_this.sectio_this.c.getCourseType()</li>
    *    <li>course => this.c</li>
    *    <li>instructor => this.i.getId()</li>
    *    <li>location => this.l</li>
    *    <li>days => this.days</li>
    *    <li>start => this.start</li>
    *    <li>end => this.end</li>
    *    <li>value => this.value</li>
    * </ul>
    *
    * @param ps PrintStream to dump text to
    */
   public void dumpAsPerlText (PrintStream ps)
   {
      ps.println
      (
         "id => \"" + this.c + "_" + this.section + "_"   + 
            this.c.getCourseType() + "\",\n" +
         "course => \"" + this.c + "\",\n"                +
         "instructor => \""   + this.i.getId() + "\",\n"  +
         "location => \""     + this.l + "\",\n"          +
         "days => \""         + this.days + "\",\n"       +
         "s => \""            + this.start + "\",\n"      +
         "e => \""            + this.end   + "\","        + 
         "value => "          + this.getValue() + ","
      );
   }
}

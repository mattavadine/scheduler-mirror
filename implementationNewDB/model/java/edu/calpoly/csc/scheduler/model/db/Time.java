package edu.calpoly.csc.scheduler.model.db;

import java.lang.*;
import java.text.*;
import java.io.Serializable;

/**
 * A time object consisting of a minute and an hour.
 * 
 * @author Jan Lorenz Soliman
 * 
 **/

public class Time implements Comparable<Time>, Serializable
{
   /** Serial Version UID */
   public static final long serialVersionUID = 42;

   /**
    * A list of all the half-hour time slots in a day.
    */
   public static final Time[] ALL_TIMES_IN_DAY = new Time[] 
   {
      new Time(0, 0),
      new Time(0, 30),
      new Time(1, 0),
      new Time(1, 30),
      new Time(2, 0),
      new Time(2, 30),
      new Time(3, 0),
      new Time(3, 30),
      new Time(4, 0),
      new Time(4, 30),
      new Time(5, 0),
      new Time(5, 30),
      new Time(6, 0),
      new Time(6, 30),
      new Time(7, 0),
      new Time(7, 30),
      new Time(8, 0),
      new Time(8, 30),
      new Time(9, 0),
      new Time(9, 30),
      new Time(10, 0),
      new Time(10, 30),
      new Time(11, 0),
      new Time(11, 30),
      new Time(12, 0),
      new Time(12, 30),
      new Time(13, 0),
      new Time(13, 30),
      new Time(14, 0),
      new Time(14, 30),
      new Time(15, 0),
      new Time(15, 30),
      new Time(16, 0),
      new Time(16, 30),
      new Time(17, 0),
      new Time(17, 30),
      new Time(18, 0),
      new Time(18, 30),
      new Time(19, 0),
      new Time(19, 30),
      new Time(20, 0),
      new Time(20, 30),
      new Time(21, 0),
      new Time(21, 30),
      new Time(22, 0),
      new Time(22, 30),
      new Time(23, 0),
      new Time(23, 30),
   };
   
   /**
    * A constructor for the Time object
    * 
    **/
   public Time (int hour, int minute)
   {
      if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60)
      {
         this.hour = hour;
         this.minute = minute;
      }
      else
      {
         throw new InvalidInputException();
      }
   }

   /**
    * A constructor that creates a Time from a String
    **/
   public Time (String str)
   {
      str = str.replaceAll(" ", "");
      if (str.length() == 4 && str.charAt(1) == ':')
      {
         hour = Integer.parseInt(str.substring(0, 1));
         minute = Integer.parseInt(str.substring(2, 4));
      }
      else if (str.length() == 5 && str.charAt(2) == ':')
      {
         hour = Integer.parseInt(str.substring(0, 2));
         minute = Integer.parseInt(str.substring(3, 5));
      }
      else if (str.length() == 1 || str.length() == 2)
      {
         hour = Integer.parseInt(str);
         minute = 0;
      }
      else
      {
         throw new InvalidInputException();
      }
      if (!(hour >= 0 && hour < 24 && minute >= 0 && minute < 60))
      {
         throw new InvalidInputException();
      }
   }

   /**
    * A constructor that creates a Time from a String and a boolean
    **/
   public Time (String str, boolean isAM)
   {
      str = str.replaceAll(" ", "");
      if (str.length() == 4 && str.charAt(1) == ':')
      {
         hour = Integer.parseInt(str.substring(0, 1));
         minute = Integer.parseInt(str.substring(2, 4));
      }
      else if (str.length() == 5 && str.charAt(2) == ':')
      {
         hour = Integer.parseInt(str.substring(0, 2));
         minute = Integer.parseInt(str.substring(3, 5));
      }
      else if (str.length() == 1 || str.length() == 2)
      {
         hour = Integer.parseInt(str);
         minute = 0;
      }
      else
      {
         throw new InvalidInputException();
      }
      if (!(hour >= 0 && hour < 24 && minute >= 0 && minute < 60))
      {
         throw new InvalidInputException();
      }
      if (!isAM)
      {
         hour = hour + 12;
      }
   }

   /**
    * Creates a new Time which is a copy of a given Time
    * 
    * @param t
    *           The time to copy
    */
   public Time (Time t)
   {
      this.minute = t.getMinute();
      this.hour = t.getHour();
   }

   /** Integer representing hour */
   protected int hour;

   /** Integer representing minute */
   protected int minute;

   /** Returns the hour */
   public int getHour ()
   {
      return hour;
   }

   /**
    * Gets the minutes in a specific time
    * 
    * <pre>
    *  pre:
    * 
    *  post:
    *         //
    *         // The minute field is an integer greater than 0
    *         // and less than 60
    *         //
    *         (minute >= 0 && minute < 60)
    * </pre>
    * 
    * @return The minute integer to set.
    **/
   public int getMinute ()
   {
      return minute;
   }

   /**
    * Sets the minutes field
    * 
    * <pre>
    *  pre:   //
    *         // The minute field is an integer greater than 0
    *         // and less than 60
    *         //
    *         (minute >= 0 && minute < 60)
    * 
    *  post:
    *         //
    *         // The minute field is equal to the parameter 
    *         //
    *         (this.minute == minute)
    * </pre>
    * 
    * @param minute
    *           The minute integer to set.
    * @return true if the value was set, false otherwise.
    **/
   public boolean setMinute (int minute)
   {
      if (minute >= 0 && minute < 60)
      {
         this.minute = minute;
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Sets the hour field
    * 
    * <pre>
    *  pre:   //
    *         // The minute field is an integer greater than 0
    *         // and less than 60
    *         //
    *         (hour >= 0 && hour < 24)
    *  post:
    *         //
    *         // The hour field is equal to the parameter 
    *         //
    *         (this.hour == hour)
    * </pre>
    * 
    * @param hour
    *           The hour integer to set.
    * @return true if the value was set, false otherwise.
    **/
   public boolean setHour (int hour)
   {
      if (hour >= 0 && hour < 24)
      {
         this.hour = hour;
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Tests if the time is equal
    * 
    * @param t
    *           A time object to compare to.
    * 
    **/
   public boolean equals (Object t)
   {
      Time time = (Time) t;
      if (this.hour == time.getHour())
      {
         return (this.minute == time.getMinute());
      }
      else
      {
         return false;
      }
   }

   /**
    * Needed for hashes of times.
    * 
    * Written by: Eric Liebowitz
    * 
    * @return the sum of the hour and min
    */
   public int hashCode ()
   {
      return this.hour + this.minute;
   }

   /**
    * For comparing times
    * 
    * @param t
    *           The compare to compare to this
    * 
    *           Written by: Eric Liebowitz
    */
   public int compareTo (Time t)
   {
      if (this.hour > t.getHour())
      {
         return 1;
      }
      else if (this.hour < t.getHour())
      {
         return -1;
      }
      else
      {
         if (this.minute > t.getMinute())
         {
            return 1;
         }
         else if (this.minute < t.getMinute())
         {
            return -1;
         }
         else
         {
            return 0;
         }
      }
   }

   /**
    * Checks if a time interval is fits into another.
    * 
    * @param start1
    *           start of the would be sub-interval
    * @param end1
    *           end of the would be sub-interval
    * @param start2
    *           start of the interval
    * @param end2
    *           end of the interval
    * @return true if [start1,end1] is within [start2,end2]
    * 
    * @author Jason Mak
    */
   public static boolean isWithin (Time start1, Time end1, Time start2,
      Time end2)
   {
      int compareStart = start1.compareTo(start2);
      int compareEnd = end1.compareTo(end2);

      if (compareStart >= 0 && compareEnd <= 0)
      {
         return true;
      }
      else if (start2.compareTo(end2) > 0)
      {
         int compare1 = start1.compareTo(end1);
         int compareBigStart1 = start1.compareTo(start2);
         int compareBigStart2 = end1.compareTo(start2);
         if (compareBigStart1 >= 0 && compareBigStart2 >= 0 && compare1 <= 0)
         {
            return true;
         }
         int compareBigEnd1 = start1.compareTo(end2);
         int compareBigEnd2 = end1.compareTo(end2);
         if (compareBigEnd1 <= 0 && compareBigEnd2 <= 0 && compare1 <= 0)
         {
            return true;
         }

      }

      return false;
   }

   /**
    * return the time in this format "HH:MM"
    * 
    * @return return the time in string format
    * @author Sasiluk Ruangrongsorakai (SRUANGRO)
    */
   public String toString ()
   {
      return (new DecimalFormat("00")).format(this.hour) + ":"
         + (new DecimalFormat("00")).format(this.minute);
   }

   public String standardString ()
   {
      String tempHour = "";
      String noon = "AM";
      if (hour >= 12)
      {
         if (hour - 12 == 0)
         {
            tempHour = 12 + "";
         }
         else
         {
            tempHour = (hour - 12) + "";
         }
         noon = "PM";
      }
      else
      {
         tempHour = hour + "";
      }

      return tempHour + ":" + (new DecimalFormat("00")).format(this.minute)
         + " " + noon;
   }

   /**
    * Adds a half-hour to a given time. Does not wrap around after 23:30. If you
    * try to do so, nothing will happen.
    */
   public boolean addHalf ()
   {
      boolean r = false;

      if (this.compareTo(new Time(23, 30)) == -1)
      {
         /* Hours goes up if minutes > 29 */
         this.hour += ((this.minute > 29) ? 1 : 0);
         /* Wrap minutes around 60 during addition */
         this.minute = ((this.minute + 30) % 60);

         r = true;
      }
      return r;
   }

   /**
    * Adds a given number of half hours to this Time
    * 
    * @param n
    *           Number of half hours to increase Time by
    */
   public void addHalves (int n)
   {
      for (int i = 0; i < n; i++)
      {
         this.addHalf();
      }
   }

   public int getHalfHoursBetween (Time t)
   {
      Time s = this, e = t;

      if (s.compareTo(e) == 1)
      {
         s = e;
         e = this;
      }

      int halfHours = ((e.getHour() - s.getHour()) * 2);
      if (e.getMinute() > s.getMinute())
      {
         halfHours++;
      }
      if (e.getMinute() < s.getMinute())
      {
         halfHours--;
      }

      return halfHours;
   }
}
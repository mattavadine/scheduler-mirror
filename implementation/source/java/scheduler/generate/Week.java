package scheduler.generate;

import java.util.Collection;
import java.util.Vector;
import java.io.Serializable;

/**
 * Represents a selection of days of the week. No duplicate days are allowed.
 * (No weeks w/ more than 7 days). 
 *
 * @author Eric Liebowitz
 * @version 13apr10
 */
public class Week extends Vector<Integer> implements Serializable
{
   /**
    * For serializing
    */
   public static final int serialVersionUID = 42;

   /* Represent days of the week */

   /** Sunday */
   public static final int SUN = 0;
   /** Monday */
   public static final int MON = 1;
   /** Tuesday */
   public static final int TUE = 2;
   /** Wednesday */
   public static final int WED = 3;
   /** Thursday */
   public static final int THU = 4;
   /** Friday */
   public static final int FRI = 5;
   /** Saturday */
   public static final int SAT = 6;

   /** A 5-day week (Mon-Fri) */
   public static final Week fiveDayWeek = 
      new Week (new int[]{MON, TUE, WED, THU, FRI});

   /**
    * Creates a Week w/ no days in it
    */
   public Week ()
   {
      super ();
   }

   /**
    * Creates a week with the specified days in it. Duplicates are not allowed 
    * and will be ignored. Ints which do not represent valid days will raise a
    * NotADayException. Asking for a Week w/ more then 7 days will raise an
    * EightDayWeekException.
    *
    * @param days Array of ints representing what days should be in this week.
    */
   public Week (int[] days)
   {
      super();
      for (Integer i: days)
      {
         this.add(i);
      }
   }

   /**
    * Creates a week with the specified days in it. Duplicates are not allowed 
    * and will be ignored. Ints which do not represent valid days will raise a
    * NotADayException. Asking for a Week w/ more then 7 days will raise an
    * EightDayWeekException.
    *
    * @param days Collection of ints representing what days should be in this 
    *             week.
    */
   public Week (Collection<Integer> days)
   {
      super();
      for (Integer i: days)
      {
         this.add(i);
      }
   }

   /**
    * Adds a day to this week.
    *
    * @param d Day to add
    *
    * @return True if the day was added. False otherwise. 
    *
    * @throws NotADayException if "d" is not one of the day constants defined 
    *         in this class.
    * @throws EightDayWeekException if this week is already full (7 days)
    */
   public boolean add (int d) throws NotADayException, EightDayWeekException
   {
      /*
       * Don't make weird days 
       */
      if (!isValidDay(d))
      {
         throw new NotADayException ();
      }
      /*
       * Our week is only 7 days...we can't add beyond that
       */
      if (this.size() == 7)
      {
         throw new EightDayWeekException ();
      }
      /*
       * Don't add duplicate days
       */
      if (this.contains(d))
      {
         return false;
      }
      return super.add(d);
   }

   /**
    * Removes a day from this Week, if it exists. 
    *
    * @param d Day to remove
    * 
    * @return True if the day existed and was thus removed. Flase otherwise. 
    *
    * @throws NotADayException if "d" is not one of the day constants defined
    *         in this class.
    */
   public boolean del (int d) throws NotADayException
   {
      if (!isValidDay(d))
      {
         throw new NotADayException ();
      }
      return this.getWeek().removeElement(d);
   }

   /**
    * Blindly removes the last element of this Week. If none exist, nothing is
    * done.
    *
    * @return True if an element was removed. False otherwise. 
    */
   public boolean chop ()
   {
      boolean r = false;
      Vector<Integer> w = this.getWeek();
      if (w.size() > 0)
      {
         /*
          * "size" isn't 0-indexed, so I have to adjust
          */
         w.remove(w.size() - 1);
         r = true;
      }
      return r;
   }

   /**
    * Returns a String representing this object
    */
   public String toString ()
   {
      String r = new String();
      for (Integer i: this)
      {
         switch (i)
         {
            case (SUN):
               r += "Sun ";
               break;
            case (MON):
               r += "Mon ";
               break;
            case (TUE):
               r += "Tue ";
               break;
            case (WED):
               r += "Wed ";
               break;
            case (THU):
               r += "Thu ";
               break;
            case (FRI):
               r += "Fri ";
               break;
            case (SAT):
               r += "Sat ";
               break;
            default:
               System.err.println ("Invalid day in week?");
         }
      }
      return r;
   }

   /**
    * Returns a list representation of this Week's days, in order from 
    * Sunday to Saturday.
    *
    * @return a list (Vector) of Integers represeting that days in this Week
    */
   public Vector<Integer> getWeek ()
   {
      return this;
   }

   /**
    * Determines whether a given day is in this Week.
    *
    * @param d Day in question
    *
    * @return whether "d" is in this Week
    */
   public boolean containsDay (int d) throws NotADayException
   {
      boolean contains = false;

      if (!isValidDay(d))
      {
         throw new NotADayException();
      }
      
      for (int ourDay: this)
      {
         contains |= (ourDay == d);
      }
      return contains;
   }

   /**
    * Determines whether a given day is valid. That is, if it is numerically 
    * greater-than-or-equal-to SUN and numerically less-than-or-equal-to SAT.
    *
    * @param d The day in question
    *
    * @return (d >= SUN && d <= SAT)
    */
   public static boolean isValidDay (int d)
   {
      return (d >= SUN && d <= SAT);
   }

   /**
    * Determines whetehr a given list of days contains all-valid days, where
    * "valid" is determined by the "isValidDay" method.
    *
    * @param days The list of days to check
    *
    * @return True if all the days are a valid day. False otherwise. 
    */
   public static boolean hasValidDays (Collection<Integer> days)
   {
      boolean r = true;
      for (int d: days)
      {
         r &= isValidDay (d);
      }
      return r;
   }

   /** 
    * Determines whether this Week shares any days with a given one.
    *
    * @param w The week to check for overlap
    *
    * @return true if this Week contains any of the days in "w". False 
    *         othwerise.
    */
   public boolean overlaps (Week w)
   {
      boolean r = false;
      
      for (int d: w.getWeek())
      {
         r |= this.containsDay(d);
      }

      return r;
   }

   /**
    * Tells you if two Weeks are equal (have the same days).
    * 
    * @param w The Week to test for equality.
    *
    * @return True if two weeks have the same days. False otherwise. 
    */
   public boolean equals (Week w)
   {
      return this.getWeek().equals(w.getWeek());
   }

   /**
    * Simple hash code comprised on the values of each day in this Week. It
    * is guaranteed that a Week which returns true from "equals" will have the
    * same hash code.
    *
    * @return the hash code on the return from this object's toString
    *
    */
   public int hashCode()
   {
      return this.toString().hashCode();
   }
}

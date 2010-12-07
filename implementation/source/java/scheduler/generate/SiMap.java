package scheduler.generate;

import java.util.TreeMap;
import java.util.Vector;

/**
 * A sorted map of ScheduleItems which'll make it easy to find the SI which is
 * most valuable to a particular instructor. This class was written to provide
 * a few convenience methods to make the sorting process easier/more 
 * modularized.
 *
 * You'll notice that this has "ScheduleItem" keys and the odd "Void" value for
 * each of said keys. This class isn't concerned with the keys...only with 
 * sorting those ScheduleItems, so it doesn't really matter what value I use. 
 * Yes, this is a bit wasteful, but it seemed the easiest way to get the SI's
 * sorted. Furthermore, should I ever need to supply more information to this
 * object, I can use the values if need be. 
 *
 * @author Eric Liebowitz
 * @version 12nov10
 */
public class SiMap extends TreeMap<ScheduleItem, Void>
{
   /**
    * Creates a new sorted mapping and adds each ScheduleItem in the list 
    * provided as keys in the map. 
    *
    * @param sis List of all ScheduleItems to add to the map as keys and sort
    */
   public SiMap (Vector<ScheduleItem> sis)
   {
      super();
      for (ScheduleItem si: sis)
      {
         if (!this.put(si))
         {
            System.err.println ("Couldn't add \n" + si + "\nb/c it has a pref " +
               "of 0");
         }
      }
   }

   /**
    * Puts a provided ScheduleItem in the map, so long as its "getValue" method
    * does not return ScheduleItem.IMPOSSIBLE, as that means that the SI is 
    * not able to be taught by its instructor
    *
    * @param si The ScheduleItem to add
    *
    * @return true if "si" was added. False if its value is 
    *         ScheduleItem.IMPOSSIBLE.
    */
   public boolean put (ScheduleItem si)
   {
      //System.err.println ("Putting into map:");
      //System.err.println (si);
      if (si.getValue() == ScheduleItem.IMPOSSIBLE)
      {
         return false;
      }
      super.put(si, null);
      return true;
   }
}

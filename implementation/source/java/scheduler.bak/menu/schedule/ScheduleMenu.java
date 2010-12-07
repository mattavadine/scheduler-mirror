package scheduler.menu.schedule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import scheduler.*;
import scheduler.db.admin.admin_ui.*;
import scheduler.menu.schedule.allInOne.*;
import scheduler.menu.schedule.lockItems.*;

/**
 * Represents the GUI for the Scheudle drop-down in the topl-evel menu bar. This
 * menu allows the user to interact with the data particular to the current
 * Schedule. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class ScheduleMenu extends JMenu
{
   /**
    * <pre>
    * Creates the Schedule menu with the following items within:
    *
    *  - "Courses"
    *  - "Instructors"
    *  - "Locations"
    *  - "Preferences"
    *  - "Schedule"
    *  - "TBAs"
    *  - "AllInOne"
    * </pre>
    */
   public ScheduleMenu ()
   {
      super ("Schedule");

      addCourse();
      addInstructor();
      addLocation();
      addPreference();
      this.add(new JSeparator());
      
      addSchedule();
      addLockItems();
      addChangeHours();
      addTBAs();
      this.add(new JSeparator());

      addAllInOne();
   }

   /**
    * Adds the "Courses" menu option to the menu. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addCourse ()/*==>*/
   {
      this.add(new JMenuItem("Courses")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new LocalCourseView().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Instructors" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addInstructor ()/*==>*/
   {
      this.add(new JMenuItem("Instructors")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new LocalInstructorView().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Locations" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addLocation ()/*==>*/
   {
      this.add(new JMenuItem("Locations")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new LocalLocationView().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Preferences" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addPreference ()/*==>*/
   {
      this.add(new JMenuItem("Preferences")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new PreferencesUI().show(150,150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Schedule" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addSchedule ()/*==>*/
   {
      
      this.add(new JMenuItem("Schedule")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new ScheduleMenuItem().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "ChangeHours" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addChangeHours()/*==>*/
   {
      
      this.add(new JMenuItem("Change Hours")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new ChangeHours().show(150, 150);
            }
         }
      );
   }

   /**
    * Added the "Lock Items" menu option. Opens a windows at 150x150.
    *
    * Hooked = No.
    */
   private void addLockItems ()/*==>*/
   {
      final LockItems li = new LockItems();
      this.add(new JMenuItem("Lock Items")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               li.show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "TBA's" menu option. Opens a window at 150x150.
    * 
    * Hooked = Yes.
    */
   private void addTBAs ()/*==>*/
   {
      this.add(new JMenuItem("TBA's")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               if (Scheduler.schedule.getTBAs().size() > 0)
               {
                  new TBA().show(150, 150);
               }
               else
               {
                  JOptionPane.showMessageDialog(null, 
                                                "No courses are TBA right now",
                                                "Note",
                                                JOptionPane.INFORMATION_MESSAGE);
               }
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "AllInOne" menu option. Opens a window at 150x150. To clarify, 
    * this window is where you'll probably go to generate schedules. All 
    * schedule-specific information is displayed in the window this spawns. 
    *
    * Hooked = Yes. 
    */
   private void addAllInOne ()/*==>*/
   {
      /*
       * Make a single object so we don't go adding lots of the same observer.
       * (See AllInOne's constructor so see where observers are added for 
       * a better description).
       */
      final AllInOne aio = new AllInOne();
      this.add(new JMenuItem("All-in-One")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               aio.show(150, 150);
            }
         }
      );
   }/*<==*/
}

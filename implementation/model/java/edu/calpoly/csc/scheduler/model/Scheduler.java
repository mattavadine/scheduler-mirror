package edu.calpoly.csc.scheduler.model;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 * The top-level model. Views will be passed this object when they are 
 * instantiated. Any view interaction w/ underlying data will be done via
 * this class. Thus, the getters/setters here will determine how the view and
 * the model can interact. 
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class Scheduler implements Serializable
{
   public static final int serialVersionUID = 42;
   
   /**
    * Our course database
    */
   public CourseDB cdb;
   /**
    * Our instrucor database
    */
   public InstructorDB idb;
   /**
    * Our location database
    */
   public LocationDB ldb;
   
   /**
    * Our preference database
    */
   public Object pdb;
   
   public Scheduler (String url)
   {
      
   }

   /**
    * Returns the course db of this model
    * 
    * @return the course db of this model
    */
   public CourseDB getCourseDB ()
   {
      return cdb;
   }

   /**
    * Sets the course db
    *
    * @param cdb the course db to set our cdb to
    */
   public void setCourseDB (CourseDB cdb)
   {
      this.cdb = cdb;
   }

   /**
    * Returns the instructor db of this model
    * 
    * @return the instructor db of this model
    */
   public InstructorDB getInstructorDB ()
   {
      return idb;
   }

   /**
    * Sets the instructor db to the given parameter.
    *
    * @param idb instructor db to set our idb to
    */
   public void setInstructorDB (InstructorDB idb)
   {
      this.idb = idb;
   }

   /**
    * Returns the location db of this model
    * 
    * @return the location db of this model
    */
   public LocationDB getLocationDB ()
   {
      return ldb;
   }

   /**
    * Sets the location db to the given parameter
    *
    * @param ldb location db to set our ldb to
    */
   public void setLocationDB (LocationDB ldb)
   {
      this.ldb = ldb;
   }

   /**
    * Returns the preference db of this model
    * 
    * @return the preference db of this model
    */
   public Object getPreferenceDB ()
   {
      return pdb;
   }

   /**
    * Sets the preference db to the given parameter
    *
    * @param pdb preference db to set our pdb to
    */
   public void setPdb (Object pdb)
   {
      this.pdb = pdb;
   }
}

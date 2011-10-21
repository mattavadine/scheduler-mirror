package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.Course.CourseType;

public class CourseDB implements DatabaseAPI<Course>
{
   private ArrayList<Course> data;
   private SQLDB             sqldb;

   public CourseDB()
   {
      initDB();
   }

   @Override
   public ArrayList<Course> getData()
   {
      return data;
   }

   private void initDB()
   {
      data = new ArrayList<Course>();
      sqldb = new SQLDB();
      pullData();
   }

   @Override
   public void pullData()
   {
      ResultSet rs = sqldb.getSQLCourses();
      try
      {
         while (rs.next())
         {
            // Retrieve by column name
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int catalogNum = rs.getInt("catalognum");
            String dept = rs.getString("dept");
            int wtus = rs.getInt("wtus");
            int scus = rs.getInt("scus");
            int numOfSections = rs.getInt("numofsections");
            String courseType = rs.getString("courseType");
            int length = rs.getInt("length");
            int enrollment = rs.getInt("enrollment");
            int labId = rs.getInt("labPairing");
            // Put items into Course object and add to data
            Course toAdd = new Course();
            toAdd.setId(id);
            toAdd.setName(name);
            toAdd.setCatalogNum(catalogNum);
            toAdd.setDept(dept);
            toAdd.setWtu(wtus);
            toAdd.setScu(scus);
            toAdd.setNumOfSections(numOfSections);
            toAdd.setType(courseType);
            toAdd.setLength(length);
            toAdd.setEnrollment(enrollment);
            Course lab = null;
            // TODO: Check what value null ints are stored as and change this
            if (labId > -1)
            {
               lab = new Course();
               lab.setId(labId);
               lab.setType(CourseType.LAB);
            }
            toAdd.setLab(lab);
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      linkLabs();
   }

   /**
    * Links all of the courses that have labs with their labs
    */
   private void linkLabs()
   {
      for (Course course : data)
      {
         // Course has a lab
         if (course.getLab() != null)
         {
            //Find lab data and put it into the object
            course.setLab(data.get(data.indexOf(course.getLab())));
         }
      }
   }

   @Override
   public void addData(Course data)
   {
      // Create insert strings
      String insertString = "insert into courses ("
            + "name, catalognum, dept, wtu, scu, numofsections, coursetype, "
            + "length, enrollment, labid)"
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      int labid = -1;
      // TODO: Clean this next part up. It's gross.
      // Check if it has a lab
      try
      {
         if (data.getLab() != null)
         {
            // Insert lab into DB and get id
            stmt.setString(1, data.getName());
            stmt.setInt(2, data.getCatalogNum());
            stmt.setString(3, data.getDept());
            stmt.setInt(4, data.getWtu());
            stmt.setInt(5, data.getScu());
            stmt.setInt(6, data.getNumOfSections());
            stmt.setString(7, data.getType().toString());
            stmt.setInt(8, data.getLength());
            stmt.setInt(9, data.getEnrollment());
            labid = sqldb.executePrepStmt(stmt);
         }
         // Set values
         stmt.setString(1, data.getName());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getDept());
         stmt.setInt(4, data.getWtu());
         stmt.setInt(5, data.getScu());
         stmt.setInt(6, data.getNumOfSections());
         stmt.setString(7, data.getType().toString());
         stmt.setInt(8, data.getLength());
         stmt.setInt(9, data.getEnrollment());
         if (labid != -1)
         {
            stmt.setInt(10, labid);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void editData(Course newData)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeData(Course data)
   {
      // TODO Auto-generated method stub

   }
}

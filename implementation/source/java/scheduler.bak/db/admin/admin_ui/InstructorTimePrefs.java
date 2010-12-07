/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LocationAvailability.java
 *
 * Created on Mar 8, 2010, 9:34:13 PM
 */

package scheduler.db.admin.admin_ui;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import scheduler.*;
import scheduler.db.*;
import scheduler.db.instructordb.*;
import scheduler.db.locationdb.*;
import scheduler.generate.DayAvail;
import scheduler.generate.Week;
/**
 *
 * @author jsoliman
 */
public class InstructorTimePrefs extends MyView {


    private static final int NUMDAYSINWEEK = 7;
    private static final int ROWS = 30;

    /* The location to change availability.  */
    Location location;
    Instructor instructor;

    /** Creates new form LocationAvailability */
    public InstructorTimePrefs(Instructor i) {
        this.instructor = i;
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        SubmitButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        SubmitButton.setText("Submit");
        SubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        //ArrayList<DayAvail> availability = Scheduler.ldb.getAvailability(location);
        Vector<Vector> tableData = buildTable( );
        String columnNames[] = new String [] {
                "Time", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
            };
        List list = Arrays.asList(columnNames);
        Vector<String> Names = new Vector<String>(list); 
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
             tableData , Names 
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setResizable(false);
        jTable1.getColumnModel().getColumn(3).setResizable(false);
        jTable1.getColumnModel().getColumn(4).setResizable(false);
        jTable1.getColumnModel().getColumn(5).setResizable(false);
        jTable1.getColumnModel().getColumn(6).setResizable(false);
        jTable1.getColumnModel().getColumn(7).setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SubmitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SubmitButton)
                    .addComponent(CancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void SubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        Vector<Vector> tableData;
        DefaultTableModel tblModel = (DefaultTableModel)jTable1.getModel();
        tableData = tblModel.getDataVector();
        //Scheduler.idb.changeTimePreferences(tableData, instructor);

        Iterator iterator = tableData.iterator();
        HashMap<Integer, LinkedHashMap<Time, TimePreference>> timePreferences = new HashMap<Integer, LinkedHashMap<Time, TimePreference>>();
        timePreferences.put(Week.SUN, new LinkedHashMap());
        timePreferences.put(Week.MON, new LinkedHashMap());
        timePreferences.put(Week.TUE, new LinkedHashMap());
        timePreferences.put(Week.WED, new LinkedHashMap());
        timePreferences.put(Week.THU, new LinkedHashMap());
        timePreferences.put(Week.FRI, new LinkedHashMap());
        timePreferences.put(Week.SAT, new LinkedHashMap());
        
        while (iterator.hasNext()) {
            Vector row = (Vector) iterator.next();
            String time = (String) row.get(0);
            int hr = Integer.parseInt(time.split(":")[0].trim());
            if (time.contains("PM") && hr != 12) {
                hr = hr + 12;
            }
            String mid = time.split(":")[1].trim();
            mid = mid.substring(0,2);
            int min = Integer.parseInt(mid);
            for (int j = 1; j < 8; j++) {
                if ((Integer) row.get(j) != 0) {
                    Time tm = new scheduler.db.Time(hr,min);
                    TimePreference tp = new TimePreference( tm,(Integer)row.get(j)  );
                    LinkedHashMap dayPreference = (LinkedHashMap)timePreferences.get(j-1);
                    dayPreference.put(tm, tp);
                }
            }
        }
        this.instructor.setTimePreferences(timePreferences);
        /*System.out.println("Sunday Time preferences are " + timePreferences.get(Week.SUN));
        System.out.println("Monday Time preferences are " + timePreferences.get(Week.MON));
        System.out.println("Tuesday Time preferences are " + timePreferences.get(Week.TUE));
        System.out.println("Wednesday Time preferences are " + timePreferences.get(Week.WED));
        System.out.println("Thursday Time preferences are " + timePreferences.get(Week.THU));
        System.out.println("Friday Time preferences are " + timePreferences.get(Week.FRI));
        System.out.println("Saturday Time preferences are " + timePreferences.get(Week.SAT));*/
        this.setVisible(false);
    }

    private ArrayList<DayAvail> convertTable (Vector<Vector> tableData) {
       ArrayList<DayAvail> availability = new ArrayList<DayAvail>();    

       for (int j = 1; j < NUMDAYSINWEEK+1; j++ ) {
          DayAvail day = new DayAvail();

          ArrayList<Boolean> column = new ArrayList<Boolean>();
          for (int i = 0; i < ROWS; i++ ) {
             //Vector<Object> row = tableData.get(i).;
             Boolean value = (Boolean) tableData.get(i).get(j);
             boolean cell = value.booleanValue();
             if (!cell) {

                 Time start = getStartTime(i);
                 Time end = getEndTime(i);
                 try {
                     day.book(start, end);
                 }
                 catch (Exception e) {
                     System.err.println("Error converting table.");
                 }
             }
          }
           
          availability.add(day); 
       }

        return availability;
    }

    private Vector<Vector> buildTable() {
        Vector<Vector> returnVal = new Vector<Vector>();
        for (int i = 0; i < ROWS; i++) {
           Time start = getStartTime(i);
           Time end = getEndTime(i);
           Vector<Object> row = new Vector<Object>();
           String time = start.standardString();
           row.add((Object)time);
           for (int j = 0; j < NUMDAYSINWEEK; j++) {
               //System.out.println("Start is " + start);
               int pref = instructor.getPreference(j, start);
               Integer cell = new Integer(pref);
               row.add((Object)cell);
           }
           returnVal.add(row); 
        } 
        return returnVal;
    }

    private Time getStartTime(int number ) {
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
        
       return new Time(hour, minute);
    }

    private Time getEndTime(int number) {
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
       if ((minute + 30) == 60) {
           return new Time(hour + 1, 0);
       }
       else {
           return new Time(hour, minute + 30);
       }
    }

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        this.setVisible(false);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new LocationAvailability().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton SubmitButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration

}


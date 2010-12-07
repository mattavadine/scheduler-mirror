package scheduler.db.preferencesdb.preferences_ui;

import java.util.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.lang.String;
import scheduler.*;
import scheduler.db.*;
import scheduler.db.preferencesdb.PreferencesDB;
import scheduler.db.preferencesdb.Preferences;

/**
 * Preferences UI
 * @author Leland Garofalo
 */
public class PreferencesUIOld extends javax.swing.JFrame implements Observer {

    private PreferencesDB database = Scheduler.pdb;
    private Preferences preference = new Preferences();

    /** Creates new form PreferencesUI */
    public PreferencesUIOld() {
        database.addObserver(this);
        initComponents();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Preferences Database");


    }

    public void update(Observable obs, Object obj) {
        database = Scheduler.pdb;
        database.addObserver(this);
        System.out.println("In Update");
        //this.initComponents();


        final ArrayList<String> names = new ArrayList<String>();
        Vector<Preferences> data = (Vector) database.getDayPreferences();
        if (data != null) {
            for (Preferences p : data) {
                names.add(p.getName());
            }
        }
        jList.setModel(new javax.swing.AbstractListModel() {

            ArrayList<String> strings = names;

            public int getSize() {
                return strings.size();
            }

            public Object getElementAt(int i) {
                return strings.get(i);
            }
        });
        jScrollPane1.setViewportView(jList);
        changeEditable(false);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jList = new javax.swing.JList();
        typeGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameBox = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        typeOneButton = new javax.swing.JRadioButton();
        typeTwoButton = new javax.swing.JRadioButton();
        typeThreeButton = new javax.swing.JRadioButton();
        typeFourButton = new javax.swing.JRadioButton();
        dataLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dataBox = new javax.swing.JTextArea();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        importanceLabel = new javax.swing.JLabel();
        importanceBox = new javax.swing.JTextField();
        violatableBox = new javax.swing.JCheckBox();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        final ArrayList<String> names = new ArrayList<String>();
        Vector<Preferences> data = (Vector) database.getDayPreferences();
        if (data != null) {
            for (Preferences p : data) {
                names.add(p.getName());
            }
        }
        jList.setModel(new javax.swing.AbstractListModel() {
            // String[] strings = { "CPE 101", "CPE 102", "CPE 103", "CPE 225" };

            ArrayList<String> strings = names;

            public int getSize() {
                return strings.size();
            }

            public Object getElementAt(int i) {
                return strings.get(i);
            }
        });
        jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        nameLabel.setText("Name:");

        nameBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameBoxActionPerformed(evt);
            }
        });

        nameBox.setEditable(false);
        dataBox.setEditable(false);


        typeLabel.setText("Type:");

        typeGroup.add(typeOneButton);
        typeOneButton.setText("Prevent Overlapping Classes");
        typeOneButton.setEnabled(false);

        typeGroup.add(typeTwoButton);
        typeTwoButton.setText("Specific Room Request");
        typeTwoButton.setEnabled(false);

        typeGroup.add(typeThreeButton);
        typeThreeButton.setText("Days Offered");
        typeThreeButton.setEnabled(false);

        typeGroup.add(typeFourButton);
        typeFourButton.setText("Lecture/Lab Time Proximity");
        typeFourButton.setEnabled(false);

        dataLabel.setText("Data:");

        dataBox.setColumns(20);
        dataBox.setRows(5);
        jScrollPane2.setViewportView(dataBox);

        addButton.setText("New");
        addButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        NewPreferenceActionPerformed(e);
                    }
                });

        editButton.setText("Edit");
        editButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        EditPreferenceActionPerformed(e);
                    }
                });

        saveButton.setText("Save");
        saveButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        RemoveCourseActionPerformed(e);
                        AddPreferenceActionPerformed(e);
                    }
                });

        removeButton.setText("Delete");
        removeButton.addActionListener(
                new ActionListener() {
                    JCheckBox dontAskBox = new JCheckBox("Turn off confirmation message\n"); 
                    public void actionPerformed(ActionEvent e) {
                       if (dontAskBox.isSelected()) {
                          RemoveCourseActionPerformed(e);
                       } else {
                          Object[] options = {dontAskBox,"Yes","No","Cancel"};
                          int n = JOptionPane.showOptionDialog(jPanel1, "Are you sure you would like to Delete?"
                                 ,"Delete Confirm",JOptionPane.YES_NO_CANCEL_OPTION,
                                 JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                          if(n == 1) {
                             RemoveCourseActionPerformed(e);
                          }
                          if(n == -1 || n == 3) {
                             dontAskBox.setSelected(false);
                          }
                       }
                    }
                });

        importanceLabel.setText("Importance:");

        violatableBox.setText("Do Not Violate");

        violatableBox.setEnabled(false);
        importanceBox.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(jPanel1Layout.createSequentialGroup().addComponent(nameLabel).addGap(18, 18, 18).addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(typeLabel).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(typeOneButton).addComponent(typeTwoButton).addComponent(typeFourButton).addComponent(typeThreeButton))).addGroup(jPanel1Layout.createSequentialGroup().addComponent(dataLabel).addGap(18, 18, 18).addComponent(jScrollPane2)))).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGap(18, 18, 18).addComponent(addButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(editButton)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(importanceLabel).addGap(18, 18, 18).addComponent(importanceBox))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(saveButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(removeButton)).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(violatableBox))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(nameLabel).addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(32, 32, 32).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(typeOneButton).addComponent(typeLabel)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(typeTwoButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(typeThreeButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(typeFourButton).addGap(33, 33, 33).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(dataLabel).addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(importanceLabel).addComponent(importanceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(violatableBox).addGap(22, 22, 22).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(removeButton).addComponent(saveButton).addComponent(editButton).addComponent(addButton)).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PreferencesUIOld().setVisible(true);
            }
        });
    }

    /**
     *  Method invoked when the Add Course button is pressed.
     *  @param evt The event action.
     **/
    private void NewPreferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCourseActionPerformed
        this.preference = new Preferences("Untitled_RENAME_ME", "Replace with data", 0, 0, 1);
        this.database.addPreference(preference);
        jList.setSelectedValue(this.preference, true);
        System.out.println("In PreferencesUI.NewCourseActionPerformed");
    }//GEN-LAST:event_AddCourseActionPerformed

    /**
     *  Method invoked when the Add Course button is pressed.
     *  @param evt The event action.
     **/
    private void AddPreferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddCourseActionPerformed
        this.preference = new Preferences(nameBox.getText(), dataBox.getText(), currentType(), Integer.parseInt(importanceBox.getText()), currentViolatable());
        this.database.addPreference(preference);
        System.out.println("In PreferencesUI.AddCourseActionPerformed");
    }//GEN-LAST:event_AddCourseActionPerformed

    public int currentType() {
        if (typeOneButton.isSelected()) {
            return 1;
        } else if (typeTwoButton.isSelected()) {
            return 2;
        } else if (typeThreeButton.isSelected()) {
            return 3;
        } else if (typeFourButton.isSelected()) {
            return 4;
        }
        return -1;
    }

    public int currentViolatable() {
        if (violatableBox.isSelected()) {
            return 1;
        }
        return 0;
    }

    /**
     *  Method invoked when the Remove Course button is pressed.
     *  @param evt The event action.
     **/
    protected void RemoveCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveCourseActionPerformed
        // TODO add your handling code here:
        Preferences removed = preference;
        this.database.removePreference(removed);
        jList.clearSelection();
        System.out.println("In PreferencesUI.RemoveCourseActionPerformed");
    }//GEN-LAST:event_RemoveCourseActionPerformed

    private void EditPreferenceActionPerformed(java.awt.event.ActionEvent evt) {
        changeEditable(!nameBox.isEditable());
    }

    private void changeEditable(boolean change) {
        nameBox.setEditable(change);
        dataBox.setEditable(change);
        typeOneButton.setEnabled(change);
        typeTwoButton.setEnabled(change);
        typeThreeButton.setEnabled(change);
        typeFourButton.setEnabled(change);
        violatableBox.setEnabled(change);
        importanceBox.setEditable(change);
    }

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_instructorListValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (jList.getSelectedValue() != null) {
                String selection = jList.getSelectedValue().toString();
                preference = database.getPreferences(selection);
                nameBox.setText(preference.getName());
                typeGroup.clearSelection();
                switch (preference.getType()) {
                    case 1:
                        typeOneButton.setSelected(true);
                        break;
                    case 2:
                        typeTwoButton.setSelected(true);
                        break;
                    case 3:
                        typeThreeButton.setSelected(true);
                        break;
                    case 4:
                        typeFourButton.setSelected(true);
                        break;
                    default:
                        break;
                }
                dataBox.setText(preference.getData());
                importanceBox.setText("" + preference.getImportance());
                if (preference.getViolatable() == 1) {
                    violatableBox.setSelected(true);
                } else {
                    violatableBox.setSelected(false);
                }
            }
        }
        changeEditable(false);
    }//GEN-LAST:event_instructorListValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup typeGroup;
    private javax.swing.JButton addButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JCheckBox violatableBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel dataLabel;
    private javax.swing.JLabel importanceLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton typeOneButton;
    private javax.swing.JRadioButton typeTwoButton;
    private javax.swing.JRadioButton typeThreeButton;
    private javax.swing.JRadioButton typeFourButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea dataBox;
    private javax.swing.JTextField nameBox;
    private javax.swing.JTextField importanceBox;
    private javax.swing.JList jList;
    // End of variables declaration//GEN-END:variables
}


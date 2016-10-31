/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import core.Client;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author feryandi
 */
public class Control extends javax.swing.JFrame {
    Client c;
    /**
     * Creates new form Control
     */
    public Control() {
        c = Client.getInstance();
        initComponents();
        
        list_friend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    Object userid = list.getModel().getElementAt(index);
                    Chat c = new Chat(userid.toString());
                    c.setVisible(true);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        btn_creategroup = new javax.swing.JButton();
        btn_addfriend = new javax.swing.JButton();
        tab = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_friend = new javax.swing.JList<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_creategroup.setText("CREATE GROUP");
        btn_creategroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_creategroupActionPerformed(evt);
            }
        });

        btn_addfriend.setText("ADD FRIEND");
        btn_addfriend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addfriendActionPerformed(evt);
            }
        });

        list_friend.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(list_friend);

        tab.addTab("Friends", jScrollPane2);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        tab.addTab("Groups", jScrollPane1);

        jList3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList3);

        tab.addTab("Active Chats", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tab)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_creategroup, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_addfriend, javax.swing.GroupLayout.PREFERRED_SIZE, 105, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tab, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_creategroup)
                    .addComponent(btn_addfriend))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tab.getAccessibleContext().setAccessibleName("tabs");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_creategroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_creategroupActionPerformed
        this.setEnabled(false);
        CreateGroup r = new CreateGroup();
        
        r.setVisible(true);
        r.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setEnabled(true);
            }
        });
    }//GEN-LAST:event_btn_creategroupActionPerformed

    private void btn_addfriendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addfriendActionPerformed
        this.setEnabled(false);
        AddFriend r = new AddFriend();
        
        r.setVisible(true);
        r.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setEnabled(true);
                try {
                    PopulateFriends();
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }//GEN-LAST:event_btn_addfriendActionPerformed

    private void PopulateFriends() throws Exception {
        String[] new_data = {""};
        
        JSONObject o = new JSONObject();
        o.put("method", "get_friend");

        JSONObject p = new JSONObject();
        p.put("userid", c.userid);
        o.put("params", p);            

        String response = c.call(o.toJSONString());
        System.out.println("Get Friend reponse: " + response);

        /* Check Response */
        JSONParser parser = new JSONParser();
        JSONObject r = (JSONObject) parser.parse(response);
        String status = (String) r.get("status");
        if (status.equals("success")) {
            JSONArray json_array = (JSONArray) r.get("data");
            new_data = new String[json_array.size()];
            for (int i=0; i<json_array.size(); i++){
                new_data[i] = json_array.get(i).toString();
            }
        }
        list_friend.setListData(new_data);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addfriend;
    private javax.swing.JButton btn_creategroup;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> list_friend;
    private javax.swing.JTabbedPane tab;
    // End of variables declaration//GEN-END:variables
}

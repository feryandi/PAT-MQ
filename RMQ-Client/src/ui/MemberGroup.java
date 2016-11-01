/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import core.Client;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author feryandi
 */
public class MemberGroup extends javax.swing.JFrame {

    Client c;
    int gid = -1;
    
    /**
     * Creates new form MemberGroup
     */
    public MemberGroup(int gid) {
        try {
            c = Client.getInstance();
            this.gid = gid;
            initComponents();
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            PopulateMembers();
        } catch (Exception ex) {
            Logger.getLogger(MemberGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list_member = new javax.swing.JList<>();
        btn_remove = new javax.swing.JButton();
        btn_new = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        list_member.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(list_member);

        btn_remove.setText("REMOVE");
        btn_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeActionPerformed(evt);
            }
        });

        btn_new.setText("ADD NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_new, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btn_remove, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_remove)
                    .addComponent(btn_new))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeActionPerformed
        try {
            JSONObject o = new JSONObject();
            o.put("method", "del_member");
            
            JSONObject p = new JSONObject();
            p.put("userid", list_member.getSelectedValue());
            p.put("group", Integer.toString(gid));
            
            o.put("params", p);   
            
            String response = c.call(o.toJSONString());          
            
            /* Check Response */
            JSONParser parser = new JSONParser();        
            JSONObject r = (JSONObject) parser.parse(response);
            String status = (String) r.get("status");
            if (status.equals("success")) {
                PopulateMembers();
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_removeActionPerformed

    public void PopulateMembers() throws Exception {
        String[] new_data = {""};
        
        JSONObject o = new JSONObject();
        o.put("method", "get_member");

        JSONObject p = new JSONObject();
        p.put("groupid", Integer.toString(gid));
        o.put("params", p);            

        String response = c.call(o.toJSONString());
        System.out.println("Get Member reponse: " + response);

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
        list_member.setListData(new_data);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_new;
    private javax.swing.JButton btn_remove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> list_member;
    // End of variables declaration//GEN-END:variables
}

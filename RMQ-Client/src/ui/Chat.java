/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import core.Client;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author feryandi
 */
public class Chat extends javax.swing.JFrame {

    Client c;
    String userid = null;
    int type = -1;
    ArrayList<String> messages = new ArrayList<>();
    
    /**
     * Creates new form Chat
     */
    public Chat(String userid, int type) {
        try {
            c = Client.getInstance();
            initComponents();
            this.userid = userid;
            lbl_chatwith.setText("Chat with " + userid + " DEBUG TYPE: " + type);
            
            this.type = type;
            if (type == 0) {
                btn_list.setVisible(false);
            }
            
            /* Message Memory */
            if (c.message_memory.containsKey(userid)) {
                messages = c.message_memory.get(userid);
            } else {
                c.message_memory.put(userid, messages);
            }
            c.active_chat.put(userid, this);
            
            refreshChat();
        } catch (ParseException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
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

        txt_message = new javax.swing.JTextField();
        btn_send = new javax.swing.JButton();
        btn_list = new javax.swing.JButton();
        lbl_chatwith = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_chat = new javax.swing.JList<>();

        btn_send.setText("SEND");
        btn_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sendActionPerformed(evt);
            }
        });

        btn_list.setText("MEMBER LIST");
        btn_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_listActionPerformed(evt);
            }
        });

        lbl_chatwith.setText("Chat with Someone");

        list_chat.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(list_chat);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_list, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txt_message)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_send))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_chatwith)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lbl_chatwith)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txt_message))
                    .addComponent(btn_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_list)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sendActionPerformed
        try {
            JSONObject o = new JSONObject();
            o.put("method", "message");
            
            JSONObject p = new JSONObject();
            p.put("message", "{\"from\":\"" + c.userid + "\", \"message\":\"" + txt_message.getText() + "\"}");
            p.put("key", userid);
            
            o.put("params", p);          
            
            String response = c.call(o.toJSONString());          
            
            /* Check Response */
            JSONParser parser = new JSONParser();        
            JSONObject r = (JSONObject) parser.parse(response);
            String status = (String) r.get("status");
            if (status.equals("success")) {
                messages.add("{\"from\":\"" + c.userid + "\", \"message\":\"" + txt_message.getText() + "\"}");
                c.message_memory.replace(userid, messages);
                refreshChat();
                txt_message.setText("");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_sendActionPerformed

    private void btn_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_listActionPerformed
        MemberGroup r = new MemberGroup(type);
        r.setVisible(true);
    }//GEN-LAST:event_btn_listActionPerformed

    public void refreshChat() throws ParseException {
        String[] raw_msg = messages.toArray(new String[0]);
        String[] msg = new String[raw_msg.length];
        
        int i = 0;
        for(String s: raw_msg) {
            JSONParser p = new JSONParser();
            JSONObject obj = (JSONObject) p.parse(s);
            
            msg[i] = obj.get("from") + ": " + obj.get("message");
            i++;
        }
        
        list_chat.setListData(msg);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_list;
    private javax.swing.JButton btn_send;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_chatwith;
    private javax.swing.JList<String> list_chat;
    private javax.swing.JTextField txt_message;
    // End of variables declaration//GEN-END:variables
}

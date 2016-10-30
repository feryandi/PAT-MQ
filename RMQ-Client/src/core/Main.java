/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ui.Login;

/**
 *
 * @author feryandi
 */
public class Main {    
    
    public static void main(String[] argv) {
        /*String response;
        try {
            Client c = new Client();
            
            String register = "{\"method\":\"register\", \"params\":{\"name\":\"fery\", \"userid\":\"fery\", \"password\":\"fery\"}}";
            String login = "{\"method\":\"login\", \"params\":{\"userid\":\"null\", \"password\":\"null\"}}";
            String message = "{\"method\":\"message\", \"params\":{\"key\":\"null\", \"message\":\"Hei World\"}}";
            
            response = c.call(login);
            JSONParser parser = new JSONParser();        
            JSONObject r = (JSONObject) parser.parse(response);    
            
            c.bind((String) r.get("userid"));
            response = c.call(message);
            
            System.out.println(response);
            
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
    
}

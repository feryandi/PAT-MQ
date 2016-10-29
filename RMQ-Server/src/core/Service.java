/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author feryandi
 */
public class Service {
    private Database db;
    
    public Service() {        
        db = new Database();
    }
    
    public String execute(String method, String params) throws ParseException, SQLException {
        String ret = null;
	JSONParser parser = new JSONParser();        
        JSONObject p = (JSONObject) parser.parse(params);
        
        switch(method) {
            case "register":
                ret = register((String) p.get("name"),
                         (String) p.get("userid"),
                         (String) p.get("password"));
                System.out.println("[x] Registered");
                break;
            case "login":
                ret = login((String) p.get("userid"),
                         (String) p.get("password"));
                System.out.println("[x] Login");
                break;
            default:
                throw new IllegalArgumentException("Invalid method: " + method);
        }
        
        return ret;
    }
    
    private String register(String name, String uid, String password) throws SQLException {
        // TO-DO: Check unique userid!
        String sql = "INSERT INTO user (name, userid, password) " +
                     "VALUES ('" + name + "', '" + uid + "', '" + password + "');"; 
        Statement stmt = db.connection.createStatement();
        stmt.executeUpdate(sql);
        
	JSONObject obj = new JSONObject();
	obj.put("status", "success");
        
        return obj.toJSONString();
    }
    
    private String login(String userid, String password) throws SQLException {
        boolean success = false;
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();     
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `user` WHERE userid='" + userid + "' AND password='" + password + "';");
        while ( rs.next() ) {
            success = true;
            obj.put("id", rs.getInt("id"));
            obj.put("userid", rs.getString("userid"));
        }
        
        rs.close();
        stmt.close();                
        
        if (success) {            
            obj.put("status", "success");
        } else {            
            obj.put("status", "failed");
        }
        
        return obj.toJSONString();
    }

    private String addFriend(String adderid, String userid) throws SQLException {
        // TO-DO: Check unique userid!
        String sql = "INSERT INTO friend (uaid, ubid) " +
                     "VALUES ('" + adderid + "', '" + userid + "');"; 
        Statement stmt = db.connection.createStatement();
        stmt.executeUpdate(sql);
        
	JSONObject obj = new JSONObject();
	obj.put("status", "success");
        
        return obj.toJSONString();
    }
    
    private String addGroup(String group) {
        return null;
    }
}

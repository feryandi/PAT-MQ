/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONArray;
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
            case "add_friend":
                ret = addFriend((String) p.get("userid"),
                            (String) p.get("friendid"));
                System.out.println("[x] Add Friend: " + ret);
                break;
            case "get_friend":
                ret = getFriend((String) p.get("userid"));
                System.out.println("[x] Get Friend");
                break;
            case "create_group":
                break;
            case "add_member":
                ret =  addGroupMember((String) p.get("userid"), 
                                    (String) p.get("group"));
                System.out.println("[x] Add Group");
                break;
            case "del_member":
                ret =  delGroupMember((String) p.get("userid"), 
                                    (String) p.get("group"));
                System.out.println("[x] Del Group");
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
        JSONObject obj = new JSONObject();
        Statement stmt = db.connection.createStatement();        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `friend` WHERE uaid='" + adderid + "' AND ubid='" + userid + "';");
        
        if ( !rs.next() ) {
            String sql = "INSERT INTO friend (uaid, ubid) " +
                         "VALUES ('" + adderid + "', '" + userid + "');"; 
            stmt = db.connection.createStatement();
            stmt.executeUpdate(sql);

            obj.put("status", "success");   
        } else {
            obj.put("status", "failed");               
        }
        
        return obj.toJSONString();
    }

    private String getFriend(String userid) throws SQLException {
        boolean success = false;
        JSONObject obj = new JSONObject();
        Statement stmt = db.connection.createStatement();        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `friend` WHERE uaid='" + userid + "' OR ubid='" + userid + "';");
        
        JSONArray list = new JSONArray();
        if ( !!rs.next() ) {            
            success = true;
            String uaid = (String) rs.getString("uaid");
            String ubid = (String) rs.getString("ubid");
            
            if (uaid.equals(userid)) {
                list.add(ubid);
            } else {
                list.add(uaid);
            }
        } 
        
        if (success)  {
            obj.put("data", list);            
            obj.put("status", "success");   
        } else {
            obj.put("status", "failed");               
        }
        
        return obj.toJSONString();
    }
        
    private String addGroupMember(String userid, String group) throws SQLException, ParseException {
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();     
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `chat_group` WHERE uid='" + userid + ";");
        
        if ( !rs.next() ) {
            JSONArray groups = new JSONArray();
            groups.add(group);
            
            String sql = "INSERT INTO `chat_group` (uid, groups) " +
                         "VALUES ('" + userid + "', '" + groups.toJSONString() + "');"; 
            stmt = db.connection.createStatement();
            stmt.executeUpdate(sql);
        } else {
            JSONParser parser = new JSONParser();        
            JSONArray p = (JSONArray) parser.parse(rs.getString("groups"));
            p.add(group);
            
            String sql = "UPDATE `chat_group` " +
                         "SET groups='" + p.toJSONString() + "' " +
                         "WHERE userid='" + userid + "');"; 
            stmt = db.connection.createStatement();
            stmt.executeUpdate(sql);
        }
        
        rs.close();
        stmt.close();
        
	obj.put("status", "success");
        return obj.toJSONString();
    }
    
    private String delGroupMember(String userid, String group) throws SQLException, ParseException {
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();     
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `chat_group` WHERE uid='" + userid + ";");
        
        JSONParser parser = new JSONParser();        
        JSONArray p = (JSONArray) parser.parse(rs.getString("groups"));
        p.remove(group);

        String sql = "UPDATE `chat_group` " +
                     "SET groups='" + p.toJSONString() + "' " +
                     "WHERE userid='" + userid + "');"; 
        stmt = db.connection.createStatement();
        stmt.executeUpdate(sql);
        
        stmt.close();
        
	obj.put("status", "success");
        return obj.toJSONString();
    }
}

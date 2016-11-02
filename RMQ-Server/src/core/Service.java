/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.rabbitmq.client.Channel;
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
            case "get_group":
                ret = getGroup((String) p.get("userid"));
                System.out.println("[x] Get Group");
                break;
            case "get_member":
                ret = getGroupMember((String) p.get("groupid"));
                System.out.println("[x] Get Member Group");
                break;
            case "create_group":
                ret = addGroup((String) p.get("userid"),
                            (String) p.get("group"));
                System.out.println("[x] Add Group");
                break;
            case "add_member":
                ret =  addGroupMember((String) p.get("userid"), 
                                    (String) p.get("group"),
                                    (String) p.get("admin_status"));
                System.out.println("[x] Add Member");
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
        
        ResultSet result_set = stmt.executeQuery("SELECT * FROM `user` WHERE userid='" + userid + "' LIMIT 1;");
        
        if (result_set.next()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM `friend` WHERE (uaid='" + adderid + "' AND ubid='" + userid + "') OR (uaid='" + userid + "' AND ubid='" + adderid + "');");

            if (!rs.next()) {
                String sql = "INSERT INTO friend (uaid, ubid) "
                        + "VALUES ('" + adderid + "', '" + userid + "');";
                stmt = db.connection.createStatement();
                stmt.executeUpdate(sql);

                obj.put("status", "success");
            } else {
                obj.put("status", "failed-alread_friend");
            }
        } else {
            obj.put("status", "failed-id_not_found");            
        }
        
        return obj.toJSONString();
    }

    private String getFriend(String userid) throws SQLException {
        boolean success = false;
        JSONObject obj = new JSONObject();
        Statement stmt = db.connection.createStatement();        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `friend` WHERE uaid='" + userid + "' OR ubid='" + userid + "';");
        
        JSONArray list = new JSONArray();
        while ( rs.next() ) {            
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
    
    private String getGroup(String userid) throws SQLException {
        boolean success = false;
        JSONObject obj = new JSONObject();
        Statement stmt = db.connection.createStatement();        
        ResultSet rs = stmt.executeQuery("SELECT group_chat.name AS group_name, group_chat.id AS group_id "
                + "FROM `group_member` "
                + "JOIN `group_chat` ON group_member.group_id = group_chat.id "
                + "WHERE group_member.uid='" + userid + "';");
                
        JSONArray list = new JSONArray();
        while ( rs.next() ) {            
            success = true;
            JSONObject gr = new JSONObject();
            gr.put("name", rs.getString("group_name"));
            gr.put("id", rs.getInt("group_id"));
            list.add(gr);
        }
        
        if (success)  {
            obj.put("data", list);            
            obj.put("status", "success");   
        } else {
            obj.put("status", "failed");               
        }
        
        return obj.toJSONString();
    }
        
    private String addGroup(String user_id, String group_name) throws SQLException, ParseException {
        Statement stmt = db.connection.createStatement();
        JSONObject obj = new JSONObject();

        String sql = "INSERT INTO `group_chat` (name) "
                + "VALUES ('" + group_name + "' );";

        stmt.executeUpdate(sql);
        ResultSet generated_keys = stmt.getGeneratedKeys();
        Integer group_id = generated_keys.getInt(1);

        stmt.close();

        addGroupMember(user_id, group_id.toString(), "1");

        obj.put("status", "success");
        obj.put("group_id", group_id.toString());
        return obj.toJSONString();
    }

    private String getGroupMember(String group_id) throws SQLException {
        boolean success = false;
        JSONObject obj = new JSONObject();
        Statement stmt = db.connection.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT user.userid AS user_id "
                + "FROM `group_member` "
                + "JOIN `user` ON group_member.uid = user.id "
                + "WHERE group_member.group_id='" + group_id + "';");
        
        JSONArray list = new JSONArray();
        while (rs.next()) {
            success = true;
            list.add(rs.getString("user_id"));
        }

        if (success) {
            obj.put("data", list);
            obj.put("status", "success");
        } else {
            obj.put("status", "failed");
        }

        return obj.toJSONString();
    }
    
    private String addGroupMember(String user_id, String group_id, String admin_status) throws SQLException, ParseException {
        JSONObject obj = new JSONObject();
        int uid = getIDByUserid(user_id);
        if (uid != -1) {
            Statement stmt = db.connection.createStatement();
            String sql = "SELECT * FROM `group_member` WHERE "
                    + "group_id='" + group_id + "' AND uid='" + uid + "' LIMIT 1;";
            ResultSet result_set = stmt.executeQuery(sql);
            if (!result_set.next()) {
                sql = "INSERT INTO `group_member` (uid, group_id, is_admin) "
                        + "VALUES ('" + uid + "', '" + group_id + "', '" + admin_status + "');";
                stmt = db.connection.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                obj.put("status", "success");
            } else {
                obj.put("status", "failed-alread_in_group");
            }
        } else {
            obj.put("status", "failed-id_not_found");
        }
        return obj.toJSONString();
    }
    
    private String delGroupMember(String userid, String group_id) throws SQLException, ParseException {
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();
        String uid = Integer.toString(getIDByUserid(userid));
        stmt.executeUpdate("DELETE FROM `group_member` WHERE uid='" + uid + "' AND group_id='" + group_id + "';");
        stmt.close();
        
	obj.put("status", "success");
        
        return obj.toJSONString();
    }
    
    public int getIDByUserid(String userid) throws SQLException {
        int id = -1;
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();     
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `user` WHERE userid='" + userid + "';");
        if (rs.next()) {
            id = rs.getInt("id");
        }
        
        stmt.close();
        
        return id;        
    }
    
    public String getNameByGroupid(String groupid) throws SQLException {
        String name = "";
        Statement stmt = db.connection.createStatement();        
	JSONObject obj = new JSONObject();     
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM `group_chat` WHERE id='" + groupid + "';");
        if (rs.next()) {
            name = rs.getString("name");
        }
        
        stmt.close();
        
        return name;        
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author feryandi
 */
public class Database {
    public Connection connection = null;
    
    public Database() {
        boolean prepare = true;
        File file = new File ("RMQ.db");
        if(file.exists()) {
            prepare = false;
        }        
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:RMQ.db");
            if (prepare) {
                prepare();
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");    
    }
    
    public void prepare() {
        try {
            Statement stmt = null;
            String sql;
            
            // User Table
            stmt = connection.createStatement();
            sql = "CREATE TABLE user " +
                    "(id INTEGER PRIMARY KEY     AUTOINCREMENT," +
                    " name           TEXT    NOT NULL, " +
                    " userid         TEXT    NOT NULL, " +
                    " password       TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
            
            // Friend Table
            stmt = connection.createStatement();
            sql = "CREATE TABLE friend " +
                    "(uaid INTEGER NOT NULL," +
                    " ubid INTEGER NOT NULL, " +
                    " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (uaid, ubid))";
            stmt.executeUpdate(sql);
            
            // Group Table
            stmt = connection.createStatement();
            sql = "CREATE TABLE chat_group " +
                    "(id INTEGER PRIMARY KEY     NOT NULL," +
                    " uid INTEGER   NOT NULL," +
                    " group_name TEXT   NOT NULL," +
                    " is_admin BOOLEAN)";
            stmt.executeUpdate(sql);
            stmt.close();            
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

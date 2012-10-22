/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider;

import java.sql.*;
import org.databaseservice.Database;

/**
 *
 * @author z0006cpz
 */
public class Server extends Database {

    @Override
    public Connection Connect() {
        Connection con = null;
        try {
            Class.forName(dbClass);
            con = DriverManager.getConnection(dbUrl);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Connection established");
        return con;
    }    
}

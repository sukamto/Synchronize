/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author z0006cpz
 */
public class Database {

    protected String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected ResultSet executeStatement(Connection con, String name) {
        try {
            CallableStatement cs = con.prepareCall(name + ";");
            cs.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }
}

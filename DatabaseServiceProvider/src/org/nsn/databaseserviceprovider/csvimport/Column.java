/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nsn.databaseserviceprovider.Database;

/**
 *
 * @author z0006cpz
 */
public class Column extends Database{

    private String CONCAT_COL_NAME = "Date_Cell";
    private Connection con;
    private List<String> existingColumnNames;

    public Column(Connection con) {
        this.con = con;
    }

    public synchronized void addDate_CellColumn(String tablename, List<String> existingColumnNames) {
        this.existingColumnNames = existingColumnNames;
        if (!columnExists(CONCAT_COL_NAME)) {
            try {
                createColumn(tablename, CONCAT_COL_NAME);
            } catch (SQLException ex) {
                System.err.println(getCurrentDateTime() + "->Error appending table " + tablename + ": " + ex);
            }
        }
    }

    public synchronized void updateColumn(String table, String column, String sql) {
        try {
            con.createStatement().executeUpdate("UPDATE [" + table + "] SET [" + column + "] = " + sql + " WHERE [" + column + "] is null");
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error appending table " + table + ": " + ex);
        }
    }

    private boolean columnExists(String name) {
        return existingColumnNames.contains(name);
    }

    private void createColumn(String tablename, String name) throws SQLException {
        con.createStatement().execute("ALTER TABLE [" + tablename + "] ADD [" + name + "] varchar(50) NULL");
    }
}

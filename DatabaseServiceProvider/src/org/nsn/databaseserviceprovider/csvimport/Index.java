/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.sql.Connection;
import java.sql.SQLException;
import org.nsn.databaseserviceprovider.Database;

/**
 *
 * @author z0006cpz
 */
public class Index extends Database {

    private String REORGANIZE_STATEMENT = "[RE_ORGANIZE_INDICES]";
    private Connection con;

    public Index(Connection con) {
        this.con = con;
    }

    public void createIndex(String table, String column, String index) {
        try {
            dropIndex(table, column, index);
            con.createStatement().execute("CREATE NONCLUSTERED INDEX "
                    + "[" + index + "] ON [" + table + "] ([" + column + "] ASC) WITH (SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF) ON [PRIMARY]");
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error creating index on " + table + ": " + ex);
        }
    }



    private void dropIndex(String table, String column, String index) {
        try {
            con.createStatement().execute("DROP INDEX "
                    + "[" + index + "] ON [" + table + "]");
        } catch (SQLException ex) {
//            System.err.println("Error creating index on " + table + ": " + ex);
        }
    }
}

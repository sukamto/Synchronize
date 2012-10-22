/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.nsn.databaseserviceprovider.Database;
import org.nsn.filemethodsservice.FileNameInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class Table extends Database {

    private String DELIMITER = "<~>";
    private Connection con;
    private List<String> tableColumn = new ArrayList<String>();
    private List<String> tableColumnType = new ArrayList<String>();

    public Table(Connection con) {
        this.con = con;
    }

    public synchronized boolean createTempTable(String name, List<String> tableColumn, List<String> tableColumnType, List<String> tableColumnSize) {
        this.tableColumn = tableColumn;
        this.tableColumnType = tableColumnType;
        dropTable(name);
        try {
            String sql = createCreateTableSQL(name);
            createTable(sql);
            return true;
        } catch (Exception e) {
            System.err.println(getCurrentDateTime() + "->Error creating temp table: " + e);
        }
        return false;
    }

    public synchronized boolean importFileToTable(String originalFile, String filePath, String tableName) {
        try {
            con.createStatement().executeUpdate("BULK INSERT " + tableName + " FROM '" + filePath + "' WITH ( FIELDTERMINATOR = '" + DELIMITER + "',ROWTERMINATOR = '\n')");
            return true;
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error importing " + originalFile + ": " + ex);
            if (ex.getMessage().contains("(type mismatch")) {
                FileNameInterface fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
                fileNameInterface.storeErrorFile(con, new File(originalFile));
            }
        }
        return false;
    }

    public boolean copyDataFromTableToTable_Unique(String fromTable, String toTable, String fromColumns, String toColumns, String originalFile) {
        try {
            int count = con.createStatement().executeUpdate("INSERT INTO " + toTable + "(" + toColumns + ")"
                    + " SELECT " + fromColumns + " FROM " + fromTable
                    + " WHERE " + fromTable + ".[Date_Cell]"
                    + " NOT IN (SELECT [Date_Cell] FROM " + toTable + " WHERE [Date_Cell] is not null)");
            System.out.println(getCurrentDateTime() + "->" + toTable + ": " + count + " rows added from " + originalFile);
            setDataInserted(count);
            addTableToUpdatedTables(count, toTable);
            return true;
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error copying data to " + toTable + ": " + ex);
        }
        return false;
    }

    public boolean copyDataFromTableToTable(String fromTable, String toTable, String fromColumns, String toColumns, String originalFile, String dateColumn) {
        try {
            int count = con.createStatement().executeUpdate("INSERT INTO " + toTable + "(" + toColumns + ")"
                    + " SELECT " + fromColumns + " FROM " + fromTable);
            System.out.println(getCurrentDateTime() + "->" + toTable + ": " + count + " rows added from " + originalFile);
            setDataInserted(count);
            addTableToUpdatedTables(count, toTable);
            return true;
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error copying data to " + toTable + ": " + ex);
        }
        return false;
    }

    public void clearTable(String table) {
        try {
            con.createStatement().executeUpdate("DELETE FROM " + table);
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error clearing " + table + ": " + ex);
        }
    }

    private void setDataInserted(int count) {
        if (count > 0) {
            GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
            gi.setDataInserted(true);
        }
    }

    private void addTableToUpdatedTables(int count, String name) {
//        if (count > 0) {
        GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
        gi.addTableToUpdatedTables(name);
//        }
    }

    private void dropTable(String name) {
        try {
            con.createStatement().executeQuery("DROP TABLE " + name);
        } catch (SQLException ex) {
            //ignore
        }
    }

    private String createCreateTableSQL(String name) throws Exception {
        String sql = "";
        amendDuplicateColumnNames();
        for (int i = 0; i < tableColumn.size(); i++) {
            if (!tableColumnType.get(i).equalsIgnoreCase("nvarchar")) {
                sql += ",[" + tableColumn.get(i) + "] ";
                sql += "[" + tableColumnType.get(i) + "] NULL";
            } else {
                sql += ",[" + tableColumn.get(i) + "] ";
                sql += "[" + tableColumnType.get(i) + "] ";
                sql += "(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL";
            }
        }
        return "CREATE TABLE " + name + " (" + sql.substring(1) + ")";
    }

    private void amendDuplicateColumnNames() {
        List<String> lst = new ArrayList<String>();
        for (String c : tableColumn) {
            lst.add(c.toLowerCase());
        }
        for (int i = lst.size() - 1; i >= 0; i--) {
            String c = lst.get(i);
            if (lst.indexOf(c) != i) {
                c += "_";
                lst.set(i, c);
                tableColumn.set(i, tableColumn.get(i) + "_");
                i++;
            }
        }
    }

    private void createTable(String sql) throws SQLException {
        con.createStatement().execute(sql);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.nsn.globalcontextservice.GlobalInterface;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class Mapping extends Database {

    private String query = "SELECT [File_Name],[Database_Table] FROM [synix].[dbo].[tblImportMapping]";
    private Statement statement;
    private ResultSet rs;
    private List<String> file = new ArrayList<String>();
    private List<String> table = new ArrayList<String>();

    public Mapping(Statement statement) {
        this.statement = statement;
    }

    public void setFileToTableMapping() {
        try {
            setRs();
            setFileTable();
            saveData();
            closeRs();
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error getting file-to-table mapping info: " + ex);
        }
    }

    private void setRs() throws SQLException {
        rs = statement.executeQuery(query);
    }

    private void setFileTable() throws SQLException {
        while (rs.next()) {
            file.add(rs.getString(1));
            table.add(rs.getString(2));
        }
    }

    private void saveData() {
        GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
        gi.setMappingFiles(file);
        gi.setMappingTables(table);
    }

    private void closeRs() throws SQLException {
        rs.close();
    }
}

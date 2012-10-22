/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.databaseservice.IndexInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.openide.util.Lookup;

@org.openide.util.lookup.ServiceProvider(service = IndexInterface.class)
public class IndexUpdater extends Database implements IndexInterface {

    private List<String> tableNames = new ArrayList<String>();
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    @Override
    public void reOrganizeAll(Connection con) {
        displayMessage();
        setTableNames();
        for (String name : tableNames) {
            try {
                con.createStatement().execute("ALTER INDEX ALL ON " + name + " REBUILD");
                removeName(name);
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }
    
    @Override
    public void reOrganizeIndex(Connection con,String tableName) {
            try {
                con.createStatement().execute("ALTER INDEX ALL ON " + tableName + " REBUILD");
            } catch (SQLException ex) {
                System.err.println(ex);
            }
    }

    private void setTableNames() {
        tableNames.addAll(gi.getUpdatedTables());
    }

    private void displayMessage() {
        if (gi.isDataInserted()) {
            System.out.println(getCurrentDateTime() + "->Rebuilding Indexes.");
        }
    }

    private void removeName(String name) {
        gi.removeTableFromUpdatedTables(name);
    }
}

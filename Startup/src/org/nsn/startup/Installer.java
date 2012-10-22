/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.startup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.databaseservice.Database;
import org.nsn.globalcontextservice.GlobalInterface;
//import org.nsn.importfiles.ScheduledRunner;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

public class Installer extends ModuleInstall {

    private Database database;
    public static Statement statement;
//    private Class[] explorerWindows = new Class[]{TableBrowserTopComponent.class};
    public static Connection con;

    @Override
    public void restored() {
        database = Database.getDefault();
        con = database.Connect();
        try {
            statement = con.createStatement();
//            ScheduledRunner runner = new ScheduledRunner();
//            runner.startScheduler();
        } catch (SQLException ex) {
            System.err.println("Error connecting! " + ex);
        }
        System.out.println("Opening Editors");
        setGlobalVariable();
    }

    private void setGlobalVariable() {
        for (GlobalInterface gi : Lookup.getDefault().lookupAll(GlobalInterface.class)) {
            gi.setDbConnection(con);
            gi.setDbStatement(statement);
        }
    }
}

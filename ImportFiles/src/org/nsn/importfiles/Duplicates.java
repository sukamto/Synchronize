/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles;

import java.sql.Connection;
import java.sql.SQLException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.ericsson.Ericsson;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class Duplicates {

    private String[] TABLES = {
        "tbl3GNSN_Avail",
        "tbl2GEric_Avail",
        "tbl2GEricssonCounters",
        "tbl2GNSN_Avail",
        "tbl2GNSNCounters",
        "tbl3GEric_Avail",
        "tbl3GEricssonCounters",
        "tbl3GNSNCounters"};
    private final static RequestProcessor requestProcessor = new RequestProcessor(Ericsson.class.getName(), 1);
    private ProgressHandle ph = null;
    private Connection con;

    public Duplicates(Connection con) {
        this.con = con;
    }

    public void deleteDuplicates() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (String t : TABLES) {
                        int d = con.createStatement().executeUpdate("DELETE FROM " + t + " WHERE [ID] NOT IN (SELECT MIN([ID]) FROM " + t + " GROUP BY [Date_Cell])");
                    }
                } catch (SQLException ex) {
                    System.err.println("Error deleting possible duplicates: " + ex);
                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("House keeping", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                if (dataInserted()) {
                    IndexThread indexT = new IndexThread(con);
                    indexT.reOrganize();
                }
            }
        });

        ph.start();
        theTask.schedule(0);
    }

    private boolean dataInserted() {
        GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
        return gi.isDataInserted();
    }
}
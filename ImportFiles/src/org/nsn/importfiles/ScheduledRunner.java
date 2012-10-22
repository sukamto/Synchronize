/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class ScheduledRunner {

    private String QUERY_PROCESS_INTERVAL = "SELECT [processing_interval] FROM [tblMisc]";
    private final static RequestProcessor requestProcessor = new RequestProcessor(Ericsson.class.getName(), 1);
    private ProgressHandle ph = null;
    private long process_interval;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public void startScheduler() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (true) {
                    setProcess_interval();
                    ImportThreading thread = new ImportThreading();
                    thread.importFiles();
                    try {
                        Thread.sleep(process_interval);
                    } catch (InterruptedException ex) {
                        System.err.println(ex);
                    }
                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Running scheduler", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                System.out.println("Scheduler stopped");
            }
        });

        ph.start();
//        return theTask;
        theTask.schedule(0);
    }

    private void setProcess_interval() {
        try {
            float interval = 300;
            ResultSet rs = getStatement().executeQuery(QUERY_PROCESS_INTERVAL);
            while (rs.next()) {
                interval = rs.getFloat(1);
            }
            rs.close();
            process_interval = (long) ((interval * 60) * 1000);
//            System.out.println(getCurrentDateTime() + "<>");
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    private Statement getStatement() {
        return gi.getDbStatement();
    }

//    private String getCurrentDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        return dateFormat.format(date);
//    }
}
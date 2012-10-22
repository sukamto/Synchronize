/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles;

import java.sql.Connection;
import org.databaseservice.IndexInterface;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.nsn.importfiles.ericsson.Ericsson;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class IndexThread {

    private final static RequestProcessor requestProcessor = new RequestProcessor(Ericsson.class.getName(), 1);
    private ProgressHandle ph = null;
    private Connection con;

    public IndexThread(Connection con) {
        this.con = con;
    }

    public void reOrganize() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                IndexInterface iI = Lookup.getDefault().lookup(IndexInterface.class);
                iI.reOrganizeAll(con);
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Rebuilding indexes", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
//                System.out.println(getCurrentDateTime() + "->Reorganizing indexes: Completed");
            }
        });

        ph.start();
        theTask.schedule(0);
    }
}

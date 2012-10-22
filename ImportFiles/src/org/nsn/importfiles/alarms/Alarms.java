/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.alarms;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.databaseservice.Import;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.nsn.filemethodsservice.FileNameInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.ImportSuper;
import org.nsn.importfiles.IndexThread;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class Alarms extends ImportSuper {

    private final static RequestProcessor requestProcessor = new RequestProcessor(Alarms.class.getName(), 1);
    private ProgressHandle ph = null;
    private List<String> alarmsFileNames = new ArrayList<String>();
    private List<String> matchingalarmsTableNames = new ArrayList<String>();
    private List<String> matchingalarmsFormats = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public Alarms() {
        this.alarmsFileNames = gi.getAlarmsFileNames();
        this.matchingalarmsTableNames = gi.getMatchingAlarmsTableNames();
        this.matchingalarmsFormats = gi.getMatchingAlarmsFormats();
        this.con = gi.getDbConnection();
    }

    public Task stageFiles() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                setFileNameInterface();
                for (int i = 0; i < alarmsFileNames.size(); i++) {
//                    System.out.println(getCurrentDateTime() + "->Processing " + alarmsFileNames.get(i));
                    CSV_Parser parser = new CSV_Parser(alarmsFileNames.get(i), getOutFileName(matchingalarmsTableNames.get(i)), matchingalarmsFormats.get(i));
                    stageFile = parser.parseFile();
                    if (stageFile) {
                        Import im = Lookup.getDefault().lookup(Import.class);
                        im.setCon(con);
                        im.importCSV_alarms(alarmsFileNames.get(i), getOutFileName(matchingalarmsTableNames.get(i)), matchingalarmsTableNames.get(i));
                    } else {
                        fileNameInterface.storeErrorFile(con, new File(alarmsFileNames.get(i)));
                    }
                }
//                synchronized (lock) {
////                    ready = true;
//                    lock.notifyAll();
//                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Importing Alarms", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                if (dataInserted()) {
                    IndexThread indexT = new IndexThread(con);
                    indexT.reOrganize();
                }
//                System.out.println(getCurrentDateTime() + "->alarms import: Completed");
//                Task nsnTask = null;
//                if (!nsnFileNames.isEmpty()) {
//                    NSN nsn = new NSN(nsnFileNames, matchingNSNTableNames, con);
//                    nsnTask = nsn.parseFile();
//                }
//                if (nsnTask != null) {
//                    nsnTask.schedule(0);
//                }
            }
        });

        ph.start();
//        return theTask;
//        theTask.schedule(0);
        return theTask;
    }

    private void setFileNameInterface() {
        fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
    }

    private String getOutFileName(String name) {
        File f = new File("");
        return f.getAbsolutePath() + "/Temp/" + name + ".csv";
    }

    private boolean dataInserted() {
        GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
        return gi.isDataInserted();
    }
}
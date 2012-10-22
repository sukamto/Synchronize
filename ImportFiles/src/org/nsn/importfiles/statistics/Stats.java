/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.statistics;

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
import org.nsn.importfiles.alarms.Alarms;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class Stats extends ImportSuper {

    private final static RequestProcessor requestProcessor = new RequestProcessor(Stats.class.getName(), 1);
    private ProgressHandle ph = null;
    private List<String> statsFileNames = new ArrayList<String>();
    private List<String> matchingstatsTableNames = new ArrayList<String>();
    private List<String> matchingstatsFormats = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public Stats() {
        this.statsFileNames = gi.getStatsFileNames();
        this.matchingstatsTableNames = gi.getMatchingStatsTableNames();
        this.matchingstatsFormats = gi.getMatchingStatsFormats();
        this.con = gi.getDbConnection();
    }

    public Task stageFiles() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                setFileNameInterface();
                for (int i = 0; i < statsFileNames.size(); i++) {
//                    System.out.println(getCurrentDateTime() + "->Processing " + statsFileNames.get(i));
                    CSV_Parser parser = new CSV_Parser(statsFileNames.get(i), getOutFileName(matchingstatsTableNames.get(i)), matchingstatsFormats.get(i));
                    stageFile = parser.parseFile();
                    if (stageFile) {
                        Import im = Lookup.getDefault().lookup(Import.class);
                        im.setCon(con);
                        im.importCSV_stats(statsFileNames.get(i), getOutFileName(matchingstatsTableNames.get(i)), matchingstatsTableNames.get(i));
                    } else {
                        fileNameInterface.storeErrorFile(con, new File(statsFileNames.get(i)));
                    }
                }
//                synchronized (lock) {
////                    ready = true;
//                    lock.notifyAll();
//                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Importing Statistics", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                Task alarmsTask = null;
                if (!gi.getAlarmsFileNames().isEmpty()) {
                    Alarms alarms = new Alarms();
                    alarmsTask = alarms.stageFiles();
                } else {
                    if (dataInserted()) {
                        IndexThread indexT = new IndexThread(con);
                        indexT.reOrganize();
                    }
                }
                if (alarmsTask != null) {
                    alarmsTask.schedule(0);
                }
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
        return gi.isDataInserted();
    }
}
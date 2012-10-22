/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.ericsson;

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
import org.nsn.importfiles.alarms.Alarms;
import org.nsn.importfiles.nsn.NSN;
import org.nsn.importfiles.statistics.Stats;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class Ericsson extends ImportSuper {

    private final static RequestProcessor requestProcessor = new RequestProcessor(Ericsson.class.getName(), 1);
    private ProgressHandle ph = null;
    private List<String> ericssonFileNames = new ArrayList<String>();
    private List<String> matchingEricssonTableNames = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public Ericsson(List<String> ericssonFileNames, List<String> matchingEricssonTableNames) {
        this.ericssonFileNames = ericssonFileNames;
        this.matchingEricssonTableNames = matchingEricssonTableNames;
        this.con = gi.getDbConnection();
    }

    public void stageFiles() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                setFileNameInterface();
                for (int i = 0; i < ericssonFileNames.size(); i++) {
//                    System.out.println(getCurrentDateTime() + "->Processing " + ericssonFileNames.get(i));
                    CSV_Parser parser = new CSV_Parser(ericssonFileNames.get(i), getOutFileName(matchingEricssonTableNames.get(i)));
                    stageFile = parser.parseFile();
                    if (stageFile) {
                        Import im = Lookup.getDefault().lookup(Import.class);
                        im.setCon(con);
                        im.importCSV(ericssonFileNames.get(i), getOutFileName(matchingEricssonTableNames.get(i)), matchingEricssonTableNames.get(i));
                    } else {
                        fileNameInterface.storeErrorFile(con, new File(ericssonFileNames.get(i)));
                    }
                }
//                synchronized (lock) {
////                    ready = true;
//                    lock.notifyAll();
//                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Importing Ericsson", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
//                System.out.println(getCurrentDateTime() + "->Ericsson import: Completed");
                Task nsnTask = null;
                Task statsTask = null;
                Task alarmsTask = null;
                if (!gi.getNsnFileNames().isEmpty()) {
                    NSN nsn = new NSN(gi.getNsnFileNames(), gi.getMatchingNSNTableNames(), con);
                    nsnTask = nsn.parseFile();
                    nsnTask.schedule(0);
                } else {
                    if (!gi.getStatsFileNames().isEmpty()) {
                        Stats stats = new Stats();
                        statsTask = stats.stageFiles();
                        statsTask.schedule(0);
                    }
                    if (!gi.getAlarmsFileNames().isEmpty()) {
                        Alarms alarms = new Alarms();
                        alarmsTask = alarms.stageFiles();
                        alarmsTask.schedule(0);
                    }
                }
            }
        });

        ph.start();
//        return theTask;
        theTask.schedule(0);
    }

    private void setFileNameInterface() {
        fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
    }

    private String getOutFileName(String name) {
        File f = new File("");
        return f.getAbsolutePath() + "/Temp/" + name + ".csv";
    }
}
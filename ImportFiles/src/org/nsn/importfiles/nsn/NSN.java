/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.nsn;

import org.nsn.excelservice.ExcelServiceInterface;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.databaseservice.Import;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.Duplicates;
import org.nsn.importfiles.ImportSuper;
import org.nsn.importfiles.IndexThread;
import org.nsn.importfiles.alarms.Alarms;
import org.nsn.importfiles.statistics.Stats;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author z0006cpz
 */
public class NSN extends ImportSuper {

    private final static RequestProcessor requestProcessor = new RequestProcessor(NSN.class.getName(), 1);
    private ProgressHandle ph = null;
    private List<String> fileNames = new ArrayList<String>();
    private List<String> tableNames = new ArrayList<String>();
    private Connection con;
    private String outputCSV;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public NSN(List<String> fileName, List<String> tableNames, Connection con) {
        this.fileNames = fileName;
        this.tableNames = tableNames;
        this.con = con;
    }

    public Task parseFile() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < fileNames.size(); i++) {
                    setOutputCSV(fileNames.get(i));
//                    System.out.println(getCurrentDateTime() + "->Processing " + fileNames.get(i));
                    ExcelServiceInterface excel = Lookup.getDefault().lookup(ExcelServiceInterface.class);
                    excel.convertXLSXtoCSV(fileNames.get(i), outputCSV,false);
                    FileColumns fileCols = new FileColumns(con, new File(outputCSV), tableNames.get(i));
                    if (fileCols.setMatchingColumnNames()) {
                        Import im = Lookup.getDefault().lookup(Import.class);
                        im.setCon(con);
                        im.importCSV(fileNames.get(i), outputCSV, tableNames.get(i));
                    }
                }
            }
        };

        RequestProcessor.Task theTask = requestProcessor.create(runnable);

        ph = ProgressHandleFactory.createHandle("Importing NSN", theTask);
        theTask.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(org.openide.util.Task task) {
                ph.finish();
                Task statsTask = null;
                Task alarmsTask = null;
                if (!gi.getStatsFileNames().isEmpty()) {
                    Stats stats = new Stats();
                    statsTask = stats.stageFiles();
                } else if (!gi.getAlarmsFileNames().isEmpty()) {
                    Alarms alarms = new Alarms();
                    alarmsTask = alarms.stageFiles();
                } else {
                    if (dataInserted()) {
                        IndexThread indexT = new IndexThread(con);
                        indexT.reOrganize();
                    }
                }
                if (statsTask != null) {
                    statsTask.schedule(0);
                }
                if (alarmsTask != null) {
                    alarmsTask.schedule(0);
                }
//                Duplicates dup = new Duplicates(con);
//                dup.deleteDuplicates();
//                if (dataInserted()) {
//                    IndexThread indexT = new IndexThread(con);
//                    indexT.reOrganize();
//                }
            }
        });
        ph.start();
        return theTask;
//        theTask.schedule(0);
    }

    private void setOutputCSV(String name) {
        File original = new File(name);
        File f = new File("");
        outputCSV = f.getAbsolutePath() + "/Temp/" + original.getName().substring(0, original.getName().lastIndexOf(".")) + ".csv";
    }

    private boolean dataInserted() {
        return gi.isDataInserted();
    }
}

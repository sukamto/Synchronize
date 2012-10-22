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
import org.nsn.filemethodsservice.FileNameInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.ImportSuper;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class StatsRunner extends ImportSuper implements Runnable {

    private List<String> statsFileNames = new ArrayList<String>();
    private List<String> matchingstatsTableNames = new ArrayList<String>();
    private List<String> matchingstatsFormats = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public StatsRunner() {
        this.statsFileNames = gi.getStatsFileNames();
        this.matchingstatsTableNames = gi.getMatchingStatsTableNames();
        this.matchingstatsFormats = gi.getMatchingStatsFormats();
        this.con = gi.getDbConnection();
    }

    @Override
    public void run() {
        displayMessage();
        setFileNameInterface();
        for (int i = 0; i < statsFileNames.size(); i++) {
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
    }

    private void displayMessage() {
        if (!statsFileNames.isEmpty()) {
            System.out.println("Importing Statistics");
        }
    }

    private void setFileNameInterface() {
        fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
    }

    private String getOutFileName(String name) {
        File f = new File("");
        return f.getAbsolutePath() + "/Temp/" + name + ".csv";
    }
}
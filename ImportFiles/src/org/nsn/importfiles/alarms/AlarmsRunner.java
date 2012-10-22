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
import org.nsn.filemethodsservice.FileNameInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.ImportSuper;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class AlarmsRunner extends ImportSuper implements Runnable {

    private List<String> alarmsFileNames = new ArrayList<String>();
    private List<String> matchingalarmsTableNames = new ArrayList<String>();
    private List<String> matchingalarmsFormats = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public AlarmsRunner() {
        this.alarmsFileNames = gi.getAlarmsFileNames();
        this.matchingalarmsTableNames = gi.getMatchingAlarmsTableNames();
        this.matchingalarmsFormats = gi.getMatchingAlarmsFormats();
        this.con = gi.getDbConnection();
    }

    @Override
    public void run() {
        displayMessage();
        setFileNameInterface();
        for (int i = 0; i < alarmsFileNames.size(); i++) {
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
    }

    private void displayMessage() {
        if (!alarmsFileNames.isEmpty()) {
            System.out.println("Importing Alarms");
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
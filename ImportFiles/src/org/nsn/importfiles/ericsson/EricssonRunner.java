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
import org.nsn.filemethodsservice.FileNameInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.ImportSuper;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class EricssonRunner extends ImportSuper implements Runnable {

    private List<String> ericssonFileNames = new ArrayList<String>();
    private List<String> matchingEricssonTableNames = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public EricssonRunner(List<String> ericssonFileNames, List<String> matchingEricssonTableNames) {
        this.ericssonFileNames = ericssonFileNames;
        this.matchingEricssonTableNames = matchingEricssonTableNames;
        this.con = gi.getDbConnection();
    }

    @Override
    public void run() {
        displayMessage();
        setFileNameInterface();
        for (int i = 0; i < ericssonFileNames.size(); i++) {
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
    }

    private void displayMessage() {
        if (!ericssonFileNames.isEmpty()) {
            System.out.println("Importing Ericsson");
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
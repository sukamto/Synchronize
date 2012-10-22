/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.CM;

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
public class CMRunner extends ImportSuper implements Runnable {

    private List<String> cmFileNames = new ArrayList<String>();
    private List<String> matchingcmTableNames = new ArrayList<String>();
    private List<String> matchingcmFormats = new ArrayList<String>();
    private boolean stageFile;
    private Connection con;
    private FileNameInterface fileNameInterface;
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);

    public CMRunner() {
        this.cmFileNames = gi.getCMFileNames();
        this.matchingcmTableNames = gi.getMatchingCMTableNames();
        this.matchingcmFormats = gi.getMatchingCMFormats();
        this.con = gi.getDbConnection();
    }

    @Override
    public void run() {
        displayMessage();
        setFileNameInterface();
        for (int i = 0; i < cmFileNames.size(); i++) {
            CSV_Parser parser = new CSV_Parser(cmFileNames.get(i), getOutFileName(matchingcmTableNames.get(i)), matchingcmFormats.get(i));
            stageFile = parser.parseFile();
            if (stageFile) {
                Import import_ = Lookup.getDefault().lookup(Import.class);
                import_.setCon(con);
                import_.importCSV_CM(cmFileNames.get(i), getOutFileName(matchingcmTableNames.get(i)), matchingcmTableNames.get(i));
            } else {
                fileNameInterface.storeErrorFile(con, new File(cmFileNames.get(i)));
            }
        }
    }

    private void displayMessage() {
        if (!cmFileNames.isEmpty()) {
            System.out.println("Importing CM data");
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
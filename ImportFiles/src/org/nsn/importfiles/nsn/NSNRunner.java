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
import org.nsn.importfiles.ImportSuper;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class NSNRunner extends ImportSuper implements Runnable {

    private List<String> fileNames = new ArrayList<String>();
    private List<String> tableNames = new ArrayList<String>();
    private Connection con;
    private String outputCSV;

    public NSNRunner(List<String> fileName, List<String> tableNames, Connection con) {
        this.fileNames = fileName;
        this.tableNames = tableNames;
        this.con = con;
    }

    @Override
    public void run() {
        displayMessage();
        for (int i = 0; i < fileNames.size(); i++) {
            setOutputCSV(fileNames.get(i));
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

    private void displayMessage() {
        if (!fileNames.isEmpty()) {
            System.out.println("Importing NSN");
        }
    }

    private void setOutputCSV(String name) {
        File original = new File(name);
        File f = new File("");
        outputCSV = f.getAbsolutePath() + "/Temp/" + original.getName().substring(0, original.getName().lastIndexOf(".")) + ".csv";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.SAF;

import org.nsn.importfiles.nsn.*;
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
public class SAF_Runner extends ImportSuper implements Runnable {

    private List<String> fileNames = new ArrayList<String>();
    private String tableNames = "tblSAF_Basic";
    private Connection con;
    private String outputCSV;
    private boolean stageFile;

    public SAF_Runner(List<String> fileName, Connection con) {
        this.fileNames = fileName;
        this.con = con;
    }

    @Override
    public void run() {
        displayMessage();
        for (int i = 0; i < fileNames.size(); i++) {
            setOutputCSV(fileNames.get(i));
            ExcelServiceInterface excel = Lookup.getDefault().lookup(ExcelServiceInterface.class);
            excel.convertXLSXtoCSV(fileNames.get(i), outputCSV, true);
            String input = outputCSV;
            setOutputCSV("ASF.csv");
            CSV_Parser parser = new CSV_Parser(input, outputCSV);
            stageFile = parser.parseFile();
            if (stageFile) {
//            FileColumns fileCols = new FileColumns(con, new File(outputCSV), tableNames);
//            if (fileCols.setMatchingColumnNames()) {
                Import im = Lookup.getDefault().lookup(Import.class);
                im.setCon(con);
                im.importCSV_SAF(fileNames.get(i), outputCSV, tableNames);
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

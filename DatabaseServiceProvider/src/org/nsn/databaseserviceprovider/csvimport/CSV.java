/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.io.File;
import org.nsn.databaseserviceprovider.csvimport.SAF.BasicTable;
import java.sql.*;
import org.databaseservice.Import;
import org.nsn.databaseserviceprovider.csvimport.SAF.BasicData;
import org.nsn.filemethodsservice.FileMethods;
import org.nsn.filemethodsservice.FileNameInterface;
import org.openide.util.Lookup;

@org.openide.util.lookup.ServiceProvider(service = Import.class)
public class CSV extends FileMethods implements Import {

    private Connection con;

    @Override
    public synchronized void setCon(Connection con) {
        this.con = con;

    }

    @Override
    public synchronized void importCSV(String originalCsvFile, String parsedCsvFile, String destinationTable) {
        System.out.println("Processing data...");
        KPI_CSV kpi = new KPI_CSV(con);
        kpi.importCSV(originalCsvFile, parsedCsvFile, destinationTable);
    }

    @Override
    public void importCSV_stats(String originalCsvFile, String csvFile, String destinationTable) {
        System.out.println("Processing data...");
        Stats_CSV stats = new Stats_CSV(con);
        stats.importCSV_stats(originalCsvFile, csvFile, destinationTable);
    }

    @Override
    public void importCSV_alarms(String originalCsvFile, String csvFile, String destinationTable) {
        System.out.println("Processing data...");
        Alarms_CSV alarms = new Alarms_CSV(con);
        alarms.importCSV_stats(originalCsvFile, csvFile, destinationTable);
    }

    @Override
    public void importCSV_CM(String originalCsvFile, String csvFile, String destinationTable) {
        System.out.println("Processing data...");
        CM_CSV cm = new CM_CSV(con);
        cm.importCSV_stats(originalCsvFile, csvFile, destinationTable);
    }

    @Override
    public synchronized void importCSV_SAF(String originalCsvFile, String parsedCsvFile, String destinationTable) {
        FileNameInterface fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
        System.out.println("Processing data...");
        BasicTable saf = new BasicTable(con);
        saf.importCSVtoBasic(originalCsvFile, parsedCsvFile, destinationTable);
        BasicData data = new BasicData(con);
        data.saveBasicData();
        fileNameInterface.storeSuccessfulFile(con, new File(originalCsvFile));
    }
}

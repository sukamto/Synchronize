/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.databaseservice;

import java.sql.Connection;

/**
 *
 * @author z0006cpz
 */
public interface Import {

    public void setCon(Connection con);

    public void importCSV(String originalCsvFile, String csvFile, String destinationTable);

    public void importCSV_stats(String originalCsvFile, String csvFile, String destinationTable);

    public void importCSV_alarms(String originalCsvFile, String csvFile, String destinationTable);

    public void importCSV_CM(String originalCsvFile, String csvFile, String destinationTable);

    public void importCSV_SAF(String originalCsvFile, String csvFile, String destinationTable);
}

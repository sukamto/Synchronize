/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.databaseservice.IndexInterface;
import org.nsn.filemethodsservice.FileMethods;
import org.nsn.filemethodsservice.FileNameInterface;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class Alarms_CSV extends FileMethods {

    private String DATE_INDICATOR = "Alarm Time";
    private Connection con;
    private String csvFile;
    private String destinationTable;
    private List<String> fileColumns;
    private List<String> fileColumnType;
    private List<String> fileColumnSize;
    private List<String> tableColumns;
    private List<String> tableColumnType;
    private List<String> tableColumnSize;
    private Table table;
    private FileNameInterface fileNameInterface;
    private String originalCsvFile;

    public Alarms_CSV(Connection con) {
        this.con = con;
        table = new Table(con);
    }

    public synchronized void importCSV_stats(String originalCsvFile, String parsedCsvFile, String destinationTable) {
        this.originalCsvFile = originalCsvFile;
        this.csvFile = parsedCsvFile;
        this.destinationTable = destinationTable;
        try {
            initLists();
            setFileNameInterface();
            grabStructures();
            addMissingColumns();
            if (table.createTempTable("tmp_" + destinationTable, fileColumns, fileColumnType, fileColumnSize)) {
                removeCSVheading();
                createIndex();
                if (table.importFileToTable(originalCsvFile, parsedCsvFile, "tmp_" + destinationTable)) {
                    updateIndex();
                    if (copyFromTableToTable()) {
                        fileNameInterface.storeSuccessfulFile(con, new File(originalCsvFile));
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println(getCurrentDateTime() + "->Error importing " + originalCsvFile + ": " + ex);
        }
    }

    private void createIndex() {
        Index index = new Index(con);
        index.createIndex("tmp_" + destinationTable, DATE_INDICATOR, "IX_tmp_" + destinationTable + DATE_INDICATOR);
    }

    private void updateIndex() {
        IndexInterface index = Lookup.getDefault().lookup(IndexInterface.class);
        index.reOrganizeIndex(con, "tmp_" + destinationTable);
        index.reOrganizeIndex(con, destinationTable);
    }

    private void initLists() {
        fileColumns = new ArrayList<String>();
        fileColumnType = new ArrayList<String>();
        fileColumnSize = new ArrayList<String>();
        tableColumns = new ArrayList<String>();
        tableColumnType = new ArrayList<String>();
        tableColumnSize = new ArrayList<String>();
    }

    private void setFileNameInterface() {
        fileNameInterface = Lookup.getDefault().lookup(FileNameInterface.class);
    }

    private void grabStructures() throws Exception {
        getTableStructure();
        getCSVstructure();
    }

    private void addMissingColumns() throws IOException {
        setReaderName(csvFile);
        List<String> fList = new ArrayList<String>();
        fList.addAll(fileToList(false));
        setWriterName(csvFile);
        for (int i = 0; i < fList.size(); i++) {
            fList.set(i, fList.get(i) + "|");
            String[] split = fList.get(i).split(DELIMITER);
            int size = split.length;
            fList.set(i, fList.get(i).substring(0, fList.get(i).length() - 1));
            for (int j = size; j < tableColumnSize.size() - 2; j++) {
                fList.set(i, fList.get(i) + DELIMITER);
            }

        }
        listToFile(fList);
    }

    private boolean copyFromTableToTable() {
        arrangeFileColumnsOrder();
        String fileCols = (getMatchingFileColumns());
        String tableCols = (getMatchingTableColumns());
        return table.copyDataFromTableToTable("tmp_" + destinationTable, destinationTable,
                fileCols, tableCols, originalCsvFile, DATE_INDICATOR);
    }

    private void getTableStructure() throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getColumns(null, null, destinationTable, null);
        while (rs.next()) {
            tableColumns.add(rs.getString("COLUMN_NAME"));
            tableColumnType.add(rs.getString("TYPE_NAME"));
            tableColumnSize.add(rs.getString("COLUMN_SIZE"));
        }
        rs.close();
    }

    private void getCSVstructure() throws IOException {
        setReaderName(csvFile);
        List<String> fileList = fileToList(false);
        String head = fileList.get(0);
        String[] split = head.split(DELIMITER);
        boolean dateTimeDetected = false;
        for (String s : split) {
            fileColumns.add(s);
            if (s.equalsIgnoreCase("skip")
                    || s.trim().toLowerCase().endsWith("name")
                    || s.toLowerCase().contains("description")
                    || s.toLowerCase().contains("text")
                    || s.toLowerCase().endsWith("_old")) {
                fileColumnType.add("nvarchar");
            } else if (s.equalsIgnoreCase("period_start_time")) {
                fileColumnType.add("datetime");
                dateTimeDetected = true;
            } else {
                fileColumnType.add("float");
                fileColumnSize.add("53");
            }
        }
        if (!dateTimeDetected) {
            fileColumnType.set(0, "datetime");
        }
        fileColumnType.set(1, "nvarchar");
    }

    private void removeCSVheading() throws IOException {
        setReaderName(csvFile);
        List<String> fList = new ArrayList<String>();
        fList.addAll(fileToList(false));
        setWriterName(csvFile);
        fList.remove(0);
        fList = removeAnchoredWBTS(fList);
        listToFile(fList);
    }

    private List<String> removeAnchoredWBTS(List<String> fList) {
        for (int i = 0; i < fList.size(); i++) {
            if (fList.get(i).toLowerCase().contains("anchored wbts")) {
                fList.remove(i);
                i--;
            }
        }
        return fList;
    }

    private String getMatchingTableColumns() {
        String str = "";
        for (int i = 0; i < tableColumns.size(); i++) {
            String tColumn = tableColumns.get(i);
            tColumn = tColumn.trim().toLowerCase().replace(" ", "_");
            for (String fColumn : fileColumns) {
                fColumn = fColumn.trim().toLowerCase().replace(" ", "_");
                if (tColumn.equals(fColumn)) {
                    str += ",[" + tableColumns.get(i) + "]";
                }
            }
        }
        return str.substring(1);
    }

    private String getMatchingFileColumns() {
        String str = "";
        for (int i = 0; i < fileColumns.size(); i++) {
            String fColumn = fileColumns.get(i);
            fColumn = fColumn.trim().toLowerCase().replace(" ", "_");
            for (String tColumn : tableColumns) {
                tColumn = tColumn.trim().toLowerCase().replace(" ", "_");
                if (tColumn.equals(fColumn)) {
                    str += ",[" + fileColumns.get(i) + "]";
                }
            }
        }
        return str.substring(1);
    }

    private void arrangeFileColumnsOrder() {
        String[] lst = new String[tableColumns.size()];
        for (int i = 0; i < tableColumns.size(); i++) {
            for (int j = 0; j < fileColumns.size(); j++) {
                if (tableColumns.get(i).trim().replace(" ", "_").equalsIgnoreCase(fileColumns.get(j).replace(" ", "_"))) {
                    lst[i] = fileColumns.get(j);
                    break;
                }
            }
        }
        fileColumns.clear();
        fileColumns.addAll(Arrays.asList(lst));
        removeNullFromList(fileColumns);
    }

    private void removeNullFromList(List lst) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i) == null) {
                lst.remove(i);
                i--;
            }
        }
    }
}

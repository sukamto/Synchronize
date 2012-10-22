/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport.SAF;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.nsn.databaseserviceprovider.csvimport.Column;
import org.nsn.databaseserviceprovider.csvimport.Index;
import org.nsn.databaseserviceprovider.csvimport.Table;
import org.nsn.databaseserviceprovider.csvimport.TableDateCell;
import org.nsn.filemethodsservice.FileMethods;
import org.nsn.filemethodsservice.FileNameInterface;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class BasicTable extends FileMethods {

    private String NOT_FOUND = "notfound";
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
    private TableDateCell datecell;
    private String originalCsvFile;

    public BasicTable(Connection con) {
        this.con = con;
        table = new Table(con);
    }

    public synchronized void importCSVtoBasic(String originalCsvFile, String parsedCsvFile, String destinationTable) {
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
                if (table.importFileToTable(originalCsvFile, parsedCsvFile, "tmp_" + destinationTable)) {
                    addIDcolumnToTemp();
                    copyFromTableToTable();
                }
            }
        } catch (Exception ex) {
            System.err.println(getCurrentDateTime() + "->Error importing " + originalCsvFile + ": " + ex);
        }
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
            for (int j = size; j < tableColumnSize.size(); j++) {
                fList.set(i, fList.get(i) + DELIMITER);
            }

        }
        listToFile(fList);
    }

    private void addIDcolumnToTemp() throws SQLException {
        con.createStatement().executeUpdate("ALTER table tmp_tblSAF_Basic add ID int IDENTITY");
    }

    private void copyFromTableToTable() {
        String fileCols = (getMatchingFileColumns());
        String tableCols = (getMatchingTableColumns());
        table.clearTable(destinationTable);
        copyDataFromTableToTable("tmp_" + destinationTable, destinationTable,
                fileCols, tableCols);
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
        for (String s : split) {
            int index = getFileColumnIndex(formatHeading(s));
            if (index > -1) {
                fileColumns.add(s);
                fileColumnType.add(tableColumnType.get(index));
                fileColumnSize.add(tableColumnSize.get(index));
            }
        }
    }

    private int getFileColumnIndex(String fileCol) {
        for (String tableCol : tableColumns) {
            if (tableCol.trim().equalsIgnoreCase(fileCol.trim())) {
                return tableColumns.indexOf(tableCol);
            }
        }
        return -1;
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
        for (int i = 0; i < tableColumns.size(); i++) {
            String fColumn = tableColumns.get(i);
            fColumn = fColumn.trim().toLowerCase().replace(" ", "_");
            for (String tColumn : fileColumns) {
                tColumn = tColumn.trim().toLowerCase().replace(" ", "_");
                if (tColumn.equals(fColumn)) {
                    str += ",[" + tableColumns.get(i) + "]";
                    break;
                }
            }
        }
        return str.substring(1);
    }

    private String formatHeading(String str) {
        str = str.trim();
        str = str.replace("  ", " ");
        str = str.replace(" ", "_");
        str = str.replace("'", "");
        str = str.replace("#", "");
        return str.toUpperCase();
    }

    private boolean copyDataFromTableToTable(String fromTable, String toTable, String fromColumns, String toColumns) {
        try {
            con.createStatement().executeUpdate("INSERT INTO " + toTable + "(ID," + toColumns + ")"
                    + " SELECT ID," + fromColumns + " FROM " + fromTable + " ORDER BY " + fromTable + ".ID");
//            System.out.println(getCurrentDateTime() + "->" + toTable + ": " + count + " rows added from " + originalFile);
            return true;
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error copying data to " + toTable + ": " + ex);
        }
        return false;
    }
}

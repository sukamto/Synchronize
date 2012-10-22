/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.nsn;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.nsn.filemethodsservice.FileMethods;
import org.openide.util.Exceptions;

/**
 *
 * @author z0006cpz
 */
public class FileColumns extends FileMethods {

    private Connection con;
    private File csv;
    private String tableName;
    private List<String> fileAlignedColumns;
    private List<String> fileTopColumns;
    private List<String> fileBottomColumns;
    private List<String> tableColumns;
    private List<String> tableColumnType;
    private List<String> tableColumnSize;

    public FileColumns(Connection con, File csv, String tableName) {
        this.con = con;
        this.csv = csv;
        this.tableName = tableName;
    }

    public boolean setMatchingColumnNames() {
        try {
            if (!isFileEmpty()) {
                initLists();
                try {
                    alignCSVcolumsToTableColumns();
                    addDateToEachLine();
                    return true;
                } catch (Exception ex) {
                    System.err.println("Error parsing NSN csv file: " + ex);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return false;
    }

    private void initLists() {
        fileTopColumns = new ArrayList<String>();
        fileBottomColumns = new ArrayList<String>();
        fileAlignedColumns = new ArrayList<String>();
        tableColumns = new ArrayList<String>();
        tableColumnType = new ArrayList<String>();
        tableColumnSize = new ArrayList<String>();
    }

    private void alignCSVcolumsToTableColumns() throws Exception {
        getTableStructure();
        fileTopColumns = getCSVcolumns(0);
        fileBottomColumns = getCSVcolumns(1);
        fileAlignedColumns.addAll(fileTopColumns);
        align();

        removeCSVheading(2);
        insertAlignedColumnsToFile();
    }

    private void align() {
        for (int i = 0; i < tableColumns.size(); i++) {
            String tc = tableColumns.get(i).trim().replace(" ", "_");
            for (int j = 0; j < fileTopColumns.size(); j++) {
                String fTc = fileTopColumns.get(j).trim().replace(" ", "_");
                if (tc.equalsIgnoreCase(fTc)) {
                    fileAlignedColumns.set(j, fTc);
                }
            }
            for (int j = 0; j < fileBottomColumns.size(); j++) {
                String Bfc = fileBottomColumns.get(j).trim().replace(" ", "_");
                if (tc.equalsIgnoreCase(Bfc)) {
                    fileAlignedColumns.set(j, Bfc);
                }
            }
        }
    }

    private void insertAlignedColumnsToFile() throws IOException {
        setReaderName(csv.getAbsolutePath());
        List<String> fList = new ArrayList<String>();
        fList.addAll(fileToList(false));
        setWriterName(csv.getAbsolutePath());
        fList.add(0, listToString(fileAlignedColumns));
        listToFile(fList);
    }

    private void getTableStructure() throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, null);
        while (rs.next()) {
            tableColumns.add(rs.getString("COLUMN_NAME"));
            tableColumnType.add(rs.getString("TYPE_NAME"));
            tableColumnSize.add(rs.getString("COLUMN_SIZE"));
        }
        rs.close();
    }

    private List<String> getCSVcolumns(int row) throws IOException {
        setReaderName(csv.getAbsolutePath());
        List<String> cols = new ArrayList<String>();
        List<String> fileList = fileToList(false);
        String head = fileList.get(row);
        String[] split = head.split(DELIMITER);
        cols.addAll(Arrays.asList(split));
        return cols;
    }

    private boolean isFileEmpty() throws IOException {
        setReaderName(csv.getAbsolutePath());
        List<String> fileList = fileToList(false);
        if (fileList.size() <= 2) {
            return true;
        }
        return false;
    }

    private void addDateToEachLine() throws Exception {
        setReaderName(csv.getAbsolutePath());
        List<String> csvLines = fileToList(false);
        for (int i = 1; i < csvLines.size(); i++) {
            String date = getDate(csvLines.get(i), i);
            csvLines.set(i, date + csvLines.get(i).substring(csvLines.get(i).indexOf(DELIMITER)));
        }
        setWriterName(csv.getAbsolutePath());
        listToFile(csvLines);
    }

    private String getDate(String str, int row) throws ParseException {
        String[] split = str.split(DELIMITER);
        Date date = stringToDateTimeNSN(split[0]);
        return dateToDBformat(date);
    }

    private void removeCSVheading(int rows) throws IOException {
        setReaderName(csv.getAbsolutePath());
        List<String> fList = new ArrayList<String>();
        fList.addAll(fileToList(false));
        setWriterName(csv.getAbsolutePath());
        for (int i = 0; i < rows; i++) {
            fList.remove(0);
        }
        listToFile(fList);
    }

    private String listToString(List<String> list) {
        String out = "";
        for (int i = 0; i < list.size(); i++) {
            out += DELIMITER + list.get(i);
        }
        return out.substring(DELIMITER.length());
    }
}

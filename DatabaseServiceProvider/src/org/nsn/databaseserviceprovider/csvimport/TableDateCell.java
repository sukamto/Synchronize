/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport;

import java.util.List;

/**
 *
 * @author z0006cpz
 */
public class TableDateCell {

    private String NOT_FOUND = "notfound";
    private String[] CELL_COLS = {"Cell", "BTS Name", "FddCell", "BTS_Name", "WCEL_name", "WCEL name"};
    private String[] DATE_COLS = {"Date", "PERIOD_START_TIME", "Period start time"};
    private String tableDateColumnName;
    private String tableCellColumnName;
    private String fileDateColumnName;
    private String fileCellColumnName;
    private List<String> tableColumns;
    private List<String> fileColumns;

    public TableDateCell(List<String> tableColumns, List<String> fileColumns) {
        this.tableColumns = tableColumns;
        this.fileColumns = fileColumns;
        setDateCellColumnNames();
    }

    private void setDateCellColumnNames() {
        tableDateColumnName = "[" + getTableDateColumnName(tableColumns) + "]";
        fileDateColumnName = "[" + getTableDateColumnName(fileColumns) + "]";
        tableCellColumnName = "[" + getTableCellColumnName(tableColumns) + "]";
        fileCellColumnName = "[" + getTableCellColumnName(fileColumns) + "]";
    }

    public String getSQLdate(List<String> cols) {
        String sql;
        for (String date : DATE_COLS) {
            for (String col : cols) {
                if (col.trim().equalsIgnoreCase(date.trim())) {
                    sql = "CAST([" + date + "] AS VARCHAR(50))";
                    return sql;
                }
            }
        }
        return NOT_FOUND;
    }

    public String getSQLCell(List<String> cols) {
        String sql;
        for (String cell : CELL_COLS) {
            for (String col : cols) {
                if (col.trim().equalsIgnoreCase(cell.trim())) {
                    sql = " + '_' + CAST([" + cell + "] AS VARCHAR(50))";
                    return sql;
                }
            }
        }
        return NOT_FOUND;
    }

    private String getTableDateColumnName(List<String> cols) {
        for (String date : DATE_COLS) {
            for (String col : cols) {
                if (col.trim().equalsIgnoreCase(date.trim())) {
                    return date;
                }
            }
        }
        return "";
    }

    public String addTableDateCellsToColumns(String columns) {
        String str = addDateCellToColumns(columns, tableDateColumnName);
        return addDateCellToColumns(str, tableCellColumnName);
    }

    public String addFileDateCellsToColumns(String columns) {
        String str = addDateCellToColumns(columns, fileDateColumnName);
        return addDateCellToColumns(str, fileCellColumnName);
    }

    private String addDateCellToColumns(String columns, String dateCellNames) {
        String[] split = columns.replace("],[", "<>").split("<>");
        for (int i = 0; i < split.length; i++) {
            String splitStr = addBrackets(split[i]);
            if (splitStr.equalsIgnoreCase(dateCellNames)) {
                return columns;
            }
        }
        return dateCellNames + "," + columns;
    }

    private String addBrackets(String str) {
        str = str.replace("[", "");
        str = str.replace("]", "");
        return "[" + str + "]";
    }
//    public String addFileCellToColumns(String columns) {
//         String[] split = columns.replace("],[", "<>").split("<>");
//        for (int i = 0; i < columns.length(); i++) {
//            String splitStr = split[i] + "]";
//            if (splitStr.equalsIgnoreCase(fileCellColumnName)) {
//                return columns;
//            }
//        }
//        return fileCellColumnName + "," + columns;
//    }
//
//    public String addTableDateToColumns(String columns) {
//        String[] split = columns.replace("],[", "<>").split("<>");
//        for (int i = 0; i < columns.length(); i++) {
//            String splitStr = split[i] + "]";
//            if (splitStr.equalsIgnoreCase(tableDateColumnName)) {
//                return columns;
//            }
//        }
//        return tableDateColumnName + "," + columns;
//    }
//
//    public String addTableCellToColumns(String columns) {
//         String[] split = columns.replace("],[", "<>").split("<>");
//        for (int i = 0; i < columns.length(); i++) {
//            String splitStr = split[i] + "]";
//            if (splitStr.equalsIgnoreCase(tableCellColumnName)) {
//                return columns;
//            }
//        }
//        return tableCellColumnName + "," + columns;
//    }

    private String getTableCellColumnName(List<String> cols) {
        for (String cell : CELL_COLS) {
            for (String col : cols) {
                if (col.trim().equalsIgnoreCase(cell.trim())) {
                    return cell;
                }
            }
        }
        return "";
    }
}

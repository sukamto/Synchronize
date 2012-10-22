/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author z0006cpz
 */
public class Excel_Reader extends Excel_Super{

    private BufferedWriter writer;
    private static short headingCount;
    private static Sheet sheet;
    private String MERGED_COLUMN_NAME = "Case_History";
    private List<String> headings = new ArrayList<String>();
    private List<String> cases = new ArrayList<String>();

    public Excel_Reader(BufferedWriter writer, String inputFileName) {
        super(writer, inputFileName);
    }

//    public void read_to_file_UnmergedCells() {
//        try {
//            int lastRow = sheet.getLastRowNum();
//            for (int r = 0; r < lastRow; r++) {
//                String rowString = "";
//                for (int c = 0; c < headingCount; c++) {
//                    Row row = sheet.getRow(r);
//                    if (missingCols(row, c)) {
//                        rowString = addEmptyCols(row, rowString);
//                    } else {
//                        Cell cell = row.getCell(c);
//                        String cellValue = getCellValue(cell, c);
//                        cellValue = remNewLineChar(cellValue);
//                        if (r == 0) {
//                            cellValue = formatHeading(cellValue);
//                        }
//                        rowString += DELIMITER + cellValue;
//                    }
//                }
//                rowString += DELIMITER + " ";
//                if (!rowString.isEmpty()) {
//                    rowString = rowString.substring(DELIMITER.length());
//                    rowString = remExtraCols(rowString);
//                }
//                writer.write(rowString + "\r\n");
//            }
//            writer.close();
//        } catch (Exception ex) {
//            System.err.println("Error opening input excel sheet: " + ex);
//        }
//    }

    public void read_to_file_MergedCells() {
//        try {
//            int lastRow = sheet.getLastRowNum();
//            for (int r = 0; r < lastRow; r++) {
//                String rowString = "";
//                for (int c = 0; c < headingCount; c++) {
//                    Row row = sheet.getRow(r);
//                    if (missingCols(row, c)) {
//                        rowString = addEmptyCols(row, rowString);
//                    } else {
//                        Cell cell = row.getCell(c);
//                        String cellValue = getCellValue(cell, c);
//                        if (r == 0) {
//                            cellValue = formatHeading(remNewLineChar(cellValue));
//                        } else {
//                            if (isMergedColumn(c)) {
//                                setCases(cellValue);
//                                cellValue = "merged_data";
//                            }
//                        }
//                        cellValue = remNewLineChar(cellValue);
//                        rowString += DELIMITER + cellValue;
//                    }
//                }
//                rowString += DELIMITER + " ";
//                if (!rowString.isEmpty()) {
//                    rowString = rowString.substring(DELIMITER.length());
//                    rowString = remExtraCols(rowString);
//                }
//                writeString(rowString);
//            }
//            writer.close();
//        } catch (Exception ex) {
//            System.err.println("Error opening input excel sheet: " + ex);
//        }
    }

//    private void init() throws Exception {
//        setSheet();
//        setHeadingCount();
//        setHeadings();
//    }
//
//    private void setHeadings() {
//        for (int c = 0; c < headingCount; c++) {
//            Row row = sheet.getRow(0);
//            Cell cell = row.getCell(c);
//            String cellValue = getCellValue(cell, c);
//            cellValue = remNewLineChar(cellValue);
//            headings.add(formatHeading(cellValue));
//        }
//    }
//
//    private boolean isMergedColumn(int index) {
//        return headings.get(index).trim().equalsIgnoreCase(MERGED_COLUMN_NAME);
//    }
//
//    private void setSheet() throws Exception {
//        System.out.println("Downloading file: " + inputFileName);
//        FileInputStream inp = new FileInputStream(inputFileName);
//        Workbook workbook = WorkbookFactory.create(inp);
//        sheet = workbook.getSheetAt(SHEET_TO_PROCESS);
//    }
//
//    private void setHeadingCount() {
//        Row headRow = sheet.getRow(0);
//        headingCount = headRow.getLastCellNum();
//    }
//
//    private void setCases(String cellValue) {
//        Merged_Cases merged = new Merged_Cases(cellValue);
//        cases.clear();
//        cases.addAll(merged.getCases());
//    }
//
//    private void writeString(String rowString) throws IOException {
//        for (String case_ : cases) {
//            String newStr = rowString.replace("merged_data", case_);
//            writer.write(newStr + "\r\n");
//        }
//        if (cases.isEmpty()) {
//            writer.write(rowString);
//        }
//    }
//
//    private boolean missingCols(Row row, int col) {
//        return row.getLastCellNum() <= (col);
//    }
//
//    private String addEmptyCols(Row row, String str) {
//        for (int i = row.getLastCellNum(); i < (headingCount + 1); i++) {
//            str += DELIMITER + " ";
//        }
//        return str;
//    }
//
//    private String getCellValue(Cell cell, int colNumber) {
//        String value = "";
//        if (cell == null) {
//            return "";
//        }
//        try {
//            if ((cell.getCellType() == NUMERIC) && !isDateColumn(colNumber)) {
//                double d = cell.getNumericCellValue();
//                if (isInt(d)) {
//                    value += (int) d;
//                } else {
//                    value += d;
//                }
//            } else if ((cell.getCellType() == NUMERIC) && isDateColumn(colNumber)) {
//                value = String.valueOf(cell.getDateCellValue());
//            } else if (cell.getCellType() == FORMULA) {
//                value = cell.getStringCellValue();
//            } else {
//                value = cell.toString();
//            }
//        } catch (Exception e) {
//            if (cell.getCellType() == FORMULA) {
//                value = "#REF!";
//            }
//        }
//        return value.trim().replace(DELIMITER, "");
//    }
//
//    private boolean isDateColumn(int col) {
//        return col == DATE_COL_INDEX;
//    }
//
//    private boolean isInt(double d) {
//        String s = String.valueOf(d);
//        return s.endsWith(".0");
//    }
//
//    private String remExtraCols(String str) {
//        List<String> split = new ArrayList<String>(Arrays.asList(str.split(DELIMITER)));
//        for (int i = split.size(); i > headingCount; i--) {
//            split.remove(split.size() - 1);
//        }
//        return listToString(split);
//    }
//
//    private String remNewLineChar(String str) {
//        String out = "";
//        for (int i = 0; i < str.length(); i++) {
//            char c = str.charAt(i);
//            if ((int) c != 10) {
//                out += c;
//            }
//        }
//        return out;
//    }
//
//    private String formatHeading(String str) {
//        str = str.trim();
//        str = str.replace("  ", " ");
//        str = str.replace(" ", "_");
//        str = str.replace("'", "");
//        str = str.replace("#", "");
//        return str.toUpperCase();
//    }
//
//    private String listToString(List lst) {
//        String out = "";
//        for (Object l : lst) {
//            out += DELIMITER + l;
//        }
//        if (out.isEmpty()) {
//            return out;
//        }
//        return out.substring(DELIMITER.length());
//    }
}

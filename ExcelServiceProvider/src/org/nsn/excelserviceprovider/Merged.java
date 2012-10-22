/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author z0006cpz
 */
public class Merged extends Excel_Super {

//    private int DATE = 3;
    private List<String> headings = new ArrayList<String>();
    private List<String> cases = new ArrayList<String>();
    private String MERGED_COLUMN_NAME = "Case_History";

    public Merged(BufferedWriter writer, String inputFileName) {
        super(writer, inputFileName);
        setHeadings();
    }

    public void read_to_file() {
        try {
            int lastRow = sheet.getLastRowNum();
            for (int r = 0; r < lastRow; r++) {
                String rowString = "";
                for (int c = 0; c < headingCount; c++) {
                    Row row = sheet.getRow(r);
                    if (missingCols(row, c)) {
                        rowString = addEmptyCols(row, rowString);
                    } else {
                        Cell cell = row.getCell(c);
                        String cellValue = getCellValue(cell);
                        if (r == 0) {
                            cellValue = formatHeading(remNewLineChar(cellValue));
                        } else {
                            if (isMergedColumn(c)) {
                                setCases(cellValue);
                                cellValue = "merged_data";
                            }
                        }
                        cellValue = remNewLineChar(cellValue);
                        rowString += DELIMITER + cellValue;
                    }
                }
                rowString += DELIMITER + " ";
                if (!rowString.isEmpty()) {
                    rowString = rowString.substring(DELIMITER.length());
                    rowString = remExtraCols(rowString);
                }
                writeString(rowString);
            }
            writer.close();
        } catch (Exception ex) {
            System.err.println("Error opening input excel sheet: " + ex);
        }
    }

    private void setHeadings() {
        for (int c = 0; c < headingCount; c++) {
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(c);
            String cellValue = getCellValue(cell);
            cellValue = remNewLineChar(cellValue);
            headings.add(formatHeading(cellValue));
        }
    }

    private String getCellValue(Cell cell) {
        String value = "";
        if (cell == null) {
            return "";
        }
        try {
            if ((cell.getCellType() == NUMERIC)) {
                if (isDate(cell)) {
                    value = String.valueOf(cell.getDateCellValue());
                } else {
                    double d = cell.getNumericCellValue();
                    if (isInt(d)) {
                        value += (int) d;
                    } else {
                        value += d;
                    }
                }
            } else if (cell.getCellType() == FORMULA) {
                value = cell.getStringCellValue();
            } else {
                value = cell.toString();
            }
        } catch (Exception e) {
            if (cell.getCellType() == FORMULA) {
                value = "#REF!";
            }
        }
        return value.trim().replace(DELIMITER, "");
    }

    private boolean isDate(Cell cell) {
        return ((cell.toString().contains("-") && !cell.toString().startsWith("-")) 
                || (cell.toString().contains("/") && !cell.toString().startsWith("/"))
                || (cell.toString().contains("\\") && !cell.toString().startsWith("\\")));
    }

    private void setCases(String cellValue) {
        Merged_Cases merged = new Merged_Cases(cellValue);
        cases.clear();
        cases.addAll(merged.getCases());
    }

    private boolean isMergedColumn(int index) {
        return headings.get(index).trim().equalsIgnoreCase(MERGED_COLUMN_NAME);
    }

    private void writeString(String rowString) throws IOException {
        for (String case_ : cases) {
            String newStr = rowString.replace("merged_data", case_);
            writer.write(newStr + "\r\n");
        }
        if (cases.isEmpty()) {
            writer.write(rowString + "\r\n");
        }
    }
}

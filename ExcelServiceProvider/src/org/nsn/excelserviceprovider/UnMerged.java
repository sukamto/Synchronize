/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.io.BufferedWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author z0006cpz
 */
public class UnMerged extends Excel_Super {

    private int DATE_COL_INDEX = 0;

    public UnMerged(BufferedWriter writer, String inputFileName) {
        super(writer, inputFileName);
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
                        String cellValue = getCellValue(cell, c);
                        cellValue = remNewLineChar(cellValue);
                        if (r == 0) {
                            cellValue = formatHeading(cellValue);
                        }
                        rowString += DELIMITER + cellValue;
                    }
                }
                rowString += DELIMITER + " ";
                if (!rowString.isEmpty()) {
                    rowString = rowString.substring(DELIMITER.length());
                    rowString = remExtraCols(rowString);
                }
                writer.write(rowString + "\r\n");
            }
            writer.close();
        } catch (Exception ex) {
            System.err.println("Error opening input excel sheet: " + ex);
        }
    }

    private String getCellValue(Cell cell, int colNumber) {
        String value = "";
        if (cell == null) {
            return "";
        }
        try {
            if ((cell.getCellType() == NUMERIC) && !isDateColumn(colNumber)) {
                double d = cell.getNumericCellValue();
                if (isInt(d)) {
                    value += (int) d;
                } else {
                    value += d;
                }
            } else if ((cell.getCellType() == NUMERIC) && isDateColumn(colNumber)) {
                value = String.valueOf(cell.getDateCellValue());
            } else {
                value += cell.getStringCellValue();
            }
        } catch (Exception e) {
            if (cell.getCellType() == FORMULA) {
                value = "#REF!";
            }
        }
        return value.trim().replace(DELIMITER, "");
    }

    private boolean isDateColumn(int col) {
        return col == DATE_COL_INDEX;
    }
}

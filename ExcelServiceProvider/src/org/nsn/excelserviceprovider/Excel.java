/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.io.*;
import org.nsn.excelservice.ExcelServiceInterface;

/**
 *
 * @author z0006cpz
 */
@org.openide.util.lookup.ServiceProvider(service = ExcelServiceInterface.class)
public class Excel implements ExcelServiceInterface {

    private static BufferedWriter writer = null;
//    private static short headingCount;
//    private static Sheet sheet;
    private String outputFileName;
    private boolean mergedCells;

    public Excel() {
    }

    @Override
    public String convertXLSXtoCSV(String inputFileName, String outputFileName, boolean mergedCells) {
        this.outputFileName = outputFileName;
        this.mergedCells = mergedCells;
        convert(inputFileName);
        return outputFileName;
    }

    private void convert(String inputFielName) {
        setWriter();
        if (writer != null) {
            if (!mergedCells) {
                UnMerged unMerged = new UnMerged(writer, inputFielName);
                unMerged.read_to_file();
            } else {
                Merged merged = new Merged(writer, inputFielName);
                merged.read_to_file();
            }
//            try {
//                System.out.println("Downloading file: " + inputFielName);
//                FileInputStream inp = new FileInputStream(inputFielName);
//                Workbook workbook = WorkbookFactory.create(inp);
//                sheet = workbook.getSheetAt(SHEET_TO_PROCESS);
//                setHeadingCount();
//                int lastRow = sheet.getLastRowNum();
//                for (int r = 0; r < lastRow; r++) {
//                    String rowString = "";
//                    for (int c = 0; c < headingCount; c++) {
//                        Row row = sheet.getRow(r);
//                        if (missingCols(row, c)) {
//                            rowString = addEmptyCols(row, rowString);
//                        } else {
//                            Cell cell = row.getCell(c);
//                            String cellValue = getCellValue(cell, c);
//                            cellValue = remNL(cellValue);
//                            if (r == 0) {
//                                cellValue = formatHeading(cellValue);
//                            }
//                            rowString += DELIMITER + cellValue;
//                        }
//                    }
//                    rowString += DELIMITER + " ";
//                    if (!rowString.isEmpty()) {
//                        rowString = rowString.substring(DELIMITER.length());
//                        rowString = remExtraCols(rowString);
//                    }
//                    writer.write(rowString + "\r\n");
//                }
//                writer.close();
//            } catch (Exception ex) {
//                System.err.println("Error opening input excel sheet: " + ex);
//            }
        }
    }

//    private void setHeadingCount() {
//        Row headRow = sheet.getRow(0);
//        headingCount = headRow.getLastCellNum();
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
//    private String remExtraCols(String str) {
//        List<String> split = new ArrayList<String>(Arrays.asList(str.split(DELIMITER)));
//        for (int i = split.size(); i > headingCount; i--) {
//            split.remove(split.size() - 1);
//        }
//        return listToString(split);
//    }
    private void setWriter() {
        boolean waited = false;
        boolean access = false;
        File f = new File(outputFileName);

        while (!access) {
            try {
                if (f.exists()) {
                    f.delete();
                }
                FileWriter fwriter = new FileWriter(f);
                writer = new BufferedWriter(fwriter);
                access = true;
            } catch (IOException ex) {
                if (!waited) {
                    System.out.println("Waiting for access to file: " + f.toString());
                }
                waited = true;
                int i = 0;
                while (i <= 2) {
                    System.gc();
                    i++;
                }
            }
        }
        if (waited) {
            System.out.println("Access granted");
        }
    }
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
//            } else {
//                value += cell.getStringCellValue();
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
//    private String remNL(String str) {
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author z0006cpz
 */
public class Excel_Super {

    protected BufferedWriter writer;
    private String inputFileName;
    private int SHEET_TO_PROCESS = 0;
    protected String DELIMITER = "<~>";
    protected static int NUMERIC = 0;
    protected static int FORMULA = 2;
    protected static short headingCount;
    protected static Sheet sheet;

    public Excel_Super(BufferedWriter writer, String inputFileName) {
        this.writer = writer;
        this.inputFileName = inputFileName;
        try {
            init();
        } catch (Exception ex) {
            System.err.println("Error opening input excel sheet: " + ex);
        }
    }

    private void init() throws Exception {
        setSheet();
        setHeadingCount();
    }

    private void setSheet() throws Exception {
        System.out.println("Downloading file: " + inputFileName);
        FileInputStream inp = new FileInputStream(inputFileName);
        Workbook workbook = WorkbookFactory.create(inp);
        sheet = workbook.getSheetAt(SHEET_TO_PROCESS);
    }

    private void setHeadingCount() {
        Row headRow = sheet.getRow(0);
        headingCount = headRow.getLastCellNum();
    }

    protected boolean missingCols(Row row, int col) {
        return row.getLastCellNum() <= (col);
    }

    protected String addEmptyCols(Row row, String str) {
        for (int i = row.getLastCellNum(); i < (headingCount + 1); i++) {
            str += DELIMITER + " ";
        }
        return str;
    }

    protected boolean isInt(double d) {
        String s = String.valueOf(d);
        return s.endsWith(".0");
    }

    protected String remExtraCols(String str) {
        List<String> split = new ArrayList<String>(Arrays.asList(str.split(DELIMITER)));
        for (int i = split.size(); i > headingCount; i--) {
            split.remove(split.size() - 1);
        }
        return listToString(split);
    }

    protected String remNewLineChar(String str) {
        String out = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((int) c != 10) {
                out += c;
            }
        }
        return out;
    }

    protected String formatHeading(String str) {
        str = str.trim();
        str = str.replace("#", "");
        str = str.trim();
        str = str.replace("  ", " ");
        str = str.replace(" ", "_");
        str = str.replace("'", "");
        return str.toUpperCase().trim();
    }

    private String listToString(List lst) {
        String out = "";
        for (Object l : lst) {
            out += DELIMITER + l;
        }
        if (out.isEmpty()) {
            return out;
        }
        return out.substring(DELIMITER.length());
    }
}

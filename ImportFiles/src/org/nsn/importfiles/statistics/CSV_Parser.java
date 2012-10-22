/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.nsn.filemethodsservice.FileMethods;

/**
 *
 * @author z0006cpz
 */
public class CSV_Parser extends FileMethods {

    private String[] ILLEGAL_TEXT = {"NaN", "Infinite"};
    private int[] ILLEGAL_DATE_CHARS = {34};
    private String OLD_DATE_COLUMNNAME = "period_start_time_old";
    private String DATE_INDICATOR = "period_start_time";
    private List<String> csvLines = new ArrayList<String>();
    private String dateFormat;
    private String inputFileName;

    public CSV_Parser(String inputFileName, String outputFileName, String dateFormat) {
        super(inputFileName, outputFileName);
        this.inputFileName = inputFileName;
        this.dateFormat = dateFormat;
    }

    public boolean parseFile() {
        try {
            setCsvLines();
            if (csvLines.size() > 1) {
//                getCSVtype();
                if (getDateColumnIndex() > -1) {
                    addDateToEachLine();
                    addNewDateColumnName();
//                    setDateColumnName();
//                    tuneColumnNames();
                    listToFile(csvLines);
                    return true;
                } else {
                    System.err.println(getCurrentDateTime() + "->Error: Unknown csv file format: Date column not found. " + inputFileName);
                }
            }
        } catch (Exception ex) {
            System.err.println(getCurrentDateTime() + "->Error reading from " + inputFileName + ": " + ex);
        }
        return false;
    }

//    private void getCSVtype() {
//        String[] split = csvLines.get(0).split(DELIMITER);
//        if (split[0].toUpperCase().trim().contains(DATE_INDICATOR)) {
//            csvType = BASIC;
//        } else if (isValidDate(split[split.length - 1].toUpperCase().trim())) {
//            csvType = NON_BASIC;
//        } else {
//            csvType = UNKNOWN;
//        }
//    }
    private void setCsvLines() throws IOException {
        csvLines.addAll(fileToList(true));
        changeDelimiter();
    }

    private void changeDelimiter() {
        for (int i = 0; i < csvLines.size(); i++) {
            csvLines.set(i, csvLines.get(i).replace(",", DELIMITER));
        }
    }

    private void addDateToEachLine() throws Exception {
        int dateIndex = getDateColumnIndex();
        for (int i = 1; i < csvLines.size(); i++) {
            String date = dateToDBformat(getDate(i, dateIndex));
            String newStr = removeTrailingDelimiter(csvLines.get(i));
            String s = sanitize(date + DELIMITER + newStr);
            if (s.endsWith(DELIMITER)) {
                s = s.substring(0, s.length() - DELIMITER.length());
            }
            csvLines.set(i, s);
        }
    }

    private void addNewDateColumnName() {
        String head = csvLines.get(0);
        head = head.replace(DATE_INDICATOR, OLD_DATE_COLUMNNAME);
        head = DATE_INDICATOR + DELIMITER + head;
        csvLines.set(0, head);
//      csvLines.set(0, NEW_DATE_COLUMNNAME + DELIMITER + csvLines.get(0));
    }

    private String removeTrailingDelimiter(String s) {
        if (s.startsWith(DELIMITER)) {
            return s.substring(DELIMITER.length());
        }
        return s;
    }

    private Date getDate(int i, int dateIndex) throws Exception {
        String[] split = csvLines.get(i).split(DELIMITER);
        String d = split[dateIndex].trim();
        d = sanitizeDateString(d);
        return stringToDateTime(d, dateFormat);
    }

    private int getDateColumnIndex() {
        String[] split = csvLines.get(0).split(DELIMITER);
        for (int i = 0; i < split.length; i++) {
            String col = split[i];
            if (col.equalsIgnoreCase(DATE_INDICATOR)) {
                return i;
            }
        }
        return -1;
    }

    private String sanitizeDateString(String str) {
        String out = "";
        boolean illegalChar = false;
        for (char c : str.toCharArray()) {
            int cInt = c;
            for (int i : ILLEGAL_DATE_CHARS) {
                if (cInt == i) {
                    illegalChar = true;
                    break;
                }
            }
            if (!illegalChar) {
                out += c;
            }
        }
        return out;
    }

//    private void setDateColumnName() {
//        String[] cols = csvLines.get(0).split(DELIMITER);
//        cols[0] = "Date";
//        csvLines.set(0, arrayToStr(cols));
//    }
//    private String arrayToStr(String[] ar) {
//        String out = "";
//        for (String s : ar) {
//            out += DELIMITER + s;
//        }
//        return out.substring(DELIMITER.length());
//    }
//
//    private void tuneColumnNames() {
//        if (csvType.equals(NON_BASIC)) {
//            String[] cols = csvLines.get(0).split(DELIMITER);
//            cols[cols.length - 1] = cols[cols.length - 2];
//            cols[cols.length - 2] = "skip";
//            csvLines.set(0, arToString(cols));
//        }
//    }
//    private String arToString(String[] ar) {
//        String out = "";
//        for (String s : ar) {
//            out += DELIMITER + s;
//        }
//        return out.substring(DELIMITER.length());
//    }
    private String sanitize(String str) {
        for (String ill : ILLEGAL_TEXT) {
            str = str.replace(ill, "");
        }
        return str;
    }
}

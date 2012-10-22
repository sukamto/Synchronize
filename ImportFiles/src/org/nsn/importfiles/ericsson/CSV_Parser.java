/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.ericsson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private String BASIC = "basic";
    private String NON_BASIC = "non_basic";
    private String UNKNOWN = "unknown";
    private int[] ILLEGAL_DATE_CHARS = {34};
    private String DATE_INDICATOR = "DAY:";
    private String csvType;
    private List<String> csvLines = new ArrayList<String>();
    private String inputFileName;

    public CSV_Parser(String inputFileName, String outputFileName) {
        super(inputFileName, outputFileName);
        this.inputFileName = inputFileName;
    }

    public boolean parseFile() {
        try {
            setCsvLines();
            if (csvLines.size() > 2) {
                getCSVtype();
                if (!csvType.equals(UNKNOWN)) {
                    addDateToEachLine();
                    setDateColumnName();
                    tuneColumnNames();
                    listToFile(csvLines);
                    return true;
                } else {
                    System.err.println(getCurrentDateTime() + "->Error: Unknown csv file format: Day column not found where expected. " + inputFileName);
                }
            }
        } catch (Exception ex) {
            System.err.println(getCurrentDateTime() + "->Error reading from " + inputFileName + ": " + ex);
        }
        return false;
    }

    private void getCSVtype() {
        String[] split = csvLines.get(0).split(DELIMITER);
        if (split[0].toUpperCase().trim().contains(DATE_INDICATOR)) {
            csvType = BASIC;
        } else if (isValidDate(split[split.length - 1].toUpperCase().trim())) {
            csvType = NON_BASIC;
        } else {
            csvType = UNKNOWN;
        }
    }

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
        String date = dateToDBformat(getDate());
        for (int i = 1; i < csvLines.size(); i++) {
            String s = sanitize(date + csvLines.get(i));
            if (s.endsWith(DELIMITER)) {
                s = s.substring(0, s.length() - DELIMITER.length());
            }
            csvLines.set(i, s);
        }
    }

    private Date getDate() throws Exception {
        String[] split = csvLines.get(0).split(DELIMITER);
        if (csvType.equals(BASIC)) {
            String d = split[0].substring(DATE_INDICATOR.length() + 1).trim();
            d = sanitizeDateString(d);
            return stringToDateTimeEricsson(d);
        } else {
            String d = split[split.length - 1];
            return stringToDateTimeEricsson(d);
        }
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

    private void setDateColumnName() {
        String[] cols = csvLines.get(0).split(DELIMITER);
        cols[0] = "Date";
        csvLines.set(0, arrayToStr(cols));
    }

    private String arrayToStr(String[] ar) {
        String out = "";
        for (String s : ar) {
            out += DELIMITER + s;
        }
        return out.substring(DELIMITER.length());
    }

    private void tuneColumnNames() {
        if (csvType.equals(NON_BASIC)) {
            String[] cols = csvLines.get(0).split(DELIMITER);
            cols[cols.length - 1] = cols[cols.length - 2];
            cols[cols.length - 2] = "skip";
            csvLines.set(0, arToString(cols));
        }
    }

    private String arToString(String[] ar) {
        String out = "";
        for (String s : ar) {
            out += DELIMITER + s;
        }
        return out.substring(DELIMITER.length());
    }

    private String sanitize(String str) {
        for (String ill : ILLEGAL_TEXT) {
            str = str.replace(ill, "");
        }
        return str;
    }
}

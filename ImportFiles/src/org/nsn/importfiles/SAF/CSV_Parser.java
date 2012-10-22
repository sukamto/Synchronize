/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.SAF;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.nsn.filemethodsservice.FileMethods;

/**
 *
 * @author z0006cpz
 */
public class CSV_Parser extends FileMethods {

    private int[] DATE_INDEX = {18, 20};
    private String[] ILLEGAL_TEXT = {"NaN", "Infinite"};
    private String BASIC = "basic";
    private String NON_BASIC = "non_basic";
    private String UNKNOWN = "unknown";
    private int[] ILLEGAL_DATE_CHARS = {34};
    private String DATE_INDICATOR = "DATE";
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
//                    setDateColumnName();
//                    tuneColumnNames();
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
        if (split[DATE_INDEX[0]].toUpperCase().trim().contains(DATE_INDICATOR)) {
            csvType = BASIC;
        } else {
            csvType = UNKNOWN;
        }
    }

    private void setCsvLines() throws IOException {
        csvLines.addAll(fileToList(true));
//        changeDelimiter();
    }

//    private void changeDelimiter() {
//        for (int i = 0; i < csvLines.size(); i++) {
//            csvLines.set(i, csvLines.get(i).replace(",", DELIMITER));
//        }
//    }

    private void addDateToEachLine() {
        for (int i = 1; i < csvLines.size(); i++) {
            for (int j = 0; j < DATE_INDEX.length; j++) {
                try {
                    Date date = getDate(i, j);
                    if (date != null) {
                        String dateStr = dateToDBformat(date);
                        String s = updateDate(csvLines.get(i), dateStr, j);
                        if (s.endsWith(DELIMITER)) {
                            s = s.substring(0, s.length() - DELIMITER.length());
                        }
                        csvLines.set(i, s);
                    }
                } catch (ParseException e) {
                    System.err.println(getCurrentDateTime() + "->Error reading from " + inputFileName + ": " + e);
                }
            }
        }
    }

    private Date getDate(int row, int dateIndex) throws ParseException {
        String[] split = csvLines.get(row).split(DELIMITER);
        String d = sanitizeDateString(split[DATE_INDEX[dateIndex]]);
        if (d.isEmpty()) {
            return null;
        }
        return stringToDateTimeNSN(d);
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

//    private void tuneColumnNames() {
//        if (csvType.equals(NON_BASIC)) {
//            String[] cols = csvLines.get(0).split(DELIMITER);
//            cols[cols.length - 1] = cols[cols.length - 2];
//            cols[cols.length - 2] = "skip";
//            csvLines.set(0, arToString(cols));
//        }
//    }

    private String arToString(String[] ar) {
        String out = "";
        for (String s : ar) {
            out += DELIMITER + s;
        }
        return out.substring(DELIMITER.length());
    }

    private String updateDate(String str, String dateStr, int dateIndex) {
        String[] split = str.split(DELIMITER);
        split[DATE_INDEX[dateIndex]] = dateStr;
        str = arToString(split);
        for (String ill : ILLEGAL_TEXT) {
            str = str.replace(ill, "");
        }
        return str;
    }
}

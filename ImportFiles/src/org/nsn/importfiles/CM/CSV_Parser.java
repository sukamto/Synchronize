/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles.CM;

import java.io.File;
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
    private String DATE_INDICATOR = "period_start_time";
    private String FILE_NAME_DATE_DELIMITER = "_";
    private int FILE_NAME_DATE_INDEX = 1;
    private List<String> csvLines = new ArrayList<String>();
    private String dateFormat;
    private String inputFileName;
    private String date;

    public CSV_Parser(String inputFileName, String outputFileName, String dateFormat) {
        super(inputFileName, outputFileName);
        this.inputFileName = inputFileName;
        this.dateFormat = dateFormat;
    }

    public boolean parseFile() {
        try {
            setCsvLines();
            if (csvLines.size() > 1) {
                try {
                    setDate();
                    addDateToEachLine();
                    addNewDateColumnName();
//                    setDateColumnName();
//                    tuneColumnNames();
                    listToFile(csvLines);
                    return true;
                } catch (Exception dateException) {
                    System.err.println(getCurrentDateTime() + "->Error extracting date from " + inputFileName + ": " + dateException);
                }
            }
        } catch (Exception ex) {
            System.err.println(getCurrentDateTime() + "->Error reading from " + inputFileName + ": " + ex);
        }
        return false;
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
        for (int i = 1; i < csvLines.size(); i++) {
            String newStr = (csvLines.get(i));
            String s = sanitize(date + DELIMITER + newStr);
            if (s.endsWith(DELIMITER)) {
                s = s.substring(0, s.length() - DELIMITER.length());
            }
            csvLines.set(i, s);
        }
    }

    private void addNewDateColumnName() {
        String head = csvLines.get(0);
        String[] split = head.split(DELIMITER);
        head = DATE_INDICATOR + DELIMITER + arrayToStr(split);
        csvLines.set(0, head);
    }

    private String arrayToStr(String[] ar) {
        String str = "";
        for (String string : ar) {
            str += DELIMITER + string;
        }
        if (str.isEmpty()) {
            return str;
        }
        return str.substring(DELIMITER.length());
    }

    private void setDate() throws Exception {
        String fileName = getFileName(inputFileName);
        String[] split = fileName.split(FILE_NAME_DATE_DELIMITER);
        String d = split[FILE_NAME_DATE_INDEX].trim();
        d = sanitizeDateString(d);
        date = dateToDBformat(stringToDateTime(d, dateFormat));
    }

    private String getFileName(String name) {
        File file = new File(name);
        return file.getName();
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

    private String sanitize(String str) {
        for (String ill : ILLEGAL_TEXT) {
            str = str.replace(ill, "");
        }
        return str;
    }
}

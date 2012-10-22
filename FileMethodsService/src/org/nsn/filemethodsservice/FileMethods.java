/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.filemethodsservice;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author z0006cpz
 */
public class FileMethods {

    protected String DELIMITER = "<~>";
    protected BufferedWriter writer = null;
    protected BufferedReader reader = null;
    private String fileName;

    public FileMethods(String readerName, String writerName) {
        setReader(readerName);
        setWriter(writerName);
    }

    public FileMethods() {
    }

    public void setReaderName(String readerName) {
        setReader(readerName);
    }

    public void setWriterName(String writerName) {
        setWriter(writerName);
    }

    public List<String> fileToList(boolean showMsg) throws IOException {
        String strLine;
        if (showMsg) {
            System.out.println("Downloading file: " + fileName);
        }
        List<String> lines = new ArrayList<String>();
        while ((strLine = reader.readLine()) != null) {
            lines.add(strLine);
        }
        reader.close();
        return lines;
    }

    protected boolean isValidDate(String str) {
        Date date;
        DateFormat formatter;
        formatter = new SimpleDateFormat("dd MMM yy");
        try {
            date = (Date) formatter.parse(str);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    protected Date stringToDateTimeEricsson(String str) throws ParseException {
        Date date;
        DateFormat formatter;
        formatter = new SimpleDateFormat("dd MMM yy");
        date = (Date) formatter.parse(str);
        return date;
    }

    protected Date stringToDateTime(String str, String dateTimeFormat) throws ParseException {
        Date date;
        DateFormat formatter;
        formatter = new SimpleDateFormat(dateTimeFormat);
        date = (Date) formatter.parse(str);
        return date;
    }

    protected Date stringToDateTimeNSN(String str) throws ParseException {
        Date date;
        DateFormat formatter;
        formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        date = (Date) formatter.parse(str);
        return date;
    }

    protected String dateToDBformat(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formatter.format(date);
    }

    protected String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void listToFile(List<String> list) throws IOException {
        for (String s : list) {
            writer.write(s + "\r\n");
        }
        writer.close();
    }

    private void setReader(String fileName) {
        FileInputStream fstream;
        try {
            this.fileName = fileName;
            fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            reader = new BufferedReader(new InputStreamReader(in));
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
    }

    private void setWriter(String fileName) {
        boolean waited = false;
        boolean access = false;
//        try {
        File f = new File(fileName);

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

//            if (f.exists()) {
//                f.delete();
//            }
//            FileWriter fwriter = new FileWriter(f);
//            writer = new BufferedWriter(fwriter);
//        } catch (IOException ex) {
//            System.err.println(ex);
//        }
    }
}

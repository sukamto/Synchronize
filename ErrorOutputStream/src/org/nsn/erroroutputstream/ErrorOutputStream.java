/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.erroroutputstream;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;

public class ErrorOutputStream
        extends OutputStream {

// *****************************************************************************
// INSTANCE PROPERTIES
// *****************************************************************************
    private JTextArea textArea;                               // target text area
    private int maxLines;                               // maximum lines allowed in text area
    private LinkedList lineLengths;                            // length of lines within text area
    private int curLength;                              // length of current line
    private byte[] oneByte;                                // array for write(int val);

// *****************************************************************************
// INSTANCE CONSTRUCTORS/INIT/CLOSE/FINALIZE
// *****************************************************************************
    public ErrorOutputStream(JTextArea ta) {
        this(ta, 1000);
    }

    public ErrorOutputStream(JTextArea ta, int ml) {
        textArea = ta;
        maxLines = ml;
        lineLengths = new LinkedList();
        curLength = 0;
        oneByte = new byte[1];
    }

// *****************************************************************************
// INSTANCE METHODS - ACCESSORS
// *****************************************************************************
    public synchronized void clear() {
        lineLengths = new LinkedList();
        curLength = 0;
        textArea.setText("");
    }

    /**
     * Get the number of lines this TextArea will hold.
     */
    public synchronized int getMaximumLines() {
        return maxLines;
    }

    /**
     * Set the number of lines this TextArea will hold.
     */
    public synchronized void setMaximumLines(int val) {
        maxLines = val;
    }

// *****************************************************************************
// INSTANCE METHODS
// *****************************************************************************
    public void close() {
        if (textArea != null) {
            textArea = null;
            lineLengths = null;
            oneByte = null;
        }
    }

    @Override
    public void flush() {
    }

    public void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    @Override
    public void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    @Override
    public synchronized void write(byte[] ba, int str, int len) {
        String message = new String(ba, str, len);
        if (message.startsWith("SetPrecentage")) {
            message = prepPercentage(message);
            int i = getProgressLocation(textArea.getText());
            message = addProChar(message);
            textArea.replaceRange("", i, textArea.getText().length());
            ba = message.getBytes();
            len = ba.length;
        }
        try {
            curLength += len;
//            if (bytesEndWith(ba, str, len, LINE_SEP)) {
//                lineLengths.addLast(new Integer(curLength));
//                curLength = 0;
//                if (lineLengths.size() > maxLines) {
//                    textArea.replaceRange(null, 0, ((Integer) lineLengths.removeFirst()).intValue());
//                }
//            }
            for (int xa = 0; xa < 10; xa++) {
                try {
                    textArea.append(new String(ba, str, len));
                    break;
                } catch (Throwable thr) {                                                 // sometimes throws a java.lang.Error: Interrupted attempt to aquire write lock
                    if (xa == 9) {
                        thr.printStackTrace();
                    } else {
                        Thread.sleep(200);
                    }
                }
            }
        } catch (Throwable thr) {
            CharArrayWriter caw = new CharArrayWriter();
            thr.printStackTrace(new PrintWriter(caw, true));
            textArea.append(System.getProperty("line.separator", "\n"));
            textArea.append(caw.toString());
        }
        setCaret();
    }

    private String prepPercentage(String str) {
        str = str.replace("SetPrecentage", "");
        str = str.replace("\n", "");
        for (int i = 0; i < 4; i++) {
            if (str.length() != 4) {
                str = " " + str;
            }
        }
        return str;
    }

    private int getProgressLocation(String str) {
        if (str.contains("%")) {
            if (str.lastIndexOf("%") >= str.length() - 3) {
                return str.lastIndexOf("%") - 4;
            }
        }
        return str.length();
    }

    private String addProChar(String str) {
        List chars = new ArrayList();
        for (Object object : PROCHARS) {
            chars.add(object);
        }
        String c = textArea.getText().substring(textArea.getText().length() - 7,
                textArea.getText().length() - 6);
        int i = chars.indexOf(c);
        if (i == chars.size() - 1) {
            i = -1;
        }
        return chars.get(i + 1) + str;
    }

    private void setCaret() {
        try {
            int lastLineStartPosi = textArea.getLineStartOffset(textArea.getLineCount() - 1);
            if (textArea.getCaretPosition() < lastLineStartPosi
                    && textArea.getText().length() > lastLineStartPosi) {
                textArea.moveCaretPosition(lastLineStartPosi);
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(ErrorOutputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean bytesEndWith(byte[] ba, int str, int len, byte[] ew) {
        if (len < LINE_SEP.length) {
            return false;
        }
        for (int xa = 0, xb = (str + len - LINE_SEP.length); xa < LINE_SEP.length; xa++, xb++) {
            if (LINE_SEP[xa] != ba[xb]) {
                return false;
            }
        }
        return true;
    }
// *****************************************************************************
// STATIC PROPERTIES
// *****************************************************************************
    static private byte[] LINE_SEP = System.getProperty("line.separator", "\n").getBytes();
    static private String[] PROCHARS = {"-", "\\", "|", "/"};
} /*
 * END PUBLIC CLASS
 */
